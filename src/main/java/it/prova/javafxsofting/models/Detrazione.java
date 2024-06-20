package it.prova.javafxsofting.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;

@Data
public class Detrazione implements Serializable {
  @SerializedName("id")
  private int id;

  @SerializedName("preventivo")
  private int idPreventivo;

  @SerializedName("auto_usata")
  private int idAutoUsata;
}
