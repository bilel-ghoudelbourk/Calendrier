module com.example.calendrier {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mnode.ical4j.core;
    requires java.net.http;
    requires org.kordamp.bootstrapfx.core;
    requires org.controlsfx.controls;
    opens com.example.calendrier to javafx.fxml;
    exports com.example.calendrier;
}