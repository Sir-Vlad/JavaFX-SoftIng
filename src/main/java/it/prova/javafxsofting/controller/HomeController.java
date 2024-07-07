package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.component.Header;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;

public class HomeController implements Initializable {
  @FXML private Header header;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Concessionari", event -> ScreenController.activate("concessionari"));
  }

  public void switchNuovo(@NotNull MouseEvent mouseEvent) {
    ScreenController.activate("scegliModello");
    mouseEvent.consume();
  }

  public void switchUsato(@NotNull MouseEvent mouseEvent) {
    ScreenController.activate("scegliUsato");
    mouseEvent.consume();
  }
}
