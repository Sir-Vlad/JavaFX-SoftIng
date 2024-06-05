from django.core.management.base import BaseCommand
from Backend_IngSoft.models import ImmaginiAutoNuove
from PIL import Image
import imagehash


class Command(BaseCommand):
    help = "Aggiorna il campo phash per tutti gli oggetti esistenti"

    def handle(self, *args, **kwargs):
        objects = ImmaginiAutoNuove.objects.all()
        for obj in objects:
            if obj.image and not obj.phash:
                image = Image.open(obj.image)
                phash = imagehash.phash(image)
                obj.phash = str(phash)
                obj.save()
                self.stdout.write(
                    self.style.SUCCESS(f"Aggiornato phash per l'oggetto ID {obj.id}")
                )
        self.stdout.write(self.style.SUCCESS("Aggiornamento completato"))
