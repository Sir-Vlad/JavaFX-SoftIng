from Backend_IngSoft.models import Periodo
from datetime import datetime
from django.core.management import BaseCommand


class Command(BaseCommand):
    help = "Aggiunge i campi al model Periodo"
    mesi = (
        "gennaio",
        "febbraio",
        "marzo",
        "aprile",
        "maggio",
        "giugno",
        "luglio",
        "agosto",
        "settembre",
        "ottobre",
        "novembre",
        "dicembre",
    )

    def handle(self, *args, **kwargs):
        current_date = datetime.now()
        if not Periodo.objects.exists():
            for year in range(current_date.year, current_date.year + 5):
                for month in self.mesi:
                    periodo = Periodo(mese=month, anno=year)
                    periodo.save()
                    self.stdout.write(
                        self.style.SUCCESS(f"Aggiunto periodo {year}-{month}")
                    )
        self.stdout.write(self.style.SUCCESS("Aggiunta completata"))
