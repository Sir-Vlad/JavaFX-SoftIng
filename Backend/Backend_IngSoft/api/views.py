from Backend_IngSoft.api.serializers import (
    AcquistoSerializer,
    AutoUsataSerializer,
    ConcessionarioSerializer,
    ConfigurazioneSerializer,
    ImmaginiAutoNuoveSerializer,
    ModelliAutoSerializer,
    OptionalSerializer,
    PreventivoSerializer,
    UtenteSerializer,
)
from Backend_IngSoft.models import (
    Acquisto,
    AutoUsata,
    Concessionario,
    ImmaginiAutoNuove,
    ModelloAuto,
    Optional,
    Preventivo,
    Utente,
)
from Backend_IngSoft.util.error import raises
from django.http import HttpResponseNotFound
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView


class UtenteListCreateAPIView(APIView):
    def get(self, request) -> Response:
        utente = Utente.objects.all()
        serializer = UtenteSerializer(utente, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = UtenteSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        error = {"message": [i for e in serializer.errors.values() for i in e]}
        print(error)

        return Response(error, status=status.HTTP_409_CONFLICT)


class UtenteDetailAPIView(APIView):
    @raises(Utente.DoesNotExist)
    def get_object(self, email):
        return Utente.objects.get(email=email)

    def get(self, request, email):
        try:
            utente = self.get_object(email)
            serializer = UtenteSerializer(utente)
            return Response(serializer.data)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

    def put(self, request, email):
        try:
            utente = self.get_object(email)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        serializer = UtenteSerializer(utente, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, email):
        try:
            utente = self.get_object(email)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        utente.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class ModelliAutoListAPIView(APIView):

    def get(self, request):
        modelli = ModelloAuto.objects.all()
        serializer = ModelliAutoSerializer(modelli, many=True)
        return Response(serializer.data)


class OptionalAutoListAPIView(APIView):
    def get(self, request, id_auto):
        try:
            auto = ModelloAuto.objects.get(id=id_auto)
        except ModelloAuto.DoesNotExist:
            return HttpResponseNotFound("Auto non esiste")

        serializer = OptionalSerializer(auto.optionals, many=True)
        return Response(serializer.data)


class OptionalsListAPIView(APIView):
    def get(self, request):
        optional = Optional.objects.all()
        serializer = OptionalSerializer(optional, many=True)
        return Response(serializer.data)


class ConcessionarioListAPIView(APIView):
    def get(self, request):
        concessionario = Concessionario.objects.all()
        serializer = ConcessionarioSerializer(concessionario, many=True)
        return Response(serializer.data)


def is_field_error(field_data, field_names):
    return field_data and any(field in field_data for field in field_names)


class PreventiviUtenteListAPIView(APIView):
    def get(self, request, id_utente):
        try:
            utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        preventivi = Preventivo.objects.filter(utente_id=utente.id)

        serializer = PreventivoSerializer(preventivi, many=True)
        return Response(serializer.data)

    def post(self, request, id_utente):
        serializer = ConfigurazioneSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)

        field_names = ("utente", "modello", "concessionario", "non_field_errors")

        if "preventivo" in serializer.errors.keys():
            field_data = serializer.errors.get("preventivo")
            if is_field_error(field_data, field_names):
                return Response(serializer.errors, status=status.HTTP_409_CONFLICT)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class AcquistoUtenteListAPIView(APIView):
    def get(self, request, id_utente):
        try:
            utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        acquisti = Acquisto.objects.filter(utente_id=utente.id)

        serializer = AcquistoSerializer(acquisti, many=True)
        return Response(serializer.data)

    def post(self, request, id_utente):
        try:
            utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        serializer = AcquistoSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class AutoUsateListAPIView(APIView):
    def get(self, request):
        auto = AutoUsata.objects.all()
        serializer = AutoUsataSerializer(auto, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = AutoUsataSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class ImmaginiAutoNuoveListAPIView(APIView):
    def get(self, request, id_auto):
        immagini = ImmaginiAutoNuove.objects.filter(auto=id_auto)
        serializer = ImmaginiAutoNuoveSerializer(immagini, many=True)
        return Response(serializer.data)


class PreventiviListAPIView(APIView):
    def get(self, request):
        preventivi = Preventivo.objects.all()
        serializer = PreventivoSerializer(preventivi, many=True)
        return Response(serializer.data)
