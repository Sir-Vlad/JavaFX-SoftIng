package it.prova.javafxsofting.skeleton;

import static javafx.animation.Animation.INDEFINITE;

import java.io.IOException;
import java.util.stream.IntStream;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CardAutoSkeleton extends VBox {
  @FXML VBox rootCardAutoSkeleton;

  public CardAutoSkeleton() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("skeleton_card_auto.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    rootCardAutoSkeleton
        .lookupAll(".gray-fill")
        .forEach(
            x -> {
              ObjectProperty<Integer> percent = new SimpleObjectProperty<>(0);
              KeyValue[] keyValues = new KeyValue[100];
              IntStream.range(0, 100).forEach(i -> keyValues[i] = new KeyValue(percent, i));
              KeyFrame[] keyFrames = new KeyFrame[100];
              IntStream.range(0, 100)
                  .forEach(i -> keyFrames[i] = new KeyFrame(Duration.millis(10 * i), keyValues[i]));

              Timeline timeline = new Timeline(keyFrames);

              percent.addListener(
                  (observable, oldValue, newValue) ->
                      x.setStyle(
                          String.format(
                              "-fx-fill: linear-gradient(to right, #e0e0e0 0%%, #f8f8f8 %d%%, #e0e0e0 100%%)",
                              newValue)));

              timeline.setCycleCount(INDEFINITE);
              timeline.setAutoReverse(true);
              timeline.play();
            });
  }
}
