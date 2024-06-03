package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.util.StaticDataStore;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.jetbrains.annotations.Contract;

@Data
public class Preventivo implements Serializable {

  @SerializedName("id")
  private int id;

  @SerializedName("utente")
  private int utenteId;

  @SerializedName("modello")
  private int modelloId;

  @SerializedName("sede")
  private int sedeId;

  @SerializedName("data_emissione")
  private LocalDate dataEmissione;

  @SerializedName("prezzo")
  private int prezzo;

  @SerializedName("config")
  private int[] idRefConfig;

  @Expose(serialize = false, deserialize = false)
  private Utente utente;

  @Expose(serialize = false, deserialize = false)
  private ModelloAuto modello;

  @Expose(serialize = false, deserialize = false)
  private Sede sede;

  @Expose(serialize = false, deserialize = false)
  private List<Optional> optionals; // optional scelti nella configurazione

  @Expose(serialize = false, deserialize = false)
  private int prezzoOptionals;

  @Expose(serialize = false, deserialize = false)
  private float totalePrezzo;

  @Contract(pure = true)
  public Preventivo(Utente utente, ModelloAuto modello, Sede sede, LocalDate dataEmissione) {
    this.utente = utente;
    this.modello = modello;
    this.sede = sede;
    this.dataEmissione = dataEmissione;
    // todo: settare il prezzo del preventivo
    this.prezzo = 0;
  }

  @Override
  public String toString() {
    return String.format(
        """
        Preventivo{
          id=%d,
          utente=%s,
          modello=%s,
          sede=%s,
          dataEmissione=%s,
          prezzo=%d,
          config=%s
        }
        """,
        id, utente, modello, sede, dataEmissione, prezzo, optionals);
  }

  public void transformIdToObject() {
    if (utenteId == App.getUtente().getId()) {
      this.utente = App.getUtente();
    }

    this.modello =
        StaticDataStore.getModelliAuto().stream()
            .filter(auto -> auto.getId() == modelloId)
            .toList()
            .getFirst();

    this.optionals =
        StaticDataStore.getOptionals().stream()
            .filter(optional -> Arrays.stream(idRefConfig).anyMatch(id -> id == optional.getId()))
            .toList();

    this.prezzoOptionals = optionals.stream().mapToInt(Optional::getPrezzo).sum();
    this.totalePrezzo = this.getModello().getPrezzoBase() + this.prezzoOptionals;
    // todo: completare appena abbiamo la lista delle sedi
    //    this.sede = ??
  }
}
