package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Preventivo;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;

public class PreventiviUtenteController implements Initializable {

  @Getter
  private static final ObservableList<Preventivo> preventiviUtente =
      FXCollections.observableArrayList();

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
    String subDirectory = String.format("utente/%d/preventivi/", App.getUtente().getId());
    List<Preventivo> preventivi;
    try {
      preventivi = Connection.getArrayDataFromBackend(subDirectory, Preventivo.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (preventivi != null) {
      preventivi.forEach(Preventivo::transformIdToObject);
      preventiviUtente.setAll(preventivi);
    }
    setTableView();
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
}
