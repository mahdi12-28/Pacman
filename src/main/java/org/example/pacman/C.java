package org.example.pacman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class C {
    Parent root;
    Stage stage;
    Scene scene;
    @FXML
    void switchToMainMenu(ActionEvent event) throws IOException {
        root =  FXMLLoader.load(getClass().getResource("pacman.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
