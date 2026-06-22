package org.example.projetjavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.example.projetjavafx.controller.HistoireController;
import org.example.projetjavafx.controller.MainController;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // JavaFX injecte automatiquement les deux sous-contrôleurs grâce aux fx:id du hello-view.fxml
    @FXML
    private HistoireController histoireViewController; // nom du fx:id + "Controller"

    @FXML
    private PersonnageController personnageViewController; // nom du fx:id + "Controller"

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (histoireViewController != null && personnageViewController != null) {
            // 1. Liaison des contrôleurs pour la synchronisation des clics
            histoireViewController.setPersonnageController(personnageViewController);

            // 2. SYNCHRONISATION DIRECTE : Le ComboBox utilise EXACTEMENT la même liste observable
            // que la ListView. Toute modification (ajout/suppression) sera instantanée !
            personnageViewController.initialiserListeHistoires(histoireViewController.getListHistoires().getItems());
        }
    }
}