package it.prova.javafxsofting;

import java.io.File;
import java.util.Objects;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

enum TipoMotore {
  GASOLIO,
  BENZINA,
  IBRIDA,
  ELETTRICA,
  IBRICA_PLUG_IN
}

@Getter
@Setter
public class ModelloAuto {
  private int index;
  private String nome;
  private String descrizione;
  private String marca;
  private int prezzoBase;

  private int altezza;
  private int lunghezza;
  private int larghezza;
  private int peso;
  private int volumeBagagliaio;

  private TipoMotore tipoMotore;
  private File[] immagini;
  private Color color;

  // todo: da aggiustare quando decidiamo come implementare gli optional
  private Objects[] optional;

  public ModelloAuto(
      int index,
      String nome,
      String marca,
      int prezzoBase,
      String descrizione,
      int altezza,
      int lunghezza,
      int peso,
      int volume_bagagliaio) {
    this.index = index;
    this.nome = nome;
    this.descrizione = descrizione;
    this.prezzoBase = prezzoBase;
    this.marca = marca;
    this.altezza = altezza;
    this.lunghezza = lunghezza;
    this.peso = peso;
    this.volumeBagagliaio = volume_bagagliaio;
  }

  @Override
  public String toString() {
    return String.format(
        "ModelloAuto{\n\tindex=%s,\n\tnome=%s,\n\tdescrizione=%s,\n\tmarca=%s,\n\taltezza=%d,\n\tlunghezza=%d,\n\tpeso=%d,\n\tvolumeBagagliaio=%d\n\t}",
        index, nome, descrizione, marca, altezza, lunghezza, peso, volumeBagagliaio);
  }
}
