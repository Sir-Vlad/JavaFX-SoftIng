import logging
import os

import pdfkit
from Backend.settings import EMAIL_HOST_USER
from Backend_IngSoft.models import Acquisto, Configurazione
from django.core.mail import EmailMultiAlternatives
from django.utils.html import strip_tags
from jinja2 import Environment, FileSystemLoader
from jinja2.exceptions import TemplateNotFound


def send_html_email(subject, to_email, context, template_name, pdf=None):
    from django.template.loader import render_to_string

    html_content = render_to_string(template_name, context)
    text_content = strip_tags(
        html_content
    )  # Fallback text content for email clients that don't support HTML

    email = EmailMultiAlternatives(subject, text_content, EMAIL_HOST_USER, [to_email])
    email.attach_alternative(html_content, "text/html")

    if pdf:
        email.attach("fattura.pdf", pdf, "application/pdf")

    email.send()


def create_pdf_file(obj: Acquisto):
    logger = logging.getLogger(__name__)

    current_dir = os.getcwd()
    template_dir = os.path.join(current_dir, "templates")

    # Stampa i percorsi per il debug
    # logger.error("Current directory: " + current_dir)
    # logger.error("Template directory: " + template_dir)

    try:
        env = Environment(loader=FileSystemLoader(template_dir))
        template = env.get_template("fattura/fattura.html")
    except TemplateNotFound as e:
        logger.error("Template not found: " + str(e))
        return

    concessionario = obj.preventivo.concessionario
    utente = obj.utente
    modello = obj.preventivo.modello
    conf: Configurazione = Configurazione.objects.get(preventivo_id=obj.preventivo.id)
    optionals = conf.optional.all()

    prezzo_con_iva = modello.prezzo_base * 22 / 100

    data = {
        "numero_fattura": obj.numero_fattura,
        "nome_concessionario": concessionario.nome,
        "indirizzo": f"{concessionario.via}, {concessionario.civico}",
        "cap": concessionario.cap,
        "citta": concessionario.citta,
        "nome_utente": f"{utente.nome} ({utente.cognome})",
        "email": utente.email,
        "modello": modello.modello,
        "prezzo_base": modello.prezzo_base,
        "optionals": optionals,
        "prezzo_totale": modello.prezzo_base,
        "prezzo_con_iva": prezzo_con_iva,
        "totale": prezzo_con_iva + modello.prezzo_base,
    }

    rendered_html = template.render(data)

    PATH_ROOT_FILE = f"{current_dir}/media/fatture"
    if not os.path.exists(PATH_ROOT_FILE):
        os.mkdir(PATH_ROOT_FILE)
        os.mkdir(f"{PATH_ROOT_FILE}/fatture_html")
        os.mkdir(f"{PATH_ROOT_FILE}/fatture_pdf")

    path_html = (
        f"{PATH_ROOT_FILE}/fatture_html/{obj.numero_fattura.replace('/', '-')}.html"
    )

    with open(path_html, "w") as f:
        f.write(rendered_html)

    config = pdfkit.configuration(wkhtmltopdf="/usr/bin/wkhtmltopdf")

    pdfkit.from_file(
        path_html,
        f"{PATH_ROOT_FILE}/fatture_pdf/{obj.numero_fattura.replace('/', '-')}.pdf",
        configuration=config,
    )

    pdf = pdfkit.from_string(
        rendered_html,
        False,
        configuration=config,
    )

    return pdf
