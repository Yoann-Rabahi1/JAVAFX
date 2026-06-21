package org.example.projetjavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML
    private StackPane conteneurCentral;

    @FXML
    public void initialize() {
        afficherVueHistoire();
    }

    @FXML
    private void afficherVueHistoire() {
        chargerSousVue("/org/example/projetjavafx/histoire-view.fxml");
    }

    @FXML
    private void afficherVuePersonnage() {
        chargerSousVue("/org/example/projetjavafx/personnage-view.fxml");
    }

    private void chargerSousVue(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent sousVue = loader.load();

            conteneurCentral.getChildren().clear();
            conteneurCentral.getChildren().add(sousVue);

        } catch (IOException e) {
            System.out.println("Erreur de chargement de la vue : " + e.getMessage());
            e.printStackTrace();
        }
    }
}