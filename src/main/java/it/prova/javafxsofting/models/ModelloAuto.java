package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.*;
import lombok.Getter;
import lombok.Setter;

enum TipoMotore {
  GASOLIO,
  BENZINA,
  IBRIDA,
  ELETTRICA,
  IBRICA_PLUG_IN
}

@Getter
@Setter
public class ModelloAuto extends Auto {
  @SerializedName("prezzo_base")
  private int prezzoBase;

  // optionals che un modello può avere
  @Expose(deserialize = false)
  private Optional[] optionals;

  public ModelloAuto(
      String modello,
      String marca,
      int prezzoBase,
      int altezza,
      int lunghezza,
      int larghezza,
      int peso,
      int volumeBagagliaio) {
    super(modello, marca, altezza, lunghezza, larghezza, peso, volumeBagagliaio);
    this.prezzoBase = prezzoBase;
  }
}
