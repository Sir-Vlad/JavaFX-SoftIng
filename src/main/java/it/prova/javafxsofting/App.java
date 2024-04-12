package it.prova.javafxsofting;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class App extends javafx.application.Application {
    public static void main(String[] args) {
        Arrays.stream(args).filter(arg -> arg.contains("-Dport")).forEach(
                arg -> Connection.setPorta(Integer.parseInt(arg.split("=")[1])));
        
        System.out.println("Porta: " + Connection.porta);
        launch();
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("configurator.fxml"));
        Pane root = new Pane();
        root.getChildren().addAll((Node) fxmlLoader.load());
        Scene scene = new Scene(root, 1200, 800);
        
        // setto lo stile dell'applicazione
        scene.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        
        stage.setResizable(false);
        stage.setTitle("Abbiamo scelto il configuratore di auto ciao! Porcodio!");
        stage.setScene(scene);
        // chiude tutte gli stage aperti
        stage.setOnHidden(windowEvent -> Platform.exit());
        stage.show();
    }
}