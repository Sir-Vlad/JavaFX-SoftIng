package it.prova.javafxsofting.models;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Preventivo {
  private int id;
  private int id_utente;
  private int id_conf;
  private int id_sede;
  private LocalDate dataEmissione;
  private int detrazione;
  private int prezzo;

  private Utente utente;
  private Configurazione configurazione;
  private Sede sede;
}
