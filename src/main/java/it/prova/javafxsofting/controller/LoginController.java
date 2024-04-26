package it.prova.javafxsofting.controller;

import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.NotImplemented;
import it.prova.javafxsofting.Utente;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class LoginController implements Initializable {
  @FXML public AnchorPane root;
  public VBox wrapperLogin;

  public MFXTextField emailField;
  public MFXPasswordField passwordField;
  public MFXCheckbox rememberMe;
  public MFXButton logInBtn;

  public Text register;
  public Text textRegister;
  public Label passwordLabel;
  public Label emailLabel;
  public Label forgotPasswordLabel;
  public HBox wrapperLogInBtn;

  public Label validateEmail;
  public Label validatePassword;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // set immagine di background
    BackgroundImage bg =
        new BackgroundImage(
            new Image(String.valueOf(App.class.getResource("immagini/car.jpeg"))),
            BackgroundRepeat.REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);

    root.setBackground(new Background(bg));

    // set background al wrapper del login
    wrapperLogin.setBackground(
        new Background(
            new BackgroundFill(Color.rgb(180, 183, 183, 0.85), new CornerRadii(25), Insets.EMPTY)));
    wrapperLogin.setEffect(
        new DropShadow(BlurType.GAUSSIAN, Color.rgb(198, 204, 197, 0.8506), 18, 0.3, 0, 0));
    wrapperLogin.setStyle(
        "-fx-border-color: #6F6F6F80; -fx-border-width: 1px 1px 1px; -fx-border-radius: " + "25; ");

    // set validate field
    setValidateEmail();
    setValidatePassword();
  }

  public void logIn(ActionEvent actionEvent) throws IOException {
    List<Constraint> constEmail = emailField.validate();
    List<Constraint> constPassword = passwordField.validate();

    showError(constEmail, emailField, validateEmail);
    showError(constPassword, passwordField, validatePassword);

    boolean isInvalidForm = fieldInvalid(emailField) || fieldInvalid(passwordField);

    if (isInvalidForm) {
      return;
    }

    // todo: check nel db

    App.utente = new Utente("Mattia", "Frigiola", "root", "root", "", LocalDate.now(), "123");

    if (!emailField.getText().equals(App.utente.getEmail())
        || !passwordField.getText().equals(App.utente.getPassword())) {
      Alert alert = new Alert(AlertType.ERROR, "Email o password errati", ButtonType.OK);
      alert.showAndWait();

      // pulisco i campu
      emailField.setText("");
      passwordField.setText("");
      return;
    }

    ScreenController.addScreen(
        "profilo",
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("profile_account.fxml"))));

    // pulisco i campu
    emailField.setText("");
    passwordField.setText("");

    // redirect alla pagina del profilo
    ScreenController.activate("config");

    actionEvent.consume();
  }

  public void forgotPassword(MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    mouseEvent.consume();
  }

  public void switchRegister(MouseEvent mouseEvent) {
    ScreenController.activate("registrazione");
    mouseEvent.consume();
  }

  private boolean fieldInvalid(MFXTextField field) {
    return field.getPseudoClassStates().stream()
        .anyMatch(pseudoClass -> pseudoClass.equals(INVALID_PSEUDO_CLASS));
  }

  private void showError(List<Constraint> constraints, MFXTextField field, Label label) {
    if (!constraints.isEmpty()) {
      field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
      field.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
      label.setText(constraints.getFirst().getMessage());
      label.setVisible(true);
      new animatefx.animation.Shake(field).play();
    }
  }

  private void setValidateEmail() {
    Constraint request =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Email necessaria")
            .setCondition(emailField.textProperty().isNotEmpty())
            .get();

    emailField.getValidator().constraint(request);
    emailField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                validateEmail.setVisible(false);
                emailField.setStyle(null);
                emailField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
              }
            });
  }

  private void setValidatePassword() {
    Constraint request =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Password necessaria")
            .setCondition(passwordField.textProperty().isNotEmpty())
            .get();

    passwordField.getValidator().constraint(request);
    passwordField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                validatePassword.setVisible(false);
                passwordField.setStyle(null);
                passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
              }
            });
  }
}
