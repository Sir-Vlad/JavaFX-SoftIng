package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.Sconto;
import java.util.List;
import java.util.logging.Logger;

public class ScontoDAOImpl implements ScontoDAO {

  private static final Logger logger = Logger.getLogger(ScontoDAOImpl.class.getName());

  @Override
  public List<Sconto> getAllSconti() {
    logger.info("Aggiornamento sconti");
    List<Sconto> newSconti;
    try {
      newSconti = Connection.getArrayDataFromBackend("modelli/sconti/", Sconto.class);
    } catch (Exception e) {
      newSconti = null;
    }
    if (newSconti != null) {
      newSconti.forEach(Sconto::transformIdToObject);
      return newSconti;
    }
    return null;
  }
}
