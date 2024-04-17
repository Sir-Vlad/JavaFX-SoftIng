package it.prova.javafxsofting;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class UtenteTest {

  @Test
  void testToString() {
    Utente utente =
        new Utente(
            "Mattia",
            "Frigiola",
            "prova@gmail.com",
            "12345678",
            "1234567891234567",
            LocalDate.now(),
            "123");
    System.out.println(utente);
  }
}
