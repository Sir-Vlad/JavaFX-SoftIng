package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.util.StaticDataStore;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class ScegliUsatoController extends ScegliAuto<AutoUsata> {
  @FXML private AnchorPane root;
  @FXML private MFXScrollPane scrollPane;

  @Override
  public void setCardAuto() {
    getCardAuto()
        .setAll(
            StaticDataStore.getAutoUsate().stream()
                .filter(autoUsata -> autoUsata.getPrezzo() > 0)
                .toList());
  }
}
