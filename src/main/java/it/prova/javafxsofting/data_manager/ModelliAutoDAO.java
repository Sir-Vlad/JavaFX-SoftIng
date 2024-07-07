package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.models.ModelloAuto;
import java.util.List;

public interface ModelliAutoDAO {
  /**
   * Restituisce tutte le auto presenti nel database
   *
   * @return la lista di tutte le {@link ModelloAuto}
   */
  List<ModelloAuto> getAllModelliAuto();
}
