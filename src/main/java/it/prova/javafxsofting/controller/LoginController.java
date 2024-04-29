package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class LoginController extends ValidateForm implements Initializable {
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
    // set validate field
    setValidateEmail();
    setValidatePassword();
  }

  public void logIn(ActionEvent actionEvent) throws IOException {
    List<Constraint> constEmail = emailField.validate();
    List<Constraint> constPassword = passwordField.validate();

    showError(constEmail, emailField, validateEmail);
    showError(constPassword, passwordField, validatePassword);

    boolean isInvalidForm = isFieldInvalid(emailField) || isFieldInvalid(passwordField);

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
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("profilo_utente.fxml"))));

    // pulisco i campu
    emailField.setText("");
    passwordField.setText("");

    // redirect alla pagina del profilo
    ScreenController.activate("home");

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

  @Override
  public void showError(List<Constraint> constraints, MFXTextField field, Label label) {
    if (!constraints.isEmpty()) {
      super.showError(constraints, field, label);
      new animatefx.animation.Shake(field).play();
    }
  }

  private void setValidateEmail() {
    addConstraintRequired(emailField, "Email necessaria");
    emailField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                removeClassInvalid(emailField, validateEmail);
              }
            });
  }

  private void setValidatePassword() {
    addConstraintRequired(passwordField, "Password necessaria");
    passwordField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (newValue) {
                removeClassInvalid(passwordField, validatePassword);
              }
            });
  }
}
