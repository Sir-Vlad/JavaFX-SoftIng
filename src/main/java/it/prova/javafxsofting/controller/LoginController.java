package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.NotImplemented;
import it.prova.javafxsofting.models.Utente;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class LoginController extends ValidateForm implements Initializable {
  @FXML private AnchorPane root;
  @FXML private VBox wrapperLogin;

  @FXML private MFXTextField emailField;
  @FXML private MFXPasswordField passwordField;
  @FXML private MFXCheckbox rememberMe;
  @FXML private MFXButton logInBtn;

  @FXML private Text register;
  @FXML private Text textRegister;
  @FXML private Label passwordLabel;
  @FXML private Label emailLabel;
  @FXML private Label forgotPasswordLabel;
  @FXML private HBox wrapperLogInBtn;

  @FXML private Label validateEmail;
  @FXML private Label validatePassword;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // ? shortcuts
    root.setOnKeyPressed(
        event -> {
          if (event.getCode().equals(KeyCode.ENTER)) {
            logIn();
          }
        });
    // set validate field
    setValidateEmail();
    setValidatePassword();
  }

  @SneakyThrows
  public void logIn() {
    List<Constraint> constEmail = emailField.validate();
    List<Constraint> constPassword = passwordField.validate();

    showError(constEmail, emailField, validateEmail);
    showError(constPassword, passwordField, validatePassword);

    boolean isInvalidForm = isFieldInvalid(emailField) || isFieldInvalid(passwordField);

    if (isInvalidForm) {
      return;
    }

    // check nel db
    try {
      App.setUtente(Connection.getDataFromBackend("utente/" + emailField.getText(), Utente.class));
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.OK);
      alert.showAndWait();
      clearField();
      return;
    }

    if (!emailField.getText().equals(App.getUtente().getEmail())
        || !passwordField.getText().equals(App.getUtente().getPassword())) {
      Alert alert = new Alert(AlertType.ERROR, "Email o password errati", ButtonType.OK);
      alert.showAndWait();
      clearField();
      return;
    }

    System.out.println(App.getUtente());

    ScreenController.addScreen(
        "profilo",
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("profilo_utente.fxml"))));
    clearField();
    // redirect alla pagina del profilo
    ScreenController.activate("home");
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
  public void showError(@NotNull List<Constraint> constraints, MFXTextField field, Label label) {
    if (!constraints.isEmpty()) {
      super.showError(constraints, field, label);
    }
  }

  private void clearField() {
    emailField.setText("");
    passwordField.setText("");
  }

  private void setValidateEmail() {
    addConstraintRequired(emailField, "Email necessaria");
    emailField
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (Boolean.TRUE.equals(newValue)) {
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
              if (Boolean.TRUE.equals(newValue)) {
                removeClassInvalid(passwordField, validatePassword);
              }
            });
  }
}
