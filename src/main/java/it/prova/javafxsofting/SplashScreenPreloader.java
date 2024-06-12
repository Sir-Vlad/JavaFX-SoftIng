package it.prova.javafxsofting;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import java.util.Objects;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

public class SplashScreenPreloader extends Preloader {
  private Stage splashStage;
  private Scene scene;

  @Override
  public void init() throws Exception {
    Parent root =
        FXMLLoader.load(
            Objects.requireNonNull(App.class.getResource("controller/splash_screen.fxml")));
    scene = new Scene(root);

    UserAgentBuilder.builder()
        .themes(JavaFXThemes.MODENA)
        .themes(MaterialFXStylesheets.forAssemble(true))
        .setDeploy(true)
        .setResolveAssets(true)
        .build()
        .setGlobal();

    scene.setFill(Color.TRANSPARENT);
  }

  @Override
  public void start(Stage primaryStage) {
    this.splashStage = primaryStage;
    splashStage.setScene(scene);
    splashStage.initStyle(StageStyle.TRANSPARENT);

    splashStage
        .getIcons()
        .add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("immagini/icon.png"))));

    splashStage.show();
  }

  @Override
  public void handleApplicationNotification(PreloaderNotification info) {
    if (info instanceof StateChangeNotification) {
      // hide after get any state update from application
      //      splashStage.hide();
    }
    //    if (info instanceof ProgressNotification progressNotification) {
    //      System.out.println(progressNotification.getProgress());
    //      SplashScreenController.progress.setProgress(progressNotification.getProgress());
    //      SplashScreenController.provaStatic.setText("Loading: " +
    // progressNotification.getProgress());
    //    }
  }

  @Override
  public void handleStateChangeNotification(@NotNull StateChangeNotification info) {
    StateChangeNotification.Type type = info.getType();
    if (Objects.requireNonNull(type) == Type.BEFORE_START) {
      splashStage.hide();
    }
  }
}
