package it.prova.javafxsofting.models;

public enum Marca {
  NISSAN("nissan"),
  MAZDA("mazda"),
  VOLKSWAGEN("volkswagen"),
  FORD("ford"),
  HONDA("honda"),
  AUDI("audi"),
  BMW("bmw"),
  ;

  Marca(String name) {}

  public static Marca getMarca(String name) {
    for (Marca marca : Marca.values()) {
      if (marca.name().equalsIgnoreCase(name)) {
        return marca;
      }
    }
    return null;
  }
}
