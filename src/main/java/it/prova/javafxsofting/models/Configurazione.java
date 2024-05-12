package it.prova.javafxsofting.models;

import lombok.Data;

@Data
public class Configurazione {
  private int id;
  private ModelloAuto modelloAuto;
  private Optional[] optionals;
}
