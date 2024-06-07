package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.models.Concessionario;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Preventivo;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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
                  if (Boolean.TRUE.equals(item)) {
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
