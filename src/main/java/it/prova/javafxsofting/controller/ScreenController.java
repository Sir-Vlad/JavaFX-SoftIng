package it.prova.javafxsofting.controller;

import java.util.*;
import java.util.Map.Entry;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class ScreenController {
  private static final HashMap<String, Pane> SCREEN_MAP = new HashMap<>();
  private static Scene main = null;
  private static String backPage = null;

  public static void setMain(Scene main) {
    ScreenController.main = main;
  }

  public static void activate(String name) {
    if (backPage == null) {
      backPage = "home";
    } else {
      backPage = getBackPage(name);
    }
    main.setRoot(SCREEN_MAP.get(name));
  }

  public static void addScreen(String name, Pane pane) {
    SCREEN_MAP.put(name, pane);
  }

  public static void back() {
    if (backPage == null) {
      return;
    }
    activate(backPage);
  }

  public static void removeScreen(String name) {
    SCREEN_MAP.remove(name);
  }

  private static String getBackPage(String name) {
    var page =
        SCREEN_MAP.entrySet().stream()
            .filter(pane -> Objects.equals(pane.getValue(), main.getRoot()))
            .map(Entry::getKey)
            .toList();

    if (page.isEmpty()) {
      return name;
    } else {
      return page.getFirst();
    }
  }
}
