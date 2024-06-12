from Backend.settings import EMAIL_HOST_USER
from Backend_IngSoft.models import Configurazione, Preventivo
from django.core.mail import EmailMultiAlternatives
from django.db.models.signals import post_save
from django.dispatch import receiver
from django.utils.html import strip_tags


# @receiver(post_save, sender=Configurazione)
# def send_email_after_preventivo_creation(sender, instance, created, **kwargs):
#     if created:
#         preventivo = instance.preventivo
#
#         optional_list = instance.optional.all()
