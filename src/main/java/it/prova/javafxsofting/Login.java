package it.prova.javafxsofting;

import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Login implements Initializable {
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
    emailField.setTextFill(Color.BLACK);
    passwordField.setTextFill(Color.BLACK);
    textRegister.setFill(Color.BLACK);
    rememberMe.setTextFill(Color.BLACK);
    forgotPasswordLabel.setTextFill(Color.BLACK);

    BackgroundImage bg =
        new BackgroundImage(
            new Image(String.valueOf(getClass().getResource("immagini/car.jpeg"))),
            BackgroundRepeat.REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);

    root.setBackground(new Background(bg));

    ImageView imageView =
        new ImageView(
            Objects.requireNonNull(getClass().getResource("immagini/car.jpeg")).toString());
    imageView.setEffect(new GaussianBlur(40));

    wrapperLogin.setBackground(
        new Background(
            new BackgroundFill(Color.rgb(184, 180, 172, 0.85), new CornerRadii(25), Insets.EMPTY)));
    wrapperLogin.setEffect(
        new DropShadow(BlurType.GAUSSIAN, Color.rgb(198, 204, 197, 0.8506), 18, 0.3, 0, 0));
    wrapperLogin.setStyle(
        "-fx-border-color: #6F6F6F80; -fx-border-width: 1px 1px 1px; -fx-border-radius: " + "25; ");

    setValidateEmail();
    setValidatePassword();
  }

  public void logIn(ActionEvent actionEvent) {
    List<Constraint> constEmail = emailField.validate();
    List<Constraint> constPassword = passwordField.validate();
    boolean validated = fieldInvalid(emailField) || fieldInvalid(passwordField);

    showError(constEmail, emailField, validateEmail);
    showError(constPassword, passwordField, validatePassword);

    // todo: check nel db

    if (!validated) {
      return;
    }

    // todo: redirect alla home

    actionEvent.consume();
  }

  public void forgotPassword(MouseEvent mouseEvent) {
    NotImplemented.notImplemented();
    mouseEvent.consume();
  }

  public void switchRegister(MouseEvent mouseEvent) throws IOException {
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
