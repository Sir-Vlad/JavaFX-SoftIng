package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import lombok.Data;

enum TipoMotore {
  GASOLIO,
  BENZINA,
  IBRIDA,
  ELETTRICA,
  IBRICA_PLUG_IN
}

@Data
public class ModelloAuto implements Serializable {
  @SerializedName("id")
  private int index;

  @SerializedName("nome")
  private String nome;

  @SerializedName("marca")
  private Marca marca;

  @SerializedName("prezzo_base")
  private int prezzoBase;

  @Expose(deserialize = false)
  private File[] immagini;

  // dati auto
  @SerializedName("altezza")
  private int altezza;

  @SerializedName("lunghezza")
  private int lunghezza;

  @SerializedName("larghezza")
  private int larghezza;

  @SerializedName("peso")
  private int peso;

  @SerializedName("volume_bagagliaio")
  private int volumeBagagliaio;

  // option
  @Expose(deserialize = false)
  private Optional[] optionals;

  public ModelloAuto(
      int index,
      String nome,
      String marca,
      int prezzoBase,
      int altezza,
      int lunghezza,
      int peso,
      int volume_bagagliaio) {
    this.index = index;
    this.nome = nome;
    this.prezzoBase = prezzoBase;
    this.marca = Marca.getMarca(marca);
    this.altezza = altezza;
    this.lunghezza = lunghezza;
    this.peso = peso;
    this.volumeBagagliaio = volume_bagagliaio;
  }

  @Override
  public String toString() {
    return String.format(
        "ModelloAuto{\n\tindex=%s,\n\tnome=%s,\n\tmarca=%s,\n\taltezza=%d,\n\tlunghezza=%d,\n\tpeso=%d,\n\tvolumeBagagliaio=%d\n\t}%s",
        index, nome, marca, altezza, lunghezza, peso, volumeBagagliaio, Arrays.toString(optionals));
  }
}
