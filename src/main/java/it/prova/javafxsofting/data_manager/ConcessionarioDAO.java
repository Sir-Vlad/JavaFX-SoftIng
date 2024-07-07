package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.models.Concessionario;
import java.util.List;

public interface ConcessionarioDAO {
  /**
   * Restituisce tutte le concessionarie presenti nel database
   *
   * @return la lista di tutte le {@link Concessionario}
   */
  List<Concessionario> getAllConcessionari();
}
