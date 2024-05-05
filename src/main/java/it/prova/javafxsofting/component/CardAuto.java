package it.prova.javafxsofting.component;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.ModelloAuto;
import it.prova.javafxsofting.controller.ScegliModelloController;
import it.prova.javafxsofting.controller.ScreenController;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CardAuto extends VBox {
  @FXML VBox rootCardAuto;
  @FXML Label labelNomeAuto;
  @FXML Label labelTipoMotore;
  @FXML Label labelPrezzo;
  @FXML VBox wrapperImagine;

  public CardAuto(ModelloAuto auto) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("cardAuto.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    labelNomeAuto.setText(auto.getNome());

    String tipoMotore = "GPL"; // accodato: tipoMotore cardAuto
    labelTipoMotore.setText(tipoMotore);

    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    labelPrezzo.setText(decimalFormat.format(auto.getPrezzoBase()));

    URL imgUrl = App.class.getResource("immagini/car.jpeg"); // accodato: imgUrl cardAuto
    wrapperImagine.setStyle("-fx-background-image: url(" + imgUrl + ")");

    rootCardAuto.setOnMouseClicked(
        event -> {
          ScegliModelloController.setAutoSelezionata(auto);

          try {
            ScreenController.addScreen(
                "config",
                FXMLLoader.load(
                    Objects.requireNonNull(App.class.getResource("controller/configurator.fxml"))));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          ScreenController.activate("config");
          event.consume();
        });
  }
}
