from django.urls import path

from Backend_IngSoft.api.views import UtenteDetailAPIView, UtenteListCreateAPIView

urlpatterns = [
    path("utente/", UtenteListCreateAPIView.as_view(), name="utente-list"),
    path("utente/<int:pk>/", UtenteDetailAPIView.as_view(), name="utente-detail"),
]
