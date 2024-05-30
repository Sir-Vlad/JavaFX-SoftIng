import base64
from io import BytesIO

from Backend_IngSoft.models import (
    Acquisto,
    AutoUsata,
    ImmaginiAutoNuove,
    Utente,
    ModelloAuto,
    Optional,
    Sede,
    Preventivo,
)
from PIL import Image
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


class SedeSerializer(serializers.ModelSerializer):
    class Meta:
        model = Sede
        fields = "__all__"


class PreventivoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Preventivo
        fields = "__all__"


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
