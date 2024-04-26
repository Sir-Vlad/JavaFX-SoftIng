package it.prova.javafxsofting;

import javafx.scene.control.Alert;

public final class NotImplemented {
  public static void notImplemented() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Not Implemented");
    alert.setHeaderText("Not Implemented");
    alert.showAndWait();
  }
}
