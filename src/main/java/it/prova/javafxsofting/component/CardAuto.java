package it.prova.javafxsofting.component;

import it.prova.javafxsofting.controller.ScreenController;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
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

  public CardAuto(String nome, String tipoMotore, int prezzo, URL imgUrl) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("cardAuto.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    labelNomeAuto.setText(nome);
    labelTipoMotore.setText(tipoMotore);

    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    labelPrezzo.setText(decimalFormat.format(prezzo));
    wrapperImagine.setStyle("-fx-background-image: url(" + imgUrl + ")");

    rootCardAuto.setOnMouseClicked(
        event -> {
          ScreenController.activate("config");
          event.consume();
        });
  }
}
