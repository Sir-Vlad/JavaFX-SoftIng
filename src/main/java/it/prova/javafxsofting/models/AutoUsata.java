package it.prova.javafxsofting.models;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoUsata extends Auto {
  private int prezzo;
  private int kmPercorsi;
  private LocalDate annoImmatricolazione;
  private String targa;

  public AutoUsata(
      String modello,
      String marca,
      int altezza,
      int lunghezza,
      int larghezza,
      int peso,
      int volumeBagagliaio,
      int kmPercorsi,
      String targa,
      LocalDate annoImmatricolazione) {
    super(modello, marca, altezza, lunghezza, larghezza, peso, volumeBagagliaio);
    this.prezzo = 0;
    this.kmPercorsi = kmPercorsi;
    this.targa = targa;
    this.annoImmatricolazione = annoImmatricolazione;
  }
}
