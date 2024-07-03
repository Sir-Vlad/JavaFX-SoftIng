package it.prova.javafxsofting.component;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Header extends HBox {
  @FXML HBox root;
  @FXML GridPane gridPane;

  int columnIndex = 0;

  public Header() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("header.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    ProfileBox profileBox = new ProfileBox();
    profileBox.setId("profile");
    profileBox.setMaxWidth(50);
    profileBox.setPadding(new Insets(4));

    root.getChildren().addLast(profileBox);
  }

  /**
   * Aggiunge una tab alla header
   *
   * @param name testo da visualizzare
   * @param eventHandler funzione da eseguire quando viene cliccata
   */
  public void addTab(String name, EventHandler<MouseEvent> eventHandler) {
    VBox vBox = new VBox();
    vBox.setId(name);
    vBox.setAlignment(Pos.CENTER);
    vBox.getStyleClass().add("text-header");
    vBox.setPrefWidth(120);
    vBox.setOnMouseClicked(eventHandler);

    Text text = new Text(name);

    vBox.getChildren().add(text);

    gridPane.add(vBox, columnIndex++, 0);
  }
}
