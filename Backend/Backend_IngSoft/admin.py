from django.contrib import admin

from .models import *


@admin.register(Utente)
class UtenteAdmin(admin.ModelAdmin):
    list_display = ("email", "nome", "cognome")


class MarcaFilter(admin.SimpleListFilter):
    title = 'marca'
    parameter_name = 'marca'

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


@admin.register(ModelloAuto)
class ModelloAutoAdmin(admin.ModelAdmin):
    list_display = ("nome", "marca", "prezzo_base")
    list_filter = (MarcaFilter,)


class TypeOptionalFilter(admin.SimpleListFilter):
    title = 'nome'
    parameter_name = 'nome'

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
class OptionalAdmin(admin.ModelAdmin):
    list_display = ("nome", "descrizione", "prezzo")
    list_filter = (TypeOptionalFilter,)

    def get_readonly_fields(self, request, obj=None):
        if request.user.is_superuser:
            return []
        return [f.name for f in self.model._meta.fields]


class PrezzoAutoUsataFilter(admin.SimpleListFilter):
    title = 'prezzo'
    parameter_name = 'prezzo'
    def lookups(self, request, model_admin):
        list_of_models = set()
        queryset = AutoUsata.objects.all()
        print(queryset)
        for entry in queryset:
            if entry.prezzo == 0:
                list_of_models.add(('non \te', 'non validate'))
            else:
                list_of_models.add(('validate', 'validate'))
        return list_of_models

    def queryset(self, request, queryset):
        if self.value() == 'non validate':
            return queryset.filter(prezzo=0)
        elif self.value() == 'validate':
            return queryset.exclude(prezzo=0)


@admin.register(AutoUsata)
class AutoUsataAdmin(admin.ModelAdmin):
    list_display = ('modello', 'prezzo')
    list_filter = (PrezzoAutoUsataFilter,)

    def get_readonly_fields(self, request, obj=None):
        if request.user.is_superuser:
            return []
        return ['modello', 'marca', 'anno_immatricolazione', 'km_percorsi', 'altezza',
                'lunghezza', 'larghezza', 'peso', 'volume_bagagliaio']


admin.site.register(Sede)
admin.site.register(Sconto)
admin.site.register(Ritiro)


# TODO: https://medium.com/django-unleashed/django-admin-displaying-images-in-your-models-bb7e9d8be105
