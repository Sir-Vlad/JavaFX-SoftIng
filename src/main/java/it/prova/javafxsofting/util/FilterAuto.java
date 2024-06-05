package it.prova.javafxsofting.util;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXSlider;
import it.prova.javafxsofting.component.CardAuto;
import it.prova.javafxsofting.models.Auto;
import it.prova.javafxsofting.models.AutoUsata;
import it.prova.javafxsofting.models.Marca;
import it.prova.javafxsofting.models.ModelloAuto;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.FlowPane;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface FilterAuto {
  default <T extends Auto> void settingMarcaFilter(
          @NotNull MFXFilterComboBox<String> marcaComboFilter, FlowPane flowPane,
          ObservableList<T> cardAuto) {
    ObservableList<String> marche =
        FXCollections.observableArrayList(
            Arrays.stream(Marca.values()).map(Enum::toString).toList());
    marche.addFirst("Tutti");
    marcaComboFilter.setItems(marche);
    marcaComboFilter.getSelectionModel().selectFirst();

    marcaComboFilter
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                if (newValue.equals("Tutti")) {
                  flowPane.getChildren().clear();
                  cardAuto.stream()
                      .map(CardAuto::new)
                      .forEach(auto -> flowPane.getChildren().add(auto));
                  return;
                }
                List<T> dataFiltered =
                    cardAuto.stream()
                        .filter(auto -> auto.getMarca().equals(Marca.valueOf(newValue)))
                        .toList();
                flowPane.getChildren().clear();
                dataFiltered.stream()
                    .map(CardAuto::new)
                    .forEach(auto -> flowPane.getChildren().add(auto));
              }
            });
  }

  default <T extends Auto> void settingPrezzoFilter(@NotNull MFXSlider slider, FlowPane flowPane,
                                                    ObservableList<T> cardAuto) {
    int[] minMaxPrezzo = minMaxPrezzoAuto(cardAuto);
    slider.setMin(minMaxPrezzo[0]);
    slider.setMax(minMaxPrezzo[1]);
    slider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null && !newValue.equals(oldValue)) {
                List<T> newAutoFiltered =
                    cardAuto.stream()
                        .filter(
                            auto -> {
                              if (auto instanceof ModelloAuto modelloAuto) {
                                return modelloAuto.getPrezzoBase() >= newValue.intValue();
                              } else if (auto instanceof AutoUsata autoUsata) {
                                return autoUsata.getPrezzo() >= newValue.intValue();
                              }
                              return false;
                            })
                        .toList();

                flowPane.getChildren().clear();
                newAutoFiltered.stream()
                    .map(CardAuto::new)
                    .forEach(auto -> flowPane.getChildren().add(auto));
              }
            });
  }

  @Contract("_ -> new")
  private <T extends Auto> int @NotNull [] minMaxPrezzoAuto(@NotNull ObservableList<T> cardAuto) {
    ToIntFunction<Object> func;
    Object auto = cardAuto.getFirst();

    if (auto instanceof ModelloAuto) {
      func = obj -> ((ModelloAuto) obj).getPrezzoBase();
    } else if (auto instanceof AutoUsata) {
      func = obj -> ((AutoUsata) obj).getPrezzo();
    } else {
      throw new IllegalArgumentException("Tipo di auto non supportato");
    }

    int max =
        cardAuto.stream().mapToInt(func).filter(modelloAuto -> modelloAuto >= 0).max().orElse(0);
    int min =
        cardAuto.stream().mapToInt(func).filter(modelloAuto -> modelloAuto >= 0).min().orElse(0);
    if (max == 0) {
      max = 1;
    }
    return new int[] {min, max};
  }
}
