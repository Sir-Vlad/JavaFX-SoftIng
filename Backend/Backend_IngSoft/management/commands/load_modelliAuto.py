import json

from Backend_IngSoft.models import ModelloAuto, Optional
from django.core.management import BaseCommand


class Command(BaseCommand):
    help = "Carica i modelli auto nel db"

    def add_arguments(self, parser):
        parser.add_argument("json_file", type=str, help="Path to json file")

    def handle(self, *args, **kwargs):
        json_file_path = kwargs.pop("json_file")
        required_optional = set(
            [q.nome for q in Optional.objects.filter(obbligatorio=True)]
        )
        optionals_ids = []
        for opt in required_optional:
            query = Optional.objects.filter(nome=opt)
            optionals_ids.extend(set([q.id for q in query]))

        print(optionals_ids)

        with open(json_file_path) as json_file:
            data = json.load(json_file)

        for item in data:
            if item["model"] == "Backend_IngSoft.modelloauto":
                fields = item["fields"]
                optionals = fields.pop("optionals", [])

                if not any(optional in optionals_ids for optional in optionals):
                    self.stdout.write(
                        self.style.ERROR(
                            f"Nessuno degli optional richiesti è presente per {fields.get('modello')}"
                        )
                    )
                    continue

                modello_auto = ModelloAuto.objects.create(**fields)

                if optionals:
                    options_instance = Optional.objects.filter(pk__in=optionals)
                    modello_auto.optionals.set(options_instance)

                self.stdout.write(
                    self.style.SUCCESS(f"ModelloAuto {modello_auto.modello}")
                )

        self.stdout.write(self.style.SUCCESS("Caricamento completato"))
