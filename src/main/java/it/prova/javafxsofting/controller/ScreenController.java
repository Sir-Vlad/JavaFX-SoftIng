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
  private static Scene main = null;
  private static String nameMain = null;
  @Getter private static String backPage = null;

  @Contract(value = " -> fail", pure = true)
  private ScreenController() {
    throw new IllegalStateException("Utility class");
  }

  public static void setMain(Scene main) {
    ScreenController.main = main;
  }

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

  public static void addScreen(String name, FXMLLoader controller) {
    SCREEN_MAP.put(name, controller);
  }

  public static void back() {
    if (backPage == null) {
      return;
    }
    activate(backPage);
  }

  public static void removeScreen(String name) {
    SCREEN_MAP.remove(name);
    SCREEN_PANE.remove(name);
  }
}
