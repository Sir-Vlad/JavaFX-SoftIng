from Backend_IngSoft.api.views import (
    AcquistoUtenteListAPIView,
    AutoUsateListAPIView,
    ConcessionarioListAPIView,
    ConfermaPreventivoUtenteAPIView,
    DetrazioneListAPIView,
    ImmaginiAutoNuoveListAPIView,
    ImmaginiAutoUsateListAPIView,
    ModelliAutoListAPIView,
    OptionalAutoListAPIView,
    OptionalsListAPIView,
    PreventiviListAPIView,
    PreventiviUtenteListAPIView,
    PreventivoAutoUsateAPIView,
    UtenteDetailAPIView,
    UtenteListCreateAPIView,
)
from django.urls import path

urlpatterns = [
    # endpoint per la lista dei dati
    path("utenti/", UtenteListCreateAPIView.as_view(), name="utente-list"),
    path("modelli/", ModelliAutoListAPIView.as_view(), name="modelli-list"),
    path("preventivi/", PreventiviListAPIView.as_view(), name="preventivi-list"),
    path("optionals/", OptionalsListAPIView.as_view(), name="optionals-list"),
    path(
        "concessionari/", ConcessionarioListAPIView.as_view(), name="concessionari-list"
    ),
    path("autoUsate/", AutoUsateListAPIView.as_view(), name="auto-usate-list"),
    # endpoint per dati specifici
    path("utente/<str:email>/", UtenteDetailAPIView.as_view(), name="utente-detail"),
    path(
        "modello/<int:id_auto>/optional/",
        OptionalAutoListAPIView.as_view(),
        name="optional-list",
    ),
    path(
        "utente/<int:id_utente>/preventivi/",
        PreventiviUtenteListAPIView.as_view(),
        name="Preventivi Utente",
    ),
    path(
        "utente/<int:id_utente>/preventiviUsato/",
        PreventivoAutoUsateAPIView.as_view(),
        name="Preventivi Auto Usate Utente",
    ),
    path(
        "utente/<int:id_utente>/preventivo/<int:id_preventivo>/conferma/",
        ConfermaPreventivoUtenteAPIView.as_view(),
        name="Acquisti Utente",
    ),
    path(
        "utente/<int:id_utente>/ordini/",
        AcquistoUtenteListAPIView.as_view(),
        name="Acquisti Utente",
    ),
    path(
        "utente/<int:id_utente>/detrazioni/",
        DetrazioneListAPIView.as_view(),
        name="Acquisti Utente",
    ),
    path(
        "immaginiAutoNuove/<int:id_auto>/",
        ImmaginiAutoNuoveListAPIView.as_view(),
        name="Immagini Auto Nuove",
    ),
    path(
        "immaginiAutoUsate/<int:id_auto>/",
        ImmaginiAutoUsateListAPIView.as_view(),
        name="Immagini Auto Usate",
    ),
    path(
        "autoUsata/<int:id_auto>/",
        AutoUsateListAPIView.as_view(),
        name="auto-usate-list",
    ),
]
