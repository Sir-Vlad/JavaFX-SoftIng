package it.prova.javafxsofting.component.profile_box;

import it.prova.javafxsofting.App;
import it.prova.javafxsofting.UserSession;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

public class ProfileBox extends VBox implements Initializable, State {
  @FXML Pane immagine;
  @FXML VBox root;

  /*variabile per gestire l'apertura del menu account*/
  private boolean isMenuAccountOpen = false;

  private ContextMenu contextMenuAccount;
  @Getter @Setter private State loggedState;

  /** Costruttore della classe */
  public ProfileBox() {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("component/profileBox.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    changeState();
  }

  public void setImage(String path) {
    immagine.setStyle("-fx-background-image: url(" + path + ")");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    root.setOnMouseClicked(
        mouseEvent -> {
          changeState();
          contextMenuAccount = createContextMenu();
          contextMenuAccount.setOnShown(
              event -> {
                Bounds bounds = root.localToScreen(root.getBoundsInLocal());
                double buttonBottomRightX = bounds.getMinX() + bounds.getWidth();
                double buttonBottomRightY = bounds.getMinY() + bounds.getHeight();

                // Posizionamento del contextMenu in modo che l'angolo in alto a destra si allinei
                // con l'angolo in basso a destra del pulsante
                contextMenuAccount.setX(buttonBottomRightX - contextMenuAccount.getWidth() + 8);
                contextMenuAccount.setY(buttonBottomRightY);
              });
          openContextMenu(contextMenuAccount);
          mouseEvent.consume();
        });
  }

  @Override
  public ContextMenu createContextMenu() {
    return loggedState.createContextMenu();
  }

  /**
   * Apertura o chiusura del contextMenu
   *
   * @param menu menu da visualizzare
   */
  private void openContextMenu(ContextMenu menu) {
    if (isMenuAccountOpen) {
      menu.hide();
    } else {
      menu.show(root, Side.BOTTOM, 0, 0);
    }
    isMenuAccountOpen = !isMenuAccountOpen;
  }

  /** Cambia lo stato del componente in base all'utente */
  private void changeState() {
    if (UserSession.getInstance().getUtente() == null) {
      State unlogged = new UnloggedContextMenu();
      setLoggedState(unlogged);
    } else {
      State logged = new LoggedContextMenu();
      setLoggedState(logged);
    }
  }
}
