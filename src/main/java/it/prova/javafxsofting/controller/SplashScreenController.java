package it.prova.javafxsofting.controller;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class SplashScreenController implements Initializable {
  public static MFXProgressSpinner progress;

  @FXML private MFXProgressSpinner progressSpinner;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    progress = progressSpinner;
  }
}
