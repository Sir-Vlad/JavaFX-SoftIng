package it.prova.javafxsofting.models;

import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;

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

  @Override
  @Contract(value = "null -> false", pure = true)
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AutoUsata autoUsata = (AutoUsata) o;
    return prezzo == autoUsata.prezzo
        && kmPercorsi == autoUsata.kmPercorsi
        && Objects.equals(annoImmatricolazione, autoUsata.annoImmatricolazione)
        && Objects.equals(targa, autoUsata.targa);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), prezzo, kmPercorsi, annoImmatricolazione, targa);
  }
}
