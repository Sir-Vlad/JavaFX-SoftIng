package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.data_manager.DataManager;
import java.io.Serializable;
import lombok.Data;

@Data
public class PreventivoUsato implements Serializable {
  @SerializedName("id")
  private int id;

  @SerializedName("utente")
  private int idUtente;

  @SerializedName("auto")
  private int idAutoUsata;

  @Expose(serialize = false, deserialize = false)
  private AutoUsata autoUsata;

  @Expose(serialize = false, deserialize = false)
  private Utente utente;

  /** Trasforma gli id in oggetti */
  public void transformIdToObject() {
    if (idUtente == UserSession.getInstance().getUtente().getId()) {
      this.utente = UserSession.getInstance().getUtente();
    }

    this.autoUsata =
        DataManager.getInstance().getAutoUsate().stream()
            .filter(autoUsata1 -> autoUsata1.getId() == idAutoUsata)
            .findFirst()
            .orElse(null);
  }
}
