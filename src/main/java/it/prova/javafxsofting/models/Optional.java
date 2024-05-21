package it.prova.javafxsofting.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class Optional implements Serializable {
  private String nome;
  private String descrizione;
  private int prezzo;

  public Optional(String nome, String descrizione, int prezzo) {
    this.nome = nome;
    this.descrizione = descrizione;
    this.prezzo = prezzo;
  }
}
