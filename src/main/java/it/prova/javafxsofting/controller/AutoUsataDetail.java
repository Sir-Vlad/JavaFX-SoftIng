package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Connection;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.models.AutoUsata;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

public class AutoUsataDetail implements Initializable {
  private static final Logger logger = Logger.getLogger(AutoUsataDetail.class.getName());
  private final List<Image> images = new ArrayList<>();
  @FXML private StackPane stackPaneImage;
  @FXML private VBox datiAuto;
  @FXML private AnchorPane root;
  @FXML private Header header;
  @FXML private MFXButton prev;
  @FXML private MFXButton next;
  private int currentIndex = 0;
  private AutoUsata autoUsata;

  private static void setSizeImage(ImageView imageView) {
    imageView.setPreserveRatio(true);
    imageView.setFitHeight(500);
    imageView.setFitWidth(500);
  }

  private static void setBlurImage(
      @NotNull ImageView newImage, MotionBlur motionBlur, @NotNull ImageView oldImage) {
    newImage.setEffect(motionBlur);
    oldImage.setEffect(motionBlur);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    autoUsata = ScegliUsatoController.getAutoSelezionata();
    if (autoUsata == null) {
      ScreenController.removeScreen("autoUsataDetail");
      ScreenController.activate("scegliUsato");
      return;
    }

    header.addTab("Home", event -> ScreenController.activate("home"));
    header.addTab("Indietro", event -> ScreenController.back());

    loadImages();
    loadDatiAuto();
  }

  public void prevImage(ActionEvent actionEvent) {
    if (images.isEmpty()) {
      return;
    }
    currentIndex = (currentIndex - 1 + images.size()) % images.size();
    sliderImages(-600);
    actionEvent.consume();
  }

  public void nextImage(ActionEvent actionEvent) {
    if (images.isEmpty()) {
      return;
    }
    currentIndex = (currentIndex + 1) % images.size();
    sliderImages(600);
    actionEvent.consume();
  }

  public void acquistaAuto(ActionEvent actionEvent) {
    if (UserSession.getInstance().getUtente() == null) {
      ScreenController.activate("login");
      actionEvent.consume();
      return;
    }

    openStageBuy();

    ScreenController.removeScreen("autoUsataDetail");
    ScreenController.activate("home");
    actionEvent.consume();
  }

  private void openStageBuy() {
    Stage stage = new Stage();
    stage.setTitle("Acquisto");
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initOwner(root.getScene().getWindow());
    AnchorPane rootScene;
    try {
      rootScene =
          FXMLLoader.load(
              Objects.requireNonNull(App.class.getResource("controller/pay_preventivo.fxml")));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Text title = (Text) rootScene.lookup("#title");
    title.setText("Acquisto Auto Usata");
    Label labelBuy = (Label) rootScene.lookup("#labelBuy");
    labelBuy.setText("Prezzo");
    MFXTextField accontoField = (MFXTextField) rootScene.lookup("#accontoField");
    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    accontoField.setText(decimalFormat.format(autoUsata.getPrezzo()));
    accontoField.setDisable(true);
    MFXProgressSpinner loading = (MFXProgressSpinner) rootScene.lookup("#loading");
    HBox wrapper = (HBox) rootScene.lookup("#wrapper");

    MFXButton sendAcquisto = (MFXButton) rootScene.lookup("#sendAcquisto");
    sendAcquisto.setText("Paga");
    sendAcquisto.setOnAction(
        event -> {
          loading.setVisible(true);
          PauseTransition pause = new PauseTransition(Duration.seconds(10));
          pause.setOnFinished(
              event1 -> {
                String subDirectory =
                    String.format(
                        "utente/%d/autoUsata/%d/compra/",
                        UserSession.getInstance().getUtente().getId(), autoUsata.getId());

                try {
                  Connection.postDataToBacked(null, subDirectory);
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }

                loading.setVisible(false);

                wrapper.getChildren().clear();
                Text text = new Text();
                text.setTextAlignment(TextAlignment.CENTER);
                text.setText("Acquisto effettuato con successo");
                text.setFont(Font.font(20));

                wrapper.getChildren().add(text);
                wrapper.setAlignment(Pos.CENTER);

                PauseTransition pause1 = new PauseTransition(Duration.seconds(3));
                pause1.setOnFinished(
                    event1x -> {
                      new Thread(
                              () -> {
                                UserSession.getInstance().setOrdini();
                                // update page ScegliUsato
                                ScegliUsatoController controller =
                                    ScreenController.getSCREEN_MAP()
                                        .get("scegliUsato")
                                        .getController();
                                controller.updatePage();
                              })
                          .start();
                      stage.close();
                    });
                pause1.play();
              });
          pause.play();
        });

    stage.setScene(new Scene(rootScene));
    stage.showAndWait();
  }

  private void loadDatiAuto() {
    HBox hBox;
    Path path = Path.of("controller", "part_configurator").resolve("dati_tecnici.fxml");
    try {
      hBox = FXMLLoader.load(Objects.requireNonNull(App.class.getResource(path.toString())));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    datiAuto.getChildren().add(hBox);

    Text fieldAltezza = (Text) hBox.lookup("#fieldAltezza");
    Text fieldLarghezza = (Text) hBox.lookup("#fieldLarghezza");
    Text fieldLunghezza = (Text) hBox.lookup("#fieldLunghezza");
    Text fieldPeso = (Text) hBox.lookup("#fieldPeso");
    Text fieldVolBagagliaio = (Text) hBox.lookup("#fieldVolBagagliaio");

    fieldAltezza.setText(autoUsata.getAltezza() + " mm");
    fieldLarghezza.setText(autoUsata.getLarghezza() + " mm");
    fieldLunghezza.setText(autoUsata.getLunghezza() + " mm");
    fieldPeso.setText(autoUsata.getPeso() + " kg");
    fieldVolBagagliaio.setText(autoUsata.getVolumeBagagliaio() + " L");
  }

  private void loadImages() {
    images.addAll(
        ScegliUsatoController.getAutoSelezionata().getImmagini().stream()
            .map(
                images -> {
                  try {
                    return new Image(String.valueOf(images.toURI().toURL()));
                  } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                  }
                })
            .toList());

    if (!images.isEmpty()) {
      currentIndex = 0;

      ImageView imageView = new ImageView(images.get(currentIndex));
      setSizeImage(imageView);

      stackPaneImage.getChildren().add(imageView);
    }

    if (images.size() <= 1) {
      toggleButton(true);
    }
  }

  private void sliderImages(double distance) {
    ImageView newImage = new ImageView(images.get(currentIndex));
    ImageView oldImage = (ImageView) stackPaneImage.getChildren().getFirst();

    setSizeImage(newImage);

    TranslateTransition newImageTransition =
        new TranslateTransition(Duration.millis(300), newImage);

    TranslateTransition oldImageTransition =
        new TranslateTransition(Duration.millis(300), oldImage);

    newImageTransition.setFromX(distance);
    newImageTransition.setToX(0);

    oldImageTransition.setByX(-distance);

    toggleButton(true);

    MotionBlur motionBlur = new MotionBlur();
    motionBlur.setRadius(50);

    stackPaneImage.getChildren().add(newImage);
    setBlurImage(newImage, motionBlur, oldImage);

    newImageTransition.setOnFinished(
        event -> {
          toggleButton(false);
          setBlurImage(newImage, null, oldImage);
        });

    oldImageTransition.setOnFinished(
        event -> {
          stackPaneImage.getChildren().remove(oldImage);
          toggleButton(false);
          setBlurImage(newImage, null, oldImage);
        });

    newImageTransition.play();
    oldImageTransition.play();
  }

  private void toggleButton(boolean value) {
    next.setDisable(value);
    prev.setDisable(value);
  }
}
