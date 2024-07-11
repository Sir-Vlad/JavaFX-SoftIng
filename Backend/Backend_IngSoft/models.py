import os.path
import re
from datetime import datetime

from django.core.exceptions import ValidationError
from django.core.validators import MaxValueValidator, MinValueValidator
from django.db import models
from django.db.models import CASCADE
from django.utils.safestring import mark_safe
from django.utils.translation import gettext_lazy as _


class Utente(models.Model):
    email = models.EmailField(max_length=100, unique=True, null=False, blank=False)
    password = models.CharField(max_length=20, null=False, blank=False)
    nome = models.CharField(max_length=20, null=False, blank=False)
    cognome = models.CharField(max_length=20, null=False, blank=False)
    indirizzo = models.CharField(max_length=100, null=True, blank=True)
    numero_telefono = models.CharField(max_length=10, null=True, blank=True)
    numero_carta = models.CharField(max_length=16, null=False, blank=False)
    data_scadenza = models.DateField(null=False, blank=False)
    cvc = models.CharField(max_length=3, null=False, blank=False)

    class Meta:
        verbose_name_plural = "Utenti"

    def __str__(self):
        return self.nome + " " + self.cognome


class MarcaAuto(models.TextChoices):
    NISSAN = "NISSAN"
    MAZDA = "MAZDA"
    VOLKSWAGEN = "VOLKSWAGEN"
    FORD = "FORD"
    HONDA = "HONDA"
    AUDI = "AUDI"
    BMW = "BMW"


class Auto(models.Model):
    modello = models.CharField(max_length=20, unique=True, null=False, blank=False)
    marca = models.CharField(
        max_length=20, null=False, blank=False, choices=MarcaAuto.choices
    )  # lista di valori noti
    # dati auto
    altezza = models.PositiveIntegerField(null=False, blank=False)
    lunghezza = models.PositiveIntegerField(null=False, blank=False)
    larghezza = models.PositiveIntegerField(null=False, blank=False)
    peso = models.PositiveIntegerField(null=False, blank=False)
    volume_bagagliaio = models.PositiveIntegerField(null=False, blank=False)

    class Meta:
        abstract = True


class Optional(models.Model):
    class Category(models.TextChoices):
        COLORE = "colore"
        CAMBIO = "cambio"
        MOTORIZZAZIONE = "motorizzazione"
        ALIMENTAZIONE = "alimentazione"
        DIM_CERCHI = "dimensione cerchi"
        STEREO = "stereo"
        FANALI = "fanali"
        FINESTRINI = "finestrini"
        SCARICO = "scarico"
        SOSPENSIONI = "sospensioni"
        SEDILI = "sedili"
        INFOTAINMENT = "infotainment"

    nome = models.CharField(max_length=20, blank=False, choices=Category)
    descrizione = models.CharField(max_length=30, blank=False)
    prezzo = models.PositiveIntegerField(blank=False)
    obbligatorio = models.BooleanField(default=False)

    class Meta:
        unique_together = ("nome", "descrizione")
        verbose_name_plural = "Optional"

    def __str__(self):
        return self.nome + " - " + self.descrizione


class ModelloAuto(Auto):
    prezzo_base = models.PositiveIntegerField(null=False, blank=False)
    optionals = models.ManyToManyField(
        Optional,
        blank=False,
    )

    class Meta:
        verbose_name_plural = "Modelli Auto"

    def __str__(self):
        return self.modello + " - " + self.marca


class Concessionario(models.Model):
    nome = models.CharField(max_length=20, unique=True, null=False, blank=False)
    via = models.CharField(max_length=20, null=False, blank=False)
    civico = models.CharField(max_length=5, null=False, blank=False)
    citta = models.CharField(max_length=20, null=False, blank=False)
    cap = models.CharField(max_length=20, null=False, blank=False)

    class Meta:
        verbose_name_plural = "Concessionari"

    def __str__(self):
        return self.nome

    @property
    def indirizzo(self):
        return {
            "via": self.via,
            "civico": self.civico,
            "citta": self.citta,
            "cap": self.cap,
        }


class StatoPreventivo(models.TextChoices):
    PAGATO = "PG", _("Pagato")
    VALIDO = "VA", _("Valida")
    SCADUTO = "SC", _("Scaduto")


class Preventivo(models.Model):
    utente = models.ForeignKey(Utente, on_delete=CASCADE, null=False, blank=False)
    modello = models.ForeignKey(ModelloAuto, on_delete=CASCADE, null=False, blank=False)
    data_emissione = models.DateField(null=True)
    concessionario = models.ForeignKey(
        Concessionario, on_delete=CASCADE, null=False, blank=False
    )
    prezzo = models.PositiveIntegerField(null=False, blank=False)
    stato = models.CharField(
        max_length=2, choices=StatoPreventivo.choices, default=StatoPreventivo.VALIDO
    )

    class Meta:
        verbose_name_plural = "Preventivi"

    def clean(self):
        super().clean()
        self.validate_fields()

    def validate_fields(self):
        """
        Verifica che non ci siano due preventivi uguali
        """
        if not self.utente:
            raise ValidationError("Utente non specificato")
        if not self.modello:
            raise ValidationError("Modello non specificato")

        optionals = Configurazione.objects.group_by_preventivo(self.pk).values_list(
            "optional", flat=True
        )
        if not optionals:
            raise ValidationError("Configurazione non specificata")

    def __str__(self):
        return "Preventivo n. " + str(self.pk)


class Configurazione(models.Model):
    preventivo = models.ForeignKey(
        Preventivo, on_delete=CASCADE, blank=False, null=False
    )
    optional = models.ManyToManyField(Optional, blank=False)


class Acquisto(models.Model):
    numero_fattura = models.CharField(
        max_length=10, unique=True, null=False, blank=False
    )
    utente = models.ForeignKey(Utente, on_delete=CASCADE, null=False, blank=False)
    preventivo = models.ForeignKey(
        Preventivo, on_delete=CASCADE, null=False, blank=False
    )
    acconto = models.PositiveIntegerField(null=False, blank=False)
    data_ritiro = models.DateField(null=False, blank=False)

    class Meta:
        unique_together = ("numero_fattura", "preventivo")
        verbose_name_plural = "Acquisti"

    def __str__(self):
        return "Fattura n. " + self.numero_fattura


def year_choice():
    return [(r, r) for r in range(current_year(), current_year() + 5)]


def current_year():
    return datetime.now().year


class Periodo(models.Model):
    class Mesi(models.TextChoices):
        gennaio = "gennaio"
        febbraio = "febbraio"
        marzo = "marzo"
        aprile = "aprile"
        maggio = "maggio"
        giugno = "giugno"
        luglio = "luglio"
        agosto = "agosto"
        settembre = "settembre"
        ottobre = "ottobre"
        novembre = "novembre"
        dicembre = "dicembre"

    mese = models.CharField(
        max_length=9,
        choices=Mesi,
        null=False,
        blank=False,
    )
    anno = models.PositiveIntegerField(
        choices=year_choice(),
        default=current_year(),
        null=False,
        blank=False,
        validators=[MaxValueValidator(9999)],
    )

    class Meta:
        unique_together = ("mese", "anno")

    def __str__(self):
        return self.mese + " " + str(self.anno)


class Sconto(models.Model):
    periodo = models.ForeignKey(Periodo, on_delete=CASCADE, null=False, blank=False)
    modello = models.ForeignKey(ModelloAuto, on_delete=CASCADE, null=False, blank=False)
    percentuale_sconto = models.PositiveIntegerField(
        validators=[MinValueValidator(1), MaxValueValidator(100)]
    )  # valori da 1 a 100

    class Meta:
        unique_together = ("periodo", "modello")
        verbose_name_plural = "Sconti"


class Ritiro(models.Model):
    preventivo = models.ForeignKey(Preventivo, on_delete=CASCADE)
    concessionario = models.ForeignKey(Concessionario, on_delete=CASCADE)

    class Meta:
        unique_together = ("preventivo", "concessionario")
        verbose_name_plural = "Ritiro Auto"


def validate_targa(value):
    if not re.match("^[A-Z]{2}\d{3}[A-Z]{2}$", value):
        raise ValidationError(
            "Formato della targa non è valida. Deve essere del tipo AA123BB"
        )


def validate_not_future_date(value):
    if value > datetime.now().date():
        raise ValidationError("La data non può essere futura")


class AutoUsata(Auto):
    prezzo = models.PositiveIntegerField(default=0, validators=[MinValueValidator(0)])
    km_percorsi = models.PositiveIntegerField(
        null=False, blank=False, validators=[MinValueValidator(0)]
    )
    anno_immatricolazione = models.DateField(
        null=False, blank=False, validators=[validate_not_future_date]
    )
    targa = models.CharField(
        max_length=7,
        null=False,
        blank=False,
        validators=[validate_targa],
        default="AA000AA",
        unique=True,
    )
    venduta = models.BooleanField(null=False, blank=False, default=False)

    class Meta:
        verbose_name_plural = "Auto Usate"

    def __str__(self):
        return self.modello + " - " + self.marca


class PreventivoUsato(models.Model):
    utente = models.ForeignKey(Utente, on_delete=CASCADE, null=False, blank=False)
    auto = models.ForeignKey(AutoUsata, on_delete=CASCADE, null=False, blank=False)

    class Meta:
        verbose_name_plural = "Preventivi Auto Usate"
        unique_together = ("utente", "auto")


class AbstractImmagini(models.Model):
    image = models.ImageField(upload_to="", default=None)

    class Meta:
        abstract = True

    def preview_img(self):
        if self.image:
            return mark_safe(f'<img src="{self.image.url}" />')
        return "Nessuna immagine"

    preview_img.short_description = "Anteprima Immagine"


class ImmaginiAutoNuove(AbstractImmagini):
    auto = models.ForeignKey(ModelloAuto, on_delete=CASCADE)

    class Meta:
        verbose_name_plural = "Immagini Auto Nuove"
        unique_together = ("image", "auto")

    def upload_to(self, filename):
        return f"imageAutoNuove/{filename}"

    def delete(self, *args, **kwargs):
        if self.image and os.path.isfile(self.image.path):
            os.remove(self.image.path)
        super().delete(*args, **kwargs)

    def save(self, *args, **kwargs):
        if self.id:
            try:
                this = ImmaginiAutoNuove.objects.get(id=self.id)
                if this.image != self.image:
                    this.image.delete(save=False)
            except ImmaginiAutoNuove.DoesNotExist:
                pass

        if self.image:
            self.image.name = self.upload_to(self.image)

        super(AbstractImmagini, self).save(*args, **kwargs)


class ImmaginiAutoUsate(AbstractImmagini):
    auto = models.ForeignKey(AutoUsata, on_delete=CASCADE)

    class Meta:
        verbose_name_plural = "Immagini Auto Usate"
        unique_together = ("image", "auto")

    def upload_to(self, filename):
        return f"imageAutoUsate/{filename}"

    def delete(self, *args, **kwargs):
        if self.image and os.path.isfile(self.image.path):
            os.remove(self.image.path)
        super().delete(*args, **kwargs)

    def save(self, *args, **kwargs):
        if self.id:
            try:
                this = ImmaginiAutoUsate.objects.get(id=self.id)
                if this.image != self.image:
                    this.image.delete(save=False)
            except ImmaginiAutoNuove.DoesNotExist:
                pass

        if self.image:
            self.image.name = self.upload_to(self.image)

        super(AbstractImmagini, self).save(*args, **kwargs)


class Detrazione(models.Model):
    preventivo = models.OneToOneField(Preventivo, on_delete=CASCADE)
    auto_usata = models.OneToOneField(AutoUsata, on_delete=CASCADE)

    class Meta:
        unique_together = ("preventivo", "auto_usata")
