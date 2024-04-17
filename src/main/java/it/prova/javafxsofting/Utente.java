package it.prova.javafxsofting;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Utente {
  private String nome;
  private String cognome;
  private String email;
  private String password;
  private String iban;
  private LocalDate dataScadenza;
  private String cvc;

  public Utente(
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

  @Override
  public String toString() {
    return String.format(
        "Utente{\n\tDati personali{\n\t\tnome='%s',\n\t\tcognome='%s',\n\t\temail='%s',\n\t\tpassword='%s'\n\t},\n\tCoordinate Bancarie{\n\t\tiban='%s',\n\t\tdataScadenza='%s,\n\t\tcvc='%s\n\t}\n}",
        nome, cognome, email, password, iban, dataScadenza, cvc);
  }
}
