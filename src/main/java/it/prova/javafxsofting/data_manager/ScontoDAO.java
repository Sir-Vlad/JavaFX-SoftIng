package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.models.Sconto;
import java.util.List;

public interface ScontoDAO {
  /**
   * Restituisce tutte le sconti presenti nel database
   *
   * @return la lista di tutte le {@link Sconto}
   */
  List<Sconto> getAllSconti();
}
