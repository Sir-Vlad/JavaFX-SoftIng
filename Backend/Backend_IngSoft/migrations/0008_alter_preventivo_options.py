# Generated by Django 5.0.6 on 2024-06-19 09:34

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ("Backend_IngSoft", "0007_alter_acquisto_unique_together"),
    ]

    operations = [
        migrations.AlterModelOptions(
            name="preventivo",
            options={"verbose_name_plural": "Preventivi"},
        ),
    ]
