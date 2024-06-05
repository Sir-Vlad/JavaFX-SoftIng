package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.util.StaticDataStore;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class ScegliUsatoController extends ScegliAuto<AutoUsata> {
  @FXML private AnchorPane root;
  @FXML private MFXScrollPane scrollPane;
  @FXML private MFXFilterComboBox<String> alimentazioneFilter;
  @FXML private MFXFilterComboBox<String> cambioFilter;

  @Override
  public void setCardAuto() {
    getCardAuto().setAll(StaticDataStore.getAutoUsate());
  }
}
