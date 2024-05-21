package it.prova.javafxsofting;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import it.prova.javafxsofting.controller.ScreenController;
import it.prova.javafxsofting.models.Utente;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class App extends javafx.application.Application {

  public static Utente utente = null;
  public static Logger log = Logger.getLogger(App.class.getName());

  public static void main(String[] args) {
    Arrays.stream(args)
        .filter(arg -> arg.contains("-Dport"))
        .forEach(arg -> Connection.setPorta(Integer.parseInt(arg.split("=")[1])));

    log.info("Porta: " + Connection.porta);

    launch();
  }

  @Override
  public void start(@NotNull Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("controller/home.fxml"));
    Pane root = new Pane();
    root.getChildren().addAll((Node) fxmlLoader.load());
    Scene scene = new Scene(root, 1200, 800);

    UserAgentBuilder.builder()
        .themes(JavaFXThemes.MODENA)
        .themes(MaterialFXStylesheets.forAssemble(true))
        .setDeploy(true)
        .setResolveAssets(true)
        .build()
        .setGlobal();

    scene
        .getStylesheets()
        .add(Objects.requireNonNull(App.class.getResource("css/root.css")).toExternalForm());

    stage.setResizable(false);
    stage.setTitle("Laboratorio di Adrenalina");
    stage
        .getIcons()
        .add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("immagini/icon.png"))));
    stage.setScene(scene);

    createScreenController();
    ScreenController.setMain(scene);

    KeyCombination kc = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
    scene.getAccelerators().put(kc, stage::close);

    KeyCombination back = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
    scene.getAccelerators().put(back, ScreenController::back);

    // chiude tutte gli stage aperti
    stage.setOnHidden(windowEvent -> Platform.exit());
    stage.show();
  }

  private void createScreenController() throws IOException {
    ScreenController.addScreen(
        "home",
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("controller/home.fxml"))));

    ScreenController.addScreen(
        "scegliModello",
        FXMLLoader.load(
            Objects.requireNonNull(App.class.getResource("controller/scegliModello.fxml"))));

    ScreenController.addScreen(
        "login",
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("controller/login.fxml"))));

    ScreenController.addScreen(
        "registrazione",
        FXMLLoader.load(
            Objects.requireNonNull(App.class.getResource("controller/registrazione.fxml"))));

    ScreenController.addScreen(
        "concessionari",
        FXMLLoader.load(
            Objects.requireNonNull(App.class.getResource("controller/concessionari.fxml"))));

    ScreenController.addScreen(
        "usato",
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("controller/usato.fxml"))));
  }
}
