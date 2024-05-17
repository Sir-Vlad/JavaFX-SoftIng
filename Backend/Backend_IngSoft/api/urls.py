from django.urls import path

from Backend_IngSoft.api.views import *

urlpatterns = [path("utenti/", UtenteListCreateAPIView.as_view(), name="utente-list"),
               path("utente/<str:email>/", UtenteDetailAPIView.as_view(),
                    name="utente-detail"),
               path("modelli/", ModelliAutoListAPIView.as_view(), name="modelli-list"),
               path("modello/<int:id_auto>/optional/", OptionalAutoAPIView.as_view(),
                    name="optional-list"),
               path("sede/", SedeListAPIView.as_view(), name="sede-list"),
               path("utente/<int:id_utente>/preventivi",
                    PreventiviUtenteListAPIView.as_view(), name="Preventivi Utente"),
               path("utente/<int:id_utente>/acquisti",
                    PreventiviUtenteListAPIView.as_view(), name="Acquisti Utente")]
