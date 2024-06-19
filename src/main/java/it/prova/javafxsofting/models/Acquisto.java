package it.prova.javafxsofting.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class Acquisto implements Serializable {
  @SerializedName("id")
  private int id;

  @SerializedName("numero_fattura")
  private String numeroFattura;

  @SerializedName("utente")
  private int idUtente;

  @SerializedName("preventivo")
  private int idPreventivo;

  @SerializedName("acconto")
  private int acconto;

  @SerializedName("data_ritiro")
  private LocalDate dataRitiro;
}
