from datetime import datetime
from typing import Any
from django import forms
from django.contrib import admin

# from unfold.contrib.filters.admin import
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from django.contrib.auth.models import Group, User
from django.db import transaction
from django.db.models import Model
from django.forms import Form
from django.http import HttpRequest
from django.utils.safestring import mark_safe
from unfold.admin import ModelAdmin, TabularInline

from .models import (
    Acquisto,
    AutoUsata,
    Concessionario,
    Detrazione,
    ImmaginiAutoNuove,
    ImmaginiAutoUsate,
    ModelloAuto,
    Optional,
    Preventivo,
    Ritiro,
    Sconto,
    Utente,
)
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
    title = "marca"  # valore che viene utilizzato nella pagina di admin
    parameter_name = (
        "marca"  # valore che viene utilizzato nell'URL per effettuare il filtro
    )

    def lookups(self, request, model_admin):
        """
        Ritorna una lista di tuple che contiene tutti i valori della colonna 'marca'
        :param request:
        :param model_admin:
        :return: list_of_models
        """
        list_of_models = set()
        queryset = ModelloAuto.objects.all()
        for query in queryset:
            if query.marca not in list_of_models:
                list_of_models.add((query.marca, query.marca))
        return list_of_models

    def queryset(self, request, queryset):
        """
        Ritorna gli elementi del queryset filtrati per 'marca'
        :param request:
        :param queryset:
        :return:
        """
        if self.value():
            return queryset.filter(marca=self.value())


class ImmaginiInlineModelloAuto(TabularInline):
    model = ImmaginiAutoNuove
    extra = 1
    formfield_overrides = {"image": {"widget": SliderWidget}}
    readonly_fields = ("image_preview",)

    def image_preview(self, obj):
        if obj.image:
            return mark_safe(
                f'<img src="{obj.image.url}" style="max-width: 200px; max-height: '
                f'200px;" />'
            )
        return "No Image"

    image_preview.short_description = "Image Preview"


class AutoAdminForm(forms.ModelForm):
    class Meta:
        model = ModelloAuto
        fields = "__all__"
        widgets = {
            "optionals": forms.CheckboxSelectMultiple,
        }


@admin.register(ModelloAuto)
class ModelloAutoAdmin(ModelAdmin):
    list_display = ("modello", "marca", "prezzo_base")
    list_filter = (MarcaFilter,)
    inlines = [ImmaginiInlineModelloAuto]
    form = AutoAdminForm


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
        if obj:
            if request.user.is_superuser:
                return []
            return [f.name for f in self.model._meta.fields]
        else:
            return []


class PrezzoAutoUsataFilter(admin.SimpleListFilter):
    title = "prezzo"
    parameter_name = "prezzo"

    def lookups(self, request, model_admin):
        list_of_models = set()
        queryset = AutoUsata.objects.all()
        for entry in queryset:
            if entry.prezzo == 0:
                list_of_models.add(("non validate", "non validate"))
            else:
                list_of_models.add(("validate", "validate"))
        return list_of_models

    def queryset(self, request, queryset):
        if self.value() == "non validate":
            return queryset.filter(prezzo=0)
        elif self.value() == "validate":
            return queryset.exclude(prezzo=0)


class ImmaginiInlineAutoUsata(TabularInline):
    model = ImmaginiAutoUsate
    extra = 1
    formfield_overrides = {"image": {"widget": SliderWidget}}
    readonly_fields = ("image_preview",)

    def image_preview(self, obj):
        if obj.image:
            return mark_safe(
                f'<img src="{obj.image.url}" style="max-width: 200px; max-height: '
                f'200px;" />'
            )
        return "No Image"

    image_preview.short_description = "Image Preview"


@admin.register(AutoUsata)
class AutoUsataAdmin(ModelAdmin):
    list_display = ("modello", "prezzo")
    list_filter = (PrezzoAutoUsataFilter, "venduta")
    inlines = [ImmaginiInlineAutoUsata]

    def get_readonly_fields(self, request, obj=None):
        all_fields = [field.name for field in self.model._meta.fields]
        all_fields_except_prezzo = [
            field.name for field in self.model._meta.fields if field.name != "prezzo"
        ]
        if obj:  # se l'oggetto esiste
            if request.user.is_superuser:
                return []

            if obj.prezzo != 0:
                return all_fields
            else:
                return all_fields_except_prezzo

    def save_model(
        self, request: HttpRequest, obj: Model, form: Form, change: Any
    ) -> None:
        with transaction.atomic():
            # Controlla se l'oggetto esiste già nel database (modifica invece di
            # creazione)
            if change:
                old_obj = AutoUsata.objects.get(pk=obj.pk)
                if (
                    old_obj.prezzo != obj.prezzo
                ):  # Controlla se il campo è stato modificato

                    preventivo = Detrazione.objects.get(auto_usata_id=obj.pk).preventivo
                    if preventivo.data_emissione is None:
                        preventivo.data_emissione = datetime.now()
                        preventivo.save()
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
    list_filter = ("concessionario",)
    list_display = ("preventivo", "concessionario")


@admin.register(Preventivo)
class PreventivoAdmin(ModelAdmin):
    list_display = (
        "id",
        "utente",
        "modello",
        "prezzo",
        "stato",
    )
    list_filter = ("stato", "utente", "modello__marca", "concessionario")


@admin.register(Acquisto)
class AcquistoAdmin(ModelAdmin):
    list_display = ("numero_fattura", "data_ritiro")
    list_filter = ("data_ritiro",)
