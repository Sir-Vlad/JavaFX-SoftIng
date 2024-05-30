package it.prova.javafxsofting;

import javafx.scene.control.Alert;
import org.jetbrains.annotations.Contract;

public final class NotImplemented {

  @Contract(value = " -> fail", pure = true)
  private NotImplemented() {
    throw new IllegalStateException("Utility class");
  }

  public static void notImplemented() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Not Implemented");
    alert.setHeaderText("Not Implemented");
    alert.showAndWait();
  }
}
