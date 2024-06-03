package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.NotImplemented;
import it.prova.javafxsofting.component.Header;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;

public class UsatoController implements Initializable {
  @FXML private Header header;

  @SneakyThrows
  public void switchVendi(MouseEvent mouseEvent) {
    ScreenController.addScreen(
        "vendiUsato",
        FXMLLoader.load(
            Objects.requireNonNull(App.class.getResource("controller/vendiUsato.fxml"))));

    ScreenController.activate("vendiUsato");
    mouseEvent.consume();
  }

  @SneakyThrows
  public void switchCompra(MouseEvent mouseEvent) {
    ScreenController.addScreen(
        "scegliUsato",
        FXMLLoader.load(
            Objects.requireNonNull(App.class.getResource("controller/scegliUsato.fxml"))));
    ScreenController.activate("scegliUsato");
    mouseEvent.consume();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Home", event -> ScreenController.activate("home"));
    header.addTab(
        "Concessionari",
        event -> {
          NotImplemented.notImplemented();
          // ScreenController.activate("concessionari");
        });
  }
}
