package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.models.AutoUsata;
import java.util.List;

public interface AutoUsateDAO {

  /**
   * Restituisce tutte le auto usate presenti nel database
   *
   * @return la lista di tutte le {@link AutoUsata}
   */
  List<AutoUsata> getAllAutoUsate();
}
