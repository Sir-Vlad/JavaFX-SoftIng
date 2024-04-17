package it.prova.javafxsofting;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

public class ConfiguratorController implements Initializable {
  @FXML public Text labelHome;
  @FXML public Text labelCambiaModello;
  @FXML public Text fieldModello;
  @FXML public Text fieldPrezzo;
  @FXML public Text fieldPrezzoValue;
  @FXML public MFXButton buttonFakeAdd;
  @FXML public MFXButton buttonFakeMinus;
  @FXML public StackPane modelVisualize;
  @FXML public Circle imgAccount;
  @FXML public VBox menu;
  @FXML public SVGPath symbolMenu;
  @FXML public VBox account;
  @FXML public AnchorPane root;
  private boolean isMenuStageOpen = false;
  private boolean isMenuAccountOpen = false;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    labelHome.setFill(Color.BLACK);
    labelCambiaModello.setFill(Color.BLACK);
    fieldModello.setFill(Color.BLACK);
    fieldPrezzo.setFill(Color.BLACK);
    fieldPrezzoValue.setFill(Color.BLACK);

    fieldPrezzoValue.setText("0 €");

    // init immagine di default per l'account
    imgAccount.setFill(
        new ImagePattern(
            new Image(String.valueOf(getClass().getResource("immagini/fake-account.png")))));

    labelHome.setOnMouseClicked(
        mouseEvent -> {
          // todo: redirect alla home
        });

    labelCambiaModello.setOnMouseClicked(
        mouseEvent -> {
          // todo: redict alla page per cambiare modello
        });

    // immagine per visualizzare qualcosa
    modelVisualize
        .getChildren()
        .add(
            new ImageView(
                new Image(String.valueOf(getClass().getResource("immagini/fake-account.png")))));

    ContextMenu contextMenu = createContextMenu();

    menu.setOnMouseClicked(
        actionEvent -> {
          Point2D point = menu.localToScreen(Point2D.ZERO);
          // posiziono la finestra rispetto al bottone
          double x = point.getX() - 120;
          double y = point.getY() - 150;
          openContextMenu(actionEvent, contextMenu, isMenuStageOpen, x, y);
          isMenuStageOpen = !isMenuStageOpen;
          actionEvent.consume();
        });

    ContextMenu contextMenuAccount = contextMenuAccount();

    account.setOnMouseClicked(
        mouseEvent -> {
          Point2D point = account.localToScreen(Point2D.ZERO);
          // posiziono la finestra rispetto al bottone
          double x = point.getX() - 60;
          double y = point.getY() + 50;
          openContextMenu(mouseEvent, contextMenuAccount, isMenuAccountOpen, x, y);
          isMenuAccountOpen = !isMenuAccountOpen;
        });
  }

  public void incrementaPrezzo() {
    fieldPrezzoValue.setText(
        Integer.parseInt(fieldPrezzoValue.getText().split(" ")[0]) + 10 + " €");
  }

  public void decrementaPrezzo() {
    fieldPrezzoValue.setText(
        Integer.parseInt(fieldPrezzoValue.getText().split(" ")[0]) - 10 + " €");
  }

  public void switchHome(MouseEvent mouseEvent) throws IOException {
    ScreenController.activate("home");
    mouseEvent.consume();
  }

  public void switchModelChange(MouseEvent mouseEvent) throws IOException {
    ScreenController.activate("modelChange");
    mouseEvent.consume();
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

    MenuItem profile = new MenuItem("Profilo");
    profile.setId("profile");

    MenuItem signOut = new MenuItem("Sign Out");
    profile.setId("signOut");

    contextMenu.getItems().addAll(profile, new SeparatorMenuItem(), signOut);

    return contextMenu;
  }
}
