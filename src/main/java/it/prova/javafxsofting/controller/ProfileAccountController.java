package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import it.prova.javafxsofting.App;
import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import lombok.Getter;
import lombok.SneakyThrows;

public class ProfileAccountController implements Initializable {
  @FXML private HBox image_account;
  @FXML private Label name_account;

  @FXML private HBox profiloBtn;
  @FXML private HBox ordiniBtn;
  @FXML private HBox preventiviBtn;
  @FXML private HBox signOutBtn;

  @FXML private SVGPath icon_profilo;
  @FXML private SVGPath icon_ordini;
  @FXML private SVGPath icon_preventivi;
  @FXML private SVGPath icon_signOut;

  @FXML private VBox content;
  @FXML private VBox sidebar;
  @FXML private MFXButton indietroBtn;
  private TabController tabController;

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // imposto i dati dell'utente
    if (App.getUtente() != null) {
      name_account.setText(App.getUtente().getNome() + " " + App.getUtente().getCognome());
    } else {
      ScreenController.activate("login");
      return;
    }

    // ridimensionamento delle icone della sidebar
    resize(icon_profilo, 20, 20);
    resize(icon_ordini, 20, 20);
    resize(icon_preventivi, 20, 25);
    resize(icon_signOut, 20, 20);

    // create le tab della sidebar
    tabController = new TabController();
    tabController.addTab(
        "profile",
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dettagli_profilo.fxml"))),
        profiloBtn);

    tabController.addTab(
        "preventivi",
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("preventivi_utente.fxml"))),
        preventiviBtn);

    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setId("ordini");

    tabController.addTab("ordini", anchorPane, ordiniBtn);

    // set default page open
    content.getChildren().add(tabController.getTab("profile"));
    profiloBtn.setStyle("-fx-background-color: #0D3BB1; -fx-background-radius: 10");
  }

  public void switchProfilo(MouseEvent mouseEvent) {
    switchTab(profiloBtn, "profile");
    mouseEvent.consume();
  }

  public void switchOrdini(MouseEvent mouseEvent) {
    switchTab(ordiniBtn, "ordini");
    mouseEvent.consume();
  }

  public void switchPreventivi(MouseEvent mouseEvent) {
    switchTab(preventiviBtn, "preventivi");
    mouseEvent.consume();
  }

  public void signOut(MouseEvent mouseEvent) {
    ScreenController.removeScreen("profile");
    ScreenController.activate("login");
    mouseEvent.consume();
  }

  public void switchIndietro(ActionEvent actionEvent) {
    ScreenController.back();
    actionEvent.consume();
  }

  private void switchTab(HBox btn, String title) {
    tabController
        .getButton(tabController.getKeyMain())
        .setStyle("-fx-background: #ffffff; -fx-background-radius: 10");
    content.getChildren().clear();

    if (!tabController.getKeyMain().equals(title)) {
      AnchorPane root = tabController.getTab(title);
      btn.setStyle("-fx-background-color: #0D3BB1; -fx-background-radius: 10");
      content.getChildren().add(root);
    }
  }

  private void resize(SVGPath svg, double width, double height) {
    double originalWidth = svg.prefWidth(-1);
    double originalHeight = svg.prefHeight(originalWidth);

    double scaleX = width / originalWidth;
    double scaleY = height / originalHeight;

    svg.setScaleX(scaleX);
    svg.setScaleY(scaleY);
  }
}

@Getter
class TabController {
  private static final HashMap<String, AnchorPane> PANE_HASH_MAP = new HashMap<>();
  private static final HashMap<String, Node> BUTTONS_MAP = new HashMap<>();
  private AnchorPane main = null;

  protected void removeTab(String name) {
    PANE_HASH_MAP.remove(name);
  }

  protected void addTab(String name, AnchorPane pane, Node button) {
    PANE_HASH_MAP.put(name, pane);
    BUTTONS_MAP.put(name, button);
  }

  protected AnchorPane getTab(String name) {
    main = PANE_HASH_MAP.get(name);
    return main;
  }

  protected Node getButton(String name) {
    return BUTTONS_MAP.get(name);
  }

  protected String getKeyMain() {
    return PANE_HASH_MAP.entrySet().stream()
        .filter(entry -> Objects.equals(entry.getValue(), main))
        .map(Map.Entry::getKey)
        .findAny()
        .orElseThrow();
  }
}
