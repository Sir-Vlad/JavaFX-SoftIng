package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.NotImplemented;
import javafx.scene.input.MouseEvent;

public class UsatoController {
  public void switchHome(MouseEvent mouseEvent) {
    ScreenController.activate("home");
    mouseEvent.consume();
  }

  public void switchConcessionari(MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    //    ScreenController.activate("concessionari");
    mouseEvent.consume();
  }

  public void switchVendi(MouseEvent mouseEvent) {
    ScreenController.activate("vendiUsato");
    mouseEvent.consume();
  }

  public void switchCompra(MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    //    ScreenController.activate("compra");
    mouseEvent.consume();
  }
}
