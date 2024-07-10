from datetime import datetime, timedelta

from Backend_IngSoft.api.serializers import (
    AcquistoSerializer,
    AutoUsataSerializer,
    ConcessionarioSerializer,
    ConfigurazioneSerializer,
    DetrazioneSerializer,
    ImmaginiAutoNuoveSerializer,
    ImmaginiAutoUsateSerializer,
    ModelliAutoSerializer,
    OptionalSerializer,
    PreventiviAutoUsateSerializer,
    PreventivoSerializer,
    ScontiSerializer,
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
    MarcaAuto,
    ModelloAuto,
    Optional,
    Preventivo,
    PreventivoUsato,
    Sconto,
    Utente,
)
from Backend_IngSoft.util.util import create_pdf_file, send_html_email
from django.db import transaction
from django.http import HttpResponseNotFound
from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView


class UtenteListAPIView(APIView):
    @swagger_auto_schema(
        responses={200: UtenteSerializer},
        operation_description="Ritorna tutti gli utenti",
    )
    def get(self, request) -> Response:
        utente = Utente.objects.all()
        serializer = UtenteSerializer(utente, many=True)
        return Response(serializer.data)

    @swagger_auto_schema(
        responses={201: UtenteSerializer, 409: "Utente esiste"},
        operation_description="Aggiunge un nuovo utente",
    )
    def post(self, request):
        serializer = UtenteSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_409_CONFLICT)


class UtenteDetailAPIView(APIView):
    @staticmethod
    def get_object(email):
        return Utente.objects.get(email=email)

    @swagger_auto_schema(
        operation_description="Ritorna un utente",
        manual_parameters=[
            openapi.Parameter(
                "email",
                openapi.IN_QUERY,
                description="email dell'utente da cercare",
                type=openapi.TYPE_STRING,
            ),
        ],
        responses={200: UtenteSerializer, 404: "Utente non esiste"},
    )
    def get(self, request, email):
        try:
            utente = self.get_object(email=email)
            serializer = UtenteSerializer(utente)
            return Response(serializer.data)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

    @swagger_auto_schema(
        operation_description="Modifica un utente",
        manual_parameters=[
            openapi.Parameter(
                "email",
                openapi.IN_QUERY,
                description="email dell'utente da cercare",
                type=openapi.TYPE_STRING,
            ),
        ],
        responses={
            204: "Utente modificato",
            400: "Parametri non validi",
            404: "Utente non esiste",
        },
    )
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

    @swagger_auto_schema(
        operation_description="Elimina un utente",
        manual_parameters=[
            openapi.Parameter(
                "email",
                openapi.IN_QUERY,
                description="email dell'utente da cercare",
                type=openapi.TYPE_STRING,
            ),
        ],
        responses={
            204: "Utente eliminato",
            404: "Utente non esiste",
        },
    )
    def delete(self, request, email):
        try:
            utente = self.get_object(email)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        utente.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class ModelliAutoListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutti i modelli di auto",
        responses={200: ModelliAutoSerializer},
    )
    def get(self, request):
        modelli = ModelloAuto.objects.all()
        serializer = ModelliAutoSerializer(modelli, many=True)
        return Response(serializer.data)


class OptionalAutoListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutti i modelli di auto",
        manual_parameters=[
            openapi.Parameter(
                "id_auto",
                openapi.IN_QUERY,
                description="id dell'auto da cercare",
                type=openapi.TYPE_STRING,
            ),
        ],
        responses={200: OptionalSerializer, 404: "Auto non esiste"},
    )
    def get(self, request, id_auto):
        try:
            auto = ModelloAuto.objects.get(id=id_auto)
        except ModelloAuto.DoesNotExist:
            return HttpResponseNotFound("Auto non esiste")

        serializer = OptionalSerializer(auto.optionals, many=True)
        return Response(serializer.data)


class OptionalsListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutti i modelli di auto",
        responses={200: OptionalSerializer},
    )
    def get(self, request):
        optional = Optional.objects.all()
        serializer = OptionalSerializer(optional, many=True)
        return Response(serializer.data)


class ConcessionarioListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutti i concessionari",
        responses={200: ConcessionarioSerializer},
    )
    def get(self, request):
        concessionario = Concessionario.objects.all()
        serializer = ConcessionarioSerializer(concessionario, many=True)
        return Response(serializer.data)


class PreventiviUtenteListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutti i preventivi di un utente",
        manual_parameters=[
            openapi.Parameter(
                "id_utente",
                openapi.IN_QUERY,
                description="id dell'utente da cercare",
                type=openapi.TYPE_STRING,
            ),
        ],
        responses={200: PreventivoSerializer, 404: "Utente non esiste"},
    )
    def get(self, request, id_utente):
        try:
            utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        preventivi = Preventivo.objects.filter(utente_id=utente.id)

        serializer = PreventivoSerializer(preventivi, many=True)
        return Response(serializer.data)

    @swagger_auto_schema(
        operation_description="Aggiunge un preventivo per un utente e gli invia la "
        "notifica tramite email",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            description="dati per salvare il preventivo. I campi data_emissione "
            "e detrazione sono esclusivi",
            properties={
                "preventivo": openapi.Schema(
                    type=openapi.TYPE_OBJECT,
                    properties={
                        "utente": openapi.Schema(type=openapi.TYPE_NUMBER),
                        "concessionario": openapi.Schema(type=openapi.TYPE_NUMBER),
                        "modello": openapi.Schema(type=openapi.TYPE_NUMBER),
                        "prezzo": openapi.Schema(type=openapi.TYPE_NUMBER),
                        "data_emissione": openapi.Schema(type=openapi.TYPE_STRING),
                    },
                    required=[
                        "utente",
                        "concessionario",
                        "modello",
                        "prezzo",
                    ],
                ),
                "optional": openapi.Schema(
                    type=openapi.TYPE_ARRAY,
                    items=openapi.Schema(type=openapi.TYPE_NUMBER),
                ),
                "detrazione": openapi.Schema(type=openapi.TYPE_NUMBER),
            },
            required=["preventivo", "optional"],
        ),
        manual_parameters=[
            openapi.Parameter(
                "id_utente",
                openapi.IN_QUERY,
                description="id dell'utente da cercare",
                type=openapi.TYPE_STRING,
            ),
        ],
        responses={
            201: "Configurazione creata",
            400: "Parametri non validi",
            404: "Utente non esiste",
        },
    )
    def post(self, request, id_utente):
        try:
            _utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        auto_usata_id = request.data.pop("detrazione", None)
        data_emissione = request.data.get("preventivo", {}).get("data_emissione")

        # controllo se il campi detrazione e data_emissione sono esclusivi
        if (not data_emissione and not auto_usata_id) or (
            data_emissione and auto_usata_id
        ):
            return Response(status=status.HTTP_400_BAD_REQUEST)

        try:
            with transaction.atomic():
                serializer = ConfigurazioneSerializer(data=request.data)
                if serializer.is_valid():
                    conf = serializer.save()

                    if auto_usata_id is not None:
                        detrazione_data = Detrazione.objects.create(
                            preventivo_id=conf.preventivo.id,
                            auto_usata_id=auto_usata_id,
                        )
                        detrazione_data.save()

                    sconto: Sconto = Sconto.objects.filter(
                        modello_id=conf.preventivo.modello.id
                    ).first()

                    if sconto is None:
                        prezzo_tot = conf.preventivo.prezzo
                    else:
                        prezzo_tot = (
                            conf.preventivo.prezzo * sconto.percentuale_sconto
                        ) / 100

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
                        "total_price": prezzo_tot,
                        "detrazione": True if auto_usata_id is not None else False,
                    }
                    # send_mail(subject, message, from_email, to_email)
                    template_name = "emails/template_email_conferma_preventivo.html"
                    send_html_email(subject, to_email, context, template_name)
                    print("email inviata")

                    return Response(status=status.HTTP_201_CREATED)
        except Exception as e:
            return Response(str(e), status=status.HTTP_400_BAD_REQUEST)

        # fixme: controllare che fa

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
    @swagger_auto_schema(
        operation_description="Ritorna tutti gli acquisti di un utente",
        manual_parameters=[
            openapi.Parameter(
                "id_utente",
                openapi.IN_QUERY,
                description="id dell'utente da cercare",
                type=openapi.TYPE_STRING,
            ),
        ],
        responses={200: AcquistoSerializer, 404: "Utente non esiste"},
    )
    def get(self, request, id_utente):
        try:
            utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        acquisti = Acquisto.objects.filter(utente_id=utente.id)

        serializer = AcquistoSerializer(acquisti, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)


class AutoUsateListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutte le auto usate",
        responses={200: AutoUsataSerializer},
    )
    def get(self, request):
        auto = AutoUsata.objects.all()
        serializer = AutoUsataSerializer(auto, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)

    @swagger_auto_schema(
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                "utente": openapi.Schema(type=openapi.TYPE_INTEGER),
                "auto": openapi.Schema(
                    type=openapi.TYPE_OBJECT,
                    properties={
                        "modello": openapi.Schema(type=openapi.TYPE_STRING),
                        "marca": openapi.Schema(
                            type=openapi.TYPE_STRING,
                            enum=list(MarcaAuto.values),
                        ),
                        "lunghezza": openapi.Schema(type=openapi.TYPE_NUMBER),
                        "larghezza": openapi.Schema(type=openapi.TYPE_NUMBER),
                        "peso": openapi.Schema(type=openapi.TYPE_NUMBER),
                        "volume_bagagliaio": openapi.Schema(type=openapi.TYPE_NUMBER),
                        "km_percorsi": openapi.Schema(type=openapi.TYPE_NUMBER),
                        "anno_immatricolazione": openapi.Schema(
                            type=openapi.TYPE_STRING,
                            format=openapi.FORMAT_DATE,
                        ),
                        "targa": openapi.Schema(type=openapi.TYPE_NUMBER),
                    },
                    required=[
                        "modello",
                        "marca",
                        "altezza",
                        "lunghezza",
                        "larghezza",
                        "peso",
                        "volume_bagagliaio",
                        "km_percorsi",
                        "anno_immatricolazione",
                        "targa",
                    ],
                ),
            },
            required=["utente", "auto"],
        ),
        responses={201: AutoUsataSerializer, 400: "Parametri non validi"},
    )
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

            return Response(status=status.HTTP_201_CREATED)
        return Response(auto_usata.errors, status=status.HTTP_400_BAD_REQUEST)


class ImmaginiAutoNuoveListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutte le immagini dei modelli nuovi",
        responses={200: ImmaginiAutoNuoveSerializer},
    )
    def get(self, request, id_auto):
        immagini = ImmaginiAutoNuove.objects.filter(auto=id_auto)
        serializer = ImmaginiAutoNuoveSerializer(immagini, many=True)
        return Response(serializer.data)


class PreventiviListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutti i preventivi",
        responses={200: PreventivoSerializer},
    )
    def get(self, request):
        preventivi = Preventivo.objects.all()
        serializer = PreventivoSerializer(preventivi, many=True)
        return Response(serializer.data)


class PreventivoAutoUsateAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutti i preventivi di un utente",
        responses={200: PreventiviAutoUsateSerializer},
    )
    def get(self, request, id_utente):
        preventivi = PreventivoUsato.objects.filter(utente=id_utente)
        serializer = PreventiviAutoUsateSerializer(preventivi, many=True)
        return Response(serializer.data)


class ImmaginiAutoUsateListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutte le immagini di un auto usata",
        responses={200: ImmaginiAutoUsateSerializer, 404: "Auto non esiste"},
    )
    def get(self, request, id_auto):
        try:
            auto = AutoUsata.objects.get(id=id_auto)
        except AutoUsata.DoesNotExist:
            return HttpResponseNotFound("Auto non esiste")

        immagini = ImmaginiAutoUsate.objects.filter(auto=auto)
        serializer = ImmaginiAutoUsateSerializer(immagini, many=True)
        return Response(serializer.data)

    @swagger_auto_schema(
        operation_description="Aggiunge le immagini di un auto usata",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                "image_name": openapi.Schema(type=openapi.TYPE_STRING),
                "image_base64": openapi.Schema(type=openapi.TYPE_STRING),
                "auto": openapi.Schema(type=openapi.TYPE_INTEGER),
            },
            required=["image"],
        ),
        responses={
            201: "Immagini aggiunte correttamente",
            400: "Parametri non validi",
            404: "Auto non esiste",
        },
    )
    def post(self, request, id_auto):
        try:
            _auto = AutoUsata.objects.get(id=id_auto)
        except AutoUsata.DoesNotExist:
            return HttpResponseNotFound("Auto non esiste")

        serializer = ImmaginiAutoUsateSerializer(data=request.data, many=True)
        if serializer.is_valid():
            serializer.save()
            return Response(
                status=status.HTTP_201_CREATED,
            )
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class ConfermaPreventivoUtenteAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Conferma un preventivo di un utente, genera la fattura e "
        "la invia all'utente insieme alla conferma dell'avvenuta "
        "conferma tramite email",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                "acconto": openapi.Schema(
                    type=openapi.TYPE_NUMBER, description="Acconto"
                )
            },
            required=["acconto"],
        ),
        responses={
            201: "Ordine creato correttamente",
            400: "Parametri non validi",
            404: "Preventivo o utente non esiste",
            406: "Preventivo scaduto",
        },
    )
    def post(self, request, id_utente, id_preventivo):
        try:
            preventivo = Preventivo.objects.get(id=id_preventivo)
        except Preventivo.DoesNotExist:
            return HttpResponseNotFound("Preventivo non esiste")

        days_diff = (preventivo.data_emissione - datetime.now().date()).days
        if days_diff > 20:
            Preventivo.objects.filter(id=id_preventivo).update(stato="SC")
            return Response("Preventivo scaduto", status=status.HTTP_406_NOT_ACCEPTABLE)

        try:
            utente: Utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

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

                Preventivo.objects.filter(id=id_preventivo).update(stato="PG")

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
        except Exception as _e:
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
        days_optionals = optionals * 10
        data_consegna = now + timedelta(days=days_optionals) + timedelta(days=30)
        return data_consegna


class DetrazioneListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutte le detrazioni relative ad un utente",
        responses={200: DetrazioneSerializer, 404: "Preventivo non trovato"},
    )
    def get(self, request, id_utente):
        preventivi_usati: list[PreventivoUsato] = list(
            PreventivoUsato.objects.filter(utente_id=id_utente).values_list(
                "auto_id", flat=True
            )
        )

        if not preventivi_usati:
            return Response(
                "Non hai ancora acquistato auto", status=status.HTTP_404_NOT_FOUND
            )

        auto_usate = AutoUsata.objects.filter(preventivousato__in=preventivi_usati)
        detrazioni = Detrazione.objects.filter(auto_usata__in=auto_usate)
        serializer = DetrazioneSerializer(detrazioni, many=True)
        return Response(serializer.data)


class AutoUsateComprate(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutte le auto usate comprate da un utente",
        responses={
            200: AutoUsataSerializer,
            402: "Auto non ancora venduta",
            404: "Preventivo o utente non trovato",
        },
    )
    def get(self, request, id_utente, id_auto):
        try:
            _utente: Utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        try:
            auto_usata: AutoUsata = AutoUsata.objects.get(id=id_auto)
        except AutoUsata.DoesNotExist:
            return HttpResponseNotFound("Auto usata non esiste")

        if not auto_usata.venduta:
            return Response(
                "Auto non ancora venduta", status=status.HTTP_402_PAYMENT_REQUIRED
            )

        serializer = AutoUsataSerializer(auto_usata)
        return Response(serializer.data)

    @swagger_auto_schema(
        operation_description="Segna un'auto usata come venduta da un utente",
        responses={
            201: "Auto usata acquistata",
            402: "Auto già venduta",
            404: "Preventivo o utente non trovato",
        },
    )
    def post(self, request, id_utente, id_auto):
        try:
            _utente: Utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        try:
            auto_usata: AutoUsata = AutoUsata.objects.get(id=id_auto)
        except AutoUsata.DoesNotExist:
            return HttpResponseNotFound("Auto usata non esiste")

        if auto_usata.venduta:
            return Response("Auto già venduta", status=status.HTTP_402_PAYMENT_REQUIRED)

        AutoUsata.objects.filter(id=id_auto).update(venduta=True)
        return Response(status=status.HTTP_201_CREATED)


class ScontiListAPIView(APIView):
    @swagger_auto_schema(
        operation_description="Ritorna tutti i sconti relativi ad un modello",
        responses={
            200: ScontiSerializer,
        },
    )
    def get(self, request):
        sconti = Sconto.objects.all()
        serializer = ScontiSerializer(sconti, many=True)
        return Response(serializer.data)
