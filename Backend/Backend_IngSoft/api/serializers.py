import base64
from io import BytesIO

from Backend_IngSoft.models import (
    Acquisto,
    AutoUsata,
    Concessionario,
    Configurazione,
    Detrazione,
    ImmaginiAutoNuove,
    ImmaginiAutoUsate,
    ModelloAuto,
    Optional,
    Periodo,
    Preventivo,
    PreventivoUsato,
    Ritiro,
    Sconto,
    Utente,
)
from PIL import Image
from django.db import IntegrityError, transaction
from django.db.models import Prefetch
from drf_extra_fields.fields import Base64ImageField
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
            with transaction.atomic():
                # controllo se esiste già un preventivo con le stesse caratteristiche
                preventivi_con_conf = Preventivo.objects.prefetch_related(
                    Prefetch(
                        "configurazione_set",
                        queryset=Configurazione.objects.prefetch_related("optional"),
                    )
                )

                optional_ids_user = [opt.id for opt in optional_ids]
                for preventivo in preventivi_con_conf:
                    optional_ids_set = set()
                    # prendo gli id degli opzional del preventivo
                    for configurazione in preventivo.configurazione_set.all():
                        optional_ids_set.update(
                            opt.id for opt in configurazione.optional.all()
                        )
                    # controllo se esiste un preventivo con le stesse opzionali
                    common_ids = optional_ids_set.intersection(optional_ids_user)
                    if len(common_ids) == len(optional_ids_user):
                        if preventivo.modello == preventivo_data["modello"]:
                            raise IntegrityError("Preventivo esistente")

                # Controllo che gli optional siano quelli del modello selezionato nel
                # preventivo
                modello_auto: ModelloAuto = ModelloAuto.objects.get(
                    id=preventivo_data["modello"].id
                )
                for opt in optional_ids:
                    if not modello_auto.optionals.get(id=opt.id):
                        raise IntegrityError("Opzionale non presente nel modello")

                # salvataggio dati nel db
                preventivo = Preventivo.objects.create(**preventivo_data)
                configurazione = Configurazione.objects.create(preventivo=preventivo)
                configurazione.optional.set(optional_ids)
                Ritiro.objects.create(
                    preventivo=preventivo, concessionario=preventivo.concessionario
                )

            return configurazione
        return None


class PreventiviAutoUsateSerializer(serializers.ModelSerializer):
    class Meta:
        model = PreventivoUsato
        fields = "__all__"


class ImmaginiAutoUsateSerializer(serializers.ModelSerializer):
    image_base64 = Base64ImageField(write_only=True)
    image_name = serializers.CharField(write_only=True, required=False)

    class Meta:
        model = ImmaginiAutoUsate
        fields = ["image", "image_name", "image_base64", "auto"]
        read_only_fields = ["image"]

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

    def create(self, validated_data):
        image_base64 = validated_data.pop("image_base64", None)
        image_name = validated_data.pop("image_name", None)

        image_base64.name = image_name

        validated_data["image"] = image_base64

        instance = ImmaginiAutoUsate.objects.create(
            image=image_base64, auto=validated_data["auto"]
        )

        return instance


class DetrazioneSerializer(serializers.ModelSerializer):
    class Meta:
        model = Detrazione
        fields = "__all__"


class ScontiSerializer(serializers.ModelSerializer):
    class Meta:
        model = Sconto
        fields = "__all__"

    def to_representation(self, instance):
        representation = super().to_representation(instance)

        periodo_id = representation.pop("periodo")

        periodo = Periodo.objects.get(id=periodo_id)

        representation["mese"] = periodo.mese
        representation["anno"] = periodo.anno

        return representation
