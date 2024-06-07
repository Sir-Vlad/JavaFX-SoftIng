package it.prova.javafxsofting;

import it.prova.javafxsofting.models.Ordine;
import it.prova.javafxsofting.models.Preventivo;
import it.prova.javafxsofting.models.Utente;
import java.util.List;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;

@Getter
@Setter
public class UserSession {
  private static UserSession instance;
  private Utente utente;
  private List<Preventivo> preventivi;
  private List<Ordine> ordini;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Logger logger = Logger.getLogger(this.getClass().getName());

  @Contract(pure = true)
  private UserSession() {}

  public static UserSession getInstance() {
    if (instance == null) {
      instance = new UserSession();
    }
    return instance;
  }

  public List<Preventivo> getPreventivi() {
    if (getInstance().preventivi == null) {
      getInstance().preventivi = fetchPreventivi();
    }

    return getInstance().preventivi;
  }

  public static void clearSession() {
    instance = null;
  }

  public void setPreventivi() {
    preventivi = fetchPreventivi();
  }

  private List<Preventivo> fetchPreventivi() {
    logger.info("fetchPreventivi");
    String subDirectory = String.format("utente/%d/preventivi/", getInstance().getUtente().getId());
    List<Preventivo> data;
    try {
      data = Connection.getArrayDataFromBackend(subDirectory, Preventivo.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (data != null) {
      data.forEach(Preventivo::transformIdToObject);
    }
    return data;
  }
}
