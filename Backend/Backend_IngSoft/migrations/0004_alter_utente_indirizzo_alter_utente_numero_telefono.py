from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ("Backend_IngSoft", "0003_alter_periodo_anno"),
    ]

    operations = [
        migrations.AlterField(
            model_name="utente",
            name="indirizzo",
            field=models.CharField(blank=True, max_length=100, null=True),
        ),
        migrations.AlterField(
            model_name="utente",
            name="numero_telefono",
            field=models.CharField(blank=True, max_length=10, null=True),
        ),
    ]
