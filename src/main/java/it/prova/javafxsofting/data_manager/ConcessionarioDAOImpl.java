package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.Concessionario;
import java.util.List;
import java.util.logging.Logger;
import lombok.SneakyThrows;

public class ConcessionarioDAOImpl implements ConcessionarioDAO {

  private static final Logger logger = Logger.getLogger(ConcessionarioDAOImpl.class.getName());

  @Override
  @SneakyThrows
  public List<Concessionario> getAllConcessionari() {
    logger.info("Aggiornamento concessionari");
    List<Concessionario> newConcessionari;
    newConcessionari = Connection.getArrayDataFromBackend("concessionari/", Concessionario.class);
    return newConcessionari;
  }
}
