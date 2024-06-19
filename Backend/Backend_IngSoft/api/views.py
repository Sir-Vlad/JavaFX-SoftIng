from datetime import datetime, timedelta

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
    Configurazione,
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
from Backend_IngSoft.util.util import create_pdf_file, send_html_email


class UtenteListCreateAPIView(APIView):
    def get(self, request) -> Response:
        utente = Utente.objects.all()
        serializer = UtenteSerializer(utente, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = UtenteSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(status=status.HTTP_201_CREATED)
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
            return Response(status=status.HTTP_204_NO_CONTENT)
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
        data_emissione = request.data.get("preventivo", {}).get("data_emissione")

        # controllo se il campi detrazione e data_emissione sono esclusivi
        if (not data_emissione and not auto_usata_id) or (
            data_emissione and auto_usata_id
        ):
            return Response(status=status.HTTP_400_BAD_REQUEST)

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

                return Response(status=status.HTTP_201_CREATED)

        field_names = ("utente", "modello", "concessionario", "non_field_errors")

        if "preventivo" in serializer.errors.keys():
            field_data = serializer.errors.get("preventivo")
            if self._is_field_error(field_data, field_names):
                return Response(serializer.errors, status=status.HTTP_409_CONFLICT)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    @staticmethod
    def _is_field_error(field_data, field_names):
        return field_data and any(field in field_data for field in field_names)


class AcquistoUtenteListAPIView(APIView):
    def get(self, request, id_utente):
        try:
            utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        acquisti = Acquisto.objects.filter(utente_id=utente.id)

        serializer = AcquistoSerializer(acquisti, many=True)
        return Response(serializer.data)


class AutoUsateListAPIView(APIView):
    def get(self, request):
        auto = AutoUsata.objects.all()
        serializer = AutoUsataSerializer(auto, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)

    def post(self, request):
        utente = request.data.pop("utente")
        immagini = request.data.pop("immagini", [])
        auto_usata = AutoUsataSerializer(data=request.data.pop("auto"))
        if auto_usata.is_valid():
            with transaction.atomic():
                auto_usata.save()

                preventivo_usato = PreventivoUsato.objects.create(
                    utente_id=utente,
                    auto_id=auto_usata.data["id"],
                )
                preventivo_usato.save()

            return Response(status=status.HTTP_201_CREATED)
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
        serializer = ImmaginiAutoUsateSerializer(data=request.data, many=True)
        if serializer.is_valid():
            instance = serializer.save()
            return Response(
                status=status.HTTP_201_CREATED,
            )
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class ConfermaPreventivoUtenteAPIView(APIView):
    def post(self, request, id_utente, id_preventivo):
        preventivo: Preventivo = Preventivo.objects.get(id=id_preventivo)
        days_diff = (preventivo.data_emissione - datetime.now().date()).days
        if days_diff > 20:
            return Response("Preventivo scaduto", status=status.HTTP_400_BAD_REQUEST)

        utente: Utente = Utente.objects.get(id=id_utente)
        data_ritiro = self._get_data_ritiro(id_preventivo)
        numero_fattura = self._genera_id_fattura()
        acconto = request.data["acconto"]

        try:
            with transaction.atomic():
                ordine = Acquisto.objects.create(
                    acconto=acconto,
                    numero_fattura=numero_fattura,
                    data_ritiro=data_ritiro,
                    utente_id=utente.id,
                    preventivo_id=preventivo.id,
                )

                Preventivo.objects.filter(id=id_preventivo).update(valid=False)

                path_pdf = create_pdf_file(ordine)

                send_html_email(
                    "Fattura",
                    utente.email,
                    {
                        "nome_utente": utente.nome + " " + utente.cognome,
                        "nome_concessionario": preventivo.concessionario.nome,
                        "marca": preventivo.modello.marca,
                        "modello": preventivo.modello.modello,
                        "numero_fattura": numero_fattura,
                        "data_emissione": datetime.now().strftime("%d/%m/%Y"),
                        "prezzo_totale": preventivo.prezzo,
                    },
                    "emails/fattura.html",
                    path_pdf,
                )

                return Response(status=status.HTTP_201_CREATED)
        except Exception as e:
            return Response(status=status.HTTP_400_BAD_REQUEST)

    @staticmethod
    def _genera_id_fattura():
        year = datetime.now().year  # anno corrente
        last_acquisto: Acquisto = Acquisto.objects.last()
        if last_acquisto is None:
            return f"0001/{year}"

        last_fattura = last_acquisto.numero_fattura
        last_id_hex, last_year = last_fattura.split("/")

        last_id = int(last_id_hex, 16)
        last_year = int(last_year)

        # se l'ultima fattura è nell'anno precedente
        if last_year != year:
            return f"0001/{year}"

        # se l'ultima fattura è nell'anno corrente
        new_id = last_id + 1
        new_id_hex = f"{new_id:04X}"
        return f"{new_id_hex}/{last_year}"

    @staticmethod
    def _get_data_ritiro(preventivo_id):
        now = datetime.now().date()
        configurazione: Configurazione = Configurazione.objects.filter(
            preventivo_id=preventivo_id
        ).first()
        optionals = configurazione.optional.filter(obbligatorio=False).count()
        # optionals = configurazione.optional.count()
        days_optionals = optionals * 10
        data_consegna = now + timedelta(days=days_optionals) + timedelta(days=30)
        return data_consegna
