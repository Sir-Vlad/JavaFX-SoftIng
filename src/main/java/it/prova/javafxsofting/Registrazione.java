package it.prova.javafxsofting;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import io.github.palexdev.mfxcore.utils.converters.DateStringConverter;
import javafx.beans.binding.Bindings;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;

public class Registrazione implements Initializable {
    @FXML private AnchorPane root;
    @FXML private VBox       wrapperRegistrazione;
    
    @FXML private Label nomeLabel;
    @FXML private Label cognomeLabel;
    @FXML private Label emailLabel;
    @FXML private Label passwordLabel;
    @FXML private Label confermaPasswordLabel;
    @FXML private Label ibanLabel;
    @FXML private Label dataScadenzaLabel;
    @FXML private Label cvcLabel;
    
    @FXML private MFXTextField     ibanField;
    @FXML private MFXTextField     nomeField;
    @FXML private MFXTextField     cognomeField;
    @FXML private MFXTextField     emailField;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXPasswordField confermaPasswordField;
    @FXML private MFXDatePicker    dataScadenzaField;
    @FXML private MFXTextField     cvcField;
    @FXML private MFXButton        createAccountBtn;
    
    @FXML private Label validateNome;
    @FXML private Label validateCognome;
    @FXML private Label validateEmail;
    @FXML private Label validatePassword;
    @FXML private Label validateIban;
    @FXML private Label validateCvc;
    @FXML private Label validateDate;
    
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BackgroundImage bg = new BackgroundImage(
                new Image(String.valueOf(getClass().getResource("immagini/car.jpeg"))),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        
        root.setBackground(new Background(bg));
        
        
        wrapperRegistrazione.setBackground(new Background(
                new BackgroundFill(Color.rgb(183, 180, 172, 0.85), new CornerRadii(25),
                        Insets.EMPTY)));
        wrapperRegistrazione.setEffect(
                new DropShadow(BlurType.GAUSSIAN, Color.rgb(198, 204, 197, 0.8506), 18, 0.3, 0, 0));
        wrapperRegistrazione.setStyle(
                "-fx-border-color: #6F6F6F80; -fx-border-width: 1px 1px 1px; -fx-border-radius: " +
                "25; ");
        
        // visualizzare nella data solo il mese e l'anno
        dataScadenzaField.setConverterSupplier(
                () -> new DateStringConverter("MM/yy", dataScadenzaField.getLocale()));
        
        cvcField.setTextLimit(3);
//        cvcField.textProperty().addListener((observable, oldValue, newValue) -> {
//            if (!newValue.matches("\\d*")) {
//                newValue = newValue.replaceAll("[^0-9]", "");
//                cvcField.setText(newValue);
//            } else {
//                cvcField.setText(newValue);
//            }
//        });
//
//        UnaryOperator<TextFormatter.Change> intergerFilter = change -> {
//            String newText = change.getControlNewText();
//            if (!newText.matches("-?([1-9][0-9]*)?")) {
//                return change;
//            }
//            return null;
//        };
//
        ibanField.setTextLimit(16);
//        ibanField.delegateSetTextFormatter(
//                new TextFormatter<>(new IntegerStringConverter(), null, intergerFilter));
        
        
        setValidateNome();
        setValidateCognome();
        setValidateEmail();
        setValidatePassword();
        setValidateIban();
        setValidateDate();
        setValidateCvc();
    }
    
    public void createAccount(ActionEvent actionEvent) {
        List<Constraint> nomeConstr     = nomeField.validate();
        List<Constraint> cognomeConstr  = cognomeField.validate();
        List<Constraint> emailConstr    = emailField.validate();
        List<Constraint> passwordConstr = passwordField.validate();
        List<Constraint> confpwdConstr  = confermaPasswordField.validate();
        List<Constraint> ibanConstr     = ibanField.validate();
        List<Constraint> dateConstr     = dataScadenzaField.validate();
        List<Constraint> cvcConstr      = cvcField.validate();
        
        showError(nomeConstr, nomeField, validateNome);
        showError(cognomeConstr, cognomeField, validateCognome);
        showError(emailConstr, emailField, validateEmail);
        showError(passwordConstr, passwordField, validatePassword);
        showError(confpwdConstr, confermaPasswordField, validatePassword);
        showError(ibanConstr, ibanField, validateIban);
        showError(dateConstr, dataScadenzaField, validateDate);
        showError(cvcConstr, cvcField, validateCvc);
        
        System.out.println(cvcField.getText());
        
        if (fieldInvalid(nomeField) || fieldInvalid(cognomeField) || fieldInvalid(emailField) ||
            fieldInvalid(passwordField) || fieldInvalid(confermaPasswordField) || fieldInvalid(
                ibanField) || fieldInvalid(dataScadenzaField) || fieldInvalid(cvcField)) {
            actionEvent.consume();
            return;
        }
        
        System.out.println("Valido");
        actionEvent.consume();
    }
    
    private boolean fieldInvalid(MFXTextField field) {
        return field.getPseudoClassStates().stream().anyMatch(
                pseudoClass -> pseudoClass.equals(INVALID_PSEUDO_CLASS));
    }
    
    private void showError(List<Constraint> constraints, MFXTextField field, Label label) {
        if (!constraints.isEmpty()) {
            field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
            label.setText(constraints.getFirst().getMessage());
            label.setVisible(true);
        }
    }
    
    
    private void setValidateNome() {
        Constraint required = Constraint.Builder.build().setSeverity(Severity.ERROR).setMessage(
                "Nome necessario").setCondition(nomeField.textProperty().isNotEmpty()).get();
        
        nomeField.getValidator().constraint(required);
        nomeField.getValidator().validProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        validateNome.setVisible(false);
                        nomeField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
                    }
                });
        nomeField.delegateFocusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                List<Constraint> constraints = nomeField.validate();
                showError(constraints, nomeField, validateNome);
            }
        });
    }
    
    private void setValidateCognome() {
        Constraint required = Constraint.Builder.build().setSeverity(Severity.ERROR).setMessage(
                "Cognome necessario").setCondition(cognomeField.textProperty().isNotEmpty()).get();
        
        cognomeField.getValidator().constraint(required);
        cognomeField.getValidator().validProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        validateCognome.setVisible(false);
                        cognomeField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
                    }
                });
    }
    
    private void setValidateEmail() {
        Constraint required = Constraint.Builder.build().setSeverity(Severity.ERROR).setMessage(
                "Email necessaria").setCondition(emailField.textProperty().isNotEmpty()).get();
        
        Constraint emailValid = Constraint.Builder.build().setSeverity(Severity.ERROR).setMessage(
                "Email non valida").setCondition(emailField
                .textProperty()
                .isEmpty()
                .or(Bindings.createBooleanBinding(
                        () -> EmailValidator.getInstance().isValid(emailField.getText()),
                        emailField.textProperty()))).get();
        
        emailField.getValidator().constraint(emailValid).constraint(required);
        emailField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                validateEmail.setVisible(false);
                emailField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
            }
        });
    }
    
    
    private void setValidatePassword() {
        Constraint required = Constraint.Builder
                .build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password necessaria")
                .setCondition(passwordField.textProperty().isNotEmpty())
                .get();
        
        Constraint lenConstraint = Constraint.Builder
                .build()
                .setSeverity(Severity.ERROR)
                .setMessage("Password deve essere almeno 8 caratteri")
                .setCondition(passwordField.textProperty().length().greaterThanOrEqualTo(8))
                .get();
        
        Constraint matchPassword = Constraint.Builder
                .build()
                .setSeverity(Severity.ERROR)
                .setMessage("Le password non corrispondono")
                .setCondition(passwordField
                        .textProperty()
                        .isEqualTo(confermaPasswordField.textProperty()))
                .get();
        
        passwordField.getValidator().constraint(required).constraint(lenConstraint).constraint(
                matchPassword);
        passwordField.getValidator().validProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        validatePassword.setVisible(false);
                        passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
                    }
                });
    }
    
    private void setValidateIban() {
        Constraint required = Constraint.Builder.build().setSeverity(Severity.ERROR).setMessage(
                "Iban necessario").setCondition(ibanField.textProperty().isNotEmpty()).get();
        
        ibanField.getValidator().constraint(required);
        ibanField.getValidator().validProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        validateIban.setVisible(false);
                        ibanField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
                    }
                });
    }
    
    private void setValidateDate() {
        Constraint required = Constraint.Builder.build().setSeverity(Severity.ERROR).setMessage(
                "Data Scadenza necessario").setCondition(
                dataScadenzaField.textProperty().isNotEmpty()).get();
        
        dataScadenzaField.getValidator().constraint(required);
        dataScadenzaField.getValidator().validProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        validateDate.setVisible(false);
                        dataScadenzaField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
                    }
                });
    }
    
    private void setValidateCvc() {
        Constraint required = Constraint.Builder.build().setSeverity(Severity.ERROR).setMessage(
                "Cvc necessario").setCondition(cvcField.textProperty().isNotEmpty()).get();
        
        cvcField.getValidator().constraint(required);
        cvcField.getValidator().validProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        validateCvc.setVisible(false);
                        cvcField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
                    }
                });
    }
    
    
}

