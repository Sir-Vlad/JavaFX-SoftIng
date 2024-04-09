package it.prova.javafxsofting;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class HelloApplication extends Application {
    static int porta;
    
    public static void main(String[] args) {
        Arrays.stream(args).filter(arg -> arg.contains("-Dport")).forEach(
                arg -> porta = Integer.parseInt(arg.split("=")[1]));
        
        System.out.println("Porta: " + porta);
        Connection.setPorta(porta);
        launch();
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Abbiamo scelto il configuratore di auto ciao! Porcodio!");
        stage.setScene(scene);
        stage.show();
    }
}