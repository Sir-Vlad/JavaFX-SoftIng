package it.prova.javafxsofting.component;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.controller.ScegliModelloController;
import it.prova.javafxsofting.controller.ScegliUsatoController;
import it.prova.javafxsofting.controller.ScreenController;
import it.prova.javafxsofting.models.Auto;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.models.ModelloAuto;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;

import it.prova.javafxsofting.models.Optional;
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

    if (auto instanceof ModelloAuto modelloAuto) {
      Optional tipoMotore =
          Arrays.stream(modelloAuto.getOptionals())
              .filter(optional -> optional.getNome().equals("alimentazione"))
              .findFirst()
              .orElse(null);

      if (tipoMotore != null) {
        labelTipoMotore.setText(tipoMotore.getDescrizione());
      } else {
        labelTipoMotore.setText("");
      }
    }

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
          if (auto instanceof ModelloAuto modelloAuto) {
            ScegliModelloController.setAutoSelezionata(modelloAuto);

            ScreenController.addScreen(
                "config", new FXMLLoader(App.class.getResource(PATH_DIR + "configurator.fxml")));

            ScreenController.activate("config");
          } else if (auto instanceof AutoUsata autoUsata) {
            ScegliUsatoController.setAutoSelezionata(autoUsata);

            ScreenController.addScreen(
                "autoUsataDetail",
                new FXMLLoader(
                    App.class.getResource(
                        Path.of("controller").resolve("auto_usata_detail.fxml").toString())));

            ScreenController.activate("autoUsataDetail");
          }
          event.consume();
        });
  }
}
