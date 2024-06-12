package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.prova.javafxsofting.UserSession;
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

  @SerializedName("concessionario")
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
  private Concessionario concessionario;

  @Expose(serialize = false, deserialize = false)
  private List<Optional> optionals; // optional scelti nella configurazione

  @Expose(serialize = false, deserialize = false)
  private int prezzoOptionals;

  @Expose(serialize = false, deserialize = false)
  private float totalePrezzo;

  @Contract(pure = true)
  public Preventivo(Utente utente, ModelloAuto modello, Concessionario concessionario, int prezzo) {
    this.utente = utente;
    this.modello = modello;
    this.concessionario = concessionario;
    // todo: settare il prezzo del preventivo
    this.prezzo = prezzo;
  }

  @Contract(pure = true)
  public Preventivo(
      Utente utente,
      ModelloAuto modello,
      Concessionario concessionario,
      LocalDate dataEmissione,
      int prezzo) {
    this(utente, modello, concessionario, prezzo);
    this.dataEmissione = dataEmissione;
  }

  @Override
  public String toString() {
    return String.format(
        """
        Preventivo{
          id=%d,
          utente=%s,
          modello=%s,
          concessionario=%s,
          dataEmissione=%s,
          prezzo=%d,
          config=%s
        }
        """,
        id, utente, modello, concessionario, dataEmissione, prezzo, optionals);
  }

  public void transformIdToObject() {
    if (utenteId == UserSession.getInstance().getUtente().getId()) {
      this.utente = UserSession.getInstance().getUtente();
    }

    this.modello =
        StaticDataStore.getModelliAuto().stream()
            .filter(auto -> auto.getId() == modelloId)
            .toList()
            .getFirst();

    this.optionals =
        StaticDataStore.getOptionals().stream()
            .filter(optional -> Arrays.stream(idRefConfig).anyMatch(opt -> opt == optional.getId()))
            .toList();

    this.prezzoOptionals = optionals.stream().mapToInt(Optional::getPrezzo).sum();
    this.totalePrezzo = this.getModello().getPrezzoBase() + this.prezzoOptionals;
    // todo: completare appena abbiamo la lista delle sedi
    //    this.concessionario = ??
  }
}
