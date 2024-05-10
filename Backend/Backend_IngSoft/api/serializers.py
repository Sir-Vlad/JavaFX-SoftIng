from rest_framework import serializers
from rest_framework.response import Response

from Backend_IngSoft.models import Utente


class UtenteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Utente
        fields = "__all__"
