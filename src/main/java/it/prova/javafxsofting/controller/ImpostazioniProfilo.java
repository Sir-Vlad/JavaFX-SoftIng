package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.NotImplemented;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ImpostazioniProfilo implements Initializable {
  public AnchorPane rootProfilo;
  @FXML private Label nomeCompletoText;
  @FXML private Label indirizzoText;
  @FXML private Label numTelefonoText;
  @FXML private Label emailText;
  @FXML private Label passwordText;

  private Pane root;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    nomeCompletoText.setText(App.getUtente().getNome() + " " + App.getUtente().getCognome());
    //    nomeCompletoText.textProperty().bindBidirectional(App.getUtente().getNomeCompleto());
    emailText.setText(App.getUtente().getEmail());
    passwordText.setText("* ".repeat(App.getUtente().getPassword().length()));
  }

  public void modificaNome(ActionEvent actionEvent) throws IOException {
    //    NotImplemented.notImplemented();
    Stage newStage = new Stage();

    newStage.setTitle("Modifica nome");
    newStage.initModality(Modality.APPLICATION_MODAL);

    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("controller/modifica_nome.fxml"));
    AnchorPane root = fxmlLoader.load();

    MFXTextField newNomeValue = (MFXTextField) root.lookup("#newNomeValue");
    MFXTextField newCognomeValue = (MFXTextField) root.lookup("#newCognomeValue");
    MFXButton modificaButton = (MFXButton) root.lookup("#modificaNomeButton");

    modificaButton.setOnAction(
        event -> {
          String newNome = newNomeValue.getText();
          String newCognome = newCognomeValue.getText();

          // todo: fare prima la put sul db e poi modificare i dati locali

          App.getUtente().setNome(newNome);
          App.getUtente().setCognome(newCognome);

          Platform.runLater(
              () -> {
                nomeCompletoText.setText(
                    App.getUtente().getNome() + " " + App.getUtente().getCognome());
              });
          newStage.close();
        });

    Scene scene = new Scene(root);
    newStage.setScene(scene);

    newStage.showAndWait();
    actionEvent.consume();
  }

  public void modificaIndirizzo(ActionEvent actionEvent) {
    NotImplemented.notImplemented();
    actionEvent.consume();
  }

  public void modificaNumTelefono(ActionEvent actionEvent) {
    NotImplemented.notImplemented();
    actionEvent.consume();
  }

  public void modificaEmail(ActionEvent actionEvent) {
    NotImplemented.notImplemented();
    actionEvent.consume();
  }

  public void modificaPassword(ActionEvent actionEvent) {
    NotImplemented.notImplemented();
    actionEvent.consume();
  }

  public void cancellaAccount(ActionEvent actionEvent) {
    try {
      Connection.deleteDataToBackend("utente/" + App.getUtente().getEmail());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    actionEvent.consume();
  }
}
