import random
from Backend_IngSoft.models import ModelloAuto, Periodo, Sconto
from django.core.management.base import BaseCommand


class Command(BaseCommand):
    help = "Genera sconti randomici per alcuni modelli in un dato periodo"

    def add_arguments(self, parser):
        parser.add_argument("mese", type=str, help="Mese del periodo")
        parser.add_argument("anno", type=int, help="Anno del periodo")
        parser.add_argument(
            "num_modelli",
            type=int,
            help="Numero di modelli su cui applicare lo sconto",
        )

    def handle(self, *args, **kwargs):
        mese = kwargs["mese"]
        anno = kwargs["anno"]
        num_modelli = kwargs["num_modelli"]

        # Recupera il periodo specificato
        periodo = Periodo.objects.get(mese=mese, anno=anno)

        # Recupera tutti i modelli
        modelli = list(ModelloAuto.objects.all())

        if num_modelli > len(modelli):
            self.stdout.write(
                self.style.ERROR(
                    "Il numero di modelli specificato è maggiore del numero totale di "
                    "modelli disponibili"
                )
            )
            return

        # Seleziona randomicamente un sottoinsieme dei modelli
        modelli_scelti = random.sample(modelli, num_modelli)

        for modello in modelli_scelti:
            percentuale_sconto = int(random.uniform(15, 45))
            sconto = Sconto(
                modello=modello, periodo=periodo, percentuale_sconto=percentuale_sconto
            )
            sconto.save()

            self.stdout.write(
                self.style.SUCCESS(
                    f"Creato sconto per {modello}: {percentuale_sconto}%"
                )
            )
