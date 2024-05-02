package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.FXCollectors;
import io.github.palexdev.materialfx.validation.Constraint;
import it.prova.javafxsofting.component.ProfileBox;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class VendiUsato extends ValidateForm implements Initializable {
  public MFXButton scegliFotoBtn;
  @FXML private VBox homeBtn;
  @FXML private ProfileBox profile;
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
  @FXML private Label pesoLabel;
  @FXML private Label validateModello;
  @FXML private Label validateMarca;
  @FXML private Label validateKmPercorsi;
  @FXML private Label validateTarga;
  @FXML private Label validateAAImmatricolazione;
  @FXML private Label validateAltezza;
  @FXML private Label validateLunghezza;
  @FXML private Label validateLarghezza;
  @FXML private Label volBagagliaioLabel;
  @FXML private Label validateVolBagagliaio;
  @FXML private Label validatePeso;
  @FXML private Label validateFoto;
  @FXML private MFXTextField modelloField;
  @FXML private MFXTextField marcaField;
  @FXML private MFXTextField kmPercorsiField;
  @FXML private MFXTextField targaField;
  @FXML private MFXFilterComboBox<String> aaImmatricolazioneCombo;
  @FXML private MFXTextField altezzaField;
  @FXML private MFXTextField lunghezzaField;
  @FXML private MFXTextField larghezzaField;
  @FXML private MFXTextField volBagagliaioField;
  @FXML private MFXTextField pesoField;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    onlyCharAlphabetical(modelloField);
    onlyCharAlphabetical(marcaField);

    onlyDigit(kmPercorsiField);
    onlyDigit(altezzaField);
    onlyDigit(larghezzaField);
    onlyDigit(lunghezzaField);
    onlyDigit(volBagagliaioField);
    onlyDigit(pesoField);

    setValueComboBox();

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
    setValidateFoto();

    // shortcuts
    wrapperRoot.setOnKeyPressed(
        event -> {
          if (event.getCode().equals(KeyCode.ENTER)) {
            richiediPreventivo();
          }
        });
  }

  public void switchHome(MouseEvent mouseEvent) {
    ScreenController.activate("home");
    mouseEvent.consume();
  }

  public void richiediPreventivo() {
    List<Constraint> modelloConstr = modelloField.validate();
    List<Constraint> marcaConstr = marcaField.validate();
    List<Constraint> kmPercorsiConstr = kmPercorsiField.validate();
    List<Constraint> targaConstr = targaField.validate();
    //    List<Constraint> aaImmatricolazioneConstr = aaImmatricolazioneField.validate();
    List<Constraint> altezzaConstr = altezzaField.validate();
    List<Constraint> lunghezzaConstr = lunghezzaField.validate();
    List<Constraint> larghezzaConstr = larghezzaField.validate();
    List<Constraint> volBagagliaioConstr = volBagagliaioField.validate();
    List<Constraint> pesoConstr = pesoField.validate();

    showError(modelloConstr, modelloField, validateModello);
    showError(marcaConstr, marcaField, validateMarca);
    showError(kmPercorsiConstr, kmPercorsiField, validateKmPercorsi);
    showError(targaConstr, targaField, validateTarga);
    //    showError(aaImmatricolazioneConstr, aaImmatricolazioneField, validateAAImmatricolazione);
    showError(altezzaConstr, altezzaField, validateAltezza);
    showError(lunghezzaConstr, lunghezzaField, validateLunghezza);
    showError(larghezzaConstr, larghezzaField, validateLarghezza);
    showError(volBagagliaioConstr, volBagagliaioField, validateVolBagagliaio);
    showError(pesoConstr, pesoField, validatePeso);

    boolean isInvalidForm =
        isFieldInvalid(modelloField)
            || isFieldInvalid(marcaField)
            || isFieldInvalid(kmPercorsiField)
            || isFieldInvalid(targaField)
            //            || isFieldInvalid(aaImmatricolazioneField)
            || isFieldInvalid(altezzaField)
            || isFieldInvalid(lunghezzaField)
            || isFieldInvalid(larghezzaField)
            || isFieldInvalid(volBagagliaioField)
            || isFieldInvalid(pesoField);

    if (isInvalidForm) {
      return;
    }
  }

  public void scegliFoto() {
    FileChooser fileChooser = new FileChooser();

    // decido le estensioni ammissibili
    fileChooser
        .getExtensionFilters()
        .addAll(new ExtensionFilter("Image File", "*.jpeg", "*.jpg", "*.png"));

    // apre una finestra dove poter scegliere i file
    List<File> listImmagini = fileChooser.showOpenMultipleDialog(null);

    // controllo se esiste la cartella dove salvare le immagini `instance/data`
    if (Files.isDirectory(new File("instance").toPath())) {
      File folderSaveImage = new File("instance/data");
      if (!Files.isDirectory(folderSaveImage.toPath())) {
        try {
          Files.createDirectory(folderSaveImage.toPath());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      System.out.println(listImmagini);
      File[] immaginiSalvate = new File[listImmagini.size()];
      int count = 0;
      for (File f : listImmagini) {
        try {
          String newName = generateAlphaFileName() + getExtension(f.getName());
          File newPath = Path.of(folderSaveImage.getPath() + "/" + newName).toFile();
          Files.copy(f.toPath(), newPath.toPath());
          immaginiSalvate[count++] = newPath;
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      System.out.println(Arrays.toString(immaginiSalvate));
    } else {
      System.out.println("file non esiste");
    }
  }

  private void setValueComboBox() {
    ObservableList<String> valueComboBox =
        IntStream.rangeClosed(1900, 2023).mapToObj(String::valueOf).collect(FXCollectors.toList());

    aaImmatricolazioneCombo.setItems(valueComboBox);
  }

  private String generateAlphaFileName() {
    String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lower = "abcdefghijklmnopqrstuvwxyz";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10; i++) {
      sb.append(upper.charAt((int) (Math.random() * upper.length())));
      sb.append(lower.charAt((int) (Math.random() * lower.length())));
    }
    return sb.toString();
  }

  private String getExtension(String str) {
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
    addConstraintRequired(targaField, "La targa è necessaria");

    // fixme: non funziona validTarga
    //    Constraint validTarga =
    //        Constraint.Builder.build()
    //            .setSeverity(Severity.ERROR)
    //            .setMessage("La targa non è valida")
    //            .setCondition(
    //                Bindings.createBooleanBinding(VendiUsato::validateTarga,
    // targaField.textProperty()))
    //            .get();
    //
    //    targaField.getValidator().constraint(validTarga);

    addEventRemoveClassInvalid(targaField, validateTarga);
  }

  private boolean validateTarga() {
    return targaField
        .textProperty()
        .toString()
        .trim()
        .matches("^[A-Za-z]{2} [0-9]{3} [A-Za-z]{2}$");
  }

  private void setValidateAAImmatricolazione() {
    //    addConstraintRequired(aaImmatricolazioneField, "L'anno di immatricolazione è necessario");
    //    addEventRemoveClassInvalid(aaImmatricolazioneField, validateAAImmatricolazione);
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

  private void setValidateFoto() {}
}
