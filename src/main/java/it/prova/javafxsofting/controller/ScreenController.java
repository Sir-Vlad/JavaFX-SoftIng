package it.prova.javafxsofting.controller;

import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class ScreenController {
  private static final HashMap<String, Pane> SCREEN_MAP = new HashMap<>();
  private static Scene main = null;
  private static Pane backpage = null;

  public static void setMain(Scene main) {
    ScreenController.main = main;
  }

  public static void removeScreen(String name) {
    SCREEN_MAP.remove(name);
  }

  public static void activate(String name) {
    main.setRoot(SCREEN_MAP.get(name));
  }

  public static void addScreen(String name, Pane pane) {
    SCREEN_MAP.put(name, pane);
  }
}
