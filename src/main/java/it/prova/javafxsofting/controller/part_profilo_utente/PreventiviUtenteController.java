package it.prova.javafxsofting.controller.part_profilo_utente;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.controller.part_profilo_utente.ProfileAccountController.TabController;
import it.prova.javafxsofting.data_manager.DataManager;
import it.prova.javafxsofting.models.ModelloAuto;
import it.prova.javafxsofting.models.Ordine;
import it.prova.javafxsofting.models.Preventivo;
import it.prova.javafxsofting.models.Sconto;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PreventiviUtenteController implements Initializable {
  private static final ObservableList<Preventivo> preventiviUtente = getPreventivi();
  private static ScheduledExecutorService scheduler;

  static {
    getPreventivi();
  }

  private final DecimalFormat decimalFormat = new DecimalFormat("###,###");
  private final Logger logger = Logger.getLogger(this.getClass().getName());
  @FXML private AnchorPane root;
  @FXML private TableColumn<Preventivo, Integer> idColumn;
  @FXML private TableColumn<Preventivo, ModelloAuto> modelloColumn;
  @FXML private TableColumn<Preventivo, ModelloAuto> prezzoBaseColumn;
  @FXML private TableColumn<Preventivo, Integer> prezzoOptionalsColumn;
  @FXML private TableColumn<Preventivo, ModelloAuto> scontoColumn;
  @FXML private TableColumn<Preventivo, Float> totPrezzoColumn;
  @FXML private TableColumn<Preventivo, LocalDate> dataEmissioneColumn;
  @FXML private TableColumn<Preventivo, Void> confermaColumn;
  @FXML private TableView<Preventivo> tableView;

  /**
   * Trasforma la lista di preventivi in una ObservableList
   *
   * @return la ObservableList dei preventivi
   */
  @Contract(" -> new")
  private static @NotNull ObservableList<Preventivo> getPreventivi() {
    ArrayList<Preventivo> preventivi =
        new ArrayList<>(
            UserSession.getInstance().getPreventivi().stream()
                .filter(
                    p ->
                        p.getStato().equalsIgnoreCase("valido")
                            || p.getStato().equalsIgnoreCase("va"))
                .toList());

    return FXCollections.observableArrayList(preventivi);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    /*Aggiorna la tabella dei preventivi quando si aggiunge un nuovo preventivo*/
    UserSession.getInstance()
        .addListenerPreventivo(
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

  private static void createLabelSuccess(HBox wrapper) {
    wrapper.getChildren().clear();
    Text text = new Text();
    text.setTextAlignment(TextAlignment.CENTER);
    text.setText("Preventivo confermato!");
    text.setFont(Font.font(20));
    wrapper.getChildren().add(text);
    wrapper.setAlignment(Pos.CENTER);
  }

  private static void postOrdine(Preventivo preventivo, Ordine acquisto) {
    try {
      String url =
          String.format(
              "utente/%d/preventivo/%d/conferma/",
              UserSession.getInstance().getUtente().getId(), preventivo.getId());
      Connection.postDataToBacked(acquisto, url);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void createStagePayPreventivo(Preventivo preventivo) {
    Stage stage = new Stage();
    stage.setTitle("Conferma preventivo");
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initOwner(root.getParent().getParent().getScene().getWindow());
    AnchorPane rootSubStage;
    try {
      rootSubStage =
          FXMLLoader.load(
              Objects.requireNonNull(App.class.getResource("controller/pay_preventivo.fxml")));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    MFXTextField accontoField = (MFXTextField) rootSubStage.lookup("#accontoField");
    MFXButton sendAcquisto = (MFXButton) rootSubStage.lookup("#sendAcquisto");
    MFXProgressSpinner loading = (MFXProgressSpinner) rootSubStage.lookup("#loading");
    HBox wrapper = (HBox) rootSubStage.lookup("#wrapper");
    sendAcquisto.setOnAction(
        event1 -> {
          String acconto = accontoField.getText();
          if (acconto.isEmpty() || preventivo.getTotalePrezzo() - Integer.parseInt(acconto) < 0) {
            return;
          }
          Ordine acquisto = new Ordine();
          acquisto.setAcconto(Integer.parseInt(acconto));
          loading.setVisible(true);
          PauseTransition pause = new PauseTransition(Duration.seconds(10));
          pause.setOnFinished(
              event -> {
                postOrdine(preventivo, acquisto);
                loading.setVisible(false);
                createLabelSuccess(wrapper);

                PauseTransition pause1 = new PauseTransition(Duration.seconds(3));
                pause1.setOnFinished(
                    event1x -> {
                      updatePreventivi();
                      UserSession.getInstance().setOrdini();
                      OrdiniUtenteController ordiniUtenteController =
                          TabController.CONTROLLER.get("ordini").getController();
                      ordiniUtenteController.updateTableView();
                      stage.close();
                    });
                pause1.play();
              });
          pause.play();
        });
    Scene scene = new Scene(rootSubStage);
    stage.setScene(scene);
    stage.showAndWait();
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
                      Preventivo preventivo = getTableView().getItems().get(getIndex());
                      createStagePayPreventivo(preventivo);
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
                  Preventivo preventivo = getTableView().getItems().get(getIndex());
                  btn.setDisable(preventivo.getDataEmissione() == null);
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
                  if (item == null) setText("");
                  else setText(item.toString());
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
                  setText(decimalFormat.format(item) + " €");
                  setAlignment(Pos.CENTER);
                }
              }
            });
  }

  private void setColumnSconto() {
    scontoColumn.setCellValueFactory(new PropertyValueFactory<>("modello"));
    scontoColumn.setCellFactory(
        column ->
            new TableCell<>() {
              @Override
              protected void updateItem(ModelloAuto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                  setText(null);
                } else {
                  Sconto sconto =
                      DataManager.getInstance().getSconti().stream()
                          .filter(s -> s.getIdModello() == item.getId())
                          .findFirst()
                          .orElse(null);

                  if (sconto == null) setText("0 %");
                  else setText(String.format("%d %%", sconto.getPercentualeSconto()));
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
                  setText(decimalFormat.format(item) + " €");
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
                  setText(decimalFormat.format(item.getPrezzoBase()) + " €");
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
    logger.info("updateTableView - Preventivi");
    Platform.runLater(
        () -> {
          tableView.getItems().clear();
          preventiviUtente.setAll(getPreventivi());
          tableView.getItems().setAll(preventiviUtente);
        });
  }
}
