from Backend_IngSoft.api.views import (
    ImmaginiAutoNuoveListAPIView,
    ModelliAutoListAPIView,
    OptionalAutoListAPIView,
    OptionalsListAPIView,
    PreventiviListAPIView,
    PreventiviUtenteListAPIView,
    SedeListAPIView,
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
    path("sedi/", SedeListAPIView.as_view(), name="sede-list"),
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
        "utente/<int:id_utente>/acquisti/",
        PreventiviUtenteListAPIView.as_view(),
        name="Acquisti Utente",
    ),
    path(
        "immaginiAutoNuove/<int:id_auto>/",
        ImmaginiAutoNuoveListAPIView.as_view(),
        name="Immagini Auto Nuove",
    ),
]
