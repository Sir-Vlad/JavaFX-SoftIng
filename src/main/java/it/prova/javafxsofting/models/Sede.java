package it.prova.javafxsofting.models;

import lombok.Data;

@Data
public class Sede {
  private String nome;
  private Indirizzo indirizzo;
}

@Data
class Indirizzo {
  private String via;
  private String citta;
  private String cap;
  private String civico;
}
