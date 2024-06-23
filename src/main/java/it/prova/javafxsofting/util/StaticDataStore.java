package it.prova.javafxsofting.util;

import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Data
public final class StaticDataStore {
  @Getter private static List<ModelloAuto> modelliAuto;
  private static List<AutoUsata> autoUsate;
  @Getter private static List<Optional> optionals;
  @Getter private static List<Concessionario> concessionari;
  @Getter private static List<Sconto> sconti;

  private static Logger logger = Logger.getLogger(StaticDataStore.class.getName());
  @Getter private static boolean serverAvailable = true;

  @Contract(pure = true)
  private StaticDataStore() {}

  public static List<AutoUsata> getAutoUsate() {
    if (autoUsate == null) {
      try {
        fetchAutoUsate();
      } catch (Exception e) {
        serverAvailable = false;
      }
    }
    return autoUsate;
  }

  public static void fetchAllData() throws Exception {
    try {
      StaticDataStore.fetchOptionals();
      StaticDataStore.fetchModelliAuto();
      StaticDataStore.fetchAutoUsate();
      StaticDataStore.fetchConcessionari();
      StaticDataStore.fetchSconti();
    } catch (Exception e) {
      serverAvailable = false;
    }
  }

  private static void fetchSconti() {
    logger.info("Aggiornamento sconti");
    List<Sconto> newSconti;
    try {
      newSconti = Connection.getArrayDataFromBackend("modelli/sconti/", Sconto.class);
    } catch (Exception e) {
      newSconti = null;
    }
    if (newSconti != null && !newSconti.equals(sconti)) {
      newSconti.forEach(Sconto::transformIdToObject);
      sconti = newSconti;
    }
  }

  public static void fetchAutoUsate() throws Exception {
    logger.info("Aggiornamento auto usate");
    List<AutoUsata> newAutoUsate;
    newAutoUsate = Connection.getArrayDataFromBackend("autoUsate/", AutoUsata.class);
    if (newAutoUsate != null && !newAutoUsate.equals(autoUsate)) {
      newAutoUsate.forEach(AutoUsata::setImmagini);
      autoUsate = newAutoUsate;
    }
  }

  private static Optional @NotNull [] transformIdInOptionals(int @NotNull [] optionals) {
    ArrayList<Optional> newOptionals =
        new ArrayList<>(
            getOptionals().stream()
                .filter(
                    optional ->
                        Arrays.stream(optionals)
                            .anyMatch(optional1 -> optional1 == optional.getId()))
                .toList());
    return newOptionals.toArray(new Optional[0]);
  }

  public static void fetchConcessionari() throws Exception {
    logger.info("Aggiornamento concessionari");
    List<Concessionario> newConcessionari;
    newConcessionari = Connection.getArrayDataFromBackend("concessionari/", Concessionario.class);
    if (newConcessionari != null && !newConcessionari.equals(concessionari)) {
      concessionari = newConcessionari;
    }
  }

  public static void fetchModelliAuto() throws Exception {
    logger.info("Aggiornamento modelli auto");
    List<ModelloAuto> newAutoNuove;
    newAutoNuove = Connection.getArrayDataFromBackend("modelli/", ModelloAuto.class);

    if (newAutoNuove != null && !newAutoNuove.equals(modelliAuto)) {
      newAutoNuove.forEach(
          modelloAuto ->
              modelloAuto.setOptionals(transformIdInOptionals(modelloAuto.getIdsOptionals())));

      newAutoNuove.forEach(ModelloAuto::setImmagini);
      modelliAuto = newAutoNuove;
    }
  }

  public static void fetchOptionals() throws Exception {
    logger.info("Aggiornamento optionals");
    List<Optional> newOptionals;
    newOptionals = Connection.getArrayDataFromBackend("optionals/", Optional.class);
    if (newOptionals != null && !newOptionals.equals(optionals)) {
      optionals = newOptionals;
    }
  }
}
