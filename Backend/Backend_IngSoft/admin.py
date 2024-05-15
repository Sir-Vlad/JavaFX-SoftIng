from django.contrib import admin

from .models import *

admin.site.register(Utente)
admin.site.register(ModelloAuto)
admin.site.register(Optional)


class PrezzoAutoUsataFilter(admin.SimpleListFilter):
    title = 'prezzo'
    parameter_name = 'prezzo'

    def lookups(self, request, model_admin):
        list_of_models = set()
        queryset = AutoUsata.objects.all()
        print(queryset)
        for entry in queryset:
            if entry.prezzo == 0:
                list_of_models.add(('non validate', 'non validate'))
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
        else:
            return ['modello', 'marca', 'anno_immatricolazione', 'km_percorsi', 'altezza',
                    'lunghezza', 'larghezza', 'peso', 'volume_bagagliaio']


admin.site.register(Sede)
admin.site.register(Sconto)
admin.site.register(Ritiro)

# TODO: https://medium.com/django-unleashed/django-admin-displaying-images-in-your-models-bb7e9d8be105
