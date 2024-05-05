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
  requires java.logging;

  opens it.prova.javafxsofting to
      javafx.fxml;

  exports it.prova.javafxsofting;
  exports it.prova.javafxsofting.component;

  opens it.prova.javafxsofting.component to
      javafx.fxml;

  exports it.prova.javafxsofting.controller;

  opens it.prova.javafxsofting.controller to
      javafx.fxml;
}
