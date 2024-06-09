package it.prova.javafxsofting.util;

import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.models.Concessionario;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Optional;
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
  @Getter private static List<AutoUsata> autoUsate;
  @Getter private static List<Optional> optionals;
  @Getter private static List<Concessionario> concessionari;

  private static Logger logger = Logger.getLogger(StaticDataStore.class.getName());

  @Contract(pure = true)
  private StaticDataStore() {}

  public static void fetchAllData() {
    StaticDataStore.fetchOptionals();
    StaticDataStore.fetchModelliAuto();
    StaticDataStore.fetchAutoUsate();
    StaticDataStore.fetchConcessionari();
  }

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
              modelloAuto.setOptionals(transformIdInOptionals(modelloAuto.getIdsOptionals())));

      newAutoNuove.forEach(ModelloAuto::setImmagini);
      modelliAuto = newAutoNuove;
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

  public static void fetchAutoUsate() {
    logger.info("Aggiornamento auto usate");
    List<AutoUsata> newAutoUsate;
    try {
      newAutoUsate = Connection.getArrayDataFromBackend("autoUsate/", AutoUsata.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (newAutoUsate != null && !newAutoUsate.equals(autoUsate)) {
      // todo: fare il fetch delle immagini
      autoUsate = newAutoUsate;
    }
  }

  public static void fetchConcessionari() {
    logger.info("Aggiornamento concessionari");
    List<Concessionario> newConcessionari;
    try {
      newConcessionari = Connection.getArrayDataFromBackend("concessionari/", Concessionario.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (newConcessionari != null && !newConcessionari.equals(concessionari)) {
      concessionari = newConcessionari;
    }
  }
}
