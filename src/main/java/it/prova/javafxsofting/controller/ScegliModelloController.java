package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.ModelloAuto;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.component.ProfileBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import lombok.SneakyThrows;

public class ScegliModelloController implements Initializable {

  private final ModelloAuto autoSelezionata =
      new ModelloAuto(0, "Skyline R-34 GTT", "Nissan", 80000, "", 1360, 4600, 1550, 180);
  public AnchorPane root;
  public MFXScrollPane scrollPane;
  public FlowPane flowPane;
  public ProfileBox profile;

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    for (int i = 0; i < 20; i++) {
      CardAuto auto =
          new CardAuto("Auto Bella", "GPL", 78000, App.class.getResource("immagini/car.jpeg"));

      flowPane.getChildren().addAll(auto);
    }
  }

  public void switchHome(MouseEvent mouseEvent) {
    ScreenController.activate("home");
    mouseEvent.consume();
  }
}
