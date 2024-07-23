package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.data_manager.DataManager;
import it.prova.javafxsofting.models.Concessionario;
import it.prova.javafxsofting.models.Indirizzo;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;

public class ConcessionariController implements Initializable {
  private static final ObservableList<Concessionario> concessionari;
  private static final String INDIRIZZO_LITERAL = "indirizzo";

  static {
    if (DataManager.getInstance().getConcessionari() != null) {
      concessionari =
          FXCollections.observableArrayList(DataManager.getInstance().getConcessionari());
    } else {
      concessionari = FXCollections.observableArrayList();
    }
  }

  @FXML private Pane wrapperRoot;

  @FXML private Header header;
  @FXML private TableView<Concessionario> tableView;
  @FXML private TableColumn<Concessionario, String> nomeColumn;
  @FXML private TableColumn<Concessionario, Indirizzo> viaColumn;
  @FXML private TableColumn<Concessionario, Indirizzo> civicoColumn;
  @FXML private TableColumn<Concessionario, Indirizzo> capColumn;
  @FXML private TableColumn<Concessionario, Indirizzo> cittaColumn;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Home", event -> ScreenController.activate("home"));

    GaussianBlur gaussianBlur = new GaussianBlur(10);
    wrapperRoot.setEffect(gaussianBlur);
    setTableView();
  }

  private void setTableView() {
    setNome();
    setVia();
    setCivico();
    setCitta();
    setCap();

    tableView.getItems().setAll(concessionari);
    tableView.setPrefHeight(tableView.getItems().size() * (tableView.getFixedCellSize() + 0.5));
  }

  private void setCap() {
    capColumn.setCellValueFactory(new PropertyValueFactory<>(INDIRIZZO_LITERAL));
    capColumn.setCellFactory(
        column ->
            new TableCell<Concessionario, Indirizzo>() {
              @Override
              protected void updateItem(Indirizzo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setGraphic(null);
                } else {
                  setText(item.getCap());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setCitta() {
    cittaColumn.setCellValueFactory(new PropertyValueFactory<>(INDIRIZZO_LITERAL));
    cittaColumn.setCellFactory(
        column ->
            new TableCell<Concessionario, Indirizzo>() {
              @Override
              protected void updateItem(Indirizzo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setGraphic(null);
                } else {
                  setText(item.getCitta());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setCivico() {
    civicoColumn.setCellValueFactory(new PropertyValueFactory<>(INDIRIZZO_LITERAL));
    civicoColumn.setCellFactory(
        column ->
            new TableCell<Concessionario, Indirizzo>() {
              @Override
              protected void updateItem(Indirizzo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setGraphic(null);
                } else {
                  setText(item.getCivico());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setVia() {
    viaColumn.setCellValueFactory(new PropertyValueFactory<>(INDIRIZZO_LITERAL));
    viaColumn.setCellFactory(
        column ->
            new TableCell<Concessionario, Indirizzo>() {
              @Override
              protected void updateItem(Indirizzo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setGraphic(null);
                } else {
                  setText(item.getVia());
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setNome() {
    nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
    nomeColumn.setCellFactory(
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
}
