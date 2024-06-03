package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXSlider;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.util.FilterAuto;
import it.prova.javafxsofting.util.StaticDataStore;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

public class ScegliUsatoController extends FilterAuto implements Initializable {
  private final ObservableList<AutoUsata> cardAuto = FXCollections.observableArrayList();
  @FXML private AnchorPane root;
  @FXML private Header header;
  @FXML private MFXScrollPane scrollPane;
  @FXML private FlowPane flowPane;
  @FXML private MFXFilterComboBox<String> marcaComboFilter;
  @FXML private MFXSlider sliderMaxPrezzo;
  @FXML private MFXFilterComboBox<String> alimentazioneFilter;
  @FXML private MFXFilterComboBox<String> cambioFilter;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Home", event -> ScreenController.activate("home"));
    header.addTab("Indietro", event -> ScreenController.back());

    cardAuto.setAll(StaticDataStore.getAutoUsate());
    cardAuto.stream().map(CardAuto::new).forEach(auto -> flowPane.getChildren().addAll(auto));

    settingMarcaFilter(marcaComboFilter, flowPane, cardAuto);
    settingPrezzoFilter(sliderMaxPrezzo, flowPane, cardAuto);
  }
}
