package it.prova.javafxsofting.component;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.controller.ScreenController;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

public class ProfileBox extends VBox implements Initializable {
  @FXML Pane immagine;
  @FXML VBox root;
  private boolean isMenuAccountOpen = false;
  private ContextMenu contextMenuAccount;

  public ProfileBox() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("profileBox.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private @NotNull MenuItem getProfile() {
    MenuItem profile = new MenuItem("Profilo");
    profile.setId("profile");
    profile.setOnAction(
        actionEvent -> {
          try {
            ScreenController.addScreen(
                "profilo",
                FXMLLoader.load(
                    Objects.requireNonNull(
                        App.class.getResource(
                            "controller/part_profilo_utente/profilo_utente.fxml"))));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          ScreenController.activate("profilo");
          actionEvent.consume();
        });
    return profile;
  }

  public void setImage(String path) {
    immagine.setStyle("-fx-background-image: url(" + path + ")");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    root.setOnMouseClicked(
        mouseEvent -> {
          contextMenuAccount = contextMenuAccount();
          contextMenuAccount.setOnShown(
              event -> {
                Bounds bounds = root.localToScreen(root.getBoundsInLocal());
                double buttonBottomRightX = bounds.getMinX() + bounds.getWidth();
                double buttonBottomRightY = bounds.getMinY() + bounds.getHeight();

                // Posizionamento del contextMenu in modo che l'angolo in alto a destra si allinei
                // con l'angolo in basso a destra del pulsante
                contextMenuAccount.setX(buttonBottomRightX - contextMenuAccount.getWidth() + 8);
                contextMenuAccount.setY(buttonBottomRightY);
              });

          openContextMenu(mouseEvent, contextMenuAccount, isMenuAccountOpen);
          isMenuAccountOpen = !isMenuAccountOpen;
        });
  }

  private void openContextMenu(MouseEvent mouseEvent, ContextMenu menu, boolean open) {
    if (open) {
      menu.hide();
    } else {
      menu.show(root, Side.BOTTOM, 0, 0);
    }
    mouseEvent.consume();
  }

  private @NotNull ContextMenu contextMenuAccount() {
    ContextMenu contextMenu = new ContextMenu();

    if (UserSession.getInstance().getUtente() == null) {
      MenuItem login = new MenuItem("Login");
      login.setId("login");
      login.setOnAction(
          actionEvent -> {
            ScreenController.activate("login");
            actionEvent.consume();
          });
      MenuItem registrazione = new MenuItem("Registrazione");
      registrazione.setId("registrazione");
      registrazione.setOnAction(
          actionEvent -> {
            ScreenController.activate("registrazione");
            actionEvent.consume();
          });
      contextMenu.getItems().addAll(login, registrazione);
    } else {
      MenuItem profile = getProfile();

      MenuItem logout = new MenuItem("Logout");
      logout.setId("logout");
      logout.setOnAction(
          actionEvent -> {
            UserSession.clearSession();
            try {
              Files.deleteIfExists(Path.of("instance/utente/utente.txt"));
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            ScreenController.activate("home");
            actionEvent.consume();
          });
      contextMenu.getItems().addAll(profile, new SeparatorMenuItem(), logout);
    }

    return contextMenu;
  }
}
