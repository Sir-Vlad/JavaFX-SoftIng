from Backend.settings import EMAIL_HOST_USER
from Backend_IngSoft.models import Preventivo
from django.core.mail import EmailMultiAlternatives
from django.db.models.signals import post_save
from django.dispatch import receiver
from django.utils.html import strip_tags


@receiver(post_save, sender=Preventivo)
def send_email_after_preventivo_creation(sender, instance, created, **kwargs):
    if created:
        subject = "Preventivo creato"
        to_email = instance.utente.email
        context = {
            "subject": subject,
            "title": "Preventivo creato",
            "message": f"Il preventivo per il modello {instance.modello} è stato creato",
        }
        # send_mail(subject, message, from_email, to_email)
        template_name = "emails/template_email_conferma_preventivo.html"
        send_html_email(subject, to_email, context, template_name)
        print("email inviata")


def send_html_email(subject, to_email, context, template_name):
    from django.template.loader import render_to_string

    html_content = render_to_string(template_name, context)
    text_content = strip_tags(
        html_content
    )  # Fallback text content for email clients that don't support HTML

    email = EmailMultiAlternatives(subject, text_content, EMAIL_HOST_USER, [to_email])
    email.attach_alternative(html_content, "text/html")
    email.send()
