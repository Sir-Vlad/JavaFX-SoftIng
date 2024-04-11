package it.prova.javafxsofting;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfiguratorController implements Initializable {
    
    public  Text       labelHome;
    public  Text       labelCambiaModello;
    public  Text       fieldModello;
    public  Text       fieldPrezzo;
    public  Text       fieldPrezzoValue;
    public  MFXButton  buttonFakeAdd;
    public  MFXButton  buttonFakeMinus;
    public  StackPane  modelVisualize;
    public  Circle     imgAccount;
    public  VBox       menu;
    public  SVGPath    symbolMenu;
    public  VBox       account;
    public  AnchorPane root;
    private boolean    isTabOpen = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelHome.setFill(Color.BLACK);
        labelCambiaModello.setFill(Color.BLACK);
        fieldModello.setFill(Color.BLACK);
        fieldPrezzo.setFill(Color.BLACK);
        fieldPrezzoValue.setFill(Color.BLACK);
        
        fieldPrezzoValue.setText("0 €");
        
        // init immagine di default per l'account
        imgAccount.setFill(new ImagePattern(
                new Image(String.valueOf(getClass().getResource("immagini/fake-account.png")))));
        
        account.setOnMouseClicked(mouseEvent -> {
            Stage stage = new Stage();
            
        });
        
        menu.setOnMouseClicked(mouseEvent -> {
            // todo: aprire un menu a tendina con i campi SOMMARIO, PREVENTIVO, CONCESSIONARIA
            //  VICINA A TE
            
            
            if (!isTabOpen) {
                root.setDisable(true);
                Point2D point = menu.localToScreen(Point2D.ZERO);
                Stage menuStage = new Stage();
                FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("menuOption.fxml"));
                try {
                    menuStage.setScene(new Scene(menuLoader.load()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                menuStage.setTitle("Nuova scheda");
//                menuStage.initStyle(StageStyle.UNDECORATED);
                menuStage.setResizable(false);
                menuStage.setAlwaysOnTop(true);
                
                double x = point.getX() - 190;
                double y = point.getY() - 200;
                
                menuStage.setX(x);
                menuStage.setY(y);
                
                // quando la stage viene chiuso si attiva questa funzione
                menuStage.setOnCloseRequest(windowEvent -> {
                    isTabOpen = false;
                    root.setDisable(false);
                });
                
                menuStage.show();
                
                isTabOpen = true;
            }
        });
        
        labelHome.setOnMouseClicked(mouseEvent -> {
            // todo: redirect alla home
        });
        
        modelVisualize.getChildren().add(new ImageView(
                new Image(String.valueOf(getClass().getResource("immagini/fake-account.png")))));
    }
    
    
    public void incrementaPrezzo() {
        fieldPrezzoValue.setText(
                Integer.parseInt(fieldPrezzoValue.getText().split(" ")[0]) + 10 + " €");
    }
    
    public void decrementaPrezzo() {
        fieldPrezzoValue.setText(
                Integer.parseInt(fieldPrezzoValue.getText().split(" ")[0]) - 10 + " €");
    }
}
