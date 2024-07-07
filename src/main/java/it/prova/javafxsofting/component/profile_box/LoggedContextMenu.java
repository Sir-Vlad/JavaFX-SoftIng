package it.prova.javafxsofting.component.profile_box;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.controller.ScreenController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.jetbrains.annotations.NotNull;

public class LoggedContextMenu implements State {
  /**
   * Crea il pulsante del profilo
   *
   * @return Il pulsante del profilo
   */
  private @NotNull MenuItem getProfileItem() {
    MenuItem profile = new MenuItem("Profilo");
    profile.setId("profile");
    profile.setOnAction(
        actionEvent -> {
          ScreenController.addScreen(
              "profilo",
              new FXMLLoader(
                  App.class.getResource("controller/part_profilo_utente/profilo_utente.fxml")));
          ScreenController.activate("profilo");
          actionEvent.consume();
        });
    return profile;
  }

  /**
   * Crea il pulsante di logout
   *
   * @return il pulsante di logout
   */
  private @NotNull MenuItem getLogoutItem() {
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
          ScreenController.removeScreen("profilo");
          actionEvent.consume();
        });
    return logout;
  }

  @Override
  public ContextMenu createContextMenu() {
    ContextMenu contextMenu = new ContextMenu();
    MenuItem profile = getProfileItem();
    MenuItem logout = getLogoutItem();
    contextMenu.getItems().addAll(profile, new SeparatorMenuItem(), logout);
    return contextMenu;
  }
}
