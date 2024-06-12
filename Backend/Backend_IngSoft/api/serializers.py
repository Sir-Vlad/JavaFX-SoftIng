import base64
from Backend_IngSoft.models import (
    Acquisto,
    AutoUsata,
    Concessionario,
    Configurazione,
    ImmaginiAutoNuove,
    ModelloAuto,
    Optional,
    Preventivo,
    PreventivoUsato,
    Utente,
)
from PIL import Image
from io import BytesIO
from rest_framework import serializers


class UtenteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Utente
        fields = "__all__"


class ModelliAutoSerializer(serializers.ModelSerializer):
    class Meta:
        model = ModelloAuto
        fields = "__all__"


class OptionalSerializer(serializers.ModelSerializer):
    class Meta:
        model = Optional
        fields = "__all__"


class ConcessionarioSerializer(serializers.ModelSerializer):
    indirizzo = serializers.SerializerMethodField()

    class Meta:
        model = Concessionario
        fields = ["id", "nome", "indirizzo"]

    def get_indirizzo(self, obj):
        return obj.indirizzo


class PreventivoSerializer(serializers.ModelSerializer):
    utente = serializers.PrimaryKeyRelatedField(queryset=Utente.objects.all())
    modello = serializers.PrimaryKeyRelatedField(queryset=ModelloAuto.objects.all())
    concessionario = serializers.PrimaryKeyRelatedField(
        queryset=Concessionario.objects.all()
    )
    config = serializers.SerializerMethodField()

    class Meta:
        model = Preventivo
        fields = "__all__"

    def get_config(self, obj):
        return (
            Configurazione.objects.filter(preventivo_id=obj.pk)
            .values_list("optional", flat=True)
            .distinct()
        )


class AcquistoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Acquisto
        fields = "__all__"


class AutoUsataSerializer(serializers.ModelSerializer):
    class Meta:
        model = AutoUsata
        fields = "__all__"


class ImmaginiAutoNuoveSerializer(serializers.ModelSerializer):
    class Meta:
        model = ImmaginiAutoNuove
        fields = ("auto", "image")
        read_only_fields = ("phash",)

    def to_representation(self, instance):
        representation = super().to_representation(instance)
        # tolgo il campo img
        img = representation.pop("image")
        # aggiungo come campo il nome dell'immagine
        representation["image_name"] = img.split("/")[-1]

        # converto l'immagine in base64 e la aggiungo al dizionario'
        if instance.image:
            extension = img.split(".")[-1]
            img = Image.open(instance.image)
            buffer = BytesIO()
            if extension == "jpeg":
                img = img.convert("RGB")
                img.save(buffer, format="JPEG")
            elif extension == "png":
                img.save(buffer, format="PNG")
            img_str = base64.b64encode(buffer.getvalue()).decode()
            representation["image_base64"] = img_str

        return representation


class ConfigurazioneSerializer(serializers.ModelSerializer):
    preventivo = PreventivoSerializer()
    optional = serializers.PrimaryKeyRelatedField(
        many=True, queryset=Optional.objects.all(), write_only=True
    )

    class Meta:
        model = Configurazione
        fields = "__all__"

    def create(self, validated_data):
        optional_ids = validated_data.pop("optional", [])
        preventivo_data = validated_data.pop("preventivo")

        if Utente.objects.get(id=preventivo_data["utente"].id):
            # salvataggio dati nel db
            preventivo = Preventivo.objects.create(**preventivo_data)
            configurazione = Configurazione.objects.create(preventivo=preventivo)
            configurazione.optional.set(optional_ids)

            return configurazione
        return None


class PreventiviAutoUsateSerializer(serializers.ModelSerializer):
    class Meta:
        model = PreventivoUsato
        fields = "__all__"
