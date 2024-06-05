package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.*;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.util.ColoriAuto;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class ConfiguratorController implements Initializable {
  private static final String PATH_DIR = "controller/part_configurator/";
  @FXML private VBox infoTecnicheBox;
  @FXML private HBox flagDetrazione;
  @FXML private Header header;
  @FXML private AnchorPane root;
  @FXML private HBox toggleColor;
  @FXML private Pane modelVisualize;
  @FXML private MFXScrollPane scrollPane;
  @FXML private Pane logoMarca;
  @FXML private Text fieldModello;
  @FXML private Text fieldMarca;
  @FXML private Text fieldModelloV;
  @FXML private MFXButton saveConfigurazioneBtn;

  private Text fieldAlimentazione;
  private Text fieldCambio;
  private Text fieldAltezza;
  private Text fieldLarghezza;
  private Text fieldLunghezza;
  private Text fieldPeso;
  private Text fieldVolBagagliaio;

  private ModelloAuto auto;
  private boolean detrazione = false;

  private void setColorOptions(ModelloAuto auto) {
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
    scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

    setDatiTecnici();
    setColorOptions(auto);
    setDetrazioneUsato();

    // immagine dell'auto a destra
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
  }

  @SneakyThrows
  @FXML
  public void salvaConfigurazione(@NotNull ActionEvent actionEvent) {
    if (detrazione) {
      ScreenController.addScreen(
          "vendiUsato",
          FXMLLoader.load(
              Objects.requireNonNull(App.class.getResource("controller/vendiUsato.fxml"))));

      ScreenController.activate("vendiUsato");
    }

    actionEvent.consume();
  }

  private void setDetrazioneUsato() {
    ToggleGroup toggleGroup = new ToggleGroup();

    MFXRadioButton yesBtn = new MFXRadioButton("Yes");
    MFXRadioButton noBtn = new MFXRadioButton("No");

    yesBtn.setToggleGroup(toggleGroup);
    yesBtn.setOnAction(event -> detrazione = true);

    noBtn.setToggleGroup(toggleGroup);
    noBtn.setOnAction(event -> detrazione = false);

    flagDetrazione.getChildren().addAll(yesBtn, noBtn);
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

  private void setDatiTecnici() {
    HBox hBox;
    try {
      hBox =
          FXMLLoader.load(
              Objects.requireNonNull(App.class.getResource(PATH_DIR + "dati_tecnici.fxml")));
      infoTecnicheBox.getChildren().addAll(hBox);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    fieldAlimentazione = (Text) hBox.lookup("#fieldAlimentazione");
    fieldCambio = (Text) hBox.lookup("#fieldCambio");
    fieldAltezza = (Text) hBox.lookup("#fieldAltezza");
    fieldLarghezza = (Text) hBox.lookup("#fieldLarghezza");
    fieldLunghezza = (Text) hBox.lookup("#fieldLunghezza");
    fieldPeso = (Text) hBox.lookup("#fieldPeso");
    fieldVolBagagliaio = (Text) hBox.lookup("#fieldVolBagagliaio");

    fieldModelloV.setText(auto.getModello());
    fieldMarca.setText(String.valueOf(auto.getMarca()));
    fieldModello.setText(auto.getModello());

    fieldAlimentazione.setText(auto.getOptionals()[0].getDescrizione());

    fieldAltezza.setText(auto.getAltezza() + " mm");
    fieldLarghezza.setText(auto.getLarghezza() + " mm");
    fieldLunghezza.setText(auto.getLunghezza() + " mm");
    fieldPeso.setText(auto.getPeso() + " kg");
    fieldVolBagagliaio.setText(auto.getVolumeBagagliaio() + " L");
  }
}
