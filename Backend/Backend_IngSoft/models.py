from datetime import datetime

from django.core.validators import MaxValueValidator, MinValueValidator
from django.db import models
from django.db.models import CASCADE


class Utente(models.Model):
    email = models.CharField(max_length=100, unique=True, null=False, blank=False)
    password = models.CharField(max_length=20, null=False, blank=False)
    nome = models.CharField(max_length=20, null=False, blank=False)
    cognome = models.CharField(max_length=20, null=False, blank=False)
    numero_carta = models.CharField(max_length=20, null=False, blank=False)
    data_scadenza = models.DateField(null=False, blank=False)
    cvc = models.CharField(max_length=3, null=False, blank=False)

    class Meta:
        verbose_name_plural = "Utenti"

    def __str__(self):
        return self.nome + ' ' + self.cognome


class ModelloAuto(models.Model):
    class MarcaAuto(models.TextChoices):
        NISSAN = "Nissan"
        MAZDA = "Mazda"
        VOLKSWAGEN = "Volkswagen"
        FORD = "Ford"
        HONDA = "Honda"
        AUDI = "Audi"
        BMW = "BMW"

    nome = models.CharField(max_length=20, unique=True, null=False, blank=False)
    marca = models.CharField(max_length=20, null=False, blank=False,
                             choices=MarcaAuto)  # lista di valori noti
    # descrizione = models.TextField()
    prezzo_base = models.IntegerField(null=False, blank=False)
    # dati auto
    altezza = models.IntegerField(null=False, blank=False)
    lunghezza = models.IntegerField(null=False, blank=False)
    larghezza = models.IntegerField(null=False, blank=False)
    peso = models.IntegerField(null=False, blank=False)
    volume_bagagliaio = models.IntegerField(null=False, blank=False)

    class Meta:
        verbose_name_plural = "Modelli Auto"

    def __str__(self):
        return self.nome + " - " + self.marca


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
        unique_together = ('nome', 'descrizione')
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


class Preventivo(models.Model):
    utente = models.ForeignKey(Utente, on_delete=CASCADE, null=False, blank=False)
    # configurazione = models.OneToOneField(Configurazione, on_delete=CASCADE, null=False,
    #                                       blank=False)
    data_emissione = models.DateField(null=False, blank=False)
    sede = models.ForeignKey(Sede, on_delete=CASCADE, null=False, blank=False)
    # detrazione = models.ManyToManyField(Detrazione, on_delete=CASCADE)
    prezzo = models.IntegerField(null=False, blank=False)


class Configurazione(models.Model):
    preventivo = models.OneToOneField(Preventivo, on_delete=CASCADE, blank=False,
                                      null=False)
    optional = models.ForeignKey(Optional, on_delete=CASCADE, null=False, blank=False)

    class Meta:
        unique_together = ("preventivo", "optional")


class Acquisto(models.Model):
    numero_fattura = models.CharField(max_length=10, unique=True, null=False, blank=False)
    utente = models.ForeignKey(Utente, on_delete=CASCADE, null=False, blank=False)
    preventivo = models.ForeignKey(Preventivo, on_delete=CASCADE, null=False, blank=False)
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
    anno = models.IntegerField(choices=year_choice(), default=current_year(), null=False,
                               blank=False, validators=[MaxValueValidator(9999)])

    class Meta:
        unique_together = ("mese", "anno")

    def __str__(self):
        return self.mese + " " + str(self.anno)


class Sconto(models.Model):
    periodo = models.ForeignKey(Periodo, on_delete=CASCADE, null=False, blank=False)
    modello = models.ForeignKey(ModelloAuto, on_delete=CASCADE, null=False, blank=False)
    percentuale_sconto = models.IntegerField(
        validators=[MinValueValidator(1), MaxValueValidator(100)])  # valori da 1 a 100

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


class AutoUsata(models.Model):
    # ? utente non dovrebbe essere qui
    utente = models.ForeignKey(Utente, on_delete=CASCADE, null=False, blank=False)
    modello = models.CharField(max_length=20, null=False, blank=False)
    marca = models.CharField(max_length=20, null=False, blank=False)
    prezzo = models.IntegerField(default=0, null=False, blank=False,
                                 validators=[MinValueValidator(0)])
    km_percorsi = models.IntegerField(null=False, blank=False)
    anno_immatricolazione = models.DateField(null=False, blank=False)
    # dati auto
    altezza = models.IntegerField(null=False, blank=False)
    lunghezza = models.IntegerField(null=False, blank=False)
    larghezza = models.IntegerField(null=False, blank=False)
    peso = models.IntegerField(null=False, blank=False)
    volume_bagagliaio = models.IntegerField(null=False, blank=False)

    class Meta:
        verbose_name_plural = "Auto Usate"

    def __str__(self):
        return self.modello + " - " + self.marca


class ImmaginiAutoNuove(models.Model):
    path = models.CharField(max_length=255)
    auto = models.ForeignKey(ModelloAuto, on_delete=CASCADE)

    class Meta:
        unique_together = ("path", "auto")


class ImmaginiAutoUsate(models.Model):
    path = models.CharField(max_length=255)
    auto = models.ForeignKey(AutoUsata, on_delete=CASCADE)

    class Meta:
        unique_together = ("path", "auto")


class Detrazione(models.Model):
    preventivo = models.OneToOneField(Preventivo, on_delete=CASCADE)
    auto_usata = models.OneToOneField(AutoUsata, on_delete=CASCADE)

    class Meta:
        unique_together = ("preventivo", "auto_usata")
