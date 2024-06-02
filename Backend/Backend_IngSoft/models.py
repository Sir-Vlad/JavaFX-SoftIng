import os.path
from datetime import datetime

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

    class Meta:
        verbose_name_plural = "Modelli Auto"

    def __str__(self):
        return self.modello + " - " + self.marca


class Optional(models.Model):
    class Category(models.TextChoices):
        COLORE = "colore"
        CAMBIO = "cambio"
        MOTORIZZAZIONE = "motorizzazione"
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

    class Meta:
        unique_together = ("nome", "descrizione")
        verbose_name_plural = "Optional"

    def __str__(self):
        return self.nome + " - " + self.descrizione


class Sede(models.Model):
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
    data_emissione = models.DateField(null=False, blank=False)
    sede = models.ForeignKey(Sede, on_delete=CASCADE, null=False, blank=False)
    prezzo = models.IntegerField(null=False, blank=False)

    class Meta:
        unique_together = ("utente", "modello", "data_emissione")


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


class Possiede(models.Model):
    modello = models.ForeignKey(ModelloAuto, on_delete=CASCADE, null=False, blank=False)
    optional = models.ForeignKey(Optional, on_delete=CASCADE, null=False, blank=False)

    class Meta:
        unique_together = ("modello", "optional")


class Ritiro(models.Model):
    preventivo = models.ForeignKey(Preventivo, on_delete=CASCADE)
    sede = models.ForeignKey(Sede, on_delete=CASCADE)

    class Meta:
        unique_together = ("preventivo", "sede")
        verbose_name_plural = "Ritiro Auto"


class AutoUsata(Auto):
    marca = models.CharField(max_length=20, null=False, blank=False)
    prezzo = models.IntegerField(
        default=0, null=False, blank=False, validators=[MinValueValidator(0)]
    )
    km_percorsi = models.IntegerField(null=False, blank=False)
    anno_immatricolazione = models.DateField(null=False, blank=False)

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

    def upload_to(self, filename):
        return f"imageAutoNuove/{filename}"

    def delete(self, *args, **kwargs):
        if self.image and os.path.isfile(self.image.path):
            os.remove(self.image.path)
        super().delete(*args, **kwargs)

    def save(self, *args, **kwargs):
        try:
            this = ImmaginiAutoNuove.objects.get(id=self.id)
            if this.image != self.image:
                this.image.delete(save=False)
        except ImmaginiAutoNuove.DoesNotExist:
            pass
        self.image.name = self.upload_to(self.image)
        super(AbstractImmagini, self).save(*args, **kwargs)

    class Meta:
        verbose_name_plural = "Immagini Auto Nuove"
        unique_together = ("image", "auto")


class ImmaginiAutoUsate(AbstractImmagini):
    auto = models.ForeignKey(AutoUsata, on_delete=CASCADE)

    def upload_to(self, filename):
        return f"imageAutoUsate/{filename}"

    def delete(self, *args, **kwargs):
        if self.image and os.path.isfile(self.image.path):
            os.remove(self.image.path)
        super().delete(*args, **kwargs)

    def save(self, *args, **kwargs):
        try:
            this = ImmaginiAutoNuove.objects.get(id=self.id)
            if this.image != self.image:
                this.image.delete(save=False)
        except ImmaginiAutoNuove.DoesNotExist:
            pass
        self.image.name = self.upload_to(self.image)
        super(AbstractImmagini, self).save(*args, **kwargs)

    class Meta:
        unique_together = ("image", "auto")


class Detrazione(models.Model):
    preventivo = models.OneToOneField(Preventivo, on_delete=CASCADE)
    auto_usata = models.OneToOneField(AutoUsata, on_delete=CASCADE)

    class Meta:
        unique_together = ("preventivo", "auto_usata")
