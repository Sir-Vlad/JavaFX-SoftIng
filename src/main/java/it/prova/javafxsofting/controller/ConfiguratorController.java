package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.*;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.NotImplemented;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.models.Configurazione;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Preventivo;
import it.prova.javafxsofting.models.Sede;
import it.prova.javafxsofting.util.ColoriAuto;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

public class ConfiguratorController implements Initializable {
  @FXML private Header header;
  @FXML private AnchorPane root;
  @FXML private HBox toggleColor;
  @FXML private MFXScrollPane scrollPane;
  @FXML private Pane logoMarca;
  @FXML private Text fieldModello;
  @FXML private Text fieldMarca;
  @FXML private Text fieldModelloV;
  @FXML private Text fieldAlimentazione;
  @FXML private Text fieldCambio;
  @FXML private Text fieldAltezza;
  @FXML private Text fieldLarghezza;
  @FXML private Text fieldLunghezza;
  @FXML private Text fieldPeso;
  @FXML private Text fieldVolBagagliaio;
  @FXML private Pane modelVisualize;
  @FXML private MFXButton saveConfigurazioneBtn;

  private ModelloAuto auto;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    auto = ScegliModelloController.getAutoSelezionata();
    String config = "config";
    if (auto == null) {
      ScreenController.removeScreen(config);
      ScreenController.activate("scegliModello");
      return;
    }

    header.addTab(
        "Home",
        event -> {
          ScreenController.removeScreen(config);
          ScreenController.activate("home");
        });
    header.addTab(
        "Cambia Modello",
        event -> {
          ScreenController.removeScreen(config);
          ScreenController.activate("scegliModello");
        });

    createBoxPrezzo(auto);

    String pathImage =
        "immagini/loghi_marche/logo-" + auto.getMarca().toString().toLowerCase() + ".png";
    URL urlImage = App.class.getResource(pathImage);
    if (urlImage != null) {
      logoMarca.setStyle(
          "-fx-background-image: url(" + urlImage + "); -fx-background-repeat: no-repeat");
    }

    fieldModelloV.setText(auto.getModello());
    fieldMarca.setText(String.valueOf(auto.getMarca()));
    fieldModello.setText(auto.getModello());

    fieldAlimentazione.setText(auto.getOptionals()[0].getDescrizione());

    fieldAltezza.setText(auto.getAltezza() + " mm");
    fieldLarghezza.setText(auto.getLarghezza() + " mm");
    fieldLunghezza.setText(auto.getLunghezza() + " mm");
    fieldPeso.setText(auto.getPeso() + " kg");
    fieldVolBagagliaio.setText(auto.getVolumeBagagliaio() + " L");

    scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

    // immagine per visualizzare qualcosa
    String resource;
    if (auto.getImmagini().isEmpty()) {
      resource = "immagini/fake-account.png";
    } else {
      resource = auto.getImmagini().getFirst().toURI().toString();
    }

    ImageView imageView = new ImageView(new Image(resource));
    imageView.setPreserveRatio(true);

    imageView.fitHeightProperty().bind(modelVisualize.heightProperty());
    imageView.fitWidthProperty().bind(modelVisualize.widthProperty());

    modelVisualize.getChildren().add(imageView);

    createToggleButton(auto);
  }

  @FXML
  public void salvaConfigurazione(@NotNull ActionEvent actionEvent) {
    NotImplemented.notImplemented();

    // todo: validare i campi obligatori
    //  creare il preventivo e inviarlo al db, ottenere l'id e creare la configurazione
    //  e inviarla al db

    Configurazione config = new Configurazione();

    Preventivo preventivo = new Preventivo(App.getUtente(), auto, new Sede(), LocalDate.now());

    actionEvent.consume();
  }

  private void createBoxPrezzo(@NotNull ModelloAuto auto) {
    VBox vbox = new VBox();
    vbox.setPrefWidth(200);
    vbox.setAlignment(Pos.CENTER);

    Text fieldPrezzo = new Text("Prezzo");
    Text fieldPrezzoValue = new Text();
    fieldPrezzoValue.setId("fieldPrezzoValue");
    fieldPrezzoValue.setStyle("-fx-font-weight: bold; -fx-font-size: 20");

    vbox.getChildren().addAll(fieldPrezzo, fieldPrezzoValue);

    int index = header.getChildren().size() - 1;
    header.getChildren().add(index, vbox);

    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    fieldPrezzoValue.setText(decimalFormat.format(auto.getPrezzoBase()) + " €");
  }

  private void createToggleButton(ModelloAuto auto) {
    // todo: quando ho i colori della macchina passarli come argomento e generare i colori così
    ColoriAuto coloriAuto = new ColoriAuto();
    ToggleGroup toggleGroup = new ToggleGroup();

    ArrayList<Color> colorList = new ArrayList<>();
    colorList.add(Color.BLACK);
    colorList.add(Color.GRAY);
    colorList.add(Color.WHITE);

    for (Color color : colorList) {
      MFXRectangleToggleNode button = new MFXRectangleToggleNode(coloriAuto.getNameColor(color));
      button.setToggleGroup(toggleGroup);
      button.setUserData(color);
      String hexValue = "#" + color.toString().split("0x")[1].substring(0, 6);
      button.setStyle("-fx-background-color: " + hexValue + ";");

      button.setOnAction(
          event -> {
            String urlPath =
                auto.getImmagini().stream()
                    .filter(file -> file.getName().startsWith(coloriAuto.getNameColor(color)))
                    .toList()
                    .getFirst()
                    .toURI()
                    .toString();
            ImageView imageView = new ImageView(new Image(urlPath));
            imageView.setPreserveRatio(true);

            imageView.fitHeightProperty().bind(modelVisualize.heightProperty());
            imageView.fitWidthProperty().bind(modelVisualize.widthProperty());

            modelVisualize.getChildren().clear();
            modelVisualize.getChildren().add(imageView);
          });

      toggleColor.getChildren().add(button);
    }
  }
}
