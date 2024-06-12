import imagehash
import os.path
import re
from PIL import Image
from datetime import datetime
from django.core.exceptions import ValidationError
from django.core.validators import MaxValueValidator, MinValueValidator
from django.db import models
from django.db.models import CASCADE
from django.utils.safestring import mark_safe


class Utente(models.Model):
    email = models.CharField(max_length=100, unique=True, null=False, blank=False)
    password = models.CharField(max_length=20, null=False, blank=False)
    nome = models.CharField(max_length=20, null=False, blank=False)
    cognome = models.CharField(max_length=20, null=False, blank=False)
    indirizzo = models.CharField(max_length=100, null=True, blank=False)
    numero_telefono = models.CharField(max_length=10, null=True, blank=False)
    numero_carta = models.CharField(max_length=16, null=False, blank=False)
    data_scadenza = models.DateField(null=False, blank=False)
    cvc = models.CharField(max_length=3, null=False, blank=False)

    class Meta:
        verbose_name_plural = "Utenti"

    def __str__(self):
        return self.nome + " " + self.cognome


class Auto(models.Model):
    modello = models.CharField(max_length=20, unique=True, null=False, blank=False)
    # dati auto
    altezza = models.IntegerField(null=False, blank=False)
    lunghezza = models.IntegerField(null=False, blank=False)
    larghezza = models.IntegerField(null=False, blank=False)
    peso = models.IntegerField(null=False, blank=False)
    volume_bagagliaio = models.IntegerField(null=False, blank=False)

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
    prezzo = models.IntegerField(blank=False)
    obbligatorio = models.BooleanField(default=False)

    class Meta:
        unique_together = ("nome", "descrizione")
        verbose_name_plural = "Optional"

    def __str__(self):
        return self.nome + " - " + self.descrizione


class ModelloAuto(Auto):
    class MarcaAuto(models.TextChoices):
        NISSAN = "NISSAN"
        MAZDA = "MAZDA"
        VOLKSWAGEN = "VOLKSWAGEN"
        FORD = "FORD"
        HONDA = "HONDA"
        AUDI = "AUDI"
        BMW = "BMW"

    marca = models.CharField(
        max_length=20, null=False, blank=False, choices=MarcaAuto
    )  # lista di valori noti
    prezzo_base = models.IntegerField(null=False, blank=False)
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
        verbose_name_plural = "Sedi"

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


class Preventivo(models.Model):
    utente = models.ForeignKey(Utente, on_delete=CASCADE, null=False, blank=False)
    modello = models.ForeignKey(ModelloAuto, on_delete=CASCADE, null=False, blank=False)
    data_emissione = models.DateField(null=True)
    concessionario = models.ForeignKey(
        Concessionario, on_delete=CASCADE, null=False, blank=False
    )
    prezzo = models.IntegerField(null=False, blank=False)
    valid = models.BooleanField(default=True)

    class Meta:
        pass
        # TODO: capire come rendere univoco un preventivo
        #   possibile idea guardare modello, utente e optional
        # unique_together = ("utente", "modello")


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
    acconto = models.IntegerField(null=False, blank=False)
    data_ritiro = models.DateField(null=False, blank=False)

    def __str__(self):
        return self.numero_fattura


def year_choice():
    return [(r, r) for r in range(2000, current_year() + 1)]


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

    mese = models.CharField(max_length=9, choices=Mesi, null=False, blank=False)
    anno = models.IntegerField(
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
    percentuale_sconto = models.IntegerField(
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
    if not re.match("^[A-Z]{2}[0-9]{3}[A-Z]{2}$", value):
        raise ValidationError(
            "Formato della targa non è valida. Deve essere del tipo " "AA123BB"
        )


def validate_not_future_date(value):
    if value > datetime.now().date():
        raise ValidationError("La data non può essere futura")


class AutoUsata(Auto):
    marca = models.CharField(max_length=20, null=False, blank=False)
    prezzo = models.IntegerField(default=0, validators=[MinValueValidator(0)])
    km_percorsi = models.IntegerField(
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
    )

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
    phash = models.CharField(max_length=64, editable=False, null=False)

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

        if self.image and not self.phash:
            image = Image.open(self.image)
            phash = imagehash.phash(image)
            self.phash = str(phash)
            self.image.name = self.upload_to(self.image)

        super(AbstractImmagini, self).save(*args, **kwargs)


class ImmaginiAutoUsate(AbstractImmagini):
    auto = models.ForeignKey(AutoUsata, on_delete=CASCADE)

    class Meta:
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

        if self.image and not self.phash:
            image = Image.open(self.image)
            phash = imagehash.phash(image)
            self.phash = str(phash)
            self.image.name = self.upload_to(self.image)

        super(AbstractImmagini, self).save(*args, **kwargs)


class Detrazione(models.Model):
    preventivo = models.OneToOneField(Preventivo, on_delete=CASCADE)
    auto_usata = models.OneToOneField(AutoUsata, on_delete=CASCADE)

    class Meta:
        unique_together = ("preventivo", "auto_usata")
