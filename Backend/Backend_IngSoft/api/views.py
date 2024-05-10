from rest_framework import status
from rest_framework.generics import get_object_or_404
from rest_framework.response import Response
from rest_framework.views import APIView

from Backend_IngSoft.api.serializers import UtenteSerializer
from Backend_IngSoft.models import Utente


class UtenteListCreateAPIView(APIView):
    def get(self, request):
        utente = Utente.objects.all()
        serializer = UtenteSerializer(utente, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = UtenteSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class UtenteDetailAPIView(APIView):
    def get_object(self, pk):
        utente = get_object_or_404(Utente, pk=pk)
        return utente

    def get(self, request, pk):
        utente = self.get_object(pk)
        serializer = UtenteSerializer(utente)
        return Response(serializer.data)

    def put(self, request, pk):
        utente = self.get_object(pk)
        serializer = UtenteSerializer(utente, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk):
        utente = self.get_object(pk)
        utente.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

