package it.prova.javafxsofting.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;

@Data
public class Indirizzo implements Serializable {
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
        via, civico, citta, cap);
  }
}
