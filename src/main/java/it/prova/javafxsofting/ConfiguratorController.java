package it.prova.javafxsofting;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfiguratorController implements Initializable {
    @FXML public Text       labelHome;
    @FXML public Text       labelCambiaModello;
    @FXML public Text       fieldModello;
    @FXML public Text       fieldPrezzo;
    @FXML public Text       fieldPrezzoValue;
    @FXML public MFXButton  buttonFakeAdd;
    @FXML public MFXButton  buttonFakeMinus;
    @FXML public StackPane  modelVisualize;
    @FXML public Circle     imgAccount;
    @FXML public VBox       menu;
    @FXML public SVGPath    symbolMenu;
    @FXML public VBox       account;
    @FXML public AnchorPane root;
    private      boolean    isMenuAccountOpen = false;
    private      boolean    isMenuStageOpen   = false;
    private      Stage      menuStage         = null;
    private      Stage      menuAccount       = null;
    
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
            if (!isMenuAccountOpen) {
                Point2D point = menu.localToScreen(Point2D.ZERO);
                menuAccount = new Stage();
                
                menuAccount.initOwner(root.getScene().getWindow());
                menuAccount.initStyle(StageStyle.UNDECORATED);
                
                menuAccount.setScene(new Scene(new Pane()));
                
                // posiziono la finestra rispetto al bottone
                double x = point.getX() -
                           170; // fixme: da aggiustare appena mettere inserisco il layout
                double y = point.getY() - 690;
                
                menuAccount.setX(x);
                menuAccount.setY(y);
                
                menuAccount.show();
                isMenuAccountOpen = true;
            } else {
                isMenuAccountOpen = false;
                menuAccount.close();
            }
            
        });
        
        menu.setOnMouseClicked(mouseEvent -> {
            if (!isMenuStageOpen) {
                Point2D point = menu.localToScreen(Point2D.ZERO);
                menuStage = new Stage();
                FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("menuOption.fxml"));
                try {
                    menuStage.setScene(new Scene(menuLoader.load()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                menuStage.initStyle(StageStyle.UNDECORATED);
                menuStage.initOwner(root.getScene().getWindow());
                
                // posiziono la finestra rispetto al bottone
                double x = point.getX() - 190;
                double y = point.getY() - 160;
                
                menuStage.setX(x);
                menuStage.setY(y);
                
                menuStage.show();
                
                isMenuStageOpen = true;
            } else {
                isMenuStageOpen = false;
                menuStage.close();
            }
        });
        
        labelHome.setOnMouseClicked(mouseEvent -> {
            // todo: redirect alla home
        });
        
        labelCambiaModello.setOnMouseClicked(mouseEvent -> {
            // todo: redict alla page per cambiare modello
        });
        
        // immagine per visualizzare qualcosa
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
    
    public void goHome() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        
        root.getChildren().clear();
        var content = (AnchorPane) loader.load();
        
        ((Pane) root.getParent()).getChildren().addAll(content);
    }
    
    public void goModelChange() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chargeModel.fxml"));
        
        root.getChildren().clear();
        var content = (AnchorPane) loader.load();
        
        ((Pane) root.getParent()).getChildren().addAll(content);
    }
}
