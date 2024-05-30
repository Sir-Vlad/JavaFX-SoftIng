package it.prova.javafxsofting.models;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Preventivo {
  private int id;
  private int idUtente;
  private int idConfigurazione;
  private int idSede;
  private LocalDate dataEmissione;
  private int detrazione;
  private int prezzo;

  private Utente utente;
  private Configurazione configurazione;
  private Sede sede;
}
