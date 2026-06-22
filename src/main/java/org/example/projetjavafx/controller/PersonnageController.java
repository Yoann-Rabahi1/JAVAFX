package org.example.projetjavafx.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.service.PersonnageService;

import java.net.URL;
import java.util.ResourceBundle;

public class PersonnageController implements Initializable {

    private final PersonnageService personnageService = new PersonnageService();

    @FXML
    private Label lblCastingTitre;

    @FXML
    private ListView<Personnage> ListPersonnage;

    @FXML
    private ComboBox<Histoire> comboHistoires;

    @FXML
    private TextField txtFieldNomPersonnage;

    @FXML
    private TextField txtFieldRolePersonnage;

    @FXML
    private TextField txtFieldDescriptionPersonnage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Écouteur déjà existant pour les détails
        ListPersonnage.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> displayPersonnageDetails(newSelection)
        );

        // Écouteur déjà existant pour le changement d'histoire
        comboHistoires.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldHistoire, nouvelleHistoire) -> rafraichirAffichageHistoire(nouvelleHistoire)
        );

        // ====================================================================
        // CONFIGURATION DU CELL FACTORY POUR N'AFFICHER QUE LE TITRE
        // ====================================================================
        comboHistoires.setCellFactory(lv -> new ListCell<Histoire>() {
            @Override
            protected void updateItem(Histoire item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitre());
            }
        });

        // Modifie aussi l'affichage du titre une fois sélectionné dans la boîte
        comboHistoires.setButtonCell(new ListCell<Histoire>() {
            @Override
            protected void updateItem(Histoire item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitre());
            }
        });
    }

    public void initialiserListeHistoires(ObservableList<Histoire> toutesLesHistoires) {
        comboHistoires.setItems(toutesLesHistoires);
    }

    private void rafraichirAffichageHistoire(Histoire histoire) {
        if (histoire != null) {
            lblCastingTitre.setText("Casting de : " + histoire.getTitre());
            ListPersonnage.getItems().setAll(histoire.getListePersonnages());
        } else {
            lblCastingTitre.setText("Casting (Sélectionnez une histoire)");
            ListPersonnage.getItems().clear();
        }
        viderChampsPersonnage();
    }

    @FXML
    public void onAjouterPersonnage() {
        Histoire histoireSelectionnee = comboHistoires.getSelectionModel().getSelectedItem();

        if (histoireSelectionnee == null) {
            afficherAlerteErreur("Sélection requise", "Veuillez sélectionner une histoire dans le menu déroulant.");
            return;
        }

        String nom = txtFieldNomPersonnage.getText();
        String role = txtFieldRolePersonnage.getText();
        String desc = txtFieldDescriptionPersonnage.getText();

        try {
            personnageService.creerPersonnage(nom, role, desc, histoireSelectionnee);
            ListPersonnage.getItems().setAll(histoireSelectionnee.getListePersonnages());
            viderChampsPersonnage();
        } catch (IllegalArgumentException e) {
            afficherAlerteErreur("Erreur de personnage", e.getMessage());
        }
    }

    @FXML
    public void onModifierPersonnage() {
        Histoire histoireSelectionnee = comboHistoires.getSelectionModel().getSelectedItem();
        Personnage personnageSelectionne = ListPersonnage.getSelectionModel().getSelectedItem();

        if (histoireSelectionnee == null || personnageSelectionne == null) {
            afficherAlerteErreur("Sélection requise", "Veuillez sélectionner l'histoire et le personnage à modifier.");
            return;
        }

        String ancienNom = personnageSelectionne.getNom_personnage();
        personnageSelectionne.setNom_personnage(txtFieldNomPersonnage.getText());
        personnageSelectionne.setRole_personnage(txtFieldRolePersonnage.getText());
        personnageSelectionne.setDescription_personnage(txtFieldDescriptionPersonnage.getText());

        try {
            personnageService.modifierPersonnage(personnageSelectionne, ancienNom, histoireSelectionnee);
            ListPersonnage.refresh();
        } catch (IllegalArgumentException e) {
            afficherAlerteErreur("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    public void onSupprimerPersonnage() {
        Histoire histoireSelectionnee = comboHistoires.getSelectionModel().getSelectedItem();
        Personnage personnageSelectionne = ListPersonnage.getSelectionModel().getSelectedItem();

        if (histoireSelectionnee == null || personnageSelectionne == null) {
            afficherAlerteErreur("Sélection requise", "Veuillez sélectionner l'histoire et le personnage à supprimer.");
            return;
        }

        personnageService.supprimerPersonnage(personnageSelectionne, histoireSelectionnee);
        ListPersonnage.getItems().setAll(histoireSelectionnee.getListePersonnages());
        viderChampsPersonnage();
    }

    private void displayPersonnageDetails(Personnage personnageSelectionne) {
        if (personnageSelectionne != null) {
            txtFieldNomPersonnage.setText(personnageSelectionne.getNom_personnage());
            txtFieldRolePersonnage.setText(personnageSelectionne.getRole_personnage());
            txtFieldDescriptionPersonnage.setText(personnageSelectionne.getDescription_personnage());
        }
    }

    private void viderChampsPersonnage() {
        txtFieldNomPersonnage.clear();
        txtFieldRolePersonnage.clear();
        txtFieldDescriptionPersonnage.clear();
        ListPersonnage.getSelectionModel().clearSelection();
    }

    private void afficherAlerteErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // LE GETTER CRUCIAL ICI
    public ComboBox<Histoire> getComboHistoires() {
        return this.comboHistoires;
    }
}