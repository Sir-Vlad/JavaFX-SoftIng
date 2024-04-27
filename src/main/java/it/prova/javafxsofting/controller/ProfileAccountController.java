package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.Utente;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import javafx.event.ActionEvent;
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

  public HBox image_account;
  public Label name_account;

  public HBox profiloBtn;
  public HBox ordiniBtn;
  public HBox preventiviBtn;
  public HBox signOutBtn;

  public SVGPath icon_profilo;
  public SVGPath icon_ordini;
  public SVGPath icon_preventivi;
  public SVGPath icon_signOut;

  public VBox content;
  public VBox sidebar;
  public MFXButton indietroBtn;
  private TabController tabController;

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    App.utente = new Utente("Mattia", "Frigiola", "root", "root", "", LocalDate.now(), "123");
    // imposto i dati dell'utente
    if (App.utente != null) {
      name_account.setText(App.utente.getNome() + " " + App.utente.getCognome());
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

    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setId("ordini");

    tabController.addTab("ordini", anchorPane, ordiniBtn);

    AnchorPane anchorPane2 = new AnchorPane();
    anchorPane2.setId("preventivi");

    tabController.addTab("preventivi", anchorPane2, preventiviBtn);

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
  private static AnchorPane main = null;

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
