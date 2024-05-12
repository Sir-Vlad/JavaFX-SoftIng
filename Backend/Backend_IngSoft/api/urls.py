from django.urls import path

from Backend_IngSoft.api.views import UtenteDetailAPIView, UtenteListCreateAPIView

urlpatterns = [
    path("utenti/", UtenteListCreateAPIView.as_view(), name="utente-list"),
    path("utente/<str:email>/", UtenteDetailAPIView.as_view(), name="utente-detail"),
]
