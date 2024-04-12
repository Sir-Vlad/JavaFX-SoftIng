module it.prova.javafxsofting {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.jetbrains.annotations;
    requires atlantafx.base;
    requires MaterialFX;
    requires jimObjModelImporterJFX;
    
    opens it.prova.javafxsofting to javafx.fxml;
    exports it.prova.javafxsofting;
}