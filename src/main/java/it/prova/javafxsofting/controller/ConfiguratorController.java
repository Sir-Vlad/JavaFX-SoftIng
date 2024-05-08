package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.*;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.ModelloAuto;
import it.prova.javafxsofting.NotImplemented;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.component.ProfileBox;
import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

public class ConfiguratorController implements Initializable {
  @FXML private Header header;
  @FXML private AnchorPane root;
  @FXML private HBox toggleColor;
  @FXML private MFXScrollPane scrollPane;
  @FXML private VBox homeBtn;
  @FXML private VBox changeModelBtn;
  @FXML private ProfileBox account;
  @FXML private VBox menu;
  @FXML private Text labelHome;
  @FXML private Text labelCambiaModello;
  @FXML private Text fieldModello;
  @FXML private Text fieldMarca;
  @FXML private Text fieldModelloV;
  @FXML private Text fieldAlimentazione;
  @FXML private Text fieldCambio;
  @FXML private Text fieldAltezza;
  @FXML private Text fieldLarghezza;
  @FXML private Text fieldLunghezza;
  @FXML private Text fieldPeso;
  @FXML private Text fieldVolBagagliaio;
  @FXML private StackPane modelVisualize;
  @FXML private SVGPath symbolMenu;
  @FXML private MFXButton saveConfigurazioneBtn;
  private boolean isMenuStageOpen = false;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    ModelloAuto auto = ScegliModelloController.getAutoSelezionata();
    if (auto == null) {
      ScreenController.removeScreen("config");
      ScreenController.activate("scegliModello");
      return;
    }

    header.addTab(
        "Home",
        event -> {
          ScreenController.removeScreen("config");
          ScreenController.activate("home");
        });
    header.addTab(
        "Cambia Modello",
        event -> {
          ScreenController.removeScreen("config");
          ScreenController.activate("scegliModello");
        });

    createBoxPrezzo(auto);

    fieldModelloV.setText(auto.getNome());
    fieldMarca.setText(auto.getMarca());
    fieldModello.setText(auto.getNome());
    fieldAltezza.setText(auto.getAltezza() + " mm");
    fieldLarghezza.setText(auto.getLarghezza() + " mm");
    fieldLunghezza.setText(auto.getLunghezza() + " mm");
    fieldPeso.setText(auto.getPeso() + " kg");
    fieldVolBagagliaio.setText(auto.getVolumeBagagliaio() + " L");

    scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

    // immagine per visualizzare qualcosa
    modelVisualize
        .getChildren()
        .add(
            new ImageView(
                new Image(String.valueOf(App.class.getResource("immagini/fake-account.png")))));

    menu.setOnMouseClicked(
        actionEvent -> {
          ContextMenu contextMenu = createContextMenu();
          Point2D point = menu.localToScreen(Point2D.ZERO);
          // posiziono la finestra rispetto al bottone
          double x = point.getX() - 80;
          double y = point.getY() - 100;
          openContextMenu(actionEvent, contextMenu, isMenuStageOpen, x, y);
          isMenuStageOpen = !isMenuStageOpen;
          actionEvent.consume();
        });

    createToggleButton();
  }

  @FXML
  public void salvaConfigurazione(@NotNull ActionEvent actionEvent) {
    NotImplemented.notImplemented();
    actionEvent.consume();
  }

  private void createBoxPrezzo(ModelloAuto auto) {
    VBox vbox = new VBox();
    vbox.setPrefWidth(200);
    vbox.setAlignment(Pos.CENTER);

    Text fieldPrezzo = new Text("Prezzo");
    Text fieldPrezzoValue = new Text();
    fieldPrezzoValue.setId("fieldPrezzoValue");
    fieldPrezzoValue.setStyle("-fx-font-weight: bold; -fx-font-size: 20");

    vbox.getChildren().addAll(fieldPrezzo, fieldPrezzoValue);

    int index = header.getChildren().size() - 1;
    header.getChildren().add(index, vbox);

    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    fieldPrezzoValue.setText(decimalFormat.format(auto.getPrezzoBase()) + " €");
  }

  private void createToggleButton() {
    // todo: quando ho i colori della macchina passarli come argomento e generare i colori così
    ToggleGroup toggleGroup = new ToggleGroup();

    ArrayList<Color> colorList = new ArrayList<>();
    colorList.add(Color.RED);
    colorList.add(Color.BLUE);
    colorList.add(Color.GREEN);

    for (Color color : colorList) {
      // fixme: il nome del colore deve essere mappato tramite una mappa nome hex
      MFXRectangleToggleNode button = new MFXRectangleToggleNode(color.toString());
      button.setToggleGroup(toggleGroup);
      button.setUserData(color);
      String hexValue = "#" + color.toString().split("0x")[1].substring(0, 6);
      button.setStyle("-fx-background-color: " + hexValue + ";");
      toggleColor.getChildren().add(button);
    }

    toggleGroup
        .selectedToggleProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // Se il newValue è null riseleziono il toggle vecchio altrimenti seleziono quello
              // nuovo. Questo mi serve per avere sempre un'alternativa selezionata.
              if (newValue == null) {
                toggleGroup.selectToggle(oldValue);
              } else {
                modelVisualize.setStyle(
                    "-fx-background-color: "
                        + "#"
                        + toggleGroup.getSelectedToggle().getUserData().toString().split("0x")[1]);
              }
            });
  }

  private void openContextMenu(
      MouseEvent mouseEvent, ContextMenu menu, boolean open, double xPos, double yPos) {
    if (open) {
      menu.hide();
    } else {
      menu.show(account, xPos, yPos);
    }
    mouseEvent.consume();
  }

  private @NotNull ContextMenu createContextMenu() {
    ContextMenu contextMenu = new ContextMenu();

    MenuItem sommario = new MenuItem("Sommario");
    sommario.setId("sommario");
    sommario.setOnAction(
        actionEvent -> {
          System.out.println("Sommario");
          actionEvent.consume();
        });

    MenuItem preventivo = new MenuItem("Preventivo");
    preventivo.setId("preventivo");
    preventivo.setOnAction(
        actionEvent -> {
          System.out.println("Preventivo");
          actionEvent.consume();
        });

    MenuItem concessionari = new MenuItem("Concessionari");
    concessionari.setId("concessionari");
    concessionari.setOnAction(
        actionEvent -> {
          System.out.println("Concessionari vicino a te");
          actionEvent.consume();
        });

    SeparatorMenuItem separator1 = new SeparatorMenuItem();
    SeparatorMenuItem separator2 = new SeparatorMenuItem();

    contextMenu.getItems().addAll(sommario, separator1, preventivo, separator2, concessionari);

    return contextMenu;
  }
}
