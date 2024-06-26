package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.models.Ordine;
import it.prova.javafxsofting.models.Preventivo;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class OrdiniUtenteController implements Initializable {
  private static final ObservableList<Ordine> ordiniUtente = getOrdini();
  private static ScheduledExecutorService scheduler;

  static {
    getOrdini();
  }

  private final Logger logger = Logger.getLogger(this.getClass().getName());

  @FXML private TableView<Ordine> tableView;
  @FXML private TableColumn<Ordine, String> numFatturaColumn;
  @FXML private TableColumn<Ordine, Preventivo> modelloColumn;
  @FXML private TableColumn<Ordine, Integer> preventivoColumn;
  @FXML private TableColumn<Ordine, Integer> accontoColumn;
  @FXML private TableColumn<Ordine, LocalDate> dataRitiroColumn;
  @FXML private TableColumn<Ordine, Preventivo> concessionarioColumn;

  /**
   * Trasforma la lista di ordini in una ObservableList
   *
   * @return la ObservableList degli ordini
   */
  @Contract(" -> new")
  private static @NotNull ObservableList<Ordine> getOrdini() {
    ArrayList<Ordine> ordini = new ArrayList<>(UserSession.getInstance().getOrdini());
    return FXCollections.observableArrayList(ordini);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setTableView();
    startPeriodicUpdate();
  }

  private void setTableView() {
    setFatturaColumn();
    setModelloColumn();
    setPreventivoColumn();
    setAccontoColumn();
    setDataRitiroColumn();
    setConcessionarioColumn();

    tableView.setItems(ordiniUtente);
  }

  private void setConcessionarioColumn() {
    concessionarioColumn.setCellValueFactory(new PropertyValueFactory<>("preventivo"));
    concessionarioColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(Preventivo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  setText(item.getConcessionario().getNome());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setDataRitiroColumn() {
    dataRitiroColumn.setCellValueFactory(new PropertyValueFactory<>("dataRitiro"));
    dataRitiroColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  setText(item.toString());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setAccontoColumn() {
    accontoColumn.setCellValueFactory(new PropertyValueFactory<>("acconto"));
    accontoColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  DecimalFormat decimalFormat = new DecimalFormat("###.###");
                  setText(decimalFormat.format(item) + " €");
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setPreventivoColumn() {
    preventivoColumn.setCellValueFactory(new PropertyValueFactory<>("preventivoID"));
    preventivoColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  setText(item.toString());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setModelloColumn() {
    modelloColumn.setCellValueFactory(new PropertyValueFactory<>("preventivo"));
    modelloColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(Preventivo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  setText(item.getModello().getModello());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setFatturaColumn() {
    numFatturaColumn.setCellValueFactory(new PropertyValueFactory<>("numeroFattura"));
    numFatturaColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  setText(item);
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void startPeriodicUpdate() {
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(this::updateOrdini, 10, 30, TimeUnit.MINUTES);
  }

  private void updateOrdini() {
    logger.info("updateOrdini");
    UserSession.getInstance().setOrdini();
    updateTableView();
  }

  public void updateTableView() {
    logger.info("updateTableView - Ordini");
    Platform.runLater(
        () -> {
          tableView.getItems().clear();
          ordiniUtente.setAll(getOrdini());
          tableView.setItems(ordiniUtente);
        });
  }
}
