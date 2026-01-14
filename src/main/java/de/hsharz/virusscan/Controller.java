package de.hsharz.virusscan;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.beans.PropertyChangeEvent;
import java.io.File;


public class Controller {

    @FXML
    private Label statusLabel;
    @FXML
    private TextField TEXT_ARTIFACT;
    @FXML
    private TextField TEXT_HASH;

    private SimpleStringProperty artifactPathProperty;
    private SimpleStringProperty md5Hash;

    private Database db;



    @FXML
    private void initialize(){
        artifactPathProperty = new SimpleStringProperty("");
        md5Hash = new SimpleStringProperty("");
        TEXT_ARTIFACT.textProperty().bindBidirectional(artifactPathProperty);
        TEXT_ARTIFACT.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty()){
                md5Hash.set("");
                return;
            }
            File f = new File(newValue);
            if(!f.exists()){
                System.err.println("File not exists!");
                md5Hash.set("");
                return;
            }
            md5Hash.set(Utils.getMD5Hash(f));
        });
        TEXT_HASH.textProperty().bindBidirectional(md5Hash);
        System.out.println(new File("/home/linusr/Schreibtisch/IdeaProjects/Fortgeschrittene_Programmierung/VirusScan/dbFile/db.scan").exists());

        File dbScanFile = new File("dbFile/db.scan");
        File hashDir = new File("HashWerte");

        if(dbScanFile.exists()) {
            System.out.println("Loading from db.scan file...");
            db = new Database(dbScanFile);
        } else {
            System.out.println("Loading from CSV files in HashWerte directory...");
            db = new Database(hashDir);
        }db.pCS.addPropertyChangeListener(this::onDbLoaded);
    }

    @FXML
    private void onFileChooserClicked(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("."));
        File f = fc.showOpenDialog(null);
        if(f == null) return;
        TEXT_ARTIFACT.setText(f.getPath());

    }

    @FXML
    private void onAddClicked(ActionEvent event){
        File f = new File(artifactPathProperty.get());
        if (db.add(md5Hash.get(), f.getName())) {
            System.out.println("Added Successfully");
        } else {
            System.out.println("Entity not added");
        }
    }

    @FXML
    private void onRemoveClicked(ActionEvent event){
        if (db.remove(md5Hash.get())) {
            System.out.println("Removed Successfully");
        } else {
            System.out.println("Entity not removed");
        }
    }

    @FXML
    private void onScanClicked(ActionEvent event){
        new Thread(() -> {
            boolean value = db.contains(md5Hash.get());
            Platform.runLater(()-> {
                if(value) {
                    statusLabel.setText("Virus detected!");
                } else {
                    statusLabel.setText("No virus detected!");
                }
            });

        }).start();

    }
    private void onDbLoaded(PropertyChangeEvent propertyChangeEvent){
        Platform.runLater(() -> statusLabel.setText("Database loaded!"));
    }
}
