package it.prova.javafxsofting.controller.part_profilo_utente;

import io.github.palexdev.materialfx.controls.MFXButton;
import it.prova.javafxsofting.App;
import it.prova.javafxsofting.UserSession;
import it.prova.javafxsofting.controller.ScreenController;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
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
import org.jetbrains.annotations.NotNull;

public class ProfileAccountController implements Initializable {
  private static final String LITERAL_ORDINI = "ordini";
  private static final String LITERAL_PROFILE = "dati_utente";
  private static final Path PATH_DIR = Path.of("controller").resolve("part_profilo_utente");
  @FXML private HBox imageAccount;
  @FXML private Label nameAccount;
  @FXML private HBox profiloBtn;
  @FXML private HBox ordiniBtn;
  @FXML private HBox preventiviBtn;
  @FXML private HBox preventiviUsatoBtn;
  @FXML private HBox signOutBtn;
  @FXML private SVGPath iconProfilo;
  @FXML private SVGPath iconOrdini;
  @FXML private SVGPath iconPreventivi;
  @FXML private SVGPath iconPreventiviUsato;
  @FXML private SVGPath iconSignOut;
  @FXML private VBox content;
  @FXML private VBox sidebar;
  @FXML private MFXButton indietroBtn;
  @Getter private TabController tabController;

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // imposto i dati dell'utente
    if (UserSession.getInstance().getUtente() != null) {
      nameAccount.setText(
          UserSession.getInstance().getUtente().getNome()
              + " "
              + UserSession.getInstance().getUtente().getCognome());
    } else {
      ScreenController.activate("login");
      return;
    }
    nameAccount
        .textProperty()
        .bindBidirectional(UserSession.getInstance().getUtente().nomeCompletoProperty());

    // ridimensionamento delle icone della sidebar
    resize(iconProfilo, 20);
    resize(iconOrdini, 20);
    resize(iconPreventivi, 25);
    resize(iconPreventiviUsato, 25);
    resize(iconSignOut, 20);

    // create le tab della sidebar
    createTabSidebar();

    // set default page open
    content.getChildren().add(tabController.getTab(LITERAL_PROFILE));
    profiloBtn.setStyle("-fx-background-color: #0D3BB1; -fx-background-radius: 10");
  }

  @FXML
  public void switchProfilo(@NotNull MouseEvent mouseEvent) {
    switchTab(profiloBtn, LITERAL_PROFILE);
    mouseEvent.consume();
  }

  @FXML
  public void switchOrdini(@NotNull MouseEvent mouseEvent) {
    switchTab(ordiniBtn, LITERAL_ORDINI);
    mouseEvent.consume();
  }

  @FXML
  public void switchPreventivi(@NotNull MouseEvent mouseEvent) {
    switchTab(preventiviBtn, "preventivi");
    mouseEvent.consume();
  }

  @FXML
  public void switchPreventiviUsato(@NotNull MouseEvent mouseEvent) {
    switchTab(preventiviUsatoBtn, "preventiviUsato");
    mouseEvent.consume();
  }

  @FXML
  public void signOut(@NotNull MouseEvent mouseEvent) {
    UserSession.clearSession();
    ScreenController.removeScreen("profilo");
    ScreenController.activate("home");
    // todo: far uscire una notifica
    mouseEvent.consume();
  }

  @FXML
  public void switchIndietro(ActionEvent actionEvent) {
    ScreenController.back();
    actionEvent.consume();
  }

  private void createTabSidebar() throws IOException {
    tabController = new TabController();
    tabController.addTab(
        LITERAL_PROFILE,
        new FXMLLoader(
            App.class.getResource(PATH_DIR.resolve("impostazioni_profilo.fxml").toString())),
        profiloBtn);

    tabController.addTab(
        "preventivi",
        new FXMLLoader(
            App.class.getResource(PATH_DIR.resolve("preventivi_utente.fxml").toString())),
        preventiviBtn);

    tabController.addTab(
        LITERAL_ORDINI,
        new FXMLLoader(App.class.getResource(PATH_DIR.resolve("ordini_utente.fxml").toString())),
        ordiniBtn);

    tabController.addTab(
        "preventiviUsato",
        new FXMLLoader(
            App.class.getResource(PATH_DIR.resolve("preventivi_usato_utente.fxml").toString())),
        preventiviUsatoBtn);
  }

  private void switchTab(HBox btn, String title) {
    if (!tabController.getKeyMain().equals(title)) {
      tabController
          .getButton(tabController.getKeyMain())
          .setStyle("-fx-background: #ffffff; -fx-background-radius: 10");
      content.getChildren().clear();

      AnchorPane root = tabController.getTab(title);
      btn.setStyle("-fx-background-color: #0D3BB1; -fx-background-radius: 10");
      content.getChildren().add(root);
    }
  }

  /**
   * Ridimensiona un SVGPath in base all'altezza dell'elemento
   *
   * @param svg elemento da ridimensionare
   * @param height altezza dell'elemento da ridimensionare
   */
  private void resize(@NotNull SVGPath svg, double height) {
    double originalWidth = svg.prefWidth(-1);
    double originalHeight = svg.prefHeight(originalWidth);

    double width = 20;
    double scaleX = width / originalWidth;
    double scaleY = height / originalHeight;

    svg.setScaleX(scaleX);
    svg.setScaleY(scaleY);
  }

  @Getter
  static class TabController {
    protected static final HashMap<String, FXMLLoader> CONTROLLER = new HashMap<>();
    private static final HashMap<String, AnchorPane> PANE_HASH_MAP = new HashMap<>();
    private static final HashMap<String, Node> BUTTONS_MAP = new HashMap<>();
    private AnchorPane main = null;

    protected String getKeyMain() {
      return PANE_HASH_MAP.entrySet().stream()
          .filter(entry -> Objects.equals(entry.getValue(), main))
          .map(Map.Entry::getKey)
          .findAny()
          .orElseThrow();
    }

    protected void removeTab(String name) {
      PANE_HASH_MAP.remove(name);
    }

    protected void addTab(String name, FXMLLoader controller, Node button) throws IOException {
      CONTROLLER.put(name, controller);
      PANE_HASH_MAP.put(name, controller.load());
      BUTTONS_MAP.put(name, button);
    }

    protected AnchorPane getTab(String name) {
      main = PANE_HASH_MAP.get(name);
      return main;
    }

    protected Node getButton(String name) {
      return BUTTONS_MAP.get(name);
    }
  }
}
