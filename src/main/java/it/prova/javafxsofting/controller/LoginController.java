package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.NotImplemented;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.models.Utente;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
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
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class LoginController extends ValidateForm implements Initializable {
  private static final String PATH_REMEMBER_UTENTE = "instance/utente";
  private final Logger logger = Logger.getLogger(LoginController.class.getName());
  @FXML private AnchorPane rootLogin;
  @FXML private MFXTextField emailField;
  @FXML private MFXPasswordField passwordField;
  @FXML private MFXCheckbox rememberMe;
  @FXML private Label validateEmail;
  @FXML private Label validatePassword;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // ? shortcuts
    rootLogin.setOnKeyPressed(
        event -> {
          if (event.getCode().equals(KeyCode.ENTER)) {
            logIn();
          }
        });
    // set validate field
    setValidateEmail();
    setValidatePassword();
  }

  /**
   * Funzione per tornare indietro
   *
   * @param actionEvent l'azione dell'utente
   */
  @FXML
  public void switchIndietro(@NotNull ActionEvent actionEvent) {
    ScreenController.back();
    clearField();
    actionEvent.consume();
  }

  @SneakyThrows
  @FXML
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
      Utente utente = Connection.getDataFromBackend("utente/" + emailField.getText(), Utente.class);
      if (utente != null) {
        UserSession.getInstance().setUtente(utente);
      } else {
        UserSession.clearSession();
        return;
      }
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.OK);
      alert.showAndWait();
      clearField();
      return;
    }

    if (!emailField.getText().equals(UserSession.getInstance().getUtente().getEmail())
        || !passwordField.getText().equals(UserSession.getInstance().getUtente().getPassword())) {
      Alert alert = new Alert(AlertType.ERROR, "Email o password errati", ButtonType.OK);
      alert.showAndWait();
      return;
    }

    logger.log(
        Level.INFO,
        () ->
            String.format(
                "Utente %s %s si è loggato",
                UserSession.getInstance().getUtente().getNome(),
                UserSession.getInstance().getUtente().getCognome()));

    if (rememberMe.isSelected()) {
      saveUtente();
    } else {
      Files.deleteIfExists(Path.of(PATH_REMEMBER_UTENTE).resolve("utente.txt"));
    }

    ScreenController.addScreen(
        "profilo",
        new FXMLLoader(
            App.class.getResource("controller/part_profilo_utente/profilo_utente.fxml")));

    clearField();

    switch (ScreenController.getBackPage()) {
      case "config" -> ScreenController.activate("config");
      case "vendiUsato" -> ScreenController.activate("vendiUsato");
      case "scegliUsato" -> ScreenController.activate("scegliUsato");
      case "autoUsataDetail" -> ScreenController.activate("autoUsataDetail");
      default -> ScreenController.activate("home");
    }
  }

  @FXML
  public void forgotPassword(@NotNull MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    mouseEvent.consume();
  }

  /**
   * Funzione per andare alla schermata di registrazione
   *
   * @param mouseEvent evento del mouse
   */
  @FXML
  public void switchRegister(@NotNull MouseEvent mouseEvent) {
    ScreenController.activate("registrazione");
    mouseEvent.consume();
  }

  @Override
  public void showError(@NotNull List<Constraint> constraints, MFXTextField field, Label label) {
    if (!constraints.isEmpty()) {
      super.showError(constraints, field, label);
    }
  }

  /**
   * Salva l'email dell'utente che si è loggato nella directory data
   *
   * @throws IOException eccezione se la directory data non esiste
   */
  private void saveUtente() throws IOException {
    Path path = Path.of(PATH_REMEMBER_UTENTE);
    try {
      Files.createDirectory(path);
    } catch (FileAlreadyExistsException ignored) {
      logger.info("La directory data esiste già");
    }

    // delete file if exists and create it
    Path fileRemember = path.resolve("utente.txt");
    if (Files.exists(fileRemember)) {
      Files.delete(fileRemember);
    }
    Path fileUtente = Files.createFile(fileRemember);
    Files.write(fileUtente, UserSession.getInstance().getUtente().getEmail().getBytes());
  }

  /** Pulisce i campi */
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
