package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.NotImplemented;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;

public class UsatoController {
  public void switchHome(MouseEvent mouseEvent) {
    ScreenController.removeScreen("vendiUsato");
    ScreenController.activate("home");
    mouseEvent.consume();
  }

  public void switchConcessionari(MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    //    ScreenController.activate("concessionari");
    mouseEvent.consume();
  }

  @SneakyThrows
  public void switchVendi(MouseEvent mouseEvent) {
    ScreenController.addScreen(
        "vendiUsato",
        FXMLLoader.load(
            Objects.requireNonNull(App.class.getResource("controller/vendiUsato.fxml"))));

    ScreenController.activate("vendiUsato");
    mouseEvent.consume();
  }

  public void switchCompra(MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    //    ScreenController.activate("compra");
    mouseEvent.consume();
  }
}
