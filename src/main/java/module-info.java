module de.hsharz.virusscan {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens de.hsharz.virusscan to javafx.fxml;
    exports de.hsharz.virusscan;
}