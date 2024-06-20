package it.prova.javafxsofting.controller;

import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.models.*;
import java.net.URL;
import java.text.DecimalFormat;
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
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

public class PreventivoUsatoUtenteController implements Initializable {
  private final ObservableList<PreventivoUsato> preventivoUsatoUtente =
      FXCollections.observableArrayList();

  @FXML private TableView<PreventivoUsato> tableView;
  @FXML private TableColumn<PreventivoUsato, Integer> idColumn;
  @FXML private TableColumn<PreventivoUsato, AutoUsata> modelloColumn;
  @FXML private TableColumn<PreventivoUsato, AutoUsata> preventivoColumn;
  @FXML private TableColumn<PreventivoUsato, AutoUsata> prezzoColumn;
  @FXML private TableColumn<PreventivoUsato, Boolean> statoColumn;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    if (UserSession.getInstance().getPreventiviUsati() != null) {
      preventivoUsatoUtente.setAll(UserSession.getInstance().getPreventiviUsati());
    }
    setTableView();
  }

  private void setTableView() {
    setColumnID();
    setColumnModello();
    setColumnPreventivo();
    setColumnPrezzo();
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
                  PreventivoUsato preventivoUsato = getTableView().getItems().get(getIndex());
                  if (preventivoUsato.getAutoUsata().getPrezzo() > 0) {
                    RadialGradient greenGradient =
                        new RadialGradient(
                            0,
                            0.1,
                            0.5,
                            0.5,
                            0.5,
                            true,
                            CycleMethod.NO_CYCLE,
                            new Stop(0, Color.LIGHTGREEN),
                            new Stop(1, Color.DARKGREEN));
                    circle.setFill(greenGradient);
                  } else {
                    RadialGradient yellowGradient =
                        new RadialGradient(
                            0,
                            0.1,
                            0.5,
                            0.5,
                            0.5,
                            true,
                            CycleMethod.NO_CYCLE,
                            new Stop(0, Color.YELLOW),
                            new Stop(1, Color.GOLD));
                    circle.setFill(yellowGradient);
                  }
                  setGraphic(circle);
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnPrezzo() {
    prezzoColumn.setCellValueFactory(new PropertyValueFactory<>("autoUsata"));
    prezzoColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(AutoUsata item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  DecimalFormat decimalFormat = new DecimalFormat("###,###");
                  setText(decimalFormat.format(item.getPrezzo()) + " €");
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnPreventivo() {
    preventivoColumn.setCellValueFactory(new PropertyValueFactory<>("autoUsata"));
    preventivoColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(AutoUsata item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  System.out.println("Item: " + item);
                  System.out.println(UserSession.getInstance().getDetrazioni());
                  int idPreventivo =
                      UserSession.getInstance().getDetrazioni().stream()
                          .filter(detrazione -> detrazione.getIdAutoUsata() == item.getId())
                          .toList()
                          .getFirst()
                          .getIdPreventivo();

                  setText(String.valueOf(idPreventivo));
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnModello() {
    modelloColumn.setCellValueFactory(new PropertyValueFactory<>("autoUsata"));
    modelloColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(AutoUsata item, boolean empty) {
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
