module org.sporticket.finalgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens org.sporticket.finalgui to javafx.fxml;
    exports org.sporticket.finalgui;
}