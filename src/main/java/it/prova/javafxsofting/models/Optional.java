package it.prova.javafxsofting.models;

import java.io.Serializable;
import lombok.Data;
import org.jetbrains.annotations.Contract;

@Data
public class Optional implements Serializable {
  private String nome;
  private String descrizione;
  private int prezzo;

  @Contract(pure = true)
  public Optional(String nome, String descrizione, int prezzo) {
    this.nome = nome;
    this.descrizione = descrizione;
    this.prezzo = prezzo;
  }

  @Override
  public String toString() {
    return String.format(
        """
        Optional{
          nome='%s',
          descrizione='%s',
          prezzo=%d
        }
       """,
        nome, descrizione, prezzo);
  }
}
