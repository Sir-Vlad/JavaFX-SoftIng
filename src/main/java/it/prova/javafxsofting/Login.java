package it.prova.javafxsofting;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Login implements Initializable {
    @FXML public AnchorPane       root;
    public       VBox             wrapperLogin;
    public       MFXTextField     username;
    public       MFXPasswordField password;
    public       MFXCheckbox      rememberMe;
    public       MFXButton        logInBtn;
    public       Text             register;
    public       Text             textRegister;
    public       Label            passwordField;
    public       Label            usernameField;
    public       Label            forgotPasswordLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usernameField.setTextFill(Color.BLACK);
        passwordField.setTextFill(Color.BLACK);
        textRegister.setFill(Color.BLACK);
        rememberMe.setTextFill(Color.BLACK);
        forgotPasswordLabel.setTextFill(Color.BLACK);
        
        BackgroundImage bg = new BackgroundImage(
                new Image(String.valueOf(getClass().getResource("immagini/car.jpeg"))),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        
        root.setBackground(new Background(bg));
        
        ImageView imageView = new ImageView(
                Objects.requireNonNull(getClass().getResource("immagini/car.jpeg")).toString());
        imageView.setEffect(new GaussianBlur(40));
        
        
        wrapperLogin.setBackground(new Background(
                new BackgroundFill(Color.rgb(183, 180, 172, 0.85), new CornerRadii(25),
                        Insets.EMPTY)));
        wrapperLogin.setEffect(
                new DropShadow(BlurType.GAUSSIAN, Color.rgb(198, 204, 197, 0.8506), 18, 0.3, 0, 0));
        wrapperLogin.setStyle(
                "-fx-border-color: #6F6F6F80; -fx-border-width: 1px 1px 1px; -fx-border-radius: " +
                "25; ");
    }
    
    
    public void logIn(ActionEvent actionEvent) {
        NotImplemented.notImplemented();
        actionEvent.consume();
    }
    
    public void forgotPassword(MouseEvent mouseEvent) {
        NotImplemented.notImplemented();
        mouseEvent.consume();
    }
    
    public void switchRegister(MouseEvent mouseEvent) {
        NotImplemented.notImplemented();
        mouseEvent.consume();
    }
}
