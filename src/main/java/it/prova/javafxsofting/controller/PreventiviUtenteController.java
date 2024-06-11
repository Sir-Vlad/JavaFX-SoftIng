package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Preventivo;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PreventiviUtenteController implements Initializable {

  private static final ObservableList<Preventivo> preventiviUtente =
      FXCollections.observableArrayList(UserSession.getInstance().getPreventivi());

  private static ScheduledExecutorService scheduler;
  private final Logger logger = Logger.getLogger(this.getClass().getName());
  @FXML private TableColumn<Preventivo, Integer> idColumn;
  @FXML private TableColumn<Preventivo, ModelloAuto> modelloColumn;
  @FXML private TableColumn<Preventivo, ModelloAuto> prezzoBaseColumn;
  @FXML private TableColumn<Preventivo, Integer> prezzoOptionalsColumn;
  @FXML private TableColumn<Preventivo, Integer> scontoColumn;
  @FXML private TableColumn<Preventivo, Float> totPrezzoColumn;
  @FXML private TableColumn<Preventivo, LocalDate> dataEmissioneColumn;
  @FXML private TableColumn<Preventivo, Void> confermaColumn;
  @FXML private TableView<Preventivo> tableView;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    /*Aggiorna la tabella dei preventivi quando si aggiunge un nuovo preventivo*/
    UserSession.getInstance()
        .addListener(
            new UserSession.PreventivoListener() {
              @Override
              public void onPreventivoChange(List<Preventivo> preventivi) {
                updateTableView();
              }

              @Override
              public void onPreventivoAdded(Preventivo preventivo) {
                preventiviUtente.add(preventivo);
              }
            });

    setTableView();
    startPeriodicUpdate();
  }

  private void setTableView() {
    setColumnID();
    setColumnModello();
    setColumnPrezzoBase();
    setColumnPrezzoOptionals();
    setColumnSconto();
    setColumnPrezzoTotale();
    setColumnDataEmissione();
    setColumnConferma();

    tableView.getItems().addAll(preventiviUtente);
  }

  private void setColumnConferma() {
    confermaColumn.setCellFactory(
        column ->
            new TableCell<>() {
              private final Button btn = new Button("conferma");

              {
                btn.setOnAction(
                    event -> {
                      // todo: implementare la conferma del preventivo
                      System.out.println("preventivo confermato");
                    });
              }

              @Override
              public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setGraphic(null);
                } else {
                  setGraphic(btn);
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnDataEmissione() {
    dataEmissioneColumn.setCellValueFactory(new PropertyValueFactory<>("dataEmissione"));
    dataEmissioneColumn.setCellFactory(
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

  private void setColumnPrezzoTotale() {
    totPrezzoColumn.setCellValueFactory(new PropertyValueFactory<>("totalePrezzo"));
    totPrezzoColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  setText(String.format("%.2f", item));
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnSconto() {
    scontoColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  if (item == null) setText("0 %");
                  else setText(String.format("%d %%", item));
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnPrezzoOptionals() {
    prezzoOptionalsColumn.setCellValueFactory(new PropertyValueFactory<>("prezzoOptionals"));
    prezzoOptionalsColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  setText(String.format("%d", item));
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnPrezzoBase() {
    prezzoBaseColumn.setCellValueFactory(new PropertyValueFactory<>("modello"));
    prezzoBaseColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(ModelloAuto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  setText("" + item.getPrezzoBase());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnModello() {
    modelloColumn.setCellValueFactory(new PropertyValueFactory<>("modello"));
    modelloColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(ModelloAuto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  setText(item.getModello());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnID() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    idColumn.setCellFactory(
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

  private void startPeriodicUpdate() {
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(this::updatePreventivi, 5, 30, TimeUnit.MINUTES);
  }

  private void updatePreventivi() {
    logger.info("updatePreventivi");
    UserSession.getInstance().setPreventivi();
    updateTableView();
  }

  private void updateTableView() {
    logger.info("updateTableView");
    preventiviUtente.setAll(UserSession.getInstance().getPreventivi());
    Platform.runLater(
        () -> {
          tableView.getItems().clear();
          tableView.getItems().setAll(preventiviUtente);
        });
  }
}
