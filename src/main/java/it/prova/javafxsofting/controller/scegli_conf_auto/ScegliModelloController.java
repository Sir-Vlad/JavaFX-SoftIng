package it.prova.javafxsofting.controller.scegli_conf_auto;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import it.prova.javafxsofting.data_manager.DataManager;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Optional;
import it.prova.javafxsofting.util.FilterAuto;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = false)
public class ScegliModelloController extends ScegliAuto<ModelloAuto>
    implements Initializable, FilterAuto {
  private static final String ELEMENT_TUTTI = "Tutti";
  private static final ObservableList<ModelloAuto> filteredCardAuto =
      FXCollections.observableArrayList();
  @Getter @Setter private static ModelloAuto autoSelezionata = null;
  private static final ScheduledExecutorService scheduler;

  static {
    scheduler =
        Executors.newScheduledThreadPool(
            1,
            runnable -> {
              Thread thread = new Thread(runnable);
              thread.setDaemon(true);
              return thread;
            });
    //    filteredCardAuto.addListener(
    //        (ListChangeListener<? super ModelloAuto>)
    //            change -> {
    //              while (change.next()) {
    //                if (change.wasAdded()) {
    //                  System.out.println("Added: " + change.getAddedSubList());
    //                }
    //              }
    //            });
  }

  private final Logger logger = Logger.getLogger(ScegliModelloController.class.getName());
  private final DataManager dataManager = DataManager.getInstance();
  @FXML private AnchorPane root;
  @FXML private MFXFilterComboBox<String> alimentazioneFilter;
  @FXML private MFXFilterComboBox<String> cambioFilter;

  /**
   * Estrae la lista dei tipi di alimentazione delle auto
   *
   * @return la lista dei tipi di alimentazione
   */
  @Contract(" -> new")
  private @NotNull List<String> getTypeAlimentazione() {
    if (dataManager.getOptionals() != null)
      return new ArrayList<>(
          dataManager.getOptionals().stream()
              .filter(optional -> optional.getNome().equals("alimentazione"))
              .map(Optional::getDescrizione)
              .map(String::toUpperCase)
              .toList());
    return Collections.emptyList();
  }

  /**
   * Estrae la lista dei tipi di cambio delle auto
   *
   * @return la lista dei tipi di cambio
   */
  @Contract(" -> new")
  private @NotNull List<String> getTypeCambio() {
    if (dataManager.getOptionals() != null)
      return new ArrayList<>(
          dataManager.getOptionals().stream()
              .filter(optional -> optional.getNome().equals("cambio"))
              .map(Optional::getDescrizione)
              .map(String::toUpperCase)
              .toList());
    return Collections.emptyList();
  }

  @Override
  public void setCardAuto() {
    if (dataManager.getModelliAuto() != null) {
      getCardAuto().setAll(dataManager.getModelliAuto());
    } else {
      getCardAuto().setAll(new ArrayList<>());
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);

    settingAlimentazioneFilter();
    settingCambioFilter();

    startPeriodicUpdate();
  }

  @FXML
  public void filterCardAuto() {
    String marca = marcaComboFilter.getValue();
    double prezzo = sliderMaxPrezzo.getValue();
    String alimentazione = alimentazioneFilter.getValue();
    String cambio = cambioFilter.getValue();

    filteredCardAuto.clear(); // clear the list of filtered  previous cards

    getCardAuto().stream()
        .filter(auto -> filterOptional(auto, alimentazione, cambio, prezzo, marca))
        .forEach(filteredCardAuto::add);

    getCardContent().getChildren().clear();
    setPagination(filteredCardAuto);
  }

  private boolean filterOptional(
      @NotNull ModelloAuto auto,
      @NotNull String alimentazione,
      @NotNull String cambio,
      double prezzo,
      @NotNull String marca) {
    byte flag = 0;
    if (alimentazione.equals(ELEMENT_TUTTI)
        || Arrays.stream(auto.getOptionals())
            .anyMatch(
                optional ->
                    optional.getNome().equals("alimentazione")
                        && optional.getDescrizione().equalsIgnoreCase(alimentazione))) {
      flag++;
    }

    if (cambio.equals(ELEMENT_TUTTI)
        || Arrays.stream(auto.getOptionals())
            .anyMatch(
                optional ->
                    optional.getNome().equals("cambio")
                        && optional.getDescrizione().equalsIgnoreCase(cambio))) {
      flag++;
    }

    if (auto.getPrezzoBase() >= prezzo) {
      flag++;
    }

    if (auto.getMarca().name().equalsIgnoreCase(marca) || marca.equals(ELEMENT_TUTTI)) {
      flag++;
    }

    return flag == 4;
  }

  /** Imposta il filtro del cambio */
  private void settingCambioFilter() {
    settingFilter(getTypeCambio(), cambioFilter);
  }

  private void settingFilter(
      @NotNull List<String> valueFilter, @NotNull MFXFilterComboBox<String> filterComboBox) {
    List<String> valueFilterCopy = new ArrayList<>(valueFilter);
    valueFilterCopy.addFirst(ELEMENT_TUTTI);
    filterComboBox.setItems(FXCollections.observableList(valueFilterCopy));
    filterComboBox.getSelectionModel().selectFirst();
  }

  /** Imposta il filtro dell'alimentazione */
  private void settingAlimentazioneFilter() {
    settingFilter(getTypeAlimentazione(), alimentazioneFilter);
  }

  private void startPeriodicUpdate() {
    scheduler.scheduleAtFixedRate(this::updateListFromDatabase, 0, 30, TimeUnit.SECONDS);
  }

  private void updateListFromDatabase() {
    try {
      DataManager.getInstance().setModelliAuto();
    } catch (Exception e) {
      logger.warning("Errore durante l'aggiornamento della lista");
      logger.log(Level.SEVERE, e.getMessage(), e);
      return;
    }

    Platform.runLater(
        () -> {
          List<ModelloAuto> modelliAuto = dataManager.getModelliAuto();
          if (modelliAuto != null && modelliAuto.size() > getCardAuto().size()) {
            logger.info("Aggiornamento della pagina scegli modello");
            getCardAuto().setAll(dataManager.getModelliAuto());
            getCardContent().getChildren().clear();
            setPagination(getCardAuto());
            filterCardAuto();
          }
        });
  }
}
