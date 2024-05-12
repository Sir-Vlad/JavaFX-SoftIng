package it.prova.javafxsofting.models;

import java.io.File;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

enum TipoMotore {
  GASOLIO,
  BENZINA,
  IBRIDA,
  ELETTRICA,
  IBRICA_PLUG_IN
}

enum Marca {
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

@Getter
@Setter
@Data
public class ModelloAuto {
  private int index;
  private String nome;
  //  private String descrizione;
  private Marca marca;
  private int prezzoBase;
  private File[] immagini;
  // dati auto
  private int altezza;
  private int lunghezza;
  private int larghezza;
  private int peso;
  private int volumeBagagliaio;
  // option
  private Optional[] optional;

  public ModelloAuto(
      int index,
      String nome,
      String marca,
      int prezzoBase,
      int altezza,
      int lunghezza,
      int peso,
      int volume_bagagliaio) {
    this.index = index;
    this.nome = nome;
    this.prezzoBase = prezzoBase;
    this.marca = Marca.getMarca(marca);
    this.altezza = altezza;
    this.lunghezza = lunghezza;
    this.peso = peso;
    this.volumeBagagliaio = volume_bagagliaio;
  }

  @Override
  public String toString() {
    return String.format(
        "ModelloAuto{\n\tindex=%s,\n\tnome=%s,\n\tmarca=%s,\n\taltezza=%d,\n\tlunghezza=%d,\n\tpeso=%d,\n\tvolumeBagagliaio=%d\n\t}",
        index, nome, marca, altezza, lunghezza, peso, volumeBagagliaio);
  }
}
