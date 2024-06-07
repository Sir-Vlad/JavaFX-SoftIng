package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.FXCollectors;
import io.github.palexdev.materialfx.validation.Constraint;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.models.AutoUsata;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Popup;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class VendiUsato extends ValidateForm implements Initializable {
  static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  static final SecureRandom RANDOM = new SecureRandom();
  private final HashMap<File, File> immagini = new HashMap<>();
  private final Logger logger = Logger.getLogger(VendiUsato.class.getName());
  @FXML private Header header;
  private MFXTextField[] targaField = null;
  @FXML private VBox wrapperRoot;
  @FXML private Label modelloLabel;
  @FXML private Label marcaLabel;
  @FXML private Label kmPercorsiLabel;
  @FXML private Label targaLabel;
  @FXML private Label aaImmatricolazioneLabel;
  @FXML private Label altezzaLabel;
  @FXML private Label lunghezzaLabel;
  @FXML private Label larghezzaLabel;
  @FXML private Label caricaFotoLabel;
  @FXML private Label volBagagliaioLabel;
  @FXML private Label pesoLabel;
  @FXML private Label validateModello;
  @FXML private Label validateMarca;
  @FXML private Label validateKmPercorsi;
  @FXML private Label validateTarga;
  @FXML private Label validateAAImmatricolazione;
  @FXML private Label validateAltezza;
  @FXML private Label validateLunghezza;
  @FXML private Label validateLarghezza;
  @FXML private Label validateVolBagagliaio;
  @FXML private Label validatePeso;
  @FXML private Label validateFoto;
  @FXML private MFXTextField modelloField;
  @FXML private MFXTextField marcaField;
  @FXML private MFXTextField kmPercorsiField;
  @FXML private MFXTextField targaFieldFirstTwoLetter;
  @FXML private MFXTextField targaFieldDigit;
  @FXML private MFXTextField targaFieldLastTwoLetter;
  @FXML private MFXFilterComboBox<String> aaImmatricolazioneCombo;
  @FXML private MFXTextField altezzaField;
  @FXML private MFXTextField lunghezzaField;
  @FXML private MFXTextField larghezzaField;
  @FXML private MFXTextField volBagagliaioField;
  @FXML private MFXTextField pesoField;
  @FXML private MFXButton scegliFotoBtn;
  @FXML private MFXButton infoBtn;
  private Popup popup;
  private Label popupContent;

  private static void alertWarning(String title, String message) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Indietro", event -> ScreenController.activate("config"));

    setBoundsTarga();

    onlyCharAlphabetical(modelloField);
    onlyCharAlphabetical(marcaField);

    onlyFloat(kmPercorsiField);
    onlyFloat(altezzaField);
    onlyFloat(larghezzaField);
    onlyFloat(lunghezzaField);
    onlyFloat(volBagagliaioField);
    onlyFloat(pesoField);

    setValueAAImmatricolazione();

    setValidateModello();
    setValidateMarca();
    setValidateKmPercorsi();
    setValidateTarga();
    setValidateAAImmatricolazione();
    setValidateAltezza();
    setValidateLunghezza();
    setValidateLarghezza();
    setValidateVolBagagliaio();
    setValidatePeso();

    createPopUp();

    // shortcuts
    wrapperRoot.setOnKeyPressed(
        event -> {
          if (event.getCode().equals(KeyCode.ENTER)) {
            richiediPreventivo();
          }
        });
  }

  public void switchHome(@NotNull MouseEvent mouseEvent) {
    ScreenController.activate("home");
    mouseEvent.consume();
  }

  public void richiediPreventivo() {
    showErrorAll();

    boolean isInvalidInfoAuto =
        isFieldInvalid(modelloField)
            || isFieldInvalid(marcaField)
            || isFieldInvalid(kmPercorsiField)
            || isFieldInvalid(targaField[0])
            || isFieldInvalid(targaField[1])
            || isFieldInvalid(targaField[2])
            || isFieldInvalid(aaImmatricolazioneCombo);

    boolean isInvalidDatiAuto =
        setValidateFoto()
            || isFieldInvalid(altezzaField)
            || isFieldInvalid(lunghezzaField)
            || isFieldInvalid(larghezzaField)
            || isFieldInvalid(volBagagliaioField)
            || isFieldInvalid(pesoField);

    if (isInvalidDatiAuto && isInvalidInfoAuto) {
      return;
    }

    AutoUsata autoUsata = createAutoUsata();

    try {
      Connection.postDataToBacked(autoUsata, "autoUsate/");
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR, e.getMessage());
      alert.showAndWait();
      return;
    }

    ScreenController.activate("home");
    Alert alert = new Alert(AlertType.INFORMATION, "La sua auto è stata inserita correttamente");
    alert.showAndWait();
  }

  public void scegliFoto() {
    if (immagini.size() > 10) {
      alertWarning("Limite massimo immagini", "Hai raggiunto il limite massimo di 10 immagini");
      return;
    }

    FileChooser fileChooser = new FileChooser();

    // decido le estensioni ammissibili
    fileChooser
        .getExtensionFilters()
        .addAll(new ExtensionFilter("Image File", "*.jpeg", "*.jpg", "*.png"));

    // apre una finestra dove poter scegliere i file
    List<File> listImmagini = fileChooser.showOpenMultipleDialog(null);
    if (listImmagini == null) {
      return;
    }
    if (listImmagini.size() > 10) {
      alertWarning(
          "Limite massimo immagini",
          "Hai raggiunto il limite massimo di 10 immagini. Seleziona meno immagini.");
      validateFoto.setText("Immagini non aggiunte");
      return;
    }

    // controllo se esiste la cartella dove salvare le immagini `instance/immaginiAutoUsata`
    if (checkFolderImmagini()) {
      for (File f : listImmagini) {
        Path rootPath = Path.of("instance/immagini/immaginiAutoUsata");
        try {
          String newName = generateAlphaFileName() + getExtension(f.getName());
          Path newPath = rootPath.resolve(newName);
          // aggiunge l'immagine solo se non è stata già aggiunta, ovvero elimino la possibilità che
          // l'utente possa aggiungere due volte la stessa immagine
          File value = immagini.putIfAbsent(f, newPath.toFile());
          if (value == null) {
            Files.copy(f.toPath(), newPath);
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    } else {
      alertWarning("Impossibile salvare le immagini", "Impossibile salvare le immagini");
    }

    // pulisce la label per gli errori
    if (validateFoto.textProperty().isNotEmpty().get()) {
      validateFoto.setText("");
    }

    // creo il testo da visualizzare per il popup di info
    StringBuilder stringBuilder = new StringBuilder();
    immagini
        .keySet()
        .forEach(
            file -> {
              stringBuilder.append(file.getName());
              stringBuilder.append("\n");
            });

    popupContent.setText(stringBuilder.toString());
  }

  public void showPopUp(MouseEvent mouseEvent) {
    if (!popup.isShowing()) {
      popup.show(wrapperRoot, mouseEvent.getSceneX(), mouseEvent.getScreenY());
    }
  }

  public void hidePopUp(MouseEvent mouseEvent) {
    if (popup.isShowing()) {
      popup.hide();
    }
    mouseEvent.consume();
  }

  private void createPopUp() {
    popup = new Popup();
    popupContent = new Label("Nessuna foto selezionata");
    popupContent.setStyle("-fx-background-color: lightgray; -fx-padding: 10;");
    popup.getContent().add(popupContent);
  }

  private @NotNull AutoUsata createAutoUsata() {
    String targa =
        Arrays.stream(targaField).map(TextInputControl::getText).collect(Collectors.joining(""));
    LocalDate date = LocalDate.of(Integer.parseInt(aaImmatricolazioneCombo.getValue()), 1, 1);

    return new AutoUsata(
        modelloField.getText(),
        marcaField.getText(),
        Integer.parseInt(altezzaField.getText()),
        Integer.parseInt(lunghezzaField.getText()),
        Integer.parseInt(larghezzaField.getText()),
        Integer.parseInt(pesoField.getText()),
        Integer.parseInt(volBagagliaioField.getText()),
        Integer.parseInt(kmPercorsiField.getText()),
        targa,
        date);
  }

  private void showErrorAll() {
    List<Constraint> modelloConstr = modelloField.validate();
    List<Constraint> marcaConstr = marcaField.validate();
    List<Constraint> kmPercorsiConstr = kmPercorsiField.validate();
    List<Constraint> targaConstr = new ArrayList<>();
    for (MFXTextField field : targaField) targaConstr.addAll(field.validate());
    List<Constraint> aaImmatricolazioneConstr = aaImmatricolazioneCombo.validate();
    List<Constraint> altezzaConstr = altezzaField.validate();
    List<Constraint> lunghezzaConstr = lunghezzaField.validate();
    List<Constraint> larghezzaConstr = larghezzaField.validate();
    List<Constraint> volBagagliaioConstr = volBagagliaioField.validate();
    List<Constraint> pesoConstr = pesoField.validate();

    showError(modelloConstr, modelloField, validateModello);
    showError(marcaConstr, marcaField, validateMarca);
    showError(kmPercorsiConstr, kmPercorsiField, validateKmPercorsi);
    for (MFXTextField field : targaField) showError(targaConstr, field, validateTarga);
    showError(aaImmatricolazioneConstr, aaImmatricolazioneCombo, validateAAImmatricolazione);
    showError(altezzaConstr, altezzaField, validateAltezza);
    showError(lunghezzaConstr, lunghezzaField, validateLunghezza);
    showError(larghezzaConstr, larghezzaField, validateLarghezza);
    showError(volBagagliaioConstr, volBagagliaioField, validateVolBagagliaio);
    showError(pesoConstr, pesoField, validatePeso);
  }

  @SneakyThrows
  private boolean checkFolderImmagini() {
    Path root = Path.of("instance/immagini/immaginiAutoUsata");
    try {
      Files.createDirectories(root);
    } catch (FileAlreadyExistsException e) {
      logger.info("La cartella instance esiste già");
    }

    return Files.exists(root);
  }

  private void setBoundsTarga() {
    // set bounds targaField
    targaField =
        new MFXTextField[] {targaFieldFirstTwoLetter, targaFieldDigit, targaFieldLastTwoLetter};

    targaFieldFirstTwoLetter.setTextLimit(2);
    targaFieldLastTwoLetter.setTextLimit(2);
    targaFieldDigit.setTextLimit(3);

    onlyCharAlphabetical(targaFieldFirstTwoLetter);
    onlyCharAlphabetical(targaFieldLastTwoLetter);
    onlyDigit(targaFieldDigit);

    for (MFXTextField f : new MFXTextField[] {targaFieldFirstTwoLetter, targaFieldLastTwoLetter}) {
      f.textProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                f.setText(newValue.toUpperCase());
                updateField(f.textProperty(), f);
              });
    }

    for (int i = 0; i < targaField.length - 1; i++) {
      MFXTextField field = targaField[i];
      int finalI = i;
      field
          .textProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if (field.getTextLimit() == field.getLength())
                  targaField[finalI + 1].requestFocus();
              });
    }
  }

  private void setValueAAImmatricolazione() {
    int yearCurrent = Year.now().getValue();
    ObservableList<String> valueComboBox =
        IntStream.rangeClosed(1960, yearCurrent)
            .boxed()
            .sorted((a, b) -> Integer.compare(b, a))
            .map(String::valueOf)
            .collect(FXCollectors.toList());

    aaImmatricolazioneCombo.setItems(valueComboBox);
    aaImmatricolazioneCombo.getSelectionModel().selectFirst();
  }

  private @NotNull String generateAlphaFileName() {
    StringBuilder sb = new StringBuilder(10);
    for (int i = 0; i < 10; i++) {
      int index = RANDOM.nextInt(ALPHABET.length());
      sb.append(ALPHABET.charAt(index));
    }

    return sb.toString();
  }

  @Contract(pure = true)
  private @NotNull String getExtension(@NotNull String str) {
    String[] split = str.split("\\.");
    return "." + split[split.length - 1];
  }

  private void setValidateModello() {
    addConstraintRequired(modelloField, "Il modello è necessario");
    addEventRemoveClassInvalid(modelloField, validateModello);
  }

  private void setValidateMarca() {
    addConstraintRequired(marcaField, "La marca è necessaria");
    addEventRemoveClassInvalid(marcaField, validateMarca);
  }

  private void setValidateKmPercorsi() {
    addConstraintRequired(kmPercorsiField, "I chilometri percorsi sono necessari");
    addEventRemoveClassInvalid(kmPercorsiField, validateKmPercorsi);
  }

  private void setValidateTarga() {
    for (MFXTextField field : targaField) {
      addConstraintRequired(field, "La targa è necessaria");
    }

    for (MFXTextField field : targaField) {
      addEventRemoveClassInvalid(field, validateTarga);
    }
  }

  private void setValidateAAImmatricolazione() {
    addConstraintRequired(aaImmatricolazioneCombo, "L'anno di immatricolazione è necessario");
    addEventRemoveClassInvalid(aaImmatricolazioneCombo, validateAAImmatricolazione);
  }

  private void setValidateAltezza() {
    addConstraintRequired(altezzaField, "L'altezza è necessaria");
    addEventRemoveClassInvalid(altezzaField, validateAltezza);
  }

  private void setValidateLunghezza() {
    addConstraintRequired(lunghezzaField, "La lunghezza è necessaria");
    addEventRemoveClassInvalid(lunghezzaField, validateLunghezza);
  }

  private void setValidateLarghezza() {
    addConstraintRequired(larghezzaField, "La larghezza è necessario");
    addEventRemoveClassInvalid(larghezzaField, validateLarghezza);
  }

  private void setValidateVolBagagliaio() {
    addConstraintRequired(volBagagliaioField, "Il volume del bagagliaio è necessario");
    addEventRemoveClassInvalid(volBagagliaioField, validateVolBagagliaio);
  }

  private void setValidatePeso() {
    addConstraintRequired(pesoField, "Il peso è necessario");
    addEventRemoveClassInvalid(pesoField, validatePeso);
  }

  private boolean setValidateFoto() {
    if (immagini.isEmpty()) {
      validateFoto.setText("Caricare almeno una foto");
      return true;
    }
    validateFoto.setText("");
    return false;
  }
}
