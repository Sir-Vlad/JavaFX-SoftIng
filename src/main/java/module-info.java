module it.prova.javafxsofting {
    requires javafx.controls;
    requires javafx.fxml;
    
    
    opens it.prova.javafxsofting to javafx.fxml;
    exports it.prova.javafxsofting;
}