package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
public class Sconto implements Serializable {
  private static final String[] mesi = {
    "Gennaio",
    "Febbraio",
    "Marzo",
    "Aprile",
    "Maggio",
    "Giugno",
    "Luglio",
    "Agosto",
    "Settembre",
    "Ottobre",
    "Novembre",
    "Dicembre"
  };

  @SerializedName("id")
  private int id;

  @SerializedName("percentuale_sconto")
  private int percentualeSconto;

  @SerializedName("modello")
  private int idModello;

  @SerializedName("mese")
  private String mese;

  @SerializedName("anno")
  private int anno;

  @Expose(serialize = false, deserialize = false)
  private LocalDate periodoSconto;

  /** Trasforma gli id in oggetti */
  public void transformIdToObject() {
    int numeroMese = -1;

    for (int i = 0; i < mesi.length; i++) {
      if (mesi[i].equalsIgnoreCase(mese)) {
        numeroMese = i + 1;
      }
    }

    if (numeroMese == -1) {
      this.periodoSconto = null;
      return;
    }

    this.periodoSconto = LocalDate.of(anno, numeroMese, 1);
  }
}
