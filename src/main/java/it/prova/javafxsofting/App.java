package it.prova.javafxsofting;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends javafx.application.Application {

  public static Utente utente = null;

  public static void main(String[] args) {
    Arrays.stream(args)
        .filter(arg -> arg.contains("-Dport"))
        .forEach(arg -> Connection.setPorta(Integer.parseInt(arg.split("=")[1])));

    System.out.println("Porta: " + Connection.porta);
    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("configurator.fxml"));
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
        .add(Objects.requireNonNull(getClass().getResource("css/style.css")).toExternalForm());

    createScreenController();
    ScreenController.setMain(scene);

    stage.setResizable(false);
    stage.setTitle("Laboratorio di Adrenalina");
    stage
        .getIcons()
        .add(
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("immagini/icon.png"))));
    stage.setScene(scene);
    // chiude tutte gli stage aperti
    stage.setOnHidden(windowEvent -> Platform.exit());
    stage.show();
  }

  private void createScreenController() throws IOException {
    ScreenController.addScreen(
        "login", FXMLLoader.load(Objects.requireNonNull(App.class.getResource("login.fxml"))));
    ScreenController.addScreen(
        "home", FXMLLoader.load(Objects.requireNonNull(App.class.getResource("home.fxml"))));
    ScreenController.addScreen(
        "chargeModel",
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("changeModel.fxml"))));
    ScreenController.addScreen(
        "registrazione",
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("registrazione.fxml"))));
    ScreenController.addScreen(
        "config",
        FXMLLoader.load(Objects.requireNonNull(App.class.getResource("configurator.fxml"))));
  }
}
