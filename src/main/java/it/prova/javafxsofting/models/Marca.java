package it.prova.javafxsofting.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum Marca {
  NISSAN(),
  MAZDA(),
  VOLKSWAGEN(),
  FORD(),
  HONDA(),
  AUDI(),
  BMW(),
  ;

  @Contract(pure = true)
  Marca() {}

  public static @Nullable Marca getMarca(String name) {
    for (Marca marca : Marca.values()) {
      if (marca.name().equalsIgnoreCase(name)) {
        return marca;
      }
    }
    return null;
  }
}
