package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public abstract class Auto implements Serializable {
  @SerializedName("id")
  private int id;

  @SerializedName("modello")
  private String modello;

  @SerializedName("marca")
  private Marca marca;

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

  @Expose(deserialize = false)
  private ArrayList<File> immagini;

  protected Auto() {}

  protected Auto(
      String modello,
      String marca,
      int altezza,
      int lunghezza,
      int larghezza,
      int peso,
      int volumeBagagliaio) {
    this.modello = modello;
    this.marca = Marca.getMarca(marca);
    this.altezza = altezza;
    this.lunghezza = lunghezza;
    this.larghezza = larghezza;
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
            }
            """,
        id, modello, marca, altezza, lunghezza, peso, volumeBagagliaio);
  }

  /** Carica le immagini dell'auto sul disco */
  public void setImmagini() {
    if (this instanceof ModelloAuto) {
      this.immagini = fetchImmagini(this.id, "immaginiAutoNuove");
    } else if (this instanceof AutoUsata) {
      this.immagini = fetchImmagini(this.id, "immaginiAutoUsate");
    }
  }

  /**
   * Recupera le immagini dall'API e le salva su disco
   *
   * @param idAuto id dell'auto a cui l'immagine fa riferimento
   * @param subDirectory url del backend per le immagini ma anche il nome della cartella in cui
   *     verranno salvate sul disco
   * @return {@link ArrayList} di {@link File}
   */
  private @NotNull ArrayList<File> fetchImmagini(int idAuto, String subDirectory) {
    ArrayList<File> immaginiList = new ArrayList<>();
    List<ImmagineAuto> immagineAutoList;
    try {
      immagineAutoList =
          Connection.getImageFromBackend(
              String.format("%s/%d/", subDirectory, idAuto), ImmagineAuto.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    File pathDir = new File(String.format("instance/immagini/%s/", subDirectory));
    if (!pathDir.exists() && pathDir.mkdirs()) {
      App.getLog().log(Level.INFO, "Cartella {0} creata", subDirectory);
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
