module it.prova.javafxsofting {
  requires javafx.controls;
  requires javafx.fxml;
  requires com.google.gson;
  requires org.jetbrains.annotations;
  requires MaterialFX;
  requires static lombok;
  requires commons.validator;
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

  exports it.prova.javafxsofting.models;

  opens it.prova.javafxsofting.models to
      javafx.fxml,
      com.google.gson;

  exports it.prova.javafxsofting.errori;

  opens it.prova.javafxsofting.errori to
      javafx.fxml;

  exports it.prova.javafxsofting.serializzatori;

  opens it.prova.javafxsofting.serializzatori to
      javafx.fxml;

  exports it.prova.javafxsofting.util;

  opens it.prova.javafxsofting.util to
      javafx.fxml;

  exports it.prova.javafxsofting.controller.part_profilo_utente;

  opens it.prova.javafxsofting.controller.part_profilo_utente to
      javafx.fxml;

  exports it.prova.javafxsofting.controller.scegli_conf_auto;

  opens it.prova.javafxsofting.controller.scegli_conf_auto to
      javafx.fxml;

  exports it.prova.javafxsofting.component.profile_box;

  opens it.prova.javafxsofting.component.profile_box to
      javafx.fxml;
}
