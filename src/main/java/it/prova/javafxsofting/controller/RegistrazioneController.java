package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import io.github.palexdev.mfxcore.utils.converters.DateStringConverter;
import it.prova.javafxsofting.Utente;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
    cvcField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue.matches("\\d*")) {
                cvcField.setText(newValue.replaceAll("\\D", ""));
                updateField(cvcField.textProperty(), cvcField);
              }
            });

    // limitazione del campo iban a 16 caratteri numerici
    // 19 = 16 (numeri della carta) + 3 spazi
    ibanField.setTextLimit(19);
    ibanField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              String newValueFormat = newValue;
              if (!newValue.matches("\\d*")) {
                newValueFormat = newValue.replaceAll("\\D", "");
              }
              newValueFormat = newValueFormat.replaceAll("(.{4})", "$1 ");
              ibanField.setText(newValueFormat);
              updateField(ibanField.textProperty(), ibanField);
            });

    // set validazione dei campi
    setValidateNome();
    setValidateCognome();
    setValidateEmail();
    setValidatePassword();
    setValidateIban();
    setValidateDate();
    setValidateCvc();
  }

  public void createAccount(ActionEvent actionEvent) {
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
        fieldInvalid(nomeField)
            || fieldInvalid(cognomeField)
            || fieldInvalid(emailField)
            || fieldInvalid(passwordField)
            || fieldInvalid(confermaPasswordField)
            || fieldInvalid(ibanField)
            || fieldInvalid(dataScadenzaField)
            || fieldInvalid(cvcField);

    if (isInvalidForm) {
      actionEvent.consume();
      return;
    }

    Utente newUtente =
        new Utente(
            nomeField.getText(),
            cognomeField.getText(),
            emailField.getText(),
            passwordField.getText(),
            ibanField.getText(),
            dataScadenzaField.getValue(),
            cvcField.getText());

    // todo: aggiungerlo nel db se ritorna un errore mostrare un errore se no redirect alla home

    System.out.println("Valido");
    System.out.println(newUtente);
    actionEvent.consume();
  }

  private void updateField(StringProperty timeText, MFXTextField field) {
    Platform.runLater(
        () -> {
          field.setText(timeText.getValue());
          field.positionCaret(timeText.getValue().length());
        });
  }

  private void setValidateNome() {
    addContraitRequired(nomeField, "Nome necessario");
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
    addContraitRequired(cognomeField, "Cognome necessario");
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
    addContraitRequired(emailField, "Email necessaria");

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
    addContraitRequired(passwordField, "Password necessaria");
    addContraitLength(passwordField, "Password devono avere almeno 8 caratteri", 8);

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
    addContraitRequired(ibanField, "Numero della carta necessario");
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
    addContraitRequired(dataScadenzaField, "Data di Scadenza necessario");
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
    addContraitRequired(cvcField, "Il codice CVC è necessario");
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
