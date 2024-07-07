package it.prova.javafxsofting;

import it.prova.javafxsofting.models.*;
import java.util.ArrayList;
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
  private List<Preventivo> preventivi = new ArrayList<>();
  private List<Ordine> ordini = new ArrayList<>();
  private List<PreventivoUsato> preventiviUsati = new ArrayList<>();
  private List<Detrazione> detrazioni = new ArrayList<>();

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Logger logger = Logger.getLogger(this.getClass().getName());

  private List<PreventivoListener> listenersPreventivi = new ArrayList<>();
  private List<PreventivoUsatoListener> listenersPreventiviUsati = new ArrayList<>();

  @Contract(pure = true)
  private UserSession() {}

  /**
   * Restituisce l'istanza singleton se esiste, altrimenti crea una nuova
   *
   * @return istanza
   */
  public static UserSession getInstance() {
    if (instance == null) {
      instance = new UserSession();
    }
    return instance;
  }

  public List<Ordine> getOrdini() {
    if (getInstance().ordini == null) {
      getInstance().setOrdini();
    }

    return getInstance().ordini;
  }

  public List<Preventivo> getPreventivi() {
    if (getInstance().preventivi == null) {
      getInstance().setPreventivi();
    }

    return getInstance().preventivi;
  }

  public void setUtente(Utente utente) {
    this.utente = utente;

    if (utente != null) {
      new Thread(
              () -> {
                logger.info("init setPreventivi");
                setPreventivi();
                logger.info("init setPreventiviUsati");
                setPreventiviUsati();
                logger.info("init setOrdini");
                setOrdini();
                if (!getPreventiviUsati().isEmpty()) {
                  logger.info("init setDetrazioni");
                  setDetrazioni();
                }
              })
          .start();
    }
  }

  /** Elimina la sessione utente */
  public static void clearSession() {
    instance = null;
  }

  public void setPreventivi() {
    preventivi = fetchPreventivi();
    notifyListenersPreventivo();
  }

  public void setPreventiviUsati() {
    preventiviUsati = fetchPreventiviUsati();
    notifyListenersPreventivoUsato();
  }

  public void setOrdini() {
    ordini = fetchOrdini();
  }

  public void setDetrazioni() {
    detrazioni = fetchDetrazioni();
  }

  private List<Detrazione> fetchDetrazioni() {
    logger.info("fetchDetrazioni");
    String subDirectory = String.format("utente/%d/detrazioni/", getInstance().getUtente().getId());
    List<Detrazione> data;
    try {
      data = Connection.getArrayDataFromBackend(subDirectory, Detrazione.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return data;
  }

  private List<Ordine> fetchOrdini() {
    logger.info("fetchOrdini");
    String subDirectory = String.format("utente/%d/ordini/", getInstance().getUtente().getId());
    List<Ordine> data;
    try {
      data = Connection.getArrayDataFromBackend(subDirectory, Ordine.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (data != null) {
      data.forEach(Ordine::transformIdToObject);
    }
    return data;
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

  public void addListenerPreventivo(PreventivoListener listener) {
    listenersPreventivi.add(listener);
  }

  public void addListenerPreventivoUsato(PreventivoUsatoListener listener) {
    listenersPreventiviUsati.add(listener);
  }

  private List<PreventivoUsato> fetchPreventiviUsati() {
    logger.info("fetchPreventiviUsati");
    String subDirectory =
        String.format("utente/%d/preventiviUsato/", getInstance().getUtente().getId());
    List<PreventivoUsato> data;
    try {
      data = Connection.getArrayDataFromBackend(subDirectory, PreventivoUsato.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (data != null) {
      data.forEach(PreventivoUsato::transformIdToObject);
    }
    return data;
  }

  private void notifyListenersPreventivo() {
    for (PreventivoListener listener : listenersPreventivi) {
      listener.onPreventivoChange(new ArrayList<>(preventivi));
    }
  }

  private void notifyListenersPreventivoUsato() {
    for (PreventivoUsatoListener listener : listenersPreventiviUsati) {
      listener.onPreventivoChange(new ArrayList<>(preventiviUsati));
    }
  }

  public interface PreventivoListener {
    void onPreventivoChange(List<Preventivo> preventivi);

    void onPreventivoAdded(Preventivo preventivo);
  }

  public interface PreventivoUsatoListener {
    void onPreventivoChange(List<PreventivoUsato> preventivi);

    void onPreventivoAdded(PreventivoUsato preventivo);
  }
}
