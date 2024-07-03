package it.prova.javafxsofting.controller;

import java.io.IOException;
import java.util.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ScreenController {
  @Getter private static final HashMap<String, FXMLLoader> SCREEN_MAP = new HashMap<>();
  private static final HashMap<String, Pane> SCREEN_PANE = new HashMap<>();
  @lombok.Setter private static Scene main = null;
  private static String nameMain = null;
  @Getter private static String backPage = null;

  @Contract(value = " -> fail", pure = true)
  private ScreenController() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Cambia la schermata dell'applicazione
   *
   * @param name il nome della schermata
   */
  public static void activate(@NotNull String name) {
    backPage = backPage == null ? "home" : nameMain;
    nameMain = name;
    if (SCREEN_MAP.containsKey(name) && !SCREEN_PANE.containsKey(name)) {
      try {
        Pane pane = SCREEN_MAP.get(name).load();
        SCREEN_PANE.put(name, pane);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    main.setRoot(SCREEN_PANE.get(name));
  }

  /**
   * Aggiunge una schermata
   *
   * @param name il nome della schermata
   * @param controller il controller della schermata
   */
  public static void addScreen(String name, FXMLLoader controller) {
    SCREEN_MAP.put(name, controller);
  }

  /** Ritorna alla schermata precedente */
  public static void back() {
    if (backPage == null) {
      return;
    }
    activate(backPage);
  }

  /**
   * Rimuove una schermata dall'applicazione
   *
   * @param name il nome della schermata
   */
  public static void removeScreen(String name) {
    SCREEN_MAP.remove(name);
    SCREEN_PANE.remove(name);
  }
}
