package it.prova.javafxsofting.controller;

import static it.prova.javafxsofting.util.Util.capitalize;

import io.github.palexdev.materialfx.controls.*;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Optional;
import it.prova.javafxsofting.util.ColoriAuto;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class ConfiguratorController implements Initializable {
  private static final String PATH_DIR = "controller/part_configurator/";
  private final DecimalFormat decimalFormat = new DecimalFormat("###,###");
  @FXML private VBox vboxOptionals;
  @FXML private VBox infoTecnicheBox;
  @FXML private HBox flagDetrazione;
  @FXML private Header header;
  @FXML private AnchorPane root;
  @FXML private HBox toggleColor;
  @FXML private FlowPane toggleMotorizzazione;
  @FXML private HBox toggleCambio;
  @FXML private FlowPane toggleAlimentazione;
  @FXML private FlowPane toggleCerchi;
  @FXML private Pane modelVisualize;
  @FXML private MFXScrollPane scrollPane;
  @FXML private VBox logoMarca;
  @FXML private Text fieldModello;
  @FXML private Text fieldMarca;
  @FXML private Text fieldModelloV;
  @FXML private MFXButton saveConfigurazioneBtn;

  private ModelloAuto auto;
  private boolean detrazione = false;

  private void setColorOptions(@NotNull ModelloAuto auto) {
    Optional[] colori =
        Arrays.stream(auto.getOptionals())
            .filter(optional -> Objects.equals(optional.getNome(), "colore"))
            .toArray(Optional[]::new);

    ColoriAuto coloriAuto = new ColoriAuto();
    List<String> nomiColori = Arrays.stream(colori).map(Optional::getDescrizione).toList();
    ToggleGroup toggleGroup = new ToggleGroup();

    ArrayList<Color> colorList =
        nomiColori.stream()
            .map(s -> coloriAuto.getColor(s.split(" - ")[0]))
            .collect(Collectors.toCollection(ArrayList::new));

    for (Color color : colorList) {
      MFXRectangleToggleNode button = new MFXRectangleToggleNode(coloriAuto.getNameColor(color));
      button.setToggleGroup(toggleGroup);
      button.setUserData(color);
      String hexValue = "#" + color.toString().split("0x")[1].substring(0, 6);
      button.setStyle("-fx-background-color: " + hexValue + ";");
      if ("BLACK".equals(coloriAuto.getNameColor(color))) {
        button.setTextFill(Paint.valueOf("WHITE"));
      }

      button.setOnAction(
          event -> {
            String urlPath =
                auto.getImmagini().stream()
                    .filter(
                        file ->
                            file.getName()
                                .startsWith(Objects.requireNonNull(coloriAuto.getNameColor(color))))
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

  private void setCambioOptions(@NotNull ModelloAuto auto) {
    createToggleOption(auto, "cambio", toggleCambio.getChildren());
  }

  private void setMotorizzazioneOptions(@NotNull ModelloAuto auto) {
    createToggleOption(auto, "motorizzazione", toggleMotorizzazione.getChildren());
  }

  private void setCerchi(@NotNull ModelloAuto auto) {
    createToggleOption(auto, "dimensione cerchi", toggleCerchi.getChildren());
  }

  private void setAlimentazione(@NotNull ModelloAuto auto) {
    createToggleOption(auto, "alimentazione", toggleAlimentazione.getChildren());
  }

  private void setOptionals(@NotNull ModelloAuto auto) {
    Set<String> fieldExclude =
        Set.of("motorizzazione", "alimentazione", "cambio", "dimensione cerchi", "colore");
    // tutti gli optional non obbligatori
    Optional[] optionals =
        Arrays.stream(auto.getOptionals())
            .filter(
                optional ->
                    fieldExclude.stream().noneMatch(s -> Objects.equals(optional.getNome(), s)))
            .toArray(Optional[]::new);
    // nomi univoci degli optionals
    Set<String> keyOpt =
        Arrays.stream(optionals).map(Optional::getNome).collect(Collectors.toSet());
    // map che collega il nome dell'optional con i valori che gli sono stati assegnati
    HashMap<String, List<Optional>> opt =
        keyOpt.stream()
            .collect(Collectors.toMap(s -> s, s -> new ArrayList<>(), (a, b) -> b, HashMap::new));

    opt.keySet()
        .forEach(
            key ->
                Arrays.stream(optionals)
                    .filter(optional -> optional.getNome().equals(key))
                    .forEachOrdered(optional -> opt.get(key).add(optional)));

    // creazione della box nel configurator per ogni optional
    for (String key : opt.keySet()) {
      VBox vBox = new VBox();
      vBox.setSpacing(15);

      Label label = new Label(capitalize(key));
      label.setFont(new Font("System", 15));

      FlowPane flowPane = new FlowPane();
      flowPane.setHgap(25);
      flowPane.setVgap(15);

      vBox.getChildren().addAll(label, flowPane);
      createToggleOption(auto, key, flowPane.getChildren());
      vboxOptionals.getChildren().add(vBox);
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
    setMotorizzazioneOptions(auto);
    setAlimentazione(auto);
    setCambioOptions(auto);
    setCerchi(auto);
    setColorOptions(auto);
    setOptionals(auto);
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

  private void createToggleOption(
      @NotNull ModelloAuto auto, String field, ObservableList<Node> toggle) {
    Optional[] optionals =
        Arrays.stream(auto.getOptionals())
            .filter(optional -> Objects.equals(optional.getNome(), field))
            .toArray(Optional[]::new);

    List<String> nomiOptionals = Arrays.stream(optionals).map(Optional::getDescrizione).toList();

    ToggleGroup toggleGroup = new ToggleGroup();
    for (String nome : nomiOptionals) {
      MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(nome);
      toggleNode.setToggleGroup(toggleGroup);
      int prezzo =
          Arrays.stream(optionals)
              .filter(optional -> Objects.equals(optional.getDescrizione(), nome))
              .map(Optional::getPrezzo)
              .toList()
              .getFirst();
      toggleNode.setUserData(prezzo);
      toggle.add(toggleNode);
    }

    toggleGroup
        .selectedToggleProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // Recupero il valore del campo dove mostro il prezzo
              Text prezzo = (Text) root.lookup("#fieldPrezzoValue");
              int newPrezzo;
              try {
                newPrezzo = decimalFormat.parse(prezzo.getText().split(" ")[0]).intValue();
              } catch (ParseException e) {
                throw new RuntimeException("Fallita il parsing del valore del prezzo", e);
              }
              // sottraggo il valore del vecchio toggle
              if (oldValue != null) {
                int oldTogglePrezzo = (int) oldValue.getUserData();
                newPrezzo -= oldTogglePrezzo;
              }
              // aggiungo il valore del nuovo toggle
              if (newValue != null) {
                int newTogglePrezzo = (int) newValue.getUserData();
                newPrezzo += newTogglePrezzo;
              }
              // Aggiorno il valore del campo di testo
              prezzo.setText(decimalFormat.format(newPrezzo) + " €");
            });
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

    Text fieldAltezza = (Text) hBox.lookup("#fieldAltezza");
    Text fieldLarghezza = (Text) hBox.lookup("#fieldLarghezza");
    Text fieldLunghezza = (Text) hBox.lookup("#fieldLunghezza");
    Text fieldPeso = (Text) hBox.lookup("#fieldPeso");
    Text fieldVolBagagliaio = (Text) hBox.lookup("#fieldVolBagagliaio");

    fieldModelloV.setText(auto.getModello());
    fieldMarca.setText(String.valueOf(auto.getMarca()));
    fieldModello.setText(auto.getModello());

    fieldAltezza.setText(auto.getAltezza() + " mm");
    fieldLarghezza.setText(auto.getLarghezza() + " mm");
    fieldLunghezza.setText(auto.getLunghezza() + " mm");
    fieldPeso.setText(auto.getPeso() + " kg");
    fieldVolBagagliaio.setText(auto.getVolumeBagagliaio() + " L");
  }
}
