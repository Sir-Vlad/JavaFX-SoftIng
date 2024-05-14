from Backend_IngSoft.api.serializers import UtenteSerializer
from Backend_IngSoft.models import Utente
from Backend_IngSoft.util.error import raises
from django.http import HttpResponseNotFound
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView


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
        error = {'message': [i for e in serializer.errors.values() for i in e]}
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

    def put(self, request, pk):
        try:
            utente = self.get_object(pk)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        serializer = UtenteSerializer(utente, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk):
        try:
            utente = self.get_object(pk)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        utente.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
