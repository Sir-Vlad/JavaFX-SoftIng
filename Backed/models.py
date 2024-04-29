from sqlalchemy import (
    Column,
    Integer,
    String,
    UniqueConstraint,
    Text,
    ForeignKey,
    Date,
)

from Backed import db


# Documentazione di SQLAlchemy di Column
# https://docs.sqlalchemy.org/en/20/core/metadata.html#sqlalchemy.schema.Column
# https://docs.sqlalchemy.org/en/20/orm/basic_relationships.html


# schema di esempio
class Utente(db.Model):
    id = Column(Integer, autoincrement=True, primary_key=True)
    email = Column(String(20), nullable=False)
    password = Column(String(20), nullable=False)
    nome = Column(String(20), nullable=False)
    cognome = Column(String(20), nullable=False)

    vincolo_unicita = UniqueConstraint("email", "password", name="uq_utente")
    # todo: controllare se sono giusti
    # preventivi = relationship("Preventivo", backref="richiedente", lazy=True)
    # acquisti = relationship("Acquisto", backref="id_utente", lazy=True)

    def __repr__(self):
        return (
            f"Utente(id={self.id}, email={self.email}, password={self.password}, "
            f"nome={self.nome}, cognome={self.cognome})"
        )


class ModelloAuto(db.Model):
    id = Column(Integer, autoincrement=True, primary_key=True)
    nome = Column(String(20), nullable=False, unique=True)
    descrizione = Column(Text, nullable=False)
    altezza = Column(Integer, nullable=False)
    lunghezza = Column(Integer, nullable=False)
    larghezza = Column(Integer, nullable=False)
    peso = Column(Integer, nullable=False)
    vol_bagagliaio = Column(Integer, nullable=False)
    marca = Column(Integer, nullable=False)

    def __repr__(self):
        return (
            f"ModelloAuto(id={self.id}, nome={self.nome}, descrizione={self.descrizione}, "
            f"marca={self.marca})"
        )


class Preventivo(db.Model):
    id = Column(Integer, autoincrement=True, primary_key=True)
    id_utente = Column(Integer, ForeignKey("utente.id"), nullable=False)
    id_configurazione = Column(
        Integer, ForeignKey("configurazione.id"), nullable=False
    )
    data_emissione = Column(Date, nullable=False)
    id_sede = Column(Integer, ForeignKey("sede.id"), nullable=False)
    detrazione = Column(Integer)
    prezzo = Column(Integer, nullable=False)

    def __repr__(self):
        return (
            f"Preventivo(id={self.id}, id_utente={self.id_utente}, id_configurazione={self.id_configurazione}, "
            f"data_emissione={self.data_emissione}, id_sede={self.id_sede}, "
            f"detrazione={self.detrazione}, prezzo={self.prezzo})"
        )


class Sede(db.Model):
    id = Column(Integer, autoincrement=True, primary_key=True)


class Configurazione(db.Model):
    id = Column(Integer, autoincrement=True, primary_key=True)
