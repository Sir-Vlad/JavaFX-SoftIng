package it.prova.javafxsofting.controller.part_profilo_utente;

import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.models.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import org.jetbrains.annotations.NotNull;

public class PreventivoUsatoUtenteController implements Initializable {
  private static final ObservableList<PreventivoUsato> preventivoUsatoUtente = getPreventiviUsati();
  private static ScheduledExecutorService scheduler;

  static {
    getPreventiviUsati();
  }

  private final Logger logger = Logger.getLogger(this.getClass().getName());
  @FXML private TableView<PreventivoUsato> tableView;
  @FXML private TableColumn<PreventivoUsato, Integer> idColumn;
  @FXML private TableColumn<PreventivoUsato, AutoUsata> modelloColumn;
  @FXML private TableColumn<PreventivoUsato, AutoUsata> preventivoColumn;
  @FXML private TableColumn<PreventivoUsato, AutoUsata> prezzoColumn;
  @FXML private TableColumn<PreventivoUsato, Boolean> statoColumn;

  private static @NotNull ObservableList<PreventivoUsato> getPreventiviUsati() {
    ArrayList<PreventivoUsato> preventiviUsati =
        new ArrayList<>(UserSession.getInstance().getPreventiviUsati());
    return FXCollections.observableArrayList(preventiviUsati);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    /*Aggiorna la tabella dei preventivi quando si aggiunge un nuovo preventivo*/
    UserSession.getInstance()
        .addListenerPreventivoUsato(
            new UserSession.PreventivoUsatoListener() {
              @Override
              public void onPreventivoChange(List<PreventivoUsato> preventivi) {
                updateTableView();
              }

              @Override
              public void onPreventivoAdded(PreventivoUsato preventivo) {
                preventivoUsatoUtente.add(preventivo);
              }
            });
    setTableView();
    startPeriodicUpdate();
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
                  UserSession.getInstance()
                      .getDetrazioni()
                      .forEach(
                          detrazione -> {
                            if (detrazione.getIdAutoUsata() == item.getId()) {
                              setText(String.valueOf(detrazione.getIdPreventivo()));
                            }
                          });

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

  private void startPeriodicUpdate() {
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(this::updatePreventiviUsati, 5, 30, TimeUnit.MINUTES);
  }

  private void updatePreventiviUsati() {
    logger.info("updatePreventiviUsati");
    UserSession.getInstance().setPreventiviUsati();
    updateTableView();
  }

  private void updateTableView() {
    logger.info("updateTableView - PreventiviUsati");
    Platform.runLater(
        () -> {
          tableView.getItems().clear();
          preventivoUsatoUtente.setAll(UserSession.getInstance().getPreventiviUsati());
          tableView.getItems().setAll(preventivoUsatoUtente);
        });
  }
}
