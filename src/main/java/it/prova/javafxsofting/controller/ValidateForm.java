package it.prova.javafxsofting.controller;

import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;

import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import java.util.List;
import javafx.scene.control.Label;

public class ValidateForm {
  public void showError(List<Constraint> constraints, MFXTextField field, Label label) {
    if (!constraints.isEmpty()) {
      field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
      field.getStyleClass().add("field-invalid");
      label.setText(constraints.getFirst().getMessage());
      label.setVisible(true);
    }
  }

  public boolean isFieldInvalid(MFXTextField field) {
    return field.getPseudoClassStates().stream()
        .anyMatch(pseudoClass -> pseudoClass.equals(INVALID_PSEUDO_CLASS));
  }

  public void addConstraintRequired(MFXTextField field, String msg) {
    Constraint request =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage(msg)
            .setCondition(field.textProperty().isNotEmpty())
            .get();

    field.getValidator().constraint(request);
  }

  public void addConstraintLength(MFXTextField field, String msg, int length) {
    Constraint lenConstraint =
        Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setMessage(msg)
            .setCondition(field.textProperty().length().greaterThanOrEqualTo(length))
            .get();

    field.getValidator().constraint(lenConstraint);
  }

  public void removeClassInvalid(MFXTextField field, Label labelMsgInvalid) {
    labelMsgInvalid.setVisible(false);
    field.getStyleClass().remove("field-invalid");
    field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
  }
}
