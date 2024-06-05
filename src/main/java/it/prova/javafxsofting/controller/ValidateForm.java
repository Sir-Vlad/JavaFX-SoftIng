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

public class ValidateForm {
  public void showError(@NotNull List<Constraint> constraints, MFXTextField field, Label label) {
    if (!constraints.isEmpty()) {
      field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
      field.getStyleClass().add("field-invalid");
      label.setText(constraints.getFirst().getMessage());
      label.setVisible(true);
    }
  }

  public boolean isFieldInvalid(@NotNull MFXTextField field) {
    return field.getPseudoClassStates().stream()
        .anyMatch(pseudoClass -> pseudoClass.equals(INVALID_PSEUDO_CLASS));
  }

  public void addConstraintRequired(@NotNull MFXTextField field, String msg) {
    Constraint request =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage(msg)
            .setCondition(field.textProperty().isNotEmpty())
            .get();

    field.getValidator().constraint(request);
  }

  public void addConstraintLength(@NotNull MFXTextField field, String msg, int length) {
    Constraint lenConstraint =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage(msg)
            .setCondition(field.textProperty().length().greaterThanOrEqualTo(length))
            .get();

    field.getValidator().constraint(lenConstraint);
  }

  public void removeClassInvalid(@NotNull MFXTextField field, @NotNull Label labelMsgInvalid) {
    labelMsgInvalid.setVisible(false);
    field.getStyleClass().remove("field-invalid");
    field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
  }

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

  protected void updateField(StringProperty timeText, MFXTextField field) {
    Platform.runLater(
        () -> {
          field.setText(timeText.getValue());
          field.positionCaret(timeText.getValue().length());
        });
  }
}
