package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.models.ModelloAuto;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;

public class ScegliModelloController implements Initializable {
  @Getter @Setter private static ModelloAuto autoSelezionata = null;

  public AnchorPane root;
  public MFXScrollPane scrollPane;
  public FlowPane flowPane;
  public Header header;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Home", event -> ScreenController.activate("home"));
    List<ModelloAuto> modelliAuto;
    try {
      modelliAuto = Connection.getArrayDataFromBackend("modelli/", ModelloAuto.class);
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR, e.getMessage());
      alert.setHeaderText("Errore del server");
      alert.showAndWait();
      Platform.exit();
      throw new RuntimeException(e);
    }
    if (modelliAuto != null) {
      modelliAuto.stream().map(CardAuto::new).forEach(auto -> flowPane.getChildren().addAll(auto));
    }
  }
}
