package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.controller.PreventiviUtenteController;
import lombok.Data;

import java.io.Serializable;

@Data
public class Ordine implements Serializable {
  @SerializedName("id")
  private int id;

  @SerializedName("numero_fattura")
  private String numeroFattura;

  @SerializedName("utente")
  private int utenteId;

  @SerializedName("preventivo")
  private int preventivoID;

  @SerializedName("acconto")
  private int acconto;

  @Expose(serialize = false, deserialize = false)
  private Utente utente;

  @Expose(serialize = false, deserialize = false)
  private Preventivo preventivo;

  public void transformIdToObject() {
    if (utenteId == App.getUtente().getId()) {
      this.utente = App.getUtente();
    }

    this.preventivo =
        PreventiviUtenteController.getPreventiviUtente().stream()
            .filter(obj -> obj.getId() == preventivoID)
            .findFirst()
            .orElse(null);
  }
}
