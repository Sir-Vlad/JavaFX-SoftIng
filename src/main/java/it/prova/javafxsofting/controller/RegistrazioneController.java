package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.MFXStepper.MFXStepperEvent;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.MFXValidator;
import io.github.palexdev.materialfx.validation.Severity;
import io.github.palexdev.materialfx.validation.Validated;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.Utente;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.IntStream;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import lombok.SneakyThrows;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RegistrazioneController extends ValidateForm implements Initializable {
  private static final Path DIR_FXML = Path.of("controller").resolve("step_registrazione");
  private final MFXCheckbox checkbox;
  private final ObservableList<String> mesi =
      FXCollections.observableArrayList(
          Arrays.stream(Month.values())
              .map(
                  month ->
                      month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()))
              .toArray(String[]::new));
  public AnchorPane rootRegistrazione;
  private MFXTextField nomeField;
  private MFXTextField cognomeField;
  private MFXTextField emailField;
  private MFXPasswordField passwordField;
  private MFXTextField ibanField;
  private LocalDate dataScadenza;
  private MFXComboBox<String> meseCombo;
  private MFXComboBox<Integer> annoCombo;
  private MFXTextField cvcField;
  @FXML private MFXStepper stepper;

  public RegistrazioneController() {
    checkbox = new MFXCheckbox("Confermi i dati inseriti?");
  }

  private void setPasswordField(MFXPasswordField confermaPasswordField) {
    addConstraintRequired(passwordField, "Password necessaria");
    addConstraintLength(passwordField, "Password devono avere almeno 8 caratteri", 8);

    Constraint matchPassword =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Le password non corrispondono")
            .setCondition(
                passwordField.textProperty().isEqualTo(confermaPasswordField.textProperty()))
            .get();

    passwordField.getValidator().constraint(matchPassword);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    List<MFXStepperToggle> stepperToggles = createSteps();
    stepper.getStepperToggles().addAll(stepperToggles);
    stepper.setOnBeforeNext(
        event -> {
          if (meseCombo.getSelectedItem() != null && annoCombo.getSelectedItem() != null) {
            dataScadenza =
                LocalDate.of(
                    annoCombo.getSelectedItem(), mesi.indexOf(meseCombo.getSelectedItem()), 1);
          }
        });
    rootRegistrazione.setOnKeyPressed(
        event -> {
          if (event.getCode().equals(KeyCode.ENTER)) {
            stepper.setFocusTraversable(true);
            stepper.next();
          }
        });
  }

  public void switchIndietro(ActionEvent actionEvent) {
    ScreenController.activate("home");
    actionEvent.consume();
  }

  @SneakyThrows
  private List<MFXStepperToggle> createSteps() {
    MFXStepperToggle step1 =
        new MFXStepperToggle("Step 1", new MFXFontIcon("fas-lock", 16, Color.web("#f1c40f")));
    VBox step1Box = createVboxStep1();

    step1Box.setAlignment(Pos.CENTER);
    step1Box.setPrefWidth(300);
    step1.setContent(step1Box);
    step1
        .getValidator()
        .dependsOn(emailField.getValidator())
        .dependsOn(passwordField.getValidator());

    MFXStepperToggle step2 =
        new MFXStepperToggle("Step 2", new MFXFontIcon("fas-user", 16, Color.web("#49a6d7")));
    VBox step2Box = createVboxStep2();
    step2.setContent(step2Box);
    step2.getValidator().dependsOn(nomeField.getValidator()).dependsOn(cognomeField.getValidator());

    MFXStepperToggle step3 =
        new MFXStepperToggle(
            "Step 3", new MFXFontIcon("fas-credit-card", 16, Color.web("#85CB33")));
    VBox step3Box = createVboxStep3();
    step3.setContent(step3Box);
    step3
        .getValidator()
        .dependsOn(ibanField.getValidator())
        .dependsOn(meseCombo.getValidator())
        .dependsOn(annoCombo.getValidator())
        .dependsOn(cvcField.getValidator());

    MFXStepperToggle step4 =
        new MFXStepperToggle("Step 3", new MFXFontIcon("fas-check", 16, Color.web("#85CB33")));
    VBox step4Grid = createGrid();

    step4.setContent(step4Grid);
    step4.getValidator().constraint("Data must be confirmed", checkbox.selectedProperty());

    return List.of(step1, step2, step3, step4);
  }

  /**
   * Formatta la stringa in input con la prima lettera maiuscola
   *
   * @param input stringa da formattare
   * @return stringa formattata
   */
  @Contract("null -> null")
  private String capitalize(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
  }

  @SneakyThrows
  private VBox createVboxStep3() {
    VBox step3Box =
        FXMLLoader.load(
            Objects.requireNonNull(
                App.class.getResource(DIR_FXML.resolve("dati_carta.fxml").toString())));

    ibanField = (MFXTextField) step3Box.lookup("#ibanField");
    Label validateIban = (Label) step3Box.lookup("#validateIban");
    addConstraintRequired(ibanField, "Numero della carta di credito è necessaria");
    nodeForValidation(ibanField, validateIban);
    ibanField.setFocusTraversable(true);

    meseCombo = (MFXComboBox<String>) step3Box.lookup("#meseCombo");
    annoCombo = (MFXComboBox<Integer>) step3Box.lookup("#annoCombo");
    Label validateDate = (Label) step3Box.lookup("#validateDate");
    setDataScadenza();
    nodeForValidation(meseCombo, validateDate);
    nodeForValidation(annoCombo, validateDate);

    cvcField = (MFXTextField) step3Box.lookup("#cvcField");
    Label validateCvc = (Label) step3Box.lookup("#validateCvc");
    addConstraintRequired(cvcField, "Il cvc è necessario");
    nodeForValidation(cvcField, validateCvc);

    // limitazione del campo cvc a 3 caratteri numerici
    cvcField.setTextLimit(3);
    onlyDigit(cvcField);

    // limitazione del campo iban a 16 caratteri numerici
    // 19 = 16 (numeri della carta) + 3 spazi
    ibanField.setTextLimit(16);
    onlyDigit(ibanField);

    return step3Box;
  }

  private void setDataScadenza() {

    meseCombo.setItems(mesi);

    // Ottenere l'anno corrente
    LocalDate currentDate = LocalDate.now();

    // Aggiungere gli anni al comboBox
    IntStream.rangeClosed(currentDate.getYear(), currentDate.getYear() + 7)
        .forEach(annoCombo.getItems()::add);

    addConstraintRequired(meseCombo, "Il mese della scadenza è necessario");
    addConstraintRequired(annoCombo, "Il mese della scadenza è necessario");
  }

  @SneakyThrows
  private @NotNull VBox createVboxStep2() {
    VBox step2Box =
        FXMLLoader.load(
            Objects.requireNonNull(
                App.class.getResource(DIR_FXML.resolve("dati_utente.fxml").toString())));

    nomeField = (MFXTextField) step2Box.lookup("#nomeField");
    Label validateNome = (Label) step2Box.lookup("#validateNome");
    addConstraintRequired(nomeField, "Nome necessario");
    nodeForValidation(nomeField, validateNome);
    nomeField.setFocusTraversable(true);

    cognomeField = (MFXTextField) step2Box.lookup("#cognomeField");
    Label validateCognome = (Label) step2Box.lookup("#validateCognome");
    addConstraintRequired(cognomeField, "Cognome necessario");
    nodeForValidation(cognomeField, validateCognome);

    onlyCharAlphabetical(nomeField);
    onlyCharAlphabetical(cognomeField);

    step2Box.setAlignment(Pos.CENTER);
    return step2Box;
  }

  private @NotNull VBox createVboxStep1() throws IOException {
    VBox step1Box =
        FXMLLoader.load(
            Objects.requireNonNull(
                App.class.getResource(DIR_FXML.resolve("dati_accesso.fxml").toString())));

    emailField = (MFXTextField) step1Box.lookup("#emailField");
    Label validateEmail = (Label) step1Box.lookup("#validateEmail");
    setEmailField();
    nodeForValidation(emailField, validateEmail);
    emailField.setFocusTraversable(true);

    passwordField = (MFXPasswordField) step1Box.lookup("#passwordField");
    MFXPasswordField confermaPasswordField =
        (MFXPasswordField) step1Box.lookup("#confermaPasswordField");
    Label validatePassword = (Label) step1Box.lookup("#validatePassword");
    setPasswordField(confermaPasswordField);
    nodeForValidation(passwordField, validatePassword);
    return step1Box;
  }

  private void setEmailField() {
    emailField.setLeadingIcon(new MFXIconWrapper("fas-user", 16, Color.web("#4D4D4D"), 24));

    addConstraintRequired(emailField, "Email necessaria");

    Constraint emailValid =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Email non valida")
            .setCondition(
                emailField
                    .textProperty()
                    .isEmpty()
                    .or(
                        Bindings.createBooleanBinding(
                            () -> EmailValidator.getInstance().isValid(emailField.getText()),
                            emailField.textProperty())))
            .get();

    emailField.getValidator().constraint(emailValid);
  }

  private <T extends Node & Validated> void nodeForValidation(T node, Label errorLabel) {
    stepper.addEventHandler(
        MFXStepperEvent.VALIDATION_FAILED_EVENT,
        event -> {
          MFXValidator validator = node.getValidator();
          List<Constraint> validate = validator.validate();
          if (!validate.isEmpty()) {
            errorLabel.setText(validate.getFirst().getMessage());
          }
        });
    stepper.addEventHandler(MFXStepperEvent.NEXT_EVENT, event -> errorLabel.setText(""));
  }

  @SneakyThrows
  private VBox createGrid() {
    VBox step4Grid =
        FXMLLoader.load(
            Objects.requireNonNull(
                App.class.getResource(DIR_FXML.resolve("conferma_valori.fxml").toString())));

    MFXTextField confermaNome = (MFXTextField) step4Grid.lookup("#confermaNome");
    confermaNome.textProperty().bind(nomeField.textProperty());

    MFXTextField confermaCognome = (MFXTextField) step4Grid.lookup("#confermaCognome");
    confermaCognome.textProperty().bind(cognomeField.textProperty());

    MFXTextField confermaEmail = (MFXTextField) step4Grid.lookup("#confermaEmail");
    confermaEmail.textProperty().bind(emailField.textProperty());

    MFXTextField confermaIban = (MFXTextField) step4Grid.lookup("#confermaIban");
    confermaIban.textProperty().bind(ibanField.textProperty());

    Text completedLabel = new Text("Completed!");
    completedLabel.setStyle("-fx-font-size: 40;-fx-font-weight: bold;");
    completedLabel.setTextAlignment(TextAlignment.CENTER);

    step4Grid.getChildren().add(checkbox);
    step4Grid.setAlignment(Pos.CENTER);
    StackPane.setAlignment(step4Grid, Pos.CENTER);

    stepper.setOnLastNext(
        event -> {
          step4Grid.getChildren().setAll(completedLabel);
          step4Grid.setAlignment(Pos.CENTER);
          stepper.setMouseTransparent(true);
          PauseTransition pause = new PauseTransition(Duration.seconds(3));
          pause.setOnFinished(
              event1 -> {
                Utente newUtente =
                    new Utente(
                        capitalize(nomeField.getText().trim()),
                        capitalize(cognomeField.getText().trim()),
                        emailField.getText().trim(),
                        passwordField.getText(),
                        ibanField.getText(),
                        dataScadenza,
                        cvcField.getText());

                try {
                  Connection.postDataToBacked(newUtente, "utenti/");
                } catch (Exception e) {
                  Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                  alert.showAndWait();
                  stepper.setMouseTransparent(false);
                  return;
                }

                App.setUtente(newUtente);
                ScreenController.activate("home");
              });
          pause.play();
        });

    return step4Grid;
  }
}
