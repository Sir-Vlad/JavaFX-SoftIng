package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.Optional;
import java.util.List;
import java.util.logging.Logger;
import lombok.SneakyThrows;

public class OptionalDAOImpl implements OptionalDAO {

  private static final Logger logger = Logger.getLogger(OptionalDAOImpl.class.getName());

  @Override
  @SneakyThrows
  public List<Optional> getAllOptionals() {
    logger.info("Aggiornamento optionals");
    List<Optional> newOptionals;
    newOptionals = Connection.getArrayDataFromBackend("optionals/", Optional.class);
    return newOptionals;
  }
}
