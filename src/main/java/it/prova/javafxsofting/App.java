package it.prova.javafxsofting;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class App extends javafx.application.Application {
    public static void main(String[] args) {
        Arrays.stream(args).filter(arg -> arg.contains("-Dport")).forEach(
                arg -> Connection.setPorta(Integer.parseInt(arg.split("=")[1])));
        
        System.out.println("Porta: " + Connection.porta);
        launch();
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("registrazione.fxml"));
        Pane root = new Pane();
        root.getChildren().addAll((Node) fxmlLoader.load());
        Scene scene = new Scene(root, 1200, 800);
        
        UserAgentBuilder.builder().themes(JavaFXThemes.MODENA).themes(
                                MaterialFXStylesheets.forAssemble(true)).setDeploy(true).setResolveAssets(true)
                        .build().setGlobal();
        
        stage.setResizable(false);
        stage.setTitle("Laboratorio di Adrenalina");
        stage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("immagini/icon.png"))));
        stage.setScene(scene);
        // chiude tutte gli stage aperti
        stage.setOnHidden(windowEvent -> Platform.exit());
        stage.show();
    }
}