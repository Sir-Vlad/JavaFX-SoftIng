package it.prova.javafxsofting;

import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class ScreenController {
  private static final HashMap<String, Pane> SCREEN_MAP = new HashMap<>();
  private static Scene main = null;

  protected static void setMain(Scene main) {
    ScreenController.main = main;
  }

  protected static void removeScreen(String name) {
    SCREEN_MAP.remove(name);
  }

  protected static void activate(String name) {
    main.setRoot(SCREEN_MAP.get(name));
  }

  protected static void addScreen(String name, Pane pane) {
    SCREEN_MAP.put(name, pane);
  }
}
