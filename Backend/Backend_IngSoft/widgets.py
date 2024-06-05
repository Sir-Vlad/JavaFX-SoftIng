from django.forms.widgets import Widget
from django.template.loader import render_to_string
from django.utils.safestring import mark_safe


class SliderWidget(Widget):
    template_name = "forms/widgets/slider.html"

    def render(self, name, value, attrs=None):
        context = self.get_context(name, value, attrs)
        return mark_safe(self._render(self.template_name, context))

    def _render(self, template_name, context):
        return render_to_string(template_name, context)
