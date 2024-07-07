package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.models.Optional;
import java.util.List;

public interface OptionalDAO {
  /**
   * Restituisce tutte le optional presenti nel database
   *
   * @return la lista di tutte le {@link Optional}
   */
  List<Optional> getAllOptionals();
}
