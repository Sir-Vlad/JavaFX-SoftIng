package it.prova.javafxsofting.controller.part_profilo_utente;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.controller.ScreenController;
import it.prova.javafxsofting.controller.ValidateForm;
import it.prova.javafxsofting.models.Utente;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;

/** Classe che gestisce il corpo della finestra */
@FunctionalInterface
interface BodyStage {
  void addBodyStage(Stage stage, AnchorPane root, MFXButton modificaButton);
}

public class ImpostazioniProfilo extends ValidateForm implements Initializable {
  static final List<String> REGIONI =
      List.of(
          "Abruzzo",
          "Basilicata",
          "Calabria",
          "Campania",
          "Emilia Romagna",
          "Friuli Venezia Giulia",
          "Lazio",
          "Liguria",
          "Lombardia",
          "Marche",
          "Molise",
          "Piemonte",
          "Puglia",
          "Sardegna",
          "Sicilia",
          "Toscana",
          "Trentino Alto Adige",
          "Umbria",
          "Val d'Aosta",
          "Veneto",
          "Trentino");

  @FXML private AnchorPane rootProfilo;
  @FXML private Label nomeCompletoText;
  @FXML private Label indirizzoText;
  @FXML private Label numTelefonoText;
  @FXML private Label emailText;
  @FXML private Label passwordText;

  private @NotNull String getSubDirectory() {
    return "utente/" + UserSession.getInstance().getUtente().getEmail() + "/";
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    nomeCompletoText
        .textProperty()
        .bindBidirectional(UserSession.getInstance().getUtente().nomeCompletoProperty());
    String indirizzo = UserSession.getInstance().getUtente().getIndirizzo();
    indirizzoText.setText(indirizzo == null ? " ---- " : indirizzo);
    String telefono = UserSession.getInstance().getUtente().getNumTelefono();
    numTelefonoText.setText(telefono == null ? " ---- " : telefono);
    emailText.setText(UserSession.getInstance().getUtente().getEmail());
    passwordText.setText("* ".repeat(UserSession.getInstance().getUtente().getPassword().length()));
  }

  public void modificaNome(@NotNull ActionEvent actionEvent) throws IOException {
    createNewStage(
        "Modifica nome",
        "controller/part_profilo_utente/modifica_nome.fxml",
        (stage, root, modificaButton) -> {
          MFXTextField newNomeValue = (MFXTextField) root.lookup("#newNomeValue");
          MFXTextField newCognomeValue = (MFXTextField) root.lookup("#newCognomeValue");

          onlyCharAlphabetical(newNomeValue);
          onlyCharAlphabetical(newCognomeValue);

          modificaButton.setOnAction(
              event -> {
                String newNome = newNomeValue.getText();
                String newCognome = newCognomeValue.getText();

                Utente newUtente = new Utente(UserSession.getInstance().getUtente());
                if (newNome.isEmpty() && newCognome.isEmpty()) {
                  return;
                } else if (newNome.isEmpty()) {
                  newUtente.setCognome(newCognome);
                } else if (newCognome.isEmpty()) {
                  newUtente.setNome(newNome);
                } else {
                  newUtente.setCognome(newCognome);
                  newUtente.setNome(newNome);
                }

                try {
                  Connection.putDataToBackend(newUtente, getSubDirectory());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                UserSession.getInstance().setUtente(newUtente);
                UserSession.getInstance()
                    .getUtente()
                    .setNomeCompleto(
                        UserSession.getInstance().getUtente().getNome()
                            + " "
                            + UserSession.getInstance().getUtente().getCognome());
                stage.close();
              });
        });

    actionEvent.consume();
  }

  public void modificaIndirizzo(@NotNull ActionEvent actionEvent) throws IOException {
    createNewStage(
        "Modifica Indirizzo",
        "controller/part_profilo_utente/modifica_indirizzo.fxml",
        (stage, root, modificaButton) -> {
          MFXTextField newVia = (MFXTextField) root.lookup("#newVia");
          MFXTextField newCivico = (MFXTextField) root.lookup("#newCivico");
          MFXTextField newCitta = (MFXTextField) root.lookup("#newCitta");
          MFXComboBox<String> newRegione = (MFXComboBox<String>) root.lookup("#newRegione");
          Label validateNewVia = (Label) root.lookup("#validateNewVia");
          Label validateNewCivico = (Label) root.lookup("#valideteNewCivico");
          Label validateNewCitta = (Label) root.lookup("#validateNewCitta");
          Label validateNewRegione = (Label) root.lookup("#validateNewRegione");

          newRegione.setItems(FXCollections.observableArrayList(REGIONI));
          onlyCharAlphabetical(newVia);
          onlyCharAlphabetical(newCitta);
          onlyDigit(newCivico);

          setValidateIndirizzo(
              newVia,
              newCivico,
              newCitta,
              newRegione,
              validateNewVia,
              validateNewCivico,
              validateNewCitta,
              validateNewRegione);

          modificaButton.setOnAction(
              event -> {
                List<Constraint> constrVia = newVia.validate();
                List<Constraint> constrCivico = newCivico.validate();
                List<Constraint> constrCitta = newCitta.validate();
                List<Constraint> constrRegione = newRegione.validate();

                showError(constrVia, newVia, validateNewVia);
                showError(constrCivico, newCivico, validateNewCivico);
                showError(constrCitta, newCitta, validateNewCitta);
                showError(constrRegione, newRegione, validateNewRegione);

                if (isFieldInvalid(newVia)
                    || isFieldInvalid(newCivico)
                    || isFieldInvalid(newCitta)
                    || isFieldInvalid(newRegione)) {
                  return;
                }

                String newViaValue = newVia.getText();
                String newCivicoValue = newCivico.getText();
                String newCittaValue = newCitta.getText();
                String newRegioneValue = newRegione.getValue();

                String newIndirizzo =
                    String.format(
                        "%s, %s, %s, %s",
                        newViaValue, newCivicoValue, newCittaValue, newRegioneValue);

                Utente newUtente = new Utente(UserSession.getInstance().getUtente());
                newUtente.setIndirizzo(newIndirizzo);

                try {
                  Connection.putDataToBackend(newIndirizzo, getSubDirectory());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                UserSession.getInstance().setUtente(newUtente);
                indirizzoText.setText(newIndirizzo);
                stage.close();
              });
        });
    actionEvent.consume();
  }

  public void modificaNumTelefono(@NotNull ActionEvent actionEvent) throws IOException {
    createNewStage(
        "Modifica Telefono",
        "controller/part_profilo_utente/modifica_telefono.fxml",
        (stage, root, modificaButton) -> {
          MFXTextField newNumTelefono = (MFXTextField) root.lookup("#newTelefono");
          Label validateTelefono = (Label) root.lookup("#validateTelefono");

          onlyDigit(newNumTelefono);
          newNumTelefono.setTextLimit(10);

          setValidateNumTelefono(newNumTelefono, validateTelefono);

          modificaButton.setOnAction(
              event -> {
                List<Constraint> constraints = newNumTelefono.validate();

                showError(constraints, newNumTelefono, validateTelefono);

                if (isFieldInvalid(newNumTelefono)) {
                  return;
                }

                String newNumTelefonoValue = newNumTelefono.getText();

                Utente newUtente = new Utente(UserSession.getInstance().getUtente());
                newUtente.setNumTelefono(newNumTelefonoValue);

                try {
                  Connection.putDataToBackend(newUtente, getSubDirectory());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                UserSession.getInstance().setUtente(newUtente);
                numTelefonoText.setText(newNumTelefonoValue);
                stage.close();
              });
        });

    actionEvent.consume();
  }

  public void modificaEmail(@NotNull ActionEvent actionEvent) throws IOException {
    createNewStage(
        "Modifica Email",
        "controller/part_profilo_utente/modifica_email.fxml",
        (stage, root, modificaButton) -> {
          MFXTextField newEmail = (MFXTextField) root.lookup("#newEmail");
          Label validateEmail = (Label) root.lookup("#validateEmail");

          setValidateEmail(newEmail, validateEmail);

          modificaButton.setOnAction(
              event -> {
                List<Constraint> constraints = newEmail.validate();
                showError(constraints, newEmail, validateEmail);

                if (isFieldInvalid(newEmail)) {
                  return;
                }

                String newEmailValue = newEmail.getText();

                Utente newUtente = null;
                newUtente = new Utente(UserSession.getInstance().getUtente());
                newUtente.setEmail(newEmailValue);

                try {
                  Connection.putDataToBackend(newUtente, getSubDirectory());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                UserSession.getInstance().setUtente(newUtente);

                emailText.setText(newEmailValue);
                stage.close();
              });
        });
    actionEvent.consume();
  }

  public void modificaPassword(@NotNull ActionEvent actionEvent) throws IOException {
    createNewStage(
        "Modifica Password",
        "controller/part_profilo_utente/modifica_password.fxml",
        (stage, root, modificaButton) -> {
          MFXTextField oldPassword = (MFXTextField) root.lookup("#oldPassword");
          MFXTextField newPassword = (MFXTextField) root.lookup("#newPassword");
          MFXTextField confermaPassword = (MFXTextField) root.lookup("#confermaPassword");
          Label validateOldPassword = (Label) root.lookup("#validateOldPassword");
          Label validateNewPassword = (Label) root.lookup("#validateNewPassword");
          Label validateConfermaPassword = (Label) root.lookup("#validateConfermaPassword");

          setValidateOldPassword(oldPassword, validateOldPassword);
          setValidateNewPassword(
              newPassword, confermaPassword, validateNewPassword, validateConfermaPassword);

          modificaButton.setOnAction(
              event -> {
                List<Constraint> constrOldPassword = oldPassword.validate();
                List<Constraint> constrNewPassword = newPassword.validate();
                List<Constraint> constrConfermaPassword = confermaPassword.validate();

                showError(constrOldPassword, oldPassword, validateOldPassword);
                showError(constrNewPassword, newPassword, validateNewPassword);
                showError(constrConfermaPassword, confermaPassword, validateConfermaPassword);

                if (isFieldInvalid(oldPassword)
                    || isFieldInvalid(newPassword)
                    || isFieldInvalid(confermaPassword)) {
                  return;
                }

                String newPasswordValue = newPassword.getText();

                Utente newUtente = new Utente(UserSession.getInstance().getUtente());
                newUtente.setPassword(newPasswordValue);

                try {
                  Connection.putDataToBackend(newUtente, getSubDirectory());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                UserSession.getInstance().setUtente(newUtente);
                passwordText.setText("* ".repeat(newPasswordValue.length()));
                stage.close();
              });
        });
    actionEvent.consume();
  }

  /**
   * Cancella l'account dell'utente
   *
   * @param actionEvent l'azione dell'utente
   */
  @FXML
  public void cancellaAccount(ActionEvent actionEvent) {
    boolean result;
    try {
      result =
          Connection.deleteDataToBackend(
              "utente/" + UserSession.getInstance().getUtente().getEmail());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (result) {
      Alert alert = new Alert(AlertType.INFORMATION, "Account eliminato");
      alert.showAndWait();
      UserSession.getInstance().setUtente(null);
      ScreenController.activate("home");
    }

    actionEvent.consume();
  }

  private void setValidateIndirizzo(
      MFXTextField newVia,
      MFXTextField newCivico,
      MFXTextField newCitta,
      MFXComboBox<String> newRegione,
      Label validateNewVia,
      Label validateNewCivico,
      Label validateNewCitta,
      Label validateNewRegione) {
    addConstraintRequired(newVia, "La via è necessario");
    addConstraintRequired(newCivico, "Il civico è necessario");
    addConstraintRequired(newCitta, "La città è necessaria");
    addConstraintRequired(newRegione, "La regione è necessaria");

    MFXTextField[] fields = new MFXTextField[] {newVia, newCivico, newCitta, newRegione};
    Label[] labels =
        new Label[] {validateNewVia, validateNewCivico, validateNewCitta, validateNewRegione};

    for (int i = 0; i < fields.length; i++) {
      MFXTextField field = fields[i];
      Label label = labels[i];
      field
          .getValidator()
          .validProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if (Boolean.TRUE.equals(newValue)) {
                  removeClassInvalid(field, label);
                }
              });
    }
  }

  private void setValidateNewPassword(
      MFXTextField newPassword,
      MFXTextField confermaPassword,
      Label validateNewPassword,
      Label validateConfermaPassword) {
    addConstraintRequired(newPassword, "Nuova password è necessaria");
    addConstraintRequired(confermaPassword, "Conferma password è necessaria");

    Constraint cmp =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("Le password non coincidono")
            .setCondition(newPassword.textProperty().isEqualTo(confermaPassword.textProperty()))
            .get();

    confermaPassword.getValidator().constraint(cmp);
    confermaPassword
        .getValidator()
        .validProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (Boolean.TRUE.equals(newValue)) {
                removeClassInvalid(newPassword, validateNewPassword);
                removeClassInvalid(confermaPassword, validateConfermaPassword);
              }
            });
  }

  private void setValidateOldPassword(MFXTextField oldPassword, Label validateOldPassword) {
    addConstraintRequired(oldPassword, "Vecchia password è necessaria");

    Constraint cmp =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage("La password inserita non è uguale")
            .setCondition(
                oldPassword
                    .textProperty()
                    .isEqualTo(UserSession.getInstance().getUtente().getPassword()))
            .get();

    oldPassword.getValidator().constraint(cmp);
    oldPassword
        .getValidator()
        .validProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (Boolean.TRUE.equals(newValue)) {
                removeClassInvalid(oldPassword, validateOldPassword);
              }
            });
  }

  private void setValidateNumTelefono(MFXTextField numTelefono, Label validateTelefono) {
    addConstraintRequired(numTelefono, "Numero di telefono è necessario");

    numTelefono
        .getValidator()
        .validProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (Boolean.TRUE.equals(newValue)) {
                removeClassInvalid(numTelefono, validateTelefono);
              }
            });
  }

  private void setValidateEmail(MFXTextField emailField, Label validateEmail) {
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
              if (Boolean.TRUE.equals(newValue)) {
                removeClassInvalid(emailField, validateEmail);
              }
            });
  }

  /**
   * Crea una nuova stage e la visualizza
   *
   * @param title titolo dello stage
   * @param pathFile path del fxml da caricare
   * @param bodyStage body da aggiungere allo stage
   * @throws IOException eccezione se il fxml non viene trovato
   */
  private void createNewStage(String title, String pathFile, @NotNull BodyStage bodyStage)
      throws IOException {
    Stage newStage = new Stage();

    newStage.setTitle(title);
    newStage.initModality(Modality.APPLICATION_MODAL);

    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(pathFile));
    AnchorPane root = fxmlLoader.load();

    MFXButton modificaButton = (MFXButton) root.lookup("#modificaNomeButton");
    MFXButton annullaButton = (MFXButton) root.lookup("#annullaBtn");

    bodyStage.addBodyStage(newStage, root, modificaButton);

    annullaButton.setOnAction(event -> newStage.close());
    Scene scene = new Scene(root);
    newStage.setScene(scene);

    newStage.showAndWait();
  }
}
