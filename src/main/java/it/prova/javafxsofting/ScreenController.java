package it.prova.javafxsofting;

import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class ScreenController {
  private static HashMap<String, Pane> screenMap = new HashMap<>();
  private static Scene main;

  public ScreenController(Scene main) {
    this.main = main;
  }

  protected void addScreen(String name, Pane pane) {
    screenMap.put(name, pane);
  }

  protected static void removeScreen(String name) {
    screenMap.remove(name);
  }

  protected static void activate(String name) {
    main.setRoot(screenMap.get(name));
  }
}
