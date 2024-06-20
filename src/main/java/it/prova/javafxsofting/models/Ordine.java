package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.prova.javafxsofting.UserSession;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

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

  @SerializedName("data_ritiro")
  private LocalDate dataRitiro;

  @Expose(serialize = false, deserialize = false)
  private Utente utente;

  @Expose(serialize = false, deserialize = false)
  private Preventivo preventivo;

  public void transformIdToObject() {
    if (utenteId == UserSession.getInstance().getUtente().getId()) {
      this.utente = UserSession.getInstance().getUtente();
    }

    this.preventivo =
        UserSession.getInstance().getPreventivi().stream()
            .filter(obj -> obj.getId() == preventivoID)
            .toList()
            .getFirst();
  }
}
