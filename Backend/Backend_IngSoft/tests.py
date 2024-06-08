# Create your tests here.
from Backend_IngSoft.models import ModelloAuto
from django.core.exceptions import ValidationError
from django.db import transaction

auto = ModelloAuto(
    modello="prova",
    marca="NISSAN",
    altezza=1,
    lunghezza=1,
    larghezza=1,
    peso=1,
    volume_bagagliaio=1,
    prezzo_base=1,
)

try:
    with transaction.atomic():
        auto.save()
except ValidationError as e:
    print(e)
