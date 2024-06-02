package it.prova.javafxsofting.models;

import lombok.Data;

@Data
public class Acquisto {
  private String numeroFattura;
  private int idUtente;
  private int idPreventivo;
  private int acconto;
  private int dataRitiro;
}
