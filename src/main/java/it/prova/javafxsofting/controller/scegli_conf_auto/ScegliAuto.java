package it.prova.javafxsofting.controller.scegli_conf_auto;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXSlider;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.controller.ScreenController;
import it.prova.javafxsofting.models.Auto;
import it.prova.javafxsofting.util.FilterAuto;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import lombok.Data;

@Data
public abstract class ScegliAuto<T extends Auto> implements Initializable, FilterAuto {
  private final ObservableList<T> cardAuto = FXCollections.observableArrayList();
  @FXML private FlowPane flowPane;
  @FXML private Header header;
  @FXML private MFXFilterComboBox<String> marcaComboFilter;
  @FXML private MFXSlider sliderMaxPrezzo;

  /** Metodo che setta automaticamente il card */
  public abstract void setCardAuto();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Home", event -> ScreenController.activate("home"));

    setCardAuto();
    cardAuto.stream().map(CardAuto::new).forEach(auto -> flowPane.getChildren().addAll(auto));

    settingMarcaFilter(marcaComboFilter, flowPane, cardAuto);
    settingPrezzoFilter(sliderMaxPrezzo, flowPane, cardAuto);
  }
}
