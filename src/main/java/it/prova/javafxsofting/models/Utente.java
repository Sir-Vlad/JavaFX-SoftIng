package it.prova.javafxsofting.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
public class Utente implements Serializable {
  private static Utente instance;
  @SerializedName("id")
  @Expose(serialize = false)
  private int id;
  @SerializedName("nome")
  private String nome;
  @SerializedName("cognome")
  private String cognome;
  @SerializedName("email")
  private String email;
  @SerializedName("password")
  private String password;
  @SerializedName("numero_carta")
  private String iban;
  @SerializedName("data_scadenza")
  private LocalDate dataScadenza;
  @SerializedName("cvc")
  private String cvc;
  @Expose(deserialize = false)
  private File imageUtente = null;

  private Utente() {}

  private Utente(
      String nome,
      String cognome,
      String email,
      String password,
      String iban,
      LocalDate dataScadenza,
      String cvc) {
    this.nome = nome;
    this.cognome = cognome;
    this.email = email;
    this.password = password;
    this.iban = iban;
    this.dataScadenza = dataScadenza;
    this.cvc = cvc;
  }

  public static Utente getInstance() {
    if (instance == null) {
      instance = new Utente();
    }
    return instance;
  }

  public static Utente getInstance(
      String nome,
      String cognome,
      String email,
      String password,
      String iban,
      LocalDate dataScadenza,
      String cvc) {
    if (instance == null) {
      instance = new Utente(nome, cognome, email, password, iban, dataScadenza, cvc);
    }
    return instance;
  }

  @Override
  public String toString() {
    return String.format(
        "Utente{\n\tDati personali{\n\t\tnome='%s',\n\t\tcognome='%s',\n\t\temail='%s',\n\t\tpassword='%s'\n\t},\n\tCoordinate Bancarie{\n\t\tiban='%s',\n\t\tdataScadenza='%s,\n\t\tcvc='%s\n\t}\n}",
        nome, cognome, email, password, iban, dataScadenza, cvc);
  }
}
