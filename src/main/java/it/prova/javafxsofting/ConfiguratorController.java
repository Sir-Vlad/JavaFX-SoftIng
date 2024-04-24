package it.prova.javafxsofting;

import io.github.palexdev.materialfx.controls.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

public class ConfiguratorController implements Initializable {
  @FXML private AnchorPane root;
  @FXML private HBox toggleColor;
  @FXML private MFXScrollPane scrollPane;

  @FXML private VBox homeBtn;
  @FXML private VBox changeModelBtn;
  @FXML private VBox account;
  @FXML private VBox menu;

  @FXML private Text labelHome;
  @FXML private Text labelCambiaModello;
  @FXML private Text fieldModello;
  @FXML private Text fieldPrezzo;
  @FXML private Text fieldPrezzoValue;

  @FXML private Text fieldMarca;
  @FXML private Text fieldModelloV;
  @FXML private Text fieldAlimentazione;
  @FXML private Text fieldCambio;
  @FXML private Text fieldAltezza;
  @FXML private Text fieldLarghezza;
  @FXML private Text fieldLunghezza;
  @FXML private Text fieldPeso;
  @FXML private Text fieldVolBagagliaio;

  @FXML private MFXButton buttonFakeAdd;
  @FXML private MFXButton buttonFakeMinus;
  @FXML private StackPane modelVisualize;
  @FXML private Circle imgAccount;
  @FXML private SVGPath symbolMenu;
  @FXML private MFXButton saveConfigurazioneBtn;

  private boolean isMenuStageOpen = false;
  private boolean isMenuAccountOpen = false;

  // auto test
  private static final ModelloAuto auto =
      new ModelloAuto(0, "Skyline R-34 GTT", "Nissan", 80000, "", 1360, 4600, 1550, 180);

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    fieldPrezzoValue.setText(auto.getPrezzoBase() + " €");

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

    // init immagine di default per l'account
    imgAccount.setFill(
        new ImagePattern(
            new Image(String.valueOf(getClass().getResource("immagini/fake-account.png")))));

    // immagine per visualizzare qualcosa
    modelVisualize
        .getChildren()
        .add(
            new ImageView(
                new Image(String.valueOf(getClass().getResource("immagini/fake-account.png")))));

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

    account.setOnMouseClicked(
        mouseEvent -> {
          ContextMenu contextMenuAccount = contextMenuAccount();
          Point2D point = account.localToScreen(Point2D.ZERO);
          // posiziono la finestra rispetto al bottone
          double x = point.getX() - 25;
          double y = point.getY() + 50;
          openContextMenu(mouseEvent, contextMenuAccount, isMenuAccountOpen, x, y);
          isMenuAccountOpen = !isMenuAccountOpen;
        });

    hoverBtn(homeBtn);
    hoverBtn(changeModelBtn);

    createToggleButton();
    Platform.runLater(() -> scrollPane.vvalueProperty().set(0.0));
  }

  private void createToggleButton() {
    ToggleGroup toggleGroup = new ToggleGroup();

    MFXRectangleToggleNode redButton = new MFXRectangleToggleNode("Red");
    redButton.setToggleGroup(toggleGroup);
    redButton.setUserData(Color.RED);
    redButton.setStyle("-fx-background-color: red");

    MFXRectangleToggleNode grayButton = new MFXRectangleToggleNode("Gray");
    grayButton.setToggleGroup(toggleGroup);
    grayButton.setUserData(Color.GRAY);
    grayButton.setStyle("-fx-background-color: gray");

    MFXRectangleToggleNode blackButton = new MFXRectangleToggleNode("Black");
    blackButton.setToggleGroup(toggleGroup);
    blackButton.setUserData(Color.BLACK);
    blackButton.setStyle("-fx-background-color: black");

    toggleGroup
        .selectedToggleProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue == null) {
                modelVisualize.setStyle("-fx-background-color: white");
              } else {
                modelVisualize.setStyle(
                    "-fx-background-color: "
                        + "#"
                        + toggleGroup.getSelectedToggle().getUserData().toString().split("0x")[1]);
              }
            });

    toggleGroup.selectToggle(redButton);

    toggleColor.getChildren().addAll(redButton, grayButton, blackButton);
  }

  @FXML
  public void switchHome(@NotNull MouseEvent mouseEvent) {
    ScreenController.activate("home");
    mouseEvent.consume();
  }

  @FXML
  public void switchModelChange(@NotNull MouseEvent mouseEvent) {
    ScreenController.activate("modelChange");
    mouseEvent.consume();
  }

  @FXML
  public void salvaConfigurazione(@NotNull ActionEvent actionEvent) {
    NotImplemented.notImplemented();
    actionEvent.consume();
  }

  private void hoverBtn(@NotNull VBox btn) {
    btn.setOnMouseEntered(event -> btn.setStyle("-fx-background-color: #6F6F6F80"));
    btn.setOnMouseExited(event -> btn.setStyle(null));
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

  private @NotNull ContextMenu contextMenuAccount() {
    ContextMenu contextMenu = new ContextMenu();

    if (App.utente == null) {
      MenuItem signIn = new MenuItem("Sign In");
      signIn.setId("signIn");
      signIn.setOnAction(
          actionEvent -> {
            ScreenController.activate("login");
            actionEvent.consume();
          });
      contextMenu.getItems().add(signIn);
    } else {
      MenuItem profile = new MenuItem("Profilo");
      profile.setId("profile");
      profile.setOnAction(
          actionEvent -> {
            ScreenController.activate("profilo");
            actionEvent.consume();
          });

      MenuItem signOut = new MenuItem("Sign Out");
      signOut.setId("signOut");
      signOut.setOnAction(
          actionEvent -> {
            App.utente = null;
            ScreenController.activate("home");
            actionEvent.consume();
          });
      contextMenu.getItems().addAll(profile, new SeparatorMenuItem(), signOut);
    }

    return contextMenu;
  }
}
