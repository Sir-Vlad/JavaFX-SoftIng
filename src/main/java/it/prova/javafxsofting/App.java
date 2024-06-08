package it.prova.javafxsofting;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import it.prova.javafxsofting.controller.ScreenController;
import it.prova.javafxsofting.models.Utente;
import it.prova.javafxsofting.util.StaticDataStore;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.application.Preloader.StateChangeNotification;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class App extends javafx.application.Application {
  @Getter private static final Logger log = Logger.getLogger(App.class.getName());

  public static void main(String[] args) {
    Arrays.stream(args)
        .filter(arg -> arg.contains("-Dport"))
        .forEach(arg -> Connection.setPorta(Integer.parseInt(arg.split("=")[1])));

    log.log(Level.INFO, "Porta: {0}", Connection.getPorta());

    System.setProperty("javafx.preloader", SplashScreenPreloader.class.getCanonicalName());
    launch(args);
  }

  private static void checkRememberUtente() {
    File path = new File("instance/utente/utente.txt");
    if (path.exists()) {
      List<String> text = null;
      try {
        text = Files.readAllLines(Path.of(path.getPath()));
        UserSession.getInstance()
            .setUtente(Connection.getDataFromBackend("utente/" + text.getFirst(), Utente.class));
      } catch (Exception ignored) {
        log.info("Utente non trovato");
      }
    } else {
      log.info("Nessun utente registrato");
    }
  }

  private static void deleteDirectory(@NotNull File dirImage) throws IOException {
    if (dirImage.isDirectory()) {
      File[] files = dirImage.listFiles();
      if (files != null) {
        for (File file : files) {
          Files.delete(Path.of(file.getPath()));
        }
      }
    }
  }

  @Override
  public void init() {
    // todo: caricare anche tutte le auto usate, le sedi e gli optional
    StaticDataStore.fetchAllData();
    checkRememberUtente();
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

    stage.setOnCloseRequest(
        event -> {
          File dirImageNuove = new File("instance/immagini/immaginiAutoNuove");
          File dirImageUsate = new File("instance/immagini/immaginiAutoUsata");

          if (dirImageNuove.exists() || dirImageUsate.exists()) {
            try {
              deleteDirectory(dirImageNuove);
              deleteDirectory(dirImageUsate);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }

          Platform.exit();
          event.consume();
          System.exit(0);
        });

    stage.show();
    notifyPreloader(new StateChangeNotification(Type.BEFORE_START));
  }

  private void createScreenController() throws IOException {
    ScreenController.addScreen(
        "home",
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("controller/home.fxml"))));

    //    ScreenController.addScreen(
    //        "scegliModello",
    //        FXMLLoader.load(
    //            Objects.requireNonNull(App.class.getResource("controller/scegliModello.fxml"))));
    // // debug

    ScreenController.addScreen(
        "login",
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("controller/login.fxml"))));

    ScreenController.addScreen(
        "registrazione",
        FXMLLoader.load(
            Objects.requireNonNull(App.class.getResource("controller/registrazione.fxml"))));
    // // debug

    ScreenController.addScreen(
        "concessionari",
        FXMLLoader.load(
            Objects.requireNonNull(App.class.getResource("controller/concessionari.fxml"))));
    // // debug

    //    ScreenController.addScreen(
    //        "scegliUsato",
    //        FXMLLoader.load(
    //            Objects.requireNonNull(App.class.getResource("controller/scegliUsato.fxml")))); //
    // debug
  }
}
