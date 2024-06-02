package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
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

  @SerializedName("modello")
  private String modello;

  @SerializedName("marca")
  private Marca marca;

  @SerializedName("prezzo_base")
  private int prezzoBase;

  @Expose(deserialize = false)
  private ArrayList<File> immagini;

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

  // optionals che un modello può avere
  @Expose(deserialize = false)
  private Optional[] optionals;

  public ModelloAuto(
      int index,
      String modello,
      String marca,
      int prezzoBase,
      int altezza,
      int lunghezza,
      int peso,
      int volumeBagagliaio) {
    this.index = index;
    this.modello = modello;
    this.prezzoBase = prezzoBase;
    this.marca = Marca.getMarca(marca);
    this.altezza = altezza;
    this.lunghezza = lunghezza;
    this.peso = peso;
    this.volumeBagagliaio = volumeBagagliaio;
  }

  @Override
  public String toString() {
    return String.format(
        """
        ModelloAuto{
          Dati generali{
            index=%s,
            nome=%s,
            marca=%s,
          }
          Dati tecnici{
            altezza=%d,
            lunghezza=%d,
            peso=%d,
            volumeBagagliaio=%d
          }
          Optionals{
            %s
          }
        }
        """,
        index,
        modello,
        marca,
        altezza,
        lunghezza,
        peso,
        volumeBagagliaio,
        Arrays.toString(optionals));
  }

  public void setImmagini() {
    this.immagini = fetchImmagini(this.index);
  }

  private ArrayList<File> fetchImmagini(int index) {
    ArrayList<File> immaginiList = new ArrayList<>();
    List<ImmagineAuto> immagineAutoList;
    try {
      immagineAutoList =
          Connection.getImageFromBackend(
              String.format("immaginiAutoNuove/%d/", index), ImmagineAuto.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    File pathDir =
        new File("src/main/resources/it/prova/javafxsofting/immagini/immaginiAutoNuove/");
    if (!pathDir.exists() && pathDir.mkdirs()) {
      App.getLog().info("Cartella immaginiAutoNuove creata");
    }

    for (ImmagineAuto immagineAuto : immagineAutoList) {
      String nameImmagine = pathDir.toPath().resolve(immagineAuto.getNomeImmagine()).toString();

      if (Files.exists(Path.of(nameImmagine))) {
        File output = new File(nameImmagine);
        immaginiList.add(output);
        continue;
      }

      String immagineBase64 = immagineAuto.getImmagineBase64();
      byte[] imageBytes = Base64.getDecoder().decode(immagineBase64);

      try (OutputStream os = new FileOutputStream(nameImmagine)) {
        os.write(imageBytes);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      File output = new File(nameImmagine);
      immaginiList.add(output);
    }
    return immaginiList;
  }
}
