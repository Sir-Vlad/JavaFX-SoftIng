package it.prova.javafxsofting.component.profile_box;

import it.prova.javafxsofting.controller.ScreenController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class UnloggedContextMenu implements State {
  @Override
  public ContextMenu createContextMenu() {
    ContextMenu contextMenu = new ContextMenu();
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
    return contextMenu;
  }
}
