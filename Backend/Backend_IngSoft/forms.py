from django import forms
from .models import ImmaginiAutoNuove, ModelloAuto


class ImmaginiAutoNuoveForm(forms.ModelForm):
    class Meta:
        model = ImmaginiAutoNuove
        fields = ("image",)


class AutoNuoveAdminForm(forms.ModelForm):
    immagini = forms.ModelMultipleChoiceField(
        queryset=ImmaginiAutoNuove.objects.all(), widget=forms.FileInput
    )

    class Meta:
        model = ModelloAuto
        fields = (
            "immagini",
            "modello",
            "prezzo_base",
            "altezza",
            "larghezza",
            "lunghezza",
            "peso",
            "volume_bagagliaio",
        )
