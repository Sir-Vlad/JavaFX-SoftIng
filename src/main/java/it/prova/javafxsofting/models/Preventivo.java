package it.prova.javafxsofting.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
public class Preventivo implements Serializable {

  @SerializedName("id")
  private int id;

  @SerializedName("utente")
  private Utente utente;

  @SerializedName("modello")
  private ModelloAuto modello;

  @SerializedName("sede")
  private Sede sede;

  @SerializedName("data_emissione")
  private LocalDate dataEmissione;

  @SerializedName("prezzo")
  private int prezzo;

  @Override
  public String toString() {
    return String.format(
        """
        Preventivo{
          id=%d,
          utente=%s,
          modello=%s,
          sede=%s,
          dataEmissione=%s,
          prezzo=%d,
        }
        """,
        id, utente, modello, sede, dataEmissione, prezzo);
  }
}
