package it.prova.javafxsofting.models;

import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Data
public class ImmagineAuto implements Serializable {
  @SerializedName("auto")
  int idAuto;

  @SerializedName("image_name")
  String nomeImmagine;

  @SerializedName("image_base64")
  String immagineBase64;

  @Contract(pure = true)
  public ImmagineAuto(int i, String name, String s) {
    this.idAuto = i;
    this.nomeImmagine = name;
    this.immagineBase64 = s;
  }

  public ImmagineAuto(int idAuto, @NotNull File file) {
    try {
      this.idAuto = idAuto;
      this.nomeImmagine = file.getName();

      byte[] imageBytes = Files.readAllBytes(Path.of(file.getPath()));
      this.immagineBase64 = Base64.getEncoder().encodeToString(imageBytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {

    return String.format(
        """
        ImmagineAuto{
          idAuto=%d,
          nomeImmagine='%s',
          immagineBase64='%s'
        }""",
        idAuto, nomeImmagine, immagineBase64.substring(0, 20));
  }
}
