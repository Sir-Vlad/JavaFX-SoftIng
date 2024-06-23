package it.prova.javafxsofting.util;

import org.jetbrains.annotations.Contract;

public final class Util {
  @Contract(value = " -> fail", pure = true)
  private Util() {
    throw new UnsupportedOperationException("Classe utilitaria");
  }

  /**
   * Formatta la stringa in input con la prima lettera maiuscola
   *
   * @param input stringa da formattare
   * @return stringa formattata
   */
  @Contract("null -> null")
  public static String capitalize(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
  }
}
