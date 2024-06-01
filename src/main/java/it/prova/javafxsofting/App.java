package it.prova.javafxsofting;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import it.prova.javafxsofting.controller.ScreenController;
import it.prova.javafxsofting.models.Utente;
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
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class App extends javafx.application.Application {
  @Getter @Setter private static Utente utente = null;
  @Getter @Setter private static Logger log = Logger.getLogger(App.class.getName());

  public static void main(String[] args) {
    Arrays.stream(args)
        .filter(arg -> arg.contains("-Dport"))
        .forEach(arg -> Connection.setPorta(Integer.parseInt(arg.split("=")[1])));

    log.log(Level.INFO, "Porta: {0}", Connection.getPorta());

    System.setProperty("javafx.preloader", SplashScreenPreloader.class.getCanonicalName());
    launch(args);
  }

  private static void checkRememberUtente() throws Exception {
    File path = new File("src/main/resources/it/prova/javafxsofting/data/utente.txt");
    if (path.exists()) {
      List<String> text = Files.readAllLines(Path.of(path.getPath()));
      App.setUtente(Connection.getDataFromBackend("utente/" + text.getFirst(), Utente.class));
    }
  }

  private static void deleteDirectory(File dirImage) throws IOException {
    if (dirImage.isDirectory()) {
      File[] files = dirImage.listFiles();
      if (files != null) {
        for (File file : files) {
          deleteDirectory(file);
        }
      }
    }
    Files.delete(dirImage.toPath());
  }

  @Override
  public void init() throws Exception {
    //    ScegliModelloController.fetchData(); // debug
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
          File dirImage =
              new File("src/main/resources/it/prova/javafxsofting/immagini/immaginiAutoNuove");

          if (dirImage.exists()) {
            try {
              deleteDirectory(dirImage);
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

    //    ScreenController.addScreen(
    //        "registrazione",
    //        FXMLLoader.load(
    //            Objects.requireNonNull(App.class.getResource("controller/registrazione.fxml"))));
    // // debug

    //    ScreenController.addScreen(
    //        "concessionari",
    //        FXMLLoader.load(
    //            Objects.requireNonNull(App.class.getResource("controller/concessionari.fxml"))));
    // // debug

    //    ScreenController.addScreen(
    //        "usato",
    //
    // FXMLLoader.load(Objects.requireNonNull(App.class.getResource("controller/usato.fxml")))); //
    // debug
  }
}
