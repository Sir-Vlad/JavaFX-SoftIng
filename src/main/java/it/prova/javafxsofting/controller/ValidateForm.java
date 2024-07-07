package it.prova.javafxsofting.controller;

import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;

import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

public abstract class ValidateForm {
  /**
   * Metodo per mostrare il messaggio di errore
   *
   * @param constraints la lista di Constraint
   * @param field il campo da validare
   * @param label il label dell'errore da mostrare
   */
  public void showError(@NotNull List<Constraint> constraints, MFXTextField field, Label label) {
    if (!constraints.isEmpty()) {
      field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
      field.getStyleClass().add("field-invalid");
      label.setText(constraints.getFirst().getMessage());
      label.setVisible(true);
    }
  }

  /**
   * Controlla se il campo è invalido
   *
   * @param field il campo da controllare
   * @return true se il campo è invalido altrimenti false
   */
  public boolean isFieldInvalid(@NotNull MFXTextField field) {
    return field.getPseudoClassStates().stream()
        .anyMatch(pseudoClass -> pseudoClass.equals(INVALID_PSEUDO_CLASS));
  }

  /**
   * Constraint per i campi obbligatori
   *
   * @param field il campo da validare
   * @param msg il messaggio di errore
   */
  public void addConstraintRequired(@NotNull MFXTextField field, String msg) {
    Constraint request =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage(msg)
            .setCondition(field.textProperty().isNotEmpty())
            .get();

    field.getValidator().constraint(request);
  }

  /**
   * Constraint per la lunghezza del testo del campo
   *
   * @param field il campo da validare
   * @param msg il messaggio di errore
   * @param length la massima lunghezza che il campo deve avere
   */
  public void addConstraintLength(@NotNull MFXTextField field, String msg, int length) {
    Constraint lenConstraint =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage(msg)
            .setCondition(field.textProperty().length().greaterThanOrEqualTo(length))
            .get();

    field.getValidator().constraint(lenConstraint);
  }

  /**
   * Metodo per rimuovere la classe di errore
   *
   * @param field il campo da validare
   * @param labelMsgInvalid il label dell'errore
   */
  public void removeClassInvalid(@NotNull MFXTextField field, @NotNull Label labelMsgInvalid) {
    labelMsgInvalid.setVisible(false);
    field.getStyleClass().remove("field-invalid");
    field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
  }

  /**
   * Metodo per rimuovere l'evento della classe di errore
   *
   * @param field il campo da validare
   * @param validate il label dell'errore
   */
  public void addEventRemoveClassInvalid(@NotNull MFXTextField field, Label validate) {
    field
        .getValidator()
        .validProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              if (Boolean.TRUE.equals(newValue)) {
                removeClassInvalid(field, validate);
              }
            });
  }

  /**
   * Metodo per validare solo caratteri alfabetici
   *
   * @param field il campo da validare
   */
  public void onlyCharAlphabetical(@NotNull MFXTextField field) {
    field
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              String newValueFormat = newValue;
              if (!newValue.matches("[A-Za-z]")) {
                newValueFormat = newValue.replaceAll("[^A-Za-z]", "");
              }
              field.setText(newValueFormat);
              updateField(field.textProperty(), field);
            });
  }

  /**
   * Metodo per validare solo numerici
   *
   * @param field il campo da validare
   */
  public void onlyDigit(@NotNull MFXTextField field) {
    field
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              String newValueFormat = newValue;
              if (!newValue.matches("\\d*")) {
                newValueFormat = newValue.replaceAll("\\D", "");
              }
              field.setText(newValueFormat);
              updateField(field.textProperty(), field);
            });
  }

  /**
   * Metodo per validare solo float
   *
   * @param field il campo da validare
   */
  public void onlyFloat(@NotNull MFXTextField field) {
    field
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              String newValueFormat = newValue;
              if (!newValue.matches("\\d*(\\.\\d+)?")) {
                newValueFormat = newValue.replaceAll("[^0-9.]", "");
                try {
                  newValueFormat =
                      newValueFormat.substring(
                          0, newValueFormat.indexOf('.', newValueFormat.indexOf('.') + 1));
                } catch (Exception ignored) {
                }
              }
              field.setText(newValueFormat);
              updateField(field.textProperty(), field);
            });
  }

  /**
   * Metodo per aggiornare il campo
   *
   * @param timeText la proprietà da aggiornare
   * @param field il campo da aggiornare
   */
  protected void updateField(StringProperty timeText, MFXTextField field) {
    Platform.runLater(
        () -> {
          field.setText(timeText.getValue());
          field.positionCaret(timeText.getValue().length());
        });
  }
}
