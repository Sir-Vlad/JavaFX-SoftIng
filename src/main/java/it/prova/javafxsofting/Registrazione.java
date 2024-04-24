package it.prova.javafxsofting;

import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import io.github.palexdev.mfxcore.utils.converters.DateStringConverter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.apache.commons.validator.routines.EmailValidator;

public class Registrazione implements Initializable {
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
    // set immagine di background
    BackgroundImage bg =
        new BackgroundImage(
            new Image(String.valueOf(getClass().getResource("immagini/car.jpeg"))),
            BackgroundRepeat.REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);

    root.setBackground(new Background(bg));

    // set background al wrapper della registrazione
    wrapperRegistrazione.setBackground(
        new Background(
            new BackgroundFill(Color.rgb(183, 180, 172, 0.85), new CornerRadii(25), Insets.EMPTY)));
    wrapperRegistrazione.setEffect(
        new DropShadow(BlurType.GAUSSIAN, Color.rgb(198, 204, 197, 0.8506), 18, 0.3, 0, 0));
    wrapperRegistrazione.setStyle(
        "-fx-border-color: #6F6F6F80; -fx-border-width: 1px 1px 1px; -fx-border-radius: " + "25; ");

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
      confermaPasswordField.setStyle("-fx-border-color: red; -fx-border-width: 1");
    } else {
      confermaPasswordField.setStyle(null);
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
    System.out.println(newUtente.getIban());
    actionEvent.consume();
  }

  private void updateField(StringProperty timeText, MFXTextField field) {
    Platform.runLater(
        () -> {
          field.setText(timeText.getValue());
          field.positionCaret(timeText.getValue().length());
        });
  }

  private boolean isFieldInvalid(MFXTextField field) {
    return field.getPseudoClassStates().stream()
        .anyMatch(pseudoClass -> pseudoClass.equals(INVALID_PSEUDO_CLASS));
  }

  private void showError(List<Constraint> constraints, MFXTextField field, Label label) {
    if (!constraints.isEmpty()) {
      field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
      field.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
      label.setText(constraints.getFirst().getMessage());
      label.setVisible(true);
    }
  }

  private void setValidateNome() {
    Constraint required =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Nome necessario")
            .setCondition(nomeField.textProperty().isNotEmpty())
            .get();

    nomeField.getValidator().constraint(required);
    nomeField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                validateNome.setVisible(false);
                nomeField.setStyle(null);
                nomeField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
              }
            });
    nomeField
        .delegateFocusedProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (oldValue && !newValue) {
                List<Constraint> constraints = nomeField.validate();
                showError(constraints, nomeField, validateNome);
              }
            });
  }

  private void setValidateCognome() {
    Constraint required =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Cognome necessario")
            .setCondition(cognomeField.textProperty().isNotEmpty())
            .get();

    cognomeField.getValidator().constraint(required);
    cognomeField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                validateCognome.setVisible(false);
                cognomeField.setStyle(null);
                cognomeField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
              }
            });
  }

  private void setValidateEmail() {
    Constraint required =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Email necessaria")
            .setCondition(emailField.textProperty().isNotEmpty())
            .get();

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

    emailField.getValidator().constraint(emailValid).constraint(required);
    emailField
        .getValidator()
        .validProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                validateEmail.setVisible(false);
                emailField.setStyle(null);
                emailField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
              }
            });
  }

  private void setValidatePassword() {
    Constraint required =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Password necessaria")
            .setCondition(passwordField.textProperty().isNotEmpty())
            .get();

    Constraint lenConstraint =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Password deve essere almeno 8 caratteri")
            .setCondition(passwordField.textProperty().length().greaterThanOrEqualTo(8))
            .get();

    Constraint matchPassword =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Le password non corrispondono")
            .setCondition(
                passwordField.textProperty().isEqualTo(confermaPasswordField.textProperty()))
            .get();

    passwordField
        .getValidator()
        .constraint(required)
        .constraint(lenConstraint)
        .constraint(matchPassword);
    passwordField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                validatePassword.setVisible(false);
                passwordField.setStyle(null);
                confermaPasswordField.setStyle(null);

                passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
              }
            });
  }

  private void setValidateIban() {
    Constraint required =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Iban necessario")
            .setCondition(ibanField.textProperty().isNotEmpty())
            .get();

    ibanField.getValidator().constraint(required);
    ibanField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                validateIban.setVisible(false);
                ibanField.setStyle(null);
                ibanField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
              }
            });
  }

  private void setValidateDate() {
    Constraint required =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Data Scadenza necessario")
            .setCondition(dataScadenzaField.textProperty().isNotEmpty())
            .get();

    dataScadenzaField.getValidator().constraint(required);
    dataScadenzaField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                validateDate.setVisible(false);
                dataScadenzaField.setStyle(null);
                dataScadenzaField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
              }
            });
  }

  private void setValidateCvc() {
    Constraint required =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Cvc necessario")
            .setCondition(cvcField.textProperty().isNotEmpty())
            .get();

    cvcField.getValidator().constraint(required);
    cvcField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                validateCvc.setVisible(false);
                cvcField.setStyle(null);
                cvcField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
              }
            });
  }
}
