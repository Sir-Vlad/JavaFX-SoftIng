package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class ModelliAutoDAOImpl implements ModelliAutoDAO {
  public static Logger logger = Logger.getLogger(ModelliAutoDAOImpl.class.getName());

  @Override
  @SneakyThrows
  public List<ModelloAuto> getAllModelliAuto() {
    logger.info("Aggiornamento modelli auto");
    List<ModelloAuto> newAutoNuove;
    newAutoNuove = Connection.getArrayDataFromBackend("modelli/", ModelloAuto.class);

    if (newAutoNuove != null) {
      newAutoNuove.forEach(
          modelloAuto ->
              modelloAuto.setOptionals(transformIdInOptionals(modelloAuto.getIdsOptionals())));

      newAutoNuove.forEach(ModelloAuto::setImmagini);
      return newAutoNuove;
    }
    return Collections.emptyList();
  }

  private Optional @NotNull [] transformIdInOptionals(int @NotNull [] optionals) {
    ArrayList<Optional> newOptionals =
        new ArrayList<>(
            DataManager.getInstance().getOptionals().stream()
                .filter(
                    optional ->
                        Arrays.stream(optionals)
                            .anyMatch(optional1 -> optional1 == optional.getId()))
                .toList());
    return newOptionals.toArray(new Optional[0]);
  }
}
