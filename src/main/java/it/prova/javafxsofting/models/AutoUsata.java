package it.prova.javafxsofting.models;

import java.io.File;
import java.time.LocalDate;
import lombok.Data;

@Data
public class AutoUsata {
  private int index;
  private String modello;
  private Marca marca;
  private int prezzo;
  private File[] immagini;
  private int kmPercorsi;
  private LocalDate anno_immatricolazione;
  // dati auto
  private int altezza;
  private int lunghezza;
  private int larghezza;
  private int peso;
  private int volumeBagagliaio;
}
