package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;

@Getter
@Setter
public class ModelloAuto extends Auto {
  @SerializedName("prezzo_base")
  private int prezzoBase;

  @SerializedName("optionals")
  private int[] idsOptionals;

  // optionals che un modello può avere
  @Expose(deserialize = false)
  private Optional[] optionals;

  public ModelloAuto(
      String modello,
      String marca,
      int prezzoBase,
      int altezza,
      int lunghezza,
      int larghezza,
      int peso,
      int volumeBagagliaio) {
    super(modello, marca, altezza, lunghezza, larghezza, peso, volumeBagagliaio);
    this.prezzoBase = prezzoBase;
  }

  @Override
  @Contract(value = "null -> false", pure = true)
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ModelloAuto that = (ModelloAuto) o;
    return prezzoBase == that.prezzoBase && Objects.deepEquals(optionals, that.optionals);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), prezzoBase, Arrays.hashCode(optionals));
  }
}
