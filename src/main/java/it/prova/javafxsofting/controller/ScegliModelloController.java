package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import it.prova.javafxsofting.ModelloAuto;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.component.Header;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class ScegliModelloController implements Initializable {
  @Getter @Setter private static ModelloAuto autoSelezionata = null;

  public AnchorPane root;
  public MFXScrollPane scrollPane;
  public FlowPane flowPane;
  public Header header;

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Home", event -> ScreenController.activate("home"));
    ArrayList<ModelloAuto> modelliAuto = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < 20; i++) {
      modelliAuto.add(
          new ModelloAuto(
              i,
              "Skyline R-34 GTT - " + i,
              "Nissan",
              random.nextInt(80000, 150000),
              "",
              1360,
              4600,
              1550,
              180));
    }

    for (int i = 0; i < 20; i++) {
      CardAuto auto = new CardAuto(modelliAuto.get(i));

      flowPane.getChildren().addAll(auto);
    }
  }

  public void switchHome(MouseEvent mouseEvent) {
    ScreenController.activate("home");
    mouseEvent.consume();
  }
}
