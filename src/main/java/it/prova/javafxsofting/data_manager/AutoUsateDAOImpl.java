package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.AutoUsata;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import lombok.SneakyThrows;

public class AutoUsateDAOImpl implements AutoUsateDAO {

  private static final Logger logger = Logger.getLogger(AutoUsateDAOImpl.class.getName());

  @Override
  @SneakyThrows
  public List<AutoUsata> getAllAutoUsate() {
    logger.info("Aggiornamento auto usate");
    List<AutoUsata> newAutoUsate;
    newAutoUsate = Connection.getArrayDataFromBackend("autoUsate/", AutoUsata.class);
    if (newAutoUsate != null) {
      newAutoUsate.forEach(AutoUsata::setImmagini);
      return newAutoUsate;
    }
    return Collections.emptyList();
  }
}
