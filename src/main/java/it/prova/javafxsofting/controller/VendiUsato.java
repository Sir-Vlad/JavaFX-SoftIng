package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.FXCollectors;
import io.github.palexdev.materialfx.validation.Constraint;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.data_manager.DataManager;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.models.Marca;
import it.prova.javafxsofting.models.PreventivoUsato;
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
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
  private static final int MAX_PHOTO_LIMIT = 10;
  private final HashMap<File, File> immagini = new HashMap<>();
  private final Logger logger = Logger.getLogger(VendiUsato.class.getName());
  @FXML private Header header;
  private MFXTextField[] targaField = null;
  @FXML private VBox wrapperRoot;
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
  @FXML private MFXFilterComboBox<String> marcaField;
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
  private Popup popup;
  private Label popupContent;

  /**
   * Controlla se i campi sono vuoti o invalidi
   *
   * @return true se i campi sono vuoti o invalidi
   */
  private boolean isInvalidDatiAuto() {
    return setValidateFoto()
        || isFieldInvalid(altezzaField)
        || isFieldInvalid(lunghezzaField)
        || isFieldInvalid(larghezzaField)
        || isFieldInvalid(volBagagliaioField)
        || isFieldInvalid(pesoField);
  }

  /**
   * Controlla se i campi sono vuoti o invalidi
   *
   * @return true se i campi sono vuoti o invalidi
   */
  private boolean isInvalidInfoAuto() {
    return isFieldInvalid(modelloField)
        || isFieldInvalid(marcaField)
        || isFieldInvalid(kmPercorsiField)
        || isFieldInvalid(targaField[0])
        || isFieldInvalid(targaField[1])
        || isFieldInvalid(targaField[2])
        || isFieldInvalid(aaImmatricolazioneCombo);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab(
        "Indietro",
        event -> {
          ScreenController.activate("config");
          ScreenController.removeScreen("vendiUsato");
        });

    setBoundsTarga();

    onlyFloat(kmPercorsiField);
    onlyFloat(altezzaField);
    onlyFloat(larghezzaField);
    onlyFloat(lunghezzaField);
    onlyFloat(volBagagliaioField);
    onlyFloat(pesoField);

    setValueMarca();
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

  /** Richiede il preventivo all'utente e lo invia al server */
  @FXML
  public void richiediPreventivo() {
    if (UserSession.getInstance().getUtente() == null) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setHeaderText(null);
      alert.setContentText("Effettua il login per poter effettuare il preventivo");
      alert.showAndWait();
      ScreenController.activate("login");
      return;
    }

    showErrorAll();

    if (isInvalidDatiAuto() && isInvalidInfoAuto()) {
      return;
    }

    // creo l'oggetto per l'auto usata
    AutoUsata autoUsata = createAutoUsata();
    // lo invio al server
    if (postPreventivo(autoUsata)) return;
    // aggiorno la lista delle auto usate
    try {
      DataManager.getInstance().setAutoUsate();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // aggiorno la lista dei preventivi
    UserSession.getInstance().setPreventiviUsati();
    // aggiorno la lista delle detrazioni
    UserSession.getInstance().setDetrazioni();
    // aggiungo le immagini all'auto usata
    autoUsata.setImmagini(new ArrayList<>(immagini.values()));
    // cerco il nuovo preventivo
    PreventivoUsato preventivo =
        UserSession.getInstance().getPreventiviUsati().stream()
            .max((Comparator.comparingInt(PreventivoUsato::getId)))
            .orElse(null);

    if (preventivo == null) {
      return;
    }
    // invio le immagini dell'auto usata al server
    postImmagini(preventivo.getIdAutoUsata(), autoUsata.getImmagini());

    // mostra un messaggio di successo
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setHeaderText("Auto Usata inserita correttamente");
    alert.setContentText(
        "La sua auto usata è stata inserita correttamente. Ora la puoi usare avere una detrazione sull'auto nuova.");
    alert.showAndWait();
    // ritorno alla schermata di configurazione
    ScreenController.activate("config");
    ScreenController.removeScreen("vendiUsato");
  }

  /**
   * Sceglie le immagini da caricare dell'auto usata
   *
   * @param mouseEvent l'azione dell'utente che ha generato l'azione del bottone
   */
  @FXML
  public void scegliFoto(ActionEvent mouseEvent) {
    if (immagini.size() > MAX_PHOTO_LIMIT) {
      alertWarning("Limite massimo immagini", "Hai raggiunto il limite massimo di 10 immagini");
      return;
    }

    FileChooser fileChooser = new FileChooser();

    // decido le estensioni ammissibili
    fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image File", "*.jpeg", "*.png"));

    // apre una finestra dove poter scegliere i file
    List<File> listImmagini = fileChooser.showOpenMultipleDialog(null);
    if (listImmagini == null) {
      return;
    }
    if (listImmagini.size() > MAX_PHOTO_LIMIT) {
      alertWarning(
          "Limite massimo immagini",
          "Hai raggiunto il limite massimo di 10 immagini. Seleziona meno immagini.");
      validateFoto.setText("Immagini non aggiunte");
      return;
    }

    // controllo se esiste la cartella dove salvare le immagini `instance/immaginiAutoUsate`
    if (checkFolderImmagini()) {
      for (File f : listImmagini) {
        Path rootPath = Path.of("instance/immagini/immaginiAutoUsate");
        try {
          String newName = generateAlphaFileName() + getExtension(f.getName());
          Path newPath = rootPath.resolve(newName);
          // aggiunge l'immagine solo se non è stata già aggiunta, ovvero elimino la possibilità che
          // l'utente possa aggiungere due volte la stessa immagine
          if (immagini.putIfAbsent(f, newPath.toFile()) == null) {
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
    immagini.forEach(
        (oldFile, newFile) -> {
          stringBuilder.append(oldFile.getName());
          stringBuilder.append("\n");
        });

    popupContent.setText(stringBuilder.toString());
    mouseEvent.consume();
  }

  /**
   * Mostra il popup a delle specifiche coordinate
   *
   * @param mouseEvent azione del mouse dell'utente che entra nel button
   */
  @FXML
  public void showPopUp(MouseEvent mouseEvent) {
    if (!popup.isShowing()) {
      popup.show(wrapperRoot, mouseEvent.getSceneX(), mouseEvent.getScreenY());
    }
  }

  /**
   * Chiude il popup
   *
   * @param mouseEvent evento del mouse dell'utente che esce dal button
   */
  @FXML
  public void hidePopUp(MouseEvent mouseEvent) {
    if (popup.isShowing()) {
      popup.hide();
    }
    mouseEvent.consume();
  }

  /**
   * Invia le immagini all'API
   *
   * @param idAutoUsata id dell'auto
   * @param immagini {@link ArrayList} di {@link File}
   */
  private void postImmagini(int idAutoUsata, ArrayList<File> immagini) {
    try {
      Connection.postImmaginiAutoUsateToBacked(idAutoUsata, immagini, "immaginiAutoUsate/");
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR, e.getMessage());
      alert.showAndWait();
    }
  }

  private void alertWarning(String title, String message) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Invia il preventivo all'API
   *
   * @param autoUsata {@link AutoUsata} da inviare all'API
   * @return true se l'operazione fallisce e false altrimenti
   */
  private boolean postPreventivo(AutoUsata autoUsata) {
    try {
      Connection.postDataToBacked(autoUsata, "autoUsate/");
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR, e.getMessage());
      alert.showAndWait();
      return true;
    }
    return false;
  }

  /** Crea il popup da visualizzare */
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

    AutoUsata autoUsata =
        new AutoUsata(
            modelloField.getText(),
            marcaField.getSelectionModel().getSelectedItem(),
            Integer.parseInt(altezzaField.getText()),
            Integer.parseInt(lunghezzaField.getText()),
            Integer.parseInt(larghezzaField.getText()),
            Integer.parseInt(pesoField.getText()),
            Integer.parseInt(volBagagliaioField.getText()),
            Integer.parseInt(kmPercorsiField.getText()),
            targa,
            date);

    autoUsata.setImmagini(new ArrayList<>(immagini.values()));

    return autoUsata;
  }

  /** Mostra gli errori dei campi compilati in rosso */
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

  /**
   * Controllare che if la cartella immaginiAutoUsate esista o meno
   *
   * @return true se la cartella esiste, false altrimenti
   */
  @SneakyThrows
  private boolean checkFolderImmagini() {
    Path root = Path.of("instance/immagini/immaginiAutoUsate");
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

    Stream.of(targaFieldFirstTwoLetter, targaFieldLastTwoLetter)
        .forEach(
            field ->
                field
                    .textProperty()
                    .addListener(
                        (observable, oldValue, newValue) -> {
                          field.setText(newValue.toUpperCase());
                          updateField(field.textProperty(), field);
                        }));

    IntStream.range(0, targaField.length - 1)
        .forEach(
            i -> {
              MFXTextField field = targaField[i];
              int nextIndex = i + 1;
              field
                  .textProperty()
                  .addListener(
                      (observable, oldValue, newValue) -> {
                        if (field.getTextLimit() == field.getLength())
                          targaField[nextIndex].requestFocus();
                      });
            });
  }

  private void setValueMarca() {
    marcaField.setItems(
        FXCollections.observableArrayList(Arrays.stream(Marca.values()).map(Enum::name).toList()));

    marcaField.getSelectionModel().selectFirst();
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

  /**
   * Genera un nome casuale per l'immagine
   *
   * @return il nome generato
   */
  private @NotNull String generateAlphaFileName() {
    StringBuilder sb = new StringBuilder(MAX_PHOTO_LIMIT);
    for (int i = 0; i < MAX_PHOTO_LIMIT; i++) {
      int index = RANDOM.nextInt(ALPHABET.length());
      sb.append(ALPHABET.charAt(index));
    }

    return sb.toString();
  }

  /**
   * Estrae l'estensione dal nome di un file
   *
   * @param str il nome del file
   * @return l'estensione del file
   */
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
