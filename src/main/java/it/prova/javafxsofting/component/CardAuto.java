package it.prova.javafxsofting.component;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.controller.ScegliModelloController;
import it.prova.javafxsofting.controller.ScreenController;
import it.prova.javafxsofting.models.Auto;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.models.ModelloAuto;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CardAuto extends VBox {
  private static final String PATH_DIR = "controller/part_configurator/";
  @FXML VBox rootCardAuto;
  @FXML Label labelNomeAuto;
  @FXML Label labelTipoMotore;
  @FXML Label labelPrezzo;
  @FXML VBox wrapperImagine;

  public CardAuto(Auto auto) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("cardAuto.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    labelNomeAuto.setText(auto.getModello());

    String tipoMotore = "GPL"; // accodato: tipoMotore cardAuto
    labelTipoMotore.setText(tipoMotore);

    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    int prezzo = 0;
    if (auto instanceof ModelloAuto modelloAuto) {
      prezzo = modelloAuto.getPrezzoBase();
    } else if (auto instanceof AutoUsata autoUsata) {
      prezzo = autoUsata.getPrezzo();
    }
    labelPrezzo.setText(decimalFormat.format(prezzo));

    URL imgURL;
    try {
      imgURL = auto.getImmagini().getFirst().toURI().toURL();
    } catch (Exception e) {
      imgURL = App.class.getResource("immagini/car.png");
    }
    wrapperImagine.setStyle("-fx-background-image: url(" + imgURL + "); ");

    rootCardAuto.setOnMouseClicked(
        event -> {
          if (auto instanceof ModelloAuto) {
            ScegliModelloController.setAutoSelezionata((ModelloAuto) auto);

            try {
              ScreenController.addScreen(
                  "config",
                  FXMLLoader.load(
                      Objects.requireNonNull(
                          App.class.getResource(PATH_DIR + "configurator.fxml"))));
            } catch (IOException e) {
              throw new RuntimeException(e);
            }

            ScreenController.activate("config");
          }
          event.consume();
        });
  }
}
