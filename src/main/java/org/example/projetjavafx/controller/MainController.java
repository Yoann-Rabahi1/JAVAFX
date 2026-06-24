package org.example.projetjavafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.example.projetjavafx.model.Histoire;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private HistoireController histoireViewController;
    @FXML private PersonnageController personnageViewController;
    @FXML private SceneController sceneViewController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (histoireViewController != null && personnageViewController != null && sceneViewController != null) {

            // On donne une référence de HistoireController aux autres pour qu'ils puissent lire l'histoire active
            personnageViewController.setHistoireController(histoireViewController);
            sceneViewController.setHistoireController(histoireViewController);

            // ÉCOUTEUR : Quand on clique sur une histoire dans la liste de gauche
            histoireViewController.getListHistoires().getSelectionModel().selectedItemProperty().addListener((obs, oldHist, newHist) -> {
                if (newHist != null) {
                    // 1. Refresh du casting (Persos) en haut
                    personnageViewController.rafraichirPersonnages(newHist);

                    // 2. Refresh des scènes en bas
                    sceneViewController.rafraichirScenes(newHist);
                } else {
                    personnageViewController.viderInterface();
                    sceneViewController.viderInterface();
                }
            });
        }
    }

    @FXML
    public void onQuitter() {
        Platform.exit();
    }
}