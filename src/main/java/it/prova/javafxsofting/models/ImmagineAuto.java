package it.prova.javafxsofting.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;

@Data
public class ImmagineAuto implements Serializable {
  @SerializedName("auto")
  int idAuto;

  @SerializedName("image_name")
  String nomeImmagine;

  @SerializedName("image_base64")
  String immagineBase64;

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
