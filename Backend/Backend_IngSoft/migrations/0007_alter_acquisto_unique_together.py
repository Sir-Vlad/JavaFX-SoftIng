# Generated by Django 5.0.6 on 2024-06-18 15:30

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ("Backend_IngSoft", "0006_alter_autousata_marca"),
    ]

    operations = [
        migrations.AlterUniqueTogether(
            name="acquisto",
            unique_together={("numero_fattura", "preventivo")},
        ),
    ]
