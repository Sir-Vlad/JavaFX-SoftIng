package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.util.StaticDataStore;
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
  @FXML private MFXScrollPane scrollPane;

  @Override
  public void setCardAuto() {
    getCardAuto()
        .setAll(
            StaticDataStore.getAutoUsate().stream()
                .filter(autoUsata -> autoUsata.getPrezzo() > 0 && !autoUsata.isVenduta())
                .toList());
  }

  public void updatePage() {
    try {
      StaticDataStore.fetchAutoUsate();
    } catch (Exception e) {
      logger.warning("Errore durante l'aggiornamento della lista");
      logger.log(Level.SEVERE, e.getMessage(), e);
    }

    Platform.runLater(
        () -> {
          logger.info("Aggiornamento della pagina scegli usato");
          getFlowPane().getChildren().clear();
          setCardAuto();
          getCardAuto().stream()
              .map(CardAuto::new)
              .forEach(auto -> getFlowPane().getChildren().add(auto));
        });
  }
}
