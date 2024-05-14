package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import io.github.palexdev.mfxcore.utils.converters.DateStringConverter;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.Utente;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import org.apache.commons.validator.routines.EmailValidator;

public class RegistrazioneController extends ValidateForm implements Initializable {
  @FXML private AnchorPane root;
  @FXML private VBox wrapperRegistrazione;

  @FXML private Label nomeLabel;
  @FXML private Label cognomeLabel;
  @FXML private Label emailLabel;
  @FXML private Label passwordLabel;
  @FXML private Label confermaPasswordLabel;
  @FXML private Label ibanLabel;
  @FXML private Label dataScadenzaLabel;
  @FXML private Label cvcLabel;

  @FXML private MFXTextField ibanField;
  @FXML private MFXTextField nomeField;
  @FXML private MFXTextField cognomeField;
  @FXML private MFXTextField emailField;
  @FXML private MFXPasswordField passwordField;
  @FXML private MFXPasswordField confermaPasswordField;
  @FXML private MFXDatePicker dataScadenzaField;
  @FXML private MFXTextField cvcField;
  @FXML private MFXButton createAccountBtn;

  @FXML private Label validateNome;
  @FXML private Label validateCognome;
  @FXML private Label validateEmail;
  @FXML private Label validatePassword;
  @FXML private Label validateIban;
  @FXML private Label validateCvc;
  @FXML private Label validateDate;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // visualizzare nella data solo il mese e l'anno
    dataScadenzaField.setConverterSupplier(
        () -> new DateStringConverter("MM/yy", dataScadenzaField.getLocale()));
    dataScadenzaField.setEditable(false);

    // limitazione del campo cvc a 3 caratteri numerici
    cvcField.setTextLimit(3);
    onlyDigit(cvcField);

    // limitazione del campo iban a 16 caratteri numerici
    // 19 = 16 (numeri della carta) + 3 spazi
    ibanField.setTextLimit(19);
    onlyDigit(ibanField);

    onlyCharAlphabetical(nomeField);
    onlyCharAlphabetical(cognomeField);

    // set validazione dei campi
    setValidateNome();
    setValidateCognome();
    setValidateEmail();
    setValidatePassword();
    setValidateIban();
    setValidateDate();
    setValidateCvc();

    // shortcuts
    root.setOnKeyPressed(
        event -> {
          if (event.getCode().equals(KeyCode.ENTER)) {
            createAccount();
          }
        });
  }

  public void createAccount() {
    List<Constraint> nomeConstr = nomeField.validate();
    List<Constraint> cognomeConstr = cognomeField.validate();
    List<Constraint> emailConstr = emailField.validate();
    List<Constraint> passwordConstr = passwordField.validate();
    List<Constraint> ibanConstr = ibanField.validate();
    List<Constraint> dateConstr = dataScadenzaField.validate();
    List<Constraint> cvcConstr = cvcField.validate();

    showError(nomeConstr, nomeField, validateNome);
    showError(cognomeConstr, cognomeField, validateCognome);
    showError(emailConstr, emailField, validateEmail);
    showError(passwordConstr, passwordField, validatePassword);
    if (!passwordConstr.isEmpty()) {
      confermaPasswordField.getStyleClass().add("field-invalid");
    } else {
      confermaPasswordField.getStyleClass().remove("field-invalid");
    }
    showError(ibanConstr, ibanField, validateIban);
    showError(dateConstr, dataScadenzaField, validateDate);
    showError(cvcConstr, cvcField, validateCvc);

    boolean isInvalidForm =
        isFieldInvalid(nomeField)
            || isFieldInvalid(cognomeField)
            || isFieldInvalid(emailField)
            || isFieldInvalid(passwordField)
            || isFieldInvalid(confermaPasswordField)
            || isFieldInvalid(ibanField)
            || isFieldInvalid(dataScadenzaField)
            || isFieldInvalid(cvcField);

    if (isInvalidForm) {
      return;
    }

    Utente newUtente =
        Utente.getInstance(
            capitalize(nomeField.getText().trim()),
            capitalize(cognomeField.getText().trim()),
            emailField.getText().trim(),
            passwordField.getText(),
            ibanField.getText(),
            dataScadenzaField.getValue(),
            cvcField.getText());

    // todo: aggiungerlo nel db se ritorna un errore mostrare un errore se no redirect alla home
    try {
      Connection.sendDataToBacked(newUtente, Connection.porta, "utenti/");
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR, e.getMessage());
      alert.showAndWait();
      return;
    }

    App.utente = newUtente;

    App.log.info(newUtente.toString());

    ScreenController.activate("home");
  }

  private String capitalize(String input) {
    return Pattern.compile("^.")
        .matcher(input)
        .replaceFirst(matchResult -> matchResult.group().toUpperCase());
  }

  private void setValidateNome() {
    addConstraintRequired(nomeField, "Nome necessario");
    nomeField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                removeClassInvalid(nomeField, validateNome);
              }
            });
  }

  private void setValidateCognome() {
    addConstraintRequired(cognomeField, "Cognome necessario");
    cognomeField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                removeClassInvalid(cognomeField, validateCognome);
              }
            });
  }

  private void setValidateEmail() {
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
    emailField
        .getValidator()
        .validProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                removeClassInvalid(emailField, validateEmail);
              }
            });
  }

  private void setValidatePassword() {
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
    passwordField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                confermaPasswordField.getStyleClass().remove("field-invalid");
                removeClassInvalid(passwordField, validatePassword);
              }
            });
  }

  private void setValidateIban() {
    addConstraintRequired(ibanField, "Numero della carta necessario");
    ibanField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                removeClassInvalid(ibanField, validateIban);
              }
            });
  }

  private void setValidateDate() {
    addConstraintRequired(dataScadenzaField, "Data di Scadenza necessario");
    dataScadenzaField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                removeClassInvalid(dataScadenzaField, validateDate);
              }
            });
  }

  private void setValidateCvc() {
    addConstraintRequired(cvcField, "Il codice CVC è necessario");
    cvcField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                removeClassInvalid(cvcField, validateCvc);
              }
            });
  }
}
