package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.util.FilterAuto;
import it.prova.javafxsofting.util.StaticDataStore;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ScegliModelloController extends ScegliAuto<ModelloAuto>
    implements Initializable, FilterAuto {
  private static final ObservableList<ModelloAuto> cardAuto = FXCollections.observableArrayList();
  private static final String ELEMENT_TUTTI = "Tutti";
  @Getter @Setter private static ModelloAuto autoSelezionata = null;
  private final Logger logger = Logger.getLogger(ScegliModelloController.class.getName());
  ScheduledExecutorService scheduler;
  @FXML private AnchorPane root;
  @FXML private MFXScrollPane scrollPane;
  @FXML private MFXFilterComboBox<String> alimentazioneFilter;
  @FXML private MFXFilterComboBox<String> cambioFilter;
  private List<ModelloAuto> autoFiltered;

  @Contract(" -> new")
  private @NotNull List<String> getTypeAlimentazione() {
    return new ArrayList<>(
        cardAuto.stream()
            .map(modelloAuto -> modelloAuto.getOptionals()[0].getDescrizione())
            .distinct()
            .sorted()
            .toList());
  }

  public static void fetchData() {
    StaticDataStore.fetchModelliAuto();
    cardAuto.setAll(StaticDataStore.getModelliAuto());
  }

  @Override
  public void setCardAuto() {
    getCardAuto().setAll(StaticDataStore.getModelliAuto());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);

    settingAlimentazioneFilter();
    settingCambioFilter();

    startPeriodicUpdate();
  }

  private void settingCambioFilter() {
    // todo: da implementare
  }

  private void settingAlimentazioneFilter() {
    List<String> a = getTypeAlimentazione();
    a.addFirst(ELEMENT_TUTTI);
    ObservableList<String> typeAlimentazione = FXCollections.observableList(a);
    alimentazioneFilter.setItems(typeAlimentazione);
    alimentazioneFilter.getSelectionModel().selectFirst();

    alimentazioneFilter
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                List<ModelloAuto> newAutoFiltered =
                    cardAuto.stream()
                        .filter(auto -> auto.getOptionals()[0].getDescrizione().equals(newValue))
                        .toList();

                if (autoFiltered == null) {
                  autoFiltered = newAutoFiltered;
                }

                if (!autoFiltered.equals(newAutoFiltered)) {
                  autoFiltered = newAutoFiltered;
                  getFlowPane().getChildren().clear();
                  autoFiltered.stream()
                      .map(CardAuto::new)
                      .forEach(auto -> getFlowPane().getChildren().add(auto));
                }
              }
            });
  }

  private void startPeriodicUpdate() {
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(this::updateListFromDatabase, 5, 5, TimeUnit.MINUTES);
  }

  private void updateListFromDatabase() {
    StaticDataStore.fetchModelliAuto();

    Platform.runLater(
        () -> {
          getFlowPane().getChildren().clear();
          cardAuto.setAll(StaticDataStore.getModelliAuto());
          cardAuto.stream()
              .map(CardAuto::new)
              .forEach(auto -> getFlowPane().getChildren().add(auto));
        });
  }
}
