package org.example.projetjavafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private HistoireController histoireViewController;
    @FXML private PersonnageController personnageViewController;
    @FXML private SceneController sceneViewController;

    @FXML private StackPane mainContentArea;
    @FXML private SplitPane paneEdition;

    private Parent vueDashboard = null;
    private DashboardController dashboardController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (histoireViewController != null && personnageViewController != null && sceneViewController != null) {
            personnageViewController.setHistoireController(histoireViewController);
            sceneViewController.setHistoireController(histoireViewController);

            histoireViewController.getListHistoires().getSelectionModel().selectedItemProperty().addListener((obs, oldHist, newHist) -> {
                if (newHist != null) {
                    personnageViewController.rafraichirPersonnages(newHist);
                    sceneViewController.rafraichirScenes(newHist);
                } else {
                    personnageViewController.viderInterface();
                    sceneViewController.viderInterface();
                }
            });
        }
    }

    @FXML
    public void afficherPageEdition() {
        if (vueDashboard != null) {
            vueDashboard.setVisible(false);
        }
        paneEdition.setVisible(true);
    }

    @FXML
    public void ouvrirDashboard() {
        try {
            paneEdition.setVisible(false);

            if (vueDashboard == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavafx/dashboard-view.fxml"));
                vueDashboard = fxmlLoader.load();
                dashboardController = fxmlLoader.getController();
                mainContentArea.getChildren().add(vueDashboard);
            }

            if (dashboardController != null) {
                dashboardController.chargerHistoiresDepuisBdd();
            }

            vueDashboard.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible de charger le tableau de bord.");
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void onQuitter() {
        Platform.exit();
    }
}