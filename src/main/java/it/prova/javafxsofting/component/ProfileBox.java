package it.prova.javafxsofting.component;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.controller.ScreenController;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
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

  private static @NotNull MenuItem getProfile() {
    MenuItem profile = new MenuItem("Profilo");
    profile.setId("profile");
    profile.setOnAction(
        actionEvent -> {
          try {
            ScreenController.addScreen(
                "profilo",
                FXMLLoader.load(
                    Objects.requireNonNull(
                        App.class.getResource("controller/profilo_utente.fxml"))));
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
          ContextMenu contextMenuAccount = contextMenuAccount();
          Point2D point = root.localToScreen(Point2D.ZERO);
          // posiziono la finestra rispetto al bottone
          double x = point.getX() - 25;
          double y = point.getY() + 50;
          openContextMenu(mouseEvent, contextMenuAccount, isMenuAccountOpen, x, y);
          isMenuAccountOpen = !isMenuAccountOpen;
        });
  }

  private void openContextMenu(
      MouseEvent mouseEvent, ContextMenu menu, boolean open, double xPos, double yPos) {
    if (open) {
      menu.hide();
    } else {
      menu.show(root, xPos, yPos);
    }
    mouseEvent.consume();
  }

  private @NotNull ContextMenu contextMenuAccount() {
    ContextMenu contextMenu = new ContextMenu();

    if (App.utente == null) {
      MenuItem signIn = new MenuItem("Sign In");
      signIn.setId("signIn");
      signIn.setOnAction(
          actionEvent -> {
            ScreenController.activate("login");
            actionEvent.consume();
          });
      contextMenu.getItems().add(signIn);
    } else {
      MenuItem profile = getProfile();

      MenuItem signOut = new MenuItem("Sign Out");
      signOut.setId("signOut");
      signOut.setOnAction(
          actionEvent -> {
            App.utente = null;
            ScreenController.activate("home");
            actionEvent.consume();
          });
      contextMenu.getItems().addAll(profile, new SeparatorMenuItem(), signOut);
    }

    return contextMenu;
  }
}
