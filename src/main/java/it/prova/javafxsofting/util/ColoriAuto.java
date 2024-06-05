package it.prova.javafxsofting.util;

import java.util.HashMap;
import java.util.Map.Entry;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ColoriAuto {
  private final HashMap<String, Color> colorMap;

  public ColoriAuto() {
    colorMap = new HashMap<>();
    initializeColors();
  }

  public Color getColor(@NotNull String colorName) {
    return colorMap.getOrDefault(colorName.toLowerCase(), Color.BLACK);
  }

  public @Nullable String getNameColor(Color color) {
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
