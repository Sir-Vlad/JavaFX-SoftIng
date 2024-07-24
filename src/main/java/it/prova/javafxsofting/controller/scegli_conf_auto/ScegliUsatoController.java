package it.prova.javafxsofting.controller.scegli_conf_auto;

import it.prova.javafxsofting.data_manager.DataManager;
import it.prova.javafxsofting.models.AutoUsata;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = false)
public class ScegliUsatoController extends ScegliAuto<AutoUsata> {
  private static final Logger logger = Logger.getLogger(ScegliUsatoController.class.getName());
  @Getter @Setter private static AutoUsata autoSelezionata = null;
  @FXML private AnchorPane root;

  @Override
  public void setCardAuto() {
    if (DataManager.getInstance().getAutoUsate() != null)
      getCardAuto()
          .setAll(
              DataManager.getInstance().getAutoUsate().stream()
                  .filter(autoUsata -> autoUsata.getPrezzo() > 0 && !autoUsata.isVenduta())
                  .toList());
  }

  /** Aggiorna la pagina scegli usato */
  public void updatePage() {
    try {
      DataManager.getInstance().setAutoUsate();
    } catch (Exception e) {
      logger.warning("Errore durante l'aggiornamento della lista");
      logger.log(Level.SEVERE, e.getMessage(), e);
      return;
    }

    Platform.runLater(
        () -> {
          logger.info("Aggiornamento della pagina scegli usato");
          setCardAuto();
          getCardContent().getChildren().clear();
          setPagination(getCardAuto());
        });
  }
}
