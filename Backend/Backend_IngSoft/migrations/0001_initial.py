# Generated by Django 5.0.6 on 2024-05-15 10:43

import django.core.validators
import django.db.models.deletion
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = []

    operations = [
        migrations.CreateModel(
            name="ModelloAuto",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("nome", models.CharField(max_length=20, unique=True)),
                (
                    "marca",
                    models.CharField(
                        choices=[
                            ("Nissan", "Nissan"),
                            ("Mazda", "Mazda"),
                            ("Volkswagen", "Volkswagen"),
                            ("Ford", "Ford"),
                            ("Honda", "Honda"),
                            ("Audi", "Audi"),
                            ("BMW", "Bmw"),
                        ],
                        max_length=20,
                    ),
                ),
                ("prezzo_base", models.IntegerField()),
                ("altezza", models.IntegerField()),
                ("lunghezza", models.IntegerField()),
                ("larghezza", models.IntegerField()),
                ("peso", models.IntegerField()),
                ("volume_bagagliaio", models.IntegerField()),
            ],
        ),
        migrations.CreateModel(
            name="Optional",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                (
                    "nome",
                    models.CharField(
                        choices=[
                            ("colore", "Colore"),
                            ("cambio", "Cambio"),
                            ("motorizzazione", "Motorizzazione"),
                            ("dimensione cerchi", "Dim Cerchi"),
                            ("stereo", "Stereo"),
                            ("fanali", "Fanali"),
                            ("finestrini", "Finestrini"),
                            ("scarico", "Scarico"),
                            ("sospensioni", "Sospensioni"),
                            ("sedili", "Sedili"),
                            ("infotainment", "Infotainment"),
                        ],
                        max_length=20,
                    ),
                ),
                ("descrizione", models.CharField(max_length=30)),
                ("prezzo", models.IntegerField()),
            ],
        ),
        migrations.CreateModel(
            name="Preventivo",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("data_emissione", models.DateField()),
                ("prezzo", models.IntegerField()),
            ],
        ),
        migrations.CreateModel(
            name="Sede",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("nome", models.CharField(max_length=20, unique=True)),
                ("via", models.CharField(max_length=20)),
                ("civico", models.CharField(max_length=5)),
                ("citta", models.CharField(max_length=20)),
                ("cap", models.CharField(max_length=20)),
            ],
        ),
        migrations.CreateModel(
            name="Utente",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("email", models.CharField(max_length=100, unique=True)),
                ("password", models.CharField(max_length=20)),
                ("nome", models.CharField(max_length=20)),
                ("cognome", models.CharField(max_length=20)),
                ("numero_carta", models.CharField(max_length=20)),
                ("data_scadenza", models.DateField()),
                ("cvc", models.CharField(max_length=3)),
            ],
        ),
        migrations.CreateModel(
            name="Periodo",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                (
                    "mese",
                    models.CharField(
                        choices=[
                            ("gennaio", "Gennaio"),
                            ("febbraio", "Febbraio"),
                            ("marzo", "Marzo"),
                            ("aprile", "Aprile"),
                            ("maggio", "Maggio"),
                            ("giugno", "Giugno"),
                            ("luglio", "Luglio"),
                            ("agosto", "Agosto"),
                            ("settembre", "Settembre"),
                            ("ottobre", "Ottobre"),
                            ("novembre", "Novembre"),
                            ("dicembre", "Dicembre"),
                        ],
                        max_length=9,
                    ),
                ),
                (
                    "anno",
                    models.IntegerField(
                        choices=[
                            (2000, 2000),
                            (2001, 2001),
                            (2002, 2002),
                            (2003, 2003),
                            (2004, 2004),
                            (2005, 2005),
                            (2006, 2006),
                            (2007, 2007),
                            (2008, 2008),
                            (2009, 2009),
                            (2010, 2010),
                            (2011, 2011),
                            (2012, 2012),
                            (2013, 2013),
                            (2014, 2014),
                            (2015, 2015),
                            (2016, 2016),
                            (2017, 2017),
                            (2018, 2018),
                            (2019, 2019),
                            (2020, 2020),
                            (2021, 2021),
                            (2022, 2022),
                            (2023, 2023),
                            (2024, 2024),
                        ],
                        default=2024,
                        validators=[django.core.validators.MaxValueValidator(9999)],
                    ),
                ),
            ],
            options={
                "unique_together": {("mese", "anno")},
            },
        ),
        migrations.CreateModel(
            name="Configurazione",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                (
                    "optional",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.optional",
                    ),
                ),
                (
                    "preventivo",
                    models.OneToOneField(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.preventivo",
                    ),
                ),
            ],
        ),
        migrations.CreateModel(
            name="Sconto",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                (
                    "percentuale_sconto",
                    models.IntegerField(
                        validators=[
                            django.core.validators.MinValueValidator(1),
                            django.core.validators.MaxValueValidator(100),
                        ]
                    ),
                ),
                (
                    "modello",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.modelloauto",
                    ),
                ),
                (
                    "periodo",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.periodo",
                    ),
                ),
            ],
        ),
        migrations.AddField(
            model_name="preventivo",
            name="sede",
            field=models.ForeignKey(
                on_delete=django.db.models.deletion.CASCADE, to="Backend_IngSoft.sede"
            ),
        ),
        migrations.AddField(
            model_name="preventivo",
            name="utente",
            field=models.ForeignKey(
                on_delete=django.db.models.deletion.CASCADE, to="Backend_IngSoft.utente"
            ),
        ),
        migrations.CreateModel(
            name="AutoUsata",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("modello", models.CharField(max_length=20)),
                ("marca", models.CharField(max_length=20)),
                ("prezzo", models.IntegerField(default=0)),
                ("km_percorsi", models.IntegerField()),
                ("anno_immatricolazione", models.DateField()),
                ("altezza", models.IntegerField()),
                ("lunghezza", models.IntegerField()),
                ("larghezza", models.IntegerField()),
                ("peso", models.IntegerField()),
                ("volume_bagagliaio", models.IntegerField()),
                (
                    "utente",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.utente",
                    ),
                ),
            ],
        ),
        migrations.CreateModel(
            name="Acquisto",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("numero_fattura", models.CharField(max_length=10, unique=True)),
                ("acconto", models.IntegerField()),
                ("data_ritiro", models.DateField()),
                (
                    "preventivo",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.preventivo",
                    ),
                ),
                (
                    "utente",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.utente",
                    ),
                ),
            ],
        ),
        migrations.CreateModel(
            name="ImmaginiAutoUsate",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("path", models.CharField(max_length=255)),
                (
                    "auto",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.autousata",
                    ),
                ),
            ],
            options={
                "unique_together": {("path", "auto")},
            },
        ),
        migrations.CreateModel(
            name="ImmaginiAutoNuove",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("path", models.CharField(max_length=255)),
                (
                    "auto",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.modelloauto",
                    ),
                ),
            ],
            options={
                "unique_together": {("path", "auto")},
            },
        ),
        migrations.CreateModel(
            name="Possiede",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                (
                    "modello",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.modelloauto",
                    ),
                ),
                (
                    "optional",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.optional",
                    ),
                ),
            ],
            options={
                "unique_together": {("modello", "optional")},
            },
        ),
        migrations.CreateModel(
            name="Detrazione",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                (
                    "auto_usata",
                    models.OneToOneField(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.autousata",
                    ),
                ),
                (
                    "preventivo",
                    models.OneToOneField(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.preventivo",
                    ),
                ),
            ],
            options={
                "unique_together": {("preventivo", "auto_usata")},
            },
        ),
        migrations.CreateModel(
            name="Ritiro",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                (
                    "preventivo",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.preventivo",
                    ),
                ),
                (
                    "sede",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="Backend_IngSoft.sede",
                    ),
                ),
            ],
            options={
                "unique_together": {("preventivo", "sede")},
            },
        ),
    ]