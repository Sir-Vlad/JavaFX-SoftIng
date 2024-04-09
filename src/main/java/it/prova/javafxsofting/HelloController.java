package it.prova.javafxsofting;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML private Label welcomeText;
    
    @FXML
    protected void onHelloButtonClick() {
        var response = Connection.sendDataToBacked("Ciao Mondo", Connection.porta, "");
        welcomeText.setText(response.toString());
    }
}