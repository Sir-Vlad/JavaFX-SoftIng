package it.prova.javafxsofting.controller.scegli_conf_auto;

import static it.prova.javafxsofting.util.Util.capitalize;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.controller.ScreenController;
import it.prova.javafxsofting.data_manager.DataManager;
import it.prova.javafxsofting.models.*;
import it.prova.javafxsofting.models.Optional;
import it.prova.javafxsofting.util.ColoriAuto;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ConfiguratorController implements Initializable {
  /** path della cartella dove ci sono i file del configuratore */
  private static final Path PATH_DIR = Path.of("controller").resolve("part_configurator");

  /** Flag per l'aggiunta della detrazione nel preventivo */
  @Getter @Setter private static boolean detrazione = false;

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
  @FXML private StackPane logoMarca;
  @FXML private Text fieldModello;
  @FXML private Text fieldMarca;
  @FXML private Text fieldModelloV;
  @FXML private MFXListView<Concessionario> listConcessionaria;
  @FXML private MFXButton saveConfigurazioneBtn;

  /** Auto selezionata all'interno del configuratore */
  private ModelloAuto auto;

  /**
   * Restituisce una lista di tutti i vBox degli optional all'interno di {@link #vboxOptionals}
   *
   * @return la lista contenete i vbox
   */
  @Contract(" -> new")
  private @NotNull List<VBox> getvBoxOptional() {
    return new ArrayList<>(
        vboxOptionals.getChildren().stream()
            .filter(VBox.class::isInstance)
            .map(VBox.class::cast)
            .toList());
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
          "-fx-background-image: url("
              + urlImage
              + "); -fx-background-repeat: no-repeat; -fx-background-position: center center");
    }
    scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

    setDatiTecnici();
    createToggleMotorizzazioneOptions(auto);
    createToggleAlimentazione(auto);
    createToggleCambioOptions(auto);
    createToggleCerchi(auto);
    createToggleColorOptions(auto);
    createToggleOptionals(auto);
    createListViewConcessionario();
    createChooseButtonDetrazioneUsato();

    // immagine dell'auto a destra
    String resource =
        auto.getImmagini().isEmpty()
            ? "immagini/fake-account.png"
            : auto.getImmagini().getFirst().toURI().toString();

    ImageView imageView = new ImageView(new Image(resource));
    imageView.setPreserveRatio(true);
    imageView.fitHeightProperty().bind(modelVisualize.heightProperty());
    imageView.fitWidthProperty().bind(modelVisualize.widthProperty());

    modelVisualize.getChildren().add(imageView);
  }

  /**
   * Metodo che salva la configurazione
   *
   * @param actionEvent l'azione dell'utente che ha generato l'azione del bottone
   */
  @FXML
  public void salvaConfigurazione(@NotNull ActionEvent actionEvent) {
    if (listConcessionaria.getSelectionModel().getSelectedValue() == null) {
      new Alert(Alert.AlertType.ERROR, "Seleziona un concessionario").showAndWait();
      return;
    }

    if (UserSession.getInstance().getUtente() == null) {
      ScreenController.activate("login");
      return;
    }
    Preventivo preventivo;
    if (detrazione) {
      preventivo = createPreventivoSenzaData();
    } else {
      preventivo = createPreventivo();
    }

    addCheckOptionalToMeasure(preventivo);

    try {
      Connection.postDataToBacked(
          preventivo,
          String.format("utente/%s/preventivi/", UserSession.getInstance().getUtente().getId()));
    } catch (Exception e) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Errore");
      alert.setHeaderText("Errore durante l'inserimento del preventivo");
      alert.setContentText("Il preventivo esiste già");
      alert.showAndWait();
      return;
    }

    new Thread(() -> UserSession.getInstance().setPreventivi()).start();

    ScreenController.removeScreen("configurazione");
    ScreenController.activate("home");

    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Informazione");
    alert.setHeaderText("Preventivo inserito correttamente");
    alert.setContentText("Il preventivo è stato inserito correttamente");
    alert.showAndWait();

    actionEvent.consume();
  }

  private @NotNull Preventivo createPreventivoSenzaData() {
    Utente utente = UserSession.getInstance().getUtente();
    Concessionario concessionario = listConcessionaria.getSelectionModel().getSelectedValue();

    return new Preventivo(utente, auto, concessionario, extractPrezzo());
  }

  /**
   * Restituisce il prezzo dell'auto dal campo di testo
   *
   * @return il prezzo totale dell'auto in intero
   */
  @SneakyThrows
  private int extractPrezzo() {
    Text prezzo = (Text) root.lookup("#fieldPrezzoValue");
    return decimalFormat.parse(prezzo.getText().split(" ")[0]).intValue();
  }

  /** Apri la schermata per la vendita di un'auto usato */
  private void openVendiUsato() {
    ScreenController.addScreen(
        "vendiUsato", new FXMLLoader(App.class.getResource("controller/vendiUsato.fxml")));

    ScreenController.activate("vendiUsato");
  }

  /**
   * Crea i toggle button per i colori dell'auto
   *
   * @param auto l'auto selezionata
   */
  private void createToggleColorOptions(@NotNull ModelloAuto auto) {
    List<Optional> colorOptionals =
        Arrays.stream(auto.getOptionals())
            .filter(optional -> "colore".equals(optional.getNome()))
            .toList();

    ColoriAuto colorUtils = new ColoriAuto();
    List<String> nomiColori = colorOptionals.stream().map(Optional::getDescrizione).toList();
    ToggleGroup toggleGroup = new ToggleGroup();

    List<Color> colorList =
        nomiColori.stream().map(s -> colorUtils.getColor(s.split(" - ")[0])).toList();

    for (Color color : colorList) {
      String colorName = colorUtils.getNameColor(color);
      MFXRectangleToggleNode button = createButton(auto, color, colorName, toggleGroup);
      toggleColor.getChildren().add(button);
    }
  }

  /**
   * Crea i toggle button per il cambio dell'auto
   *
   * @param auto l'auto selezionata
   */
  private void createToggleCambioOptions(@NotNull ModelloAuto auto) {
    createToggleOption(auto, "cambio", toggleCambio.getChildren());
  }

  /**
   * Crea i toggle button per la motorizzazione dell'auto
   *
   * @param auto l'auto selezionata
   */
  private void createToggleMotorizzazioneOptions(@NotNull ModelloAuto auto) {
    createToggleOption(auto, "motorizzazione", toggleMotorizzazione.getChildren());
  }

  /**
   * Crea i toggle button per la dimensione dei cerchi dell'auto
   *
   * @param auto l'auto selezionata
   */
  private void createToggleCerchi(@NotNull ModelloAuto auto) {
    createToggleOption(auto, "dimensione cerchi", toggleCerchi.getChildren());
  }

  /**
   * Crea i toggle button per il tipo di alimentazione dell'auto
   *
   * @param auto l'auto selezionata
   */
  private void createToggleAlimentazione(@NotNull ModelloAuto auto) {
    createToggleOption(auto, "alimentazione", toggleAlimentazione.getChildren());
  }

  /**
   * Crea i toggle button per gli optional non obbligatori dell'auto
   *
   * @param auto l'auto selezionata
   */
  private void createToggleOptionals(@NotNull ModelloAuto auto) {
    Set<String> excludedFields =
        Set.of("motorizzazione", "alimentazione", "cambio", "dimensione cerchi", "colore");

    // tutti gli optional non obbligatori
    Optional[] optionals =
        Arrays.stream(auto.getOptionals())
            .filter(optional -> !excludedFields.contains(optional.getNome()))
            .toArray(Optional[]::new);

    // map che collega il nome dell'optional con i valori che gli sono stati assegnati
    HashMap<String, List<Optional>> optionalMap =
        Arrays.stream(optionals)
            .collect(Collectors.groupingBy(Optional::getNome, HashMap::new, Collectors.toList()));

    // creazione della box nel configurator per ogni optional
    for (String key : optionalMap.keySet()) {
      VBox vBox = new VBox();
      vBox.setSpacing(15);

      Label label = new Label(capitalize(key));
      label.setFont(new Font("System", 15));
      label.getStyleClass().addAll("text-field-font", "name-optional");

      FlowPane flowPane = new FlowPane();
      flowPane.setHgap(25);
      flowPane.setVgap(15);

      vBox.getChildren().addAll(label, flowPane);
      createToggleOption(auto, key, flowPane.getChildren());
      vboxOptionals.getChildren().add(vBox);
    }
  }

  /**
   * Aggiunge gli optionals selezionati al preventivo
   *
   * @param preventivo istanza del preventivo
   */
  private void addCheckOptionalToMeasure(@NotNull Preventivo preventivo) {
    List<Optional> checkOptionals = getSelectedOptionals(getvBoxOptional());
    preventivo.setOptionals(checkOptionals);
  }

  /**
   * Crea un preventivo da configurare
   *
   * @return il preventivo da configurare
   */
  @Contract(" -> new")
  private @NotNull Preventivo createPreventivo() {
    Utente utente = UserSession.getInstance().getUtente();
    Concessionario concessionario = listConcessionaria.getSelectionModel().getSelectedValue();
    LocalDate currentDate = LocalDate.now();

    return new Preventivo(utente, auto, concessionario, currentDate, extractPrezzo());
  }

  /**
   * Restituisce la lista degli optional selezionati
   *
   * @param vBoxOptional la lista degli vBox all'interno di {@link #vboxOptionals}
   * @return la lista degli optional selezionati
   */
  private List<Optional> getSelectedOptionals(@NotNull List<VBox> vBoxOptional) {
    return vBoxOptional.stream()
        .flatMap(vBox -> vBox.getChildren().stream())
        .filter(FlowPane.class::isInstance)
        .map(FlowPane.class::cast)
        .flatMap(flowPane -> flowPane.getChildren().stream())
        .filter(
            node2 -> node2 instanceof MFXRectangleToggleNode toggleNode && toggleNode.isSelected())
        .map(MFXRectangleToggleNode.class::cast)
        .map(Node::getUserData)
        .map(Optional.class::cast)
        .toList();
  }

  /**
   * Crea un bottone per selezionare il colore dell'auto e cambia il colore dell'immagine dell'auto
   *
   * @param auto l'auto da configurare
   * @param color il colore dell'immagine
   * @param colorName il nome del colore
   * @param toggleGroup il toggleGroup
   * @return il bottone per l'immagine dell'auto
   */
  private @NotNull MFXRectangleToggleNode createButton(
      @NotNull ModelloAuto auto, Color color, String colorName, ToggleGroup toggleGroup) {
    MFXRectangleToggleNode button = new MFXRectangleToggleNode(colorName);
    button.setToggleGroup(toggleGroup);
    button.setUserData(color);
    button.setStyle(getBackgroundColor(color));
    button.setTextFill(getFontColor(colorName));

    button.setOnAction(event -> updateImageView(auto, colorName));
    return button;
  }

  /**
   * Restituisce il background color in base al colore
   *
   * @param color il colore del background
   * @return la stringa css per modificare il background
   */
  private @NotNull String getBackgroundColor(@NotNull Color color) {
    return "-fx-background-color: #" + color.toString().substring(2, 8) + ";";
  }

  /**
   * Restituisce il color del testo in base al colore
   *
   * @param colorName il nome del colore
   * @return il color del testo
   */
  private Paint getFontColor(String colorName) {
    return "BLACK".equals(colorName) ? Paint.valueOf("WHITE") : Paint.valueOf("BLACK");
  }

  /**
   * Aggiorna l'immagine dell'auto con l'immagine corrispondente al colore selezionato
   *
   * @param auto l'auto selezionata
   * @param colorName il nome del colore dell'immagine dell'auto da aggiornare
   */
  private void updateImageView(@NotNull ModelloAuto auto, String colorName) {
    String urlPath =
        auto.getImmagini().stream()
            .filter(file -> file.getName().startsWith(colorName))
            .findFirst()
            .map(File::toURI)
            .map(URI::toString)
            .orElse("");

    ImageView imageView = new ImageView(new Image(urlPath));
    imageView.setPreserveRatio(true);
    imageView.fitHeightProperty().bind(modelVisualize.heightProperty());
    imageView.fitWidthProperty().bind(modelVisualize.widthProperty());

    modelVisualize.getChildren().clear();
    modelVisualize.getChildren().add(imageView);
  }

  /** Crea la lista delle concessionarie */
  private void createListViewConcessionario() {
    listConcessionaria.setItems(
        FXCollections.observableArrayList(DataManager.getInstance().getConcessionari()));
    listConcessionaria.setConverter(
        FunctionalStringConverter.to(
            concessionario -> {
              if (concessionario != null) {
                Indirizzo indirizzo = concessionario.getIndirizzo();
                return String.format(
                    "%s - %s, %s - %s",
                    concessionario.getNome(),
                    indirizzo.getVia(),
                    indirizzo.getCivico(),
                    indirizzo.getCitta());
              }
              return "";
            }));
    listConcessionaria.setCellFactory(
        concessionario -> new ConcessionarioCellFactory(listConcessionaria, concessionario));
    listConcessionaria.features().enableBounceEffect();
    listConcessionaria.features().enableSmoothScrolling(0.5);
  }

  /**
   * Crea i toggle button per a seconda di {@code field} e li aggiunge a {@code toggle}
   *
   * @param auto l'auto selezionata
   * @param field il nome del campo per il quale creare i toggle button
   * @param toggle box dei toggle
   */
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
      Optional optional =
          Arrays.stream(optionals).filter(o -> nome.equals(o.getDescrizione())).toList().getFirst();
      toggleNode.setUserData(optional);
      toggle.add(toggleNode);
    }

    toggleGroup
        .selectedToggleProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // se clicco sulla stesso toggle non si leva il selected
              if (newValue == null && oldValue != null) {
                toggleGroup.selectToggle(oldValue);
              }
              // Recupero il valore del campo dove mostro il prezzo
              Text prezzo = (Text) root.lookup("#fieldPrezzoValue");
              int newPrezzo = calculateNewPrice(oldValue, newValue, prezzo);
              // Aggiorno il valore del campo di testo
              prezzo.setText(decimalFormat.format(newPrezzo) + " €");
            });
    toggleGroup.getToggles().getFirst().setSelected(true);
  }

  /**
   * Calcola il nuovo prezzo in base all'opzione selezionata
   *
   * @param oldValue l'opzione precedente
   * @param newValue l'opzione selezionata
   * @param prezzo il campo dove mostrare il prezzo
   * @return il nuovo prezzo
   */
  private int calculateNewPrice(Toggle oldValue, Toggle newValue, @NotNull Text prezzo) {
    int newPrezzo;
    try {
      newPrezzo = decimalFormat.parse(prezzo.getText().split(" ")[0]).intValue();
    } catch (ParseException e) {
      throw new RuntimeException("Fallita il parsing del valore del prezzo", e);
    }
    // sottraggo il valore del vecchio toggle
    if (oldValue != null) {
      ((MFXRectangleToggleNode) oldValue).getStyleClass().remove("toggle-button-selected");
      int oldTogglePrezzo = ((Optional) oldValue.getUserData()).getPrezzo();
      newPrezzo -= oldTogglePrezzo;
    }
    // aggiungo il valore del nuovo toggle
    if (newValue != null) {
      ((MFXRectangleToggleNode) newValue).getStyleClass().add("toggle-button-selected");
      int newTogglePrezzo = ((Optional) newValue.getUserData()).getPrezzo();
      newPrezzo += newTogglePrezzo;
    }
    return newPrezzo;
  }

  /** Crea dei radio button per la detrazione usato */
  private void createChooseButtonDetrazioneUsato() {
    ToggleGroup toggleGroup = new ToggleGroup();

    MFXRadioButton yesBtn = new MFXRadioButton("Yes");
    MFXRadioButton noBtn = new MFXRadioButton("No");

    yesBtn.setToggleGroup(toggleGroup);
    yesBtn.setOnAction(
        event -> {
          if (yesBtn.isSelected()) {
            detrazione = true;
            openVendiUsato();
          }
        });

    noBtn.setToggleGroup(toggleGroup);
    noBtn.setOnAction(event -> detrazione = false);
    noBtn.setSelected(true);

    flagDetrazione.getChildren().addAll(yesBtn, noBtn);
  }

  /**
   * Crea la box per il prezzo dell'auto nell'header
   *
   * @param auto l'auto selezionata
   */
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

  /** Carica l'fxml dei dati tecnici e li setta con i dati dell'auto selezionata */
  private void setDatiTecnici() {
    HBox hBox;
    try {
      hBox =
          FXMLLoader.load(
              Objects.requireNonNull(
                  App.class.getResource(PATH_DIR.resolve("dati_tecnici.fxml").toString())));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    infoTecnicheBox.getChildren().add(hBox);

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

  /** Cell factory per la lista dei concessionari */
  private static class ConcessionarioCellFactory extends MFXListCell<Concessionario> {

    /** Icona del concessionario */
    private final MFXFontIcon icon;

    /**
     * Costruttore della cella per il concessionario
     *
     * @param listView la listView dove aggiungere il concessionario
     * @param concessionario il concessionario da mostrare
     */
    public ConcessionarioCellFactory(
        MFXListView<Concessionario> listView, Concessionario concessionario) {
      super(listView, concessionario);

      icon = new MFXFontIcon("fas-car", 18);
      render(concessionario);
    }

    /**
     * Renderizza il concessionario e aggiunge l'icona a destra
     *
     * @param data il concessionario
     */
    @Override
    protected void render(Concessionario data) {
      super.render(data);
      if (icon != null) {
        getChildren().addFirst(icon);
      }
    }
  }
}
