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
        Sede {
          nome='%s',
          indirizzo=%s
        """,
        nome, indirizzo);
  }
}

@Data
class Indirizzo implements Serializable {
  @SerializedName("via")
  private String via;

  @SerializedName("civico")
  private String civico;

  @SerializedName("citta")
  private String citta;

  @SerializedName("cap")
  private String cap;

  @Override
  public String toString() {
    return String.format(
        """
        Indirizzo {
          via='%s',
          civico='%s',
          citta='%s',
          cap='%s'
        }
        """,
        via, citta, cap, civico);
  }
}
