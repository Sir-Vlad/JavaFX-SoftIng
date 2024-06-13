from Backend.settings import EMAIL_HOST_USER
from django.core.mail import EmailMultiAlternatives
from django.utils.html import strip_tags


def send_html_email(subject, to_email, context, template_name):
    from django.template.loader import render_to_string

    html_content = render_to_string(template_name, context)
    text_content = strip_tags(
        html_content
    )  # Fallback text content for email clients that don't support HTML

    email = EmailMultiAlternatives(subject, text_content, EMAIL_HOST_USER, [to_email])
    email.attach_alternative(html_content, "text/html")
    email.send(fail_silently=True)
