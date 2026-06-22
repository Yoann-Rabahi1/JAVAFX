package org.example.projetjavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.service.HistoireService;

import java.net.URL;
import java.util.ResourceBundle;

public class HistoireController implements Initializable {

    private final HistoireService histoireService = new HistoireService();

    // Référence vers le contrôleur des personnages pour la synchronisation
    private PersonnageController personnageController;

    @FXML
    private ListView<Histoire> ListHistoires;

    @FXML
    private TextField txtFieldTitre;

    @FXML
    private TextField txtFieldAuteur;

    @FXML
    private TextArea txtAreaResumer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ÉCOUTEUR : Quand on sélectionne une histoire à gauche, on met à jour les champs
        // et on synchronise automatiquement le ComboBox de droite
        ListHistoires.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayHistoireDetails(newSelection);

                // Synchronisation avec le ComboBox de PersonnageController
                if (personnageController != null && personnageController.getComboHistoires() != null) {
                    personnageController.getComboHistoires().getSelectionModel().select(newSelection);
                }
            }
        });
    }

    // Setter indispensable appelé par le MainController pour lier les deux vues
    public void setPersonnageController(PersonnageController personnageController) {
        this.personnageController = personnageController;
    }

    // Getter indispensable pour que le MainController puisse lier la liste au ComboBox
    public ListView<Histoire> getListHistoires() {
        return this.ListHistoires;
    }

    // ==========================================
    // MÉTHODES D'ACTION (CRUD)
    // ==========================================

    @FXML
    public void onAjouterHistoire() {
        String titre = txtFieldTitre.getText();
        String auteur = txtFieldAuteur.getText();
        String resume = txtAreaResumer.getText();

        try {
            Histoire nouvelleHistoire = histoireService.creerHistoire(titre, auteur, resume);

            // Ajout à la liste partagée
            ListHistoires.getItems().add(nouvelleHistoire);

            // Force la sélection dans le ComboBox pour mise à jour visuelle immédiate
            if (personnageController != null && personnageController.getComboHistoires() != null) {
                personnageController.getComboHistoires().getSelectionModel().select(nouvelleHistoire);
            }

            viderChampsHistoire();
        } catch (IllegalArgumentException e) {
            afficherAlerteErreur("Erreur de création", e.getMessage());
        }
    }

    @FXML
    public void onModifierHistoire() {
        Histoire histoireSelectionnee = ListHistoires.getSelectionModel().getSelectedItem();

        if (histoireSelectionnee == null) {
            afficherAlerteErreur("Sélection requise", "Veuillez sélectionner une histoire à modifier.");
            return;
        }

        histoireSelectionnee.setTitre(txtFieldTitre.getText());
        histoireSelectionnee.setAuteur(txtFieldAuteur.getText());
        histoireSelectionnee.setResumer(txtAreaResumer.getText());

        try {
            histoireService.modifierHistoire(histoireSelectionnee);

            // Rafraîchit la liste de gauche
            ListHistoires.refresh();

            // Rafraîchit et force la mise à jour visuelle du titre dans le ComboBox de droite
            if (personnageController != null && personnageController.getComboHistoires() != null) {
                int index = personnageController.getComboHistoires().getItems().indexOf(histoireSelectionnee);
                if (index >= 0) {
                    personnageController.getComboHistoires().getItems().set(index, histoireSelectionnee);
                    personnageController.getComboHistoires().getSelectionModel().select(histoireSelectionnee);
                }
            }
        } catch (IllegalArgumentException e) {
            afficherAlerteErreur("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    public void onSupprimerHistoire() {
        Histoire histoireSelectionnee = ListHistoires.getSelectionModel().getSelectedItem();

        if (histoireSelectionnee == null) {
            afficherAlerteErreur("Sélection requise", "Veuillez sélectionner une histoire à supprimer.");
            return;
        }

        histoireService.supprimerHistoire(histoireSelectionnee);
        ListHistoires.getItems().remove(histoireSelectionnee);

        // Désélectionne l'histoire du ComboBox à droite puisque l'objet n'existe plus
        if (personnageController != null && personnageController.getComboHistoires() != null) {
            personnageController.getComboHistoires().getSelectionModel().clearSelection();
        }

        viderChampsHistoire();
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    private void displayHistoireDetails(Histoire histoireSelectionnee) {
        if (histoireSelectionnee != null) {
            txtFieldAuteur.setText(histoireSelectionnee.getAuteur());
            txtFieldTitre.setText(histoireSelectionnee.getTitre());
            txtAreaResumer.setText(histoireSelectionnee.getResumer());
        }
    }

    private void viderChampsHistoire() {
        txtFieldTitre.clear();
        txtFieldAuteur.clear();
        txtAreaResumer.clear();
        ListHistoires.getSelectionModel().clearSelection();
    }

    private void afficherAlerteErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}