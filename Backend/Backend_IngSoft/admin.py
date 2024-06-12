from django.contrib import admin
# from unfold.contrib.filters.admin import
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from django.contrib.auth.models import Group, User
from django.db.models import Model
from django.forms import Form
from django.http import HttpRequest
from typing import Any
from unfold.admin import ModelAdmin, TabularInline

from .models import *
from .util.util import send_html_email
from .widgets import SliderWidget

admin.site.unregister(User)
admin.site.unregister(Group)


@admin.register(User)
class UserAdmin(BaseUserAdmin, ModelAdmin):
    pass


@admin.register(Group)
class GroupAdmin(ModelAdmin):
    pass


@admin.register(Utente)
class UtenteAdmin(ModelAdmin):
    list_display = ("email", "nome", "cognome")


class MarcaFilter(admin.SimpleListFilter):
    title = "marca"
    parameter_name = "marca"

    def lookups(self, request, model_admin):
        list_of_models = set()
        queryset = ModelloAuto.objects.all()
        for query in queryset:
            if query.marca not in list_of_models:
                list_of_models.add((query.marca, query.marca))
        return list_of_models

    def queryset(self, request, queryset):
        if self.value():
            return queryset.filter(marca=self.value())


class ImmaginiInline(TabularInline):
    model = ImmaginiAutoNuove
    extra = 1
    formfield_overrides = {"image": {"widget": SliderWidget}}


@admin.register(ModelloAuto)
class ModelloAutoAdmin(ModelAdmin):
    list_display = ("modello", "marca", "prezzo_base")
    list_filter = (MarcaFilter,)
    inlines = [ImmaginiInline]


class TypeOptionalFilter(admin.SimpleListFilter):
    title = "nome"
    parameter_name = "nome"

    def lookups(self, request, model_admin):
        list_of_models = set()
        queryset = Optional.objects.all()
        for query in queryset:
            if query.nome not in list_of_models:
                list_of_models.add((query.nome, query.nome))
        return list_of_models

    def queryset(self, request, queryset):
        if self.value():
            return queryset.filter(nome=self.value())


@admin.register(Optional)
class OptionalAdmin(ModelAdmin):
    list_display = ("nome", "descrizione", "prezzo")
    list_filter = (TypeOptionalFilter,)

    def get_readonly_fields(self, request, obj=None):
        if request.user.is_superuser:
            return []
        return [f.name for f in self.model._meta.fields]


class PrezzoAutoUsataFilter(admin.SimpleListFilter):
    title = "prezzo"
    parameter_name = "prezzo"

    def lookups(self, request, model_admin):
        list_of_models = set()
        queryset = AutoUsata.objects.all()
        print(queryset)
        for entry in queryset:
            if entry.prezzo == 0:
                list_of_models.add(("non \te", "non validate"))
            else:
                list_of_models.add(("validate", "validate"))
        return list_of_models

    def queryset(self, request, queryset):
        if self.value() == "non validate":
            return queryset.filter(prezzo=0)
        elif self.value() == "validate":
            return queryset.exclude(prezzo=0)


@admin.register(AutoUsata)
class AutoUsataAdmin(ModelAdmin):
    list_display = ("modello", "prezzo")
    list_filter = (PrezzoAutoUsataFilter,)

    def get_readonly_fields(self, request, obj=None):
        if request.user.is_superuser:
            return []
        return [
            "modello",
            "marca",
            "anno_immatricolazione",
            "km_percorsi",
            "altezza",
            "lunghezza",
            "larghezza",
            "peso",
            "volume_bagagliaio",
        ]

    def save_model(
            self, request: HttpRequest, obj: Model, form: Form, change: Any
    ) -> None:
        # Controlla se l'oggetto esiste già nel database (modifica invece di creazione)
        if change:
            old_obj = AutoUsata.objects.get(pk=obj.pk)
            if old_obj.prezzo != obj.prezzo:  # Controlla se il campo è stato

                preventivo = Detrazione.objects.get(auto_usata_id=obj.pk).preventivo
                utente = preventivo.utente
                modello = preventivo.modello
                from_email = utente.email
                template_name = "emails/valutazione_usato.html"

                # modificato
                send_html_email(
                    "Valutazione usato",
                    from_email,
                    {
                        "modello": old_obj.modello,
                        "customer_name": f"{utente.nome} {utente.cognome}",
                        "prezzo": obj.prezzo,
                        "modello_nuovo": modello.modello,
                        "prezzo_base": modello.prezzo_base,
                        "prezzo_detrazione": (modello.prezzo_base - obj.prezzo),
                    },
                    template_name,
                )
        super().save_model(request, obj, form, change)


@admin.register(Concessionario)
class ConcessionarioAdmin(ModelAdmin):
    pass


@admin.register(Sconto)
class ScontoAdmin(ModelAdmin):
    pass


@admin.register(Ritiro)
class RitiroAdmin(ModelAdmin):
    pass


@admin.register(ImmaginiAutoNuove)
class ImmaginiAutoNuoveAdmin(ModelAdmin):
    pass


@admin.register(Preventivo)
class PreventivoAdmin(ModelAdmin):
    pass


@admin.register(Configurazione)
class ConfigurazioneAdmin(admin.ModelAdmin):
    pass


@admin.register(Acquisto)
class AcquistoAdmin(ModelAdmin):
    pass


# TODO: https://medium.com/django-unleashed/django-admin-displaying-images-in-your-models-bb7e9d8be105
