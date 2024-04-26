package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class HomeController implements Initializable {
  @FXML private VBox wrapperNuovo;
  @FXML private VBox wrapperConcessionari;
  @FXML private VBox wrapperUsato;
  @FXML private VBox wrapperPreventivo;
  @FXML private VBox wrapperRoot;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    BackgroundImage bg =
        new BackgroundImage(
            new Image(String.valueOf(App.class.getResource("immagini/car.jpeg"))),
            BackgroundRepeat.REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);

    wrapperRoot.setBackground(new Background(bg));
  }

  public void switchNuovo(MouseEvent mouseEvent) {
    ScreenController.activate("scegliModello");
    mouseEvent.consume();
  }

  public void switchConcessionari(MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    //    ScreenController.activate("concessionari");
    mouseEvent.consume();
  }

  public void switchUsato(MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    //    ScreenController.activate("usato");
    mouseEvent.consume();
  }

  public void switchPreventivi(MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    //    ScreenController.activate("preventiviUsato");
    mouseEvent.consume();
  }
}
