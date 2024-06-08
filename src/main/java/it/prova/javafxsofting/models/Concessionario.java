package it.prova.javafxsofting.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;

@Data
public class Concessionario implements Serializable {
  @SerializedName("nome")
  private String nome;

  @SerializedName("indirizzo")
  private Indirizzo indirizzo;

  @Override
  public String toString() {
    return String.format(
        """
        Concessionario {
          nome='%s',
          indirizzo=%s
        """,
        nome, indirizzo);
  }
}
