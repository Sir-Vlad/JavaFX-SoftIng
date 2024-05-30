package it.prova.javafxsofting;

import java.util.HashMap;
import java.util.Map.Entry;
import javafx.scene.paint.Color;

public class ColoriAuto {
  private HashMap<String, Color> colorMap;

  public ColoriAuto() {
    colorMap = new HashMap<>();
    initializeColors();
  }

  public Color getColor(String colorName) {
    return colorMap.getOrDefault(colorName.toLowerCase(), Color.BLACK);
  }

  public String getNameColor(Color color) {
    for (Entry<String, Color> entry : colorMap.entrySet()) {
      String colorName = entry.getKey();
      Color colorValue = entry.getValue();
      if (colorValue == color) return colorName;
    }
    return null;
  }

  private void initializeColors() {
    colorMap.put("BLACK", Color.BLACK);
    colorMap.put("GRAY", Color.GRAY);
    colorMap.put("WHITE", Color.WHITE);
  }
}
