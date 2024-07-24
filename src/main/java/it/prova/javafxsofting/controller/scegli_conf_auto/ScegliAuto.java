package it.prova.javafxsofting.controller.scegli_conf_auto;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXSlider;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.component.Header;
import it.prova.javafxsofting.controller.ScreenController;
import it.prova.javafxsofting.models.Auto;
import it.prova.javafxsofting.skeleton.CardAutoSkeleton;
import it.prova.javafxsofting.util.FilterAuto;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Pagination;
import javafx.scene.layout.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public abstract class ScegliAuto<T extends Auto> implements Initializable, FilterAuto {
  private final ObservableList<T> cardAuto = FXCollections.observableArrayList();
  @FXML protected MFXFilterComboBox<String> marcaComboFilter;
  @FXML protected MFXSlider sliderMaxPrezzo;
  @FXML private Header header;
  @FXML private VBox cardContent;
  private MFXScrollPane scrollPane;
  private Pagination pagination;

  public void setPagination(@NotNull ObservableList<T> cardAuto) {
    int pageCount = (int) Math.ceil((double) cardAuto.size() / 12);
    pagination = new Pagination(pageCount == 0 ? 1 : pageCount, 0);
    pagination.setPageFactory(indexPage -> createPage(indexPage, cardAuto));
    pagination.setPadding(new Insets(10, 20, 10, 20));

    VBox.setVgrow(pagination, Priority.ALWAYS);
    cardContent.getChildren().add(pagination);
  }

  /** Metodo che setta automaticamente il card */
  public abstract void setCardAuto();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    header.addTab("Home", event -> ScreenController.activate("home"));

    setCardAuto();
    setPagination(getCardAuto());

    settingMarcaFilter(marcaComboFilter);
    settingPrezzoFilter(sliderMaxPrezzo, cardAuto);
  }

  private @NotNull FlowPane createPage(Integer indexPage, ObservableList<T> cardAuto) {
    FlowPane flowPane = new FlowPane(20, 20);
    boolean isScegliModello =
        Arrays.asList(this.getClass().getTypeName().split("\\."))
            .contains("ScegliModelloController");
    if (isScegliModello && cardAuto.isEmpty()) {
      IntStream.range(0, 12)
          .mapToObj(i -> new CardAutoSkeleton())
          .forEach(card -> flowPane.getChildren().add(card));
    } else {
      if (indexPage == 0) {
        cardAuto.stream()
            .map(CardAuto::new)
            .limit(12)
            .forEach(card -> flowPane.getChildren().add(card));
        return flowPane;
      }

      cardAuto.stream()
          .skip((long) indexPage * 12)
          .map(CardAuto::new)
          .forEach(card -> flowPane.getChildren().addAll(card));
    }
    return flowPane;
  }
}
