package it.prova.javafxsofting.data_manager;

import it.prova.javafxsofting.models.*;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
public class DataManager {
  private static DataManager instance = null;
  private final ModelliAutoDAO modelliAutoDAO;
  private final AutoUsateDAO autoUsateDAO;
  private final OptionalDAO optionalsDAO;
  private final ConcessionarioDAO concessionariDAO;
  private final ScontoDAO scontiDAO;
  private List<ModelloAuto> modelliAuto;
  private List<AutoUsata> autoUsate;
  private List<Optional> optionals;
  private List<Concessionario> concessionari;
  private List<Sconto> sconti;

  @Contract(pure = true)
  private DataManager() {
    optionalsDAO = new OptionalsDAOImpl();
    modelliAutoDAO = new ModelliAutoDAOImpl();
    autoUsateDAO = new AutoUsateDAOImpl();
    concessionariDAO = new ConcessionarioDAOImpl();
    scontiDAO = new ScontoDAOImpl();
  }

  @Contract(value = " -> new", pure = true)
  @SneakyThrows
  public static synchronized @NotNull DataManager getInstance() {
    if (instance == null) {
      instance = new DataManager();
      instance.fetchAllData();
    }
    return instance;
  }

  @Contract(pure = true)
  public void refreshAllData() {
    try {
      fetchAllData();
    } catch (Exception e) {
      throw new RuntimeException("Errore durante l'aggiornamento dei dati");
    }
  }

  private void fetchAllData() {
    concessionari = concessionariDAO.getAllConcessionari();
    optionals = optionalsDAO.getAllOptionals();
    modelliAuto = modelliAutoDAO.getAllModelliAuto();
    autoUsate = autoUsateDAO.getAllAutoUsate();
    sconti = scontiDAO.getAllSconti();
  }
}
