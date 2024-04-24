module it.prova.javafxsofting {
  requires javafx.controls;
  requires javafx.fxml;
  requires com.google.gson;
  requires org.jetbrains.annotations;
  requires MaterialFX;
  requires static lombok;
  requires commons.validator;
  requires AnimateFX;
  requires javafx.web;

  opens it.prova.javafxsofting to
      javafx.fxml;

  exports it.prova.javafxsofting;
}
