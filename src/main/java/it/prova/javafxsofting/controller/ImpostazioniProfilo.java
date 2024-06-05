package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
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

  static final List<String> PREFIX_TELEFONO =
      List.of(
          "+ 355", // Albania
          "+ 32", // Belgio
          "+ 359", // Bulgaria
          "+ 45", // Danimarca
          "+ 358", // Finlandia
          "+ 350", // Gibilterra
          "+ 299", // Groenlandia
          "+ 39", // Italia
          "+ 370", // Lituania
          "+ 356", // Malta
          "+ 47", // Norvegia
          "+ 351", // Portogallo
          "+ 40", // Romania
          "+ 421", // Slovacchia
          "+ 46", // Svezia
          "+ 36", // Ungheria
          "+ 376", // Andorra
          "+ 375", // Bielorussia
          "+ 357", // Cipro
          "+ 372", // Estonia
          "+ 33", // Francia
          "+ 44", // Gran Bretagna
          "+ 353", // Irlanda
          "+ 371", // Lettonia
          "+ 352", // Lussemburgo
          "+ 373", // Moldavia
          "+ 31", // Olanda
          "+ 377", // Principato Monaco
          "+ 7", // Russia
          "+ 386", // Slovenia
          "+ 41", // Svizzera
          "+ 43", // Austria
          "+ 387", // Bosnia Erzegovina
          "+ 385", // Croazia
          "+ 298", // Faer Oer Isole
          "+ 49", // Germania
          "+ 30", // Grecia
          "+ 354", // Islanda
          "+ 423", // Liechtenstein
          "+ 389", // Macedonia
          "+ 382", // Montenegro
          "+ 48", // Polonia
          "+ 420", // Repubblica Ceca
          "+ 381", // Serbia
          "+ 34", // Spagna
          "+ 380" // Ucraina
          );

  @FXML private AnchorPane rootProfilo;
  @FXML private Label nomeCompletoText;
  @FXML private Label indirizzoText;
  @FXML private Label numTelefonoText;
  @FXML private Label emailText;
  @FXML private Label passwordText;

  private String getSubDirectory() {
    return "utente/" + App.getUtente().getEmail() + "/";
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    nomeCompletoText.textProperty().bindBidirectional(App.getUtente().nomeCompletoProperty());
    String indirizzo = App.getUtente().getIndirizzo();
    indirizzoText.setText(indirizzo == null ? " ---- " : indirizzo);
    String telefono = App.getUtente().getNumTelefono();
    numTelefonoText.setText(telefono == null ? " ---- " : telefono);
    emailText.setText(App.getUtente().getEmail());
    passwordText.setText("* ".repeat(App.getUtente().getPassword().length()));
  }

  public void modificaNome(ActionEvent actionEvent) throws IOException {
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

                Utente newUtente = new Utente(App.getUtente());
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

                App.setUtente(newUtente);
                App.getUtente()
                    .setNomeCompleto(
                        App.getUtente().getNome() + " " + App.getUtente().getCognome());
                stage.close();
              });
        });

    actionEvent.consume();
  }

  public void modificaIndirizzo(ActionEvent actionEvent) throws IOException {
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

                Utente newUtente = new Utente(App.getUtente());
                newUtente.setIndirizzo(newIndirizzo);

                try {
                  Connection.putDataToBackend(newIndirizzo, getSubDirectory());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                App.setUtente(newUtente);
                indirizzoText.setText(newIndirizzo);
                stage.close();
              });
        });
    actionEvent.consume();
  }

  public void modificaNumTelefono(ActionEvent actionEvent) throws IOException {
    createNewStage(
        "Modifica Telefono",
        "controller/part_profilo_utente/modifica_telefono.fxml",
        (stage, root, modificaButton) -> {
          MFXTextField newNumTelefono = (MFXTextField) root.lookup("#newTelefono");
          MFXComboBox<String> prefissoTelefono =
              (MFXComboBox<String>) root.lookup("#prefissoTelefono");
          Label validateTelefono = (Label) root.lookup("#validateTelefono");

          onlyDigit(newNumTelefono);
          newNumTelefono.setTextLimit(10);

          prefissoTelefono.setItems(FXCollections.observableArrayList(PREFIX_TELEFONO));
          prefissoTelefono.setValue("+ 39");

          setValidateNumTelefono(newNumTelefono, validateTelefono);

          modificaButton.setOnAction(
              event -> {
                List<Constraint> constraints = newNumTelefono.validate();

                showError(constraints, newNumTelefono, validateTelefono);

                if (isFieldInvalid(newNumTelefono)) {
                  return;
                }

                String newNumTelefonoValue = newNumTelefono.getText();
                String prefixTelefono = prefissoTelefono.getValue();

                Utente newUtente = new Utente(App.getUtente());
                newUtente.setNumTelefono(newNumTelefonoValue);

                try {
                  Connection.putDataToBackend(newUtente, getSubDirectory());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                App.setUtente(newUtente);
                numTelefonoText.setText(prefixTelefono + " " + newNumTelefonoValue);
                stage.close();
              });
        });

    actionEvent.consume();
  }

  public void modificaEmail(ActionEvent actionEvent) throws IOException {
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
                newUtente = new Utente(App.getUtente());
                newUtente.setEmail(newEmailValue);

                try {
                  Connection.putDataToBackend(newUtente, getSubDirectory());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                App.setUtente(newUtente);

                emailText.setText(newEmailValue);
                stage.close();
              });
        });
    actionEvent.consume();
  }

  public void modificaPassword(ActionEvent actionEvent) throws IOException {
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

                Utente newUtente = new Utente(App.getUtente());
                newUtente.setPassword(newPasswordValue);

                try {
                  Connection.putDataToBackend(newUtente, getSubDirectory());
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                App.setUtente(newUtente);
                passwordText.setText("* ".repeat(newPasswordValue.length()));
                stage.close();
              });
        });
    actionEvent.consume();
  }

  public void cancellaAccount(ActionEvent actionEvent) {
    boolean result;
    try {
      result = Connection.deleteDataToBackend("utente/" + App.getUtente().getEmail());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (result) {
      Alert alert = new Alert(AlertType.INFORMATION, "Account eliminato");
      alert.showAndWait();
      App.setUtente(null);
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
            .setCondition(oldPassword.textProperty().isEqualTo(App.getUtente().getPassword()))
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

  private void createNewStage(String title, String pathFile, BodyStage bodyStage)
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
