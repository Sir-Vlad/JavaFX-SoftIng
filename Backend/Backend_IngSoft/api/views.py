from django.db import transaction
from django.http import HttpResponseNotFound
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView

from Backend_IngSoft.api.serializers import (
    AcquistoSerializer,
    AutoUsataSerializer,
    ConcessionarioSerializer,
    ConfigurazioneSerializer,
    ImmaginiAutoNuoveSerializer,
    ImmaginiAutoUsateSerializer,
    ModelliAutoSerializer,
    OptionalSerializer,
    PreventiviAutoUsateSerializer,
    PreventivoSerializer,
    UtenteSerializer,
)
from Backend_IngSoft.models import (
    Acquisto,
    AutoUsata,
    Concessionario,
    Detrazione,
    ImmaginiAutoNuove,
    ImmaginiAutoUsate,
    ModelloAuto,
    Optional,
    Preventivo,
    PreventivoUsato,
    Utente,
)
from Backend_IngSoft.util.error import raises
from Backend_IngSoft.util.util import send_html_email


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
        return Response(serializer.errors, status=status.HTTP_409_CONFLICT)


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

        preventivi = Preventivo.objects.filter(utente_id=utente.id, valid=True)

        serializer = PreventivoSerializer(preventivi, many=True)
        return Response(serializer.data)

    def post(self, request, id_utente):
        auto_usata_id = request.data.pop("detrazione", None)
        with transaction.atomic():
            serializer = ConfigurazioneSerializer(data=request.data)
            if serializer.is_valid():
                conf = serializer.save()

                if auto_usata_id is not None:
                    detrazione_data = Detrazione.objects.create(
                        preventivo_id=conf.preventivo.id, auto_usata_id=auto_usata_id
                    )
                    detrazione_data.save()

                # invio email
                subject = "Preventivo creato"
                to_email = conf.preventivo.utente.email
                context = {
                    "customer_name": conf.preventivo.utente.nome
                    + " "
                    + conf.preventivo.utente.cognome,
                    "car_model": conf.preventivo.modello.modello,
                    "base_price": conf.preventivo.modello.prezzo_base,
                    "optionals": conf.optional.all(),
                    "total_price": conf.preventivo.prezzo,
                    "detrazione": True if auto_usata_id is not None else False,
                }
                # send_mail(subject, message, from_email, to_email)
                template_name = "emails/template_email_conferma_preventivo.html"
                send_html_email(subject, to_email, context, template_name)
                print("email inviata")

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
        return Response(serializer.data, status=status.HTTP_200_OK)

    def post(self, request):
        utente = request.data.pop("utente")
        auto_usata = AutoUsataSerializer(data=request.data.pop("auto"))
        if auto_usata.is_valid():
            with transaction.atomic():
                auto_usata.save()

                preventivo_usato = PreventivoUsato.objects.create(
                    utente_id=utente,
                    auto_id=auto_usata.data["id"],
                )
                preventivo_usato.save()

            return Response(auto_usata.data, status=status.HTTP_201_CREATED)
        return Response(auto_usata.errors, status=status.HTTP_400_BAD_REQUEST)


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


class PreventivoAutoUsateAPIView(APIView):
    def get(self, request, id_utente):
        preventivi = PreventivoUsato.objects.filter(utente=id_utente)
        serializer = PreventiviAutoUsateSerializer(preventivi, many=True)
        return Response(serializer.data)


class ImmaginiAutoUsateListAPIView(APIView):
    def get(self, request, id_auto):
        immagini = ImmaginiAutoUsate.objects.filter(auto=id_auto)
        serializer = ImmaginiAutoUsateSerializer(immagini, many=True)
        return Response(serializer.data)

    def post(self, request, id_auto):
        serializer = ImmaginiAutoUsateSerializer(data=request.data)
        if serializer.is_valid():
            instance = serializer.save()
            return Response(
                ImmaginiAutoUsateSerializer(instance).data,
                status=status.HTTP_201_CREATED,
            )
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
