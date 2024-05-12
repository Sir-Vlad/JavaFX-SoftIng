package it.prova.javafxsofting.models;

import lombok.Data;

@Data
public class Acquisto {
  private String numero_fattura;
  private int id_Utente;
  private int id_preventivo;
  private int acconto;
  private int data_ritiro;
}
