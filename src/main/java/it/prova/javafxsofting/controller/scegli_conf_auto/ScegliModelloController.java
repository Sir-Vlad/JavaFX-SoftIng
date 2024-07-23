package it.prova.javafxsofting.controller.scegli_conf_auto;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import it.prova.javafxsofting.component.CardAuto;
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
  @Getter @Setter private static ModelloAuto autoSelezionata = null;
  private static ScheduledExecutorService scheduler;
  private final Logger logger = Logger.getLogger(ScegliModelloController.class.getName());
  private final DataManager dataManager = DataManager.getInstance();
  @FXML private AnchorPane root;
  @FXML private MFXScrollPane scrollPane;
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

  /**
   * Imposta il filtro
   *
   * @param filter campo del filtro da impostare
   */
  private void setTypeFilter(@NotNull MFXFilterComboBox<String> filter) {
    filter.getSelectionModel().selectFirst();

    filter
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                if (newValue.equals(ELEMENT_TUTTI)) {
                  getFlowPane().getChildren().clear();
                  getCardAuto().stream()
                      .map(CardAuto::new)
                      .forEach(auto -> getFlowPane().getChildren().add(auto));
                  return;
                }
                List<ModelloAuto> newAutoFiltered =
                    getCardAuto().stream()
                        .filter(
                            auto ->
                                Arrays.stream(auto.getOptionals())
                                    .anyMatch(
                                        optional ->
                                            optional.getDescrizione().equalsIgnoreCase(newValue)))
                        .toList();
                getFlowPane().getChildren().clear();
                newAutoFiltered.stream()
                    .map(CardAuto::new)
                    .forEach(auto -> getFlowPane().getChildren().add(auto));
              }
            });
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

  /** Imposta il filtro del cambio */
  private void settingCambioFilter() {
    settingFilter(getTypeCambio(), cambioFilter);
  }

  private void settingFilter(
      @NotNull List<String> valueFilter, @NotNull MFXFilterComboBox<String> filterComboBox) {
    List<String> valueFilterCopy = new ArrayList<>(valueFilter);
    valueFilterCopy.addFirst(ELEMENT_TUTTI);
    filterComboBox.setItems(FXCollections.observableList(valueFilterCopy));
    setTypeFilter(filterComboBox);
  }

  /** Imposta il filtro dell'alimentazione */
  private void settingAlimentazioneFilter() {
    settingFilter(getTypeAlimentazione(), alimentazioneFilter);
  }

  private void startPeriodicUpdate() {
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(this::updateListFromDatabase, 5, 5, TimeUnit.MINUTES);
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
          getFlowPane().getChildren().clear();
          getCardAuto().setAll(dataManager.getModelliAuto());
          getCardAuto().stream()
              .map(CardAuto::new)
              .forEach(auto -> getFlowPane().getChildren().add(auto));
        });
  }
}
