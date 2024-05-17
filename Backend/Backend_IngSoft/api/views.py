from django.http import HttpResponseNotFound
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView

from Backend_IngSoft.api.serializers import *
from Backend_IngSoft.models import ModelloAuto, Utente
from Backend_IngSoft.util.error import raises


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
        error = {
            'message': [i for e in serializer.errors.values() for i in e]
        }
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


class ModelliAutoListAPIView(APIView):

    def get(self, request):
        modelli = ModelloAuto.objects.all()
        serializer = ModelliAutoSerializer(modelli, many=True)
        return Response(serializer.data)


class OptionalAutoAPIView(APIView):
    def get(self, request, id_auto):
        try:
            auto = ModelloAuto.objects.get(id=id_auto)
        except ModelloAuto.DoesNotExist:
            return HttpResponseNotFound("Auto non esiste")

        optional = Possiede.objects.filter(modello_id=auto.id)

        list_optional = []
        for opt in optional:
            list_optional.append(Optional.objects.filter(id=opt.optional_id))

        # unpacking query
        list_optional = [i for q in list_optional for i in q]

        serializer = OptionalSerializer(list_optional, many=True)
        return Response(serializer.data)


class SedeListAPIView(APIView):
    def get(self, request):
        sede = Sede.objects.all()
        serializer = SedeSerializer(sede, many=True)
        return Response(serializer.data)


class PreventiviUtenteListAPIView(APIView):
    def get(self, request, id_utente):
        try:
            utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        preventivi = Preventivo.objects.filter(utente_id=utente.id)

        serializer = PreventivoSerializer(preventivi, many=True)
        return Response(serializer.data)

    def post(self, request, id_utente):
        try:
            utente = Utente.objects.get(id=id_utente)
        except Utente.DoesNotExist:
            return HttpResponseNotFound("Utente non esiste")

        serializer = PreventivoSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        error = {
            'message': [i for e in serializer.errors.values() for i in e]
        }
        return Response(error, status=status.HTTP_400_BAD_REQUEST)


class AcquistoUtenteAPIView(APIView):
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
        error = {
            'message': [i for e in serializer.errors.values() for i in e]
        }
        return Response(error, status=status.HTTP_400_BAD_REQUEST)


class AutoUsateListAPIView(APIView):
    def get(self, request):
        auto = AutoUsata.objects.all()
        serializer = AutoUsataSerializer(auto, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = AutoUsataSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        error = {
            'message': [i for e in serializer.errors.values() for i in e]
        }
        return Response(error, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, id_auto):
        try:
            auto = AutoUsata.objects.get(id=id_auto)
        except AutoUsata.DoesNotExist:
            return HttpResponseNotFound("Auto non esiste")
        auto.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
