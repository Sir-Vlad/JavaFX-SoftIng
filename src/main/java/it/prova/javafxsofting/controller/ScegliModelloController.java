package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXSlider;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.models.Marca;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Optional;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ScegliModelloController implements Initializable {
  @Getter @Setter private static ModelloAuto autoSelezionata = null;
  @FXML private AnchorPane root;
  @FXML private MFXScrollPane scrollPane;
  @FXML private FlowPane flowPane;
  @FXML private Header header;
  @FXML private MFXFilterComboBox<String> marcaComboFilter;
  @FXML private MFXSlider sliderMaxPrezzo;
  @FXML private MFXFilterComboBox<String> alimentazioneFilter;
  @FXML private MFXFilterComboBox<String> cambioFilter;

  private ObservableList<ModelloAuto> cardAuto;
  private List<ModelloAuto> autoFiltered;

  ScheduledExecutorService scheduler;

  private List<String> getTypeAlimentazione() {
    return cardAuto.stream()
        .map(modelloAuto -> modelloAuto.getOptionals()[0].getDescrizione())
        .distinct()
        .toList();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Home", event -> ScreenController.activate("home"));
    List<ModelloAuto> modelliAuto;
    try {
      modelliAuto = Connection.getArrayDataFromBackend("modelli/", ModelloAuto.class);
      // todo: fare le get per gli optional dei modelli
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR, e.getMessage());
      alert.setHeaderText("Errore del server");
      alert.showAndWait();
      Platform.exit();
      return;
    }
    if (modelliAuto != null) {
      // accodato
      modelliAuto.forEach(
          modelloAuto ->
              modelloAuto.setOptionals(new Optional[] {new Optional("Alimentazione", "GPL", 0)}));
      cardAuto = FXCollections.observableList(modelliAuto);
      cardAuto.stream().map(CardAuto::new).forEach(auto -> flowPane.getChildren().addAll(auto));
    }

    settingMarcaFilter();
    settingPrezzoFilter();
    settingAlimentazioneFilter();
    settingCambioFilter();

    startPeriodicUpdate();
  }

  private void settingCambioFilter() {}

  private void settingAlimentazioneFilter() {

    List<String> a = getTypeAlimentazione();
    a.addFirst("Tutti");
    ObservableList<String> typeAlimentazione = FXCollections.observableList(a);
    alimentazioneFilter.setItems(typeAlimentazione);

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
                  flowPane.getChildren().clear();
                  autoFiltered.stream()
                      .map(CardAuto::new)
                      .forEach(auto -> flowPane.getChildren().add(auto));
                }
              }
            });
  }

  private void settingPrezzoFilter() {
    int[] minMaxPrezzo = minMaxPrezzoAuto();
    sliderMaxPrezzo.setMin(minMaxPrezzo[0]);
    sliderMaxPrezzo.setMax(minMaxPrezzo[1]);
    sliderMaxPrezzo
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null && !newValue.equals(oldValue)) {
                List<ModelloAuto> newAutoFiltered =
                    cardAuto.stream()
                        .filter(auto -> auto.getPrezzoBase() >= newValue.intValue())
                        .toList();

                if (autoFiltered == null) {
                  autoFiltered = newAutoFiltered;
                }

                if (!autoFiltered.equals(newAutoFiltered)) {
                  autoFiltered = newAutoFiltered;
                  flowPane.getChildren().clear();
                  autoFiltered.stream()
                      .map(CardAuto::new)
                      .forEach(auto -> flowPane.getChildren().add(auto));
                }
              }
            });
  }

  @Contract(" -> new")
  private int @NotNull [] minMaxPrezzoAuto() {
    int max =
        cardAuto.stream()
            .mapToInt(ModelloAuto::getPrezzoBase)
            .filter(modelloAuto -> modelloAuto >= 0)
            .max()
            .orElse(0);
    int min =
        cardAuto.stream()
            .mapToInt(ModelloAuto::getPrezzoBase)
            .filter(modelloAuto -> modelloAuto >= 0)
            .min()
            .orElse(0);
    if (max == 0) {
      max = 1;
    }
    return new int[] {min, max};
  }

  private void settingMarcaFilter() {
    ObservableList<String> marche =
        FXCollections.observableArrayList(
            Arrays.stream(Marca.values()).map(Enum::toString).toList());
    marche.addFirst("Tutti");
    marcaComboFilter.setItems(marche);

    marcaComboFilter
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                if (newValue.equals("Tutti")) {
                  flowPane.getChildren().clear();
                  cardAuto.stream()
                      .map(CardAuto::new)
                      .forEach(auto -> flowPane.getChildren().add(auto));
                  return;
                }
                List<ModelloAuto> dataFiltered =
                    cardAuto.stream()
                        .filter(auto -> auto.getMarca().equals(Marca.valueOf(newValue)))
                        .toList();
                flowPane.getChildren().clear();
                dataFiltered.stream()
                    .map(CardAuto::new)
                    .forEach(auto -> flowPane.getChildren().add(auto));
              }
            });
  }

  private void startPeriodicUpdate() {
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(this::updateListFromDatabase, 0, 5, TimeUnit.MINUTES);
  }

  private void updateListFromDatabase() {
    App.getLog().info("Updating list from database");
    List<ModelloAuto> newData;
    try {
      newData = Connection.getArrayDataFromBackend("modelli/", ModelloAuto.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (newData != null && !newData.equals(cardAuto)) {
      // accodato
      newData.forEach(
          modelloAuto ->
              modelloAuto.setOptionals(new Optional[] {new Optional("Alimentazione", "GPL", 0)}));
      Platform.runLater(
          () -> {
            cardAuto.setAll(newData);
            flowPane.getChildren().clear();
            cardAuto.stream().map(CardAuto::new).forEach(auto -> flowPane.getChildren().add(auto));
          });
    }
  }
}
