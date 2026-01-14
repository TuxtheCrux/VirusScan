module de.hsharz.virusscan {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.hsharz.virusscan to javafx.fxml;
    exports de.hsharz.virusscan;
}