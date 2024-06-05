package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Ordine;
import it.prova.javafxsofting.models.Preventivo;
import it.prova.javafxsofting.models.Concessionario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PreventivoUsatoController implements Initializable {
  private final ObservableList<Preventivo> preventivoUsatoUtente =
      FXCollections.observableArrayList();
  @FXML private TableView<Preventivo> tableView;
  @FXML private TableColumn<Preventivo, Integer> idColumn;
  @FXML private TableColumn<Preventivo, ModelloAuto> modelloColumn;
  @FXML private TableColumn<Preventivo, Preventivo> preventivoColumn;
  @FXML private TableColumn<Preventivo, Integer> prezzoColumn;
  @FXML private TableColumn<Preventivo, LocalDate> dataRitiroColumn;
  @FXML private TableColumn<Preventivo, Concessionario> concessionarioColumn;
  @FXML private TableColumn<Preventivo, Boolean> statoColumn;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //    String subDirectory = String.format("utente/%d/ordini/", App.getUtente().getId());
    //    List<Preventivo> preventivoUsato;
    //    try {
    //      preventivoUsato = Connection.getArrayDataFromBackend(subDirectory, Ordine.class);
    //    } catch (Exception e) {
    //      //      throw new RuntimeException(e);
    //      preventivoUsato = new ArrayList<>();
    //    }
    //    if (preventivoUsato != null) {
    //      preventivoUsato.forEach(Ordine::transformIdToObject);
    //      preventivoUsatoUtente.setAll(preventivoUsato);
    //    }
    setTableView();
  }

  private void setTableView() {
    setColumnID();
    setColumnModello();
    setColumnPreventivo();
    setColumnPrezzo();
    setColumnDataRitiro();
    setColumnConcessionario();
    setColumnStato();

    tableView.getItems().addAll(preventivoUsatoUtente);
  }

  private void setColumnStato() {
    statoColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setGraphic(null);
                } else {
                  Circle circle = new Circle(10);
                  if (item) {
                    circle.setFill(Color.GREEN);
                  } else {
                    circle.setFill(Color.YELLOW);
                  }
                  setGraphic(circle);
                }
              }
            });
  }

  private void setColumnConcessionario() {}

  private void setColumnDataRitiro() {}

  private void setColumnPrezzo() {}

  private void setColumnPreventivo() {}

  private void setColumnModello() {}

  private void setColumnID() {}
}
