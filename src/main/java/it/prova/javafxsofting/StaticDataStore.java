package it.prova.javafxsofting;

import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Optional;
import it.prova.javafxsofting.models.Sede;
import java.util.List;
import java.util.logging.Logger;
import lombok.Data;
import lombok.Getter;

@Data
public class StaticDataStore {
  @Getter private static List<ModelloAuto> modelliAuto;
  @Getter private static List<AutoUsata> autoUsate;
  @Getter private static List<Optional> optionals;
  @Getter private static List<Sede> sedi;

  private static Logger logger = Logger.getLogger(StaticDataStore.class.getName());

  private StaticDataStore() {}

  public static void fetchModelliAuto() {
    logger.info("Aggiornamento modelli auto");
    List<ModelloAuto> newAutoNuove;
    try {
      newAutoNuove = Connection.getArrayDataFromBackend("modelli/", ModelloAuto.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (newAutoNuove != null && !newAutoNuove.equals(modelliAuto)) {
      // accodato
      newAutoNuove.forEach(
          modelloAuto ->
              modelloAuto.setOptionals(new Optional[] {new Optional("Alimentazione", "GPL", 0)}));

      newAutoNuove.forEach(ModelloAuto::setImmagini);
      modelliAuto = newAutoNuove;
    }
  }

  public static void fetchOptionals() {
    logger.info("Aggiornamento optionals");
    List<Optional> newOptionals;
    try {
      newOptionals = Connection.getArrayDataFromBackend("optionals/", Optional.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (newOptionals != null && !newOptionals.equals(optionals)) {
      optionals = newOptionals;
    }
  }
}
