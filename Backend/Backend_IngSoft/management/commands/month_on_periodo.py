from datetime import datetime

from django.core.management import BaseCommand

from Backend_IngSoft.models import Periodo


class Command(BaseCommand):
    help = "Aggiunge i campi al model Periodo"

    def handle(self, *args, **kwargs):
        current_date = datetime.now()
        if not Periodo.objects.exists():
            for year in range(current_date.year, current_date.year + 5):
                for month in range(1, 13):
                    periodo = Periodo(mese=month, anno=year)
                    periodo.save()
                    self.stdout.write(
                        self.style.SUCCESS(f"Aggiunto periodo {year}-{month}")
                    )
        periodo = Periodo(anno=current_date.year, mese=current_date.month)
        periodo.save()
        self.stdout.write(
            self.style.SUCCESS(
                f"Aggiunto periodo {current_date.year}-{current_date.month}"
            )
        )
        self.stdout.write(self.style.SUCCESS("Aggiunta completata"))
