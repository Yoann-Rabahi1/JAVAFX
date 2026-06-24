package org.example.projetjavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.dao.MySqlPersonnageDAO;
import org.example.projetjavafx.service.PersonnageService;

import java.net.URL;
import java.util.ResourceBundle;

public class PersonnageController implements Initializable {

    // Injection de dépendance via le constructeur pour respecter SOLID (DIP)
    private final PersonnageService personnageService = new PersonnageService(new MySqlPersonnageDAO());

    private HistoireController histoireController;
    private String ancienNomPersonnage;

    @FXML private ComboBox<Histoire> ComboHistoires;
    @FXML private ListView<Personnage> ListPersonnages;
    @FXML private TextField txtFieldNomPerso;
    @FXML private TextField txtFieldRolePerso;
    @FXML private TextArea txtAreaDescPerso;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ÉCOUTEUR : Quand on clique sur un personnage dans la liste
        ListPersonnages.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtFieldNomPerso.setText(newSelection.getNom_personnage());
                txtFieldRolePerso.setText(newSelection.getRole_personnage());
                txtAreaDescPerso.setText(newSelection.getDescription_personnage());
                // On sauvegarde le nom d'origine pour pouvoir gérer la détection des doublons lors d'un UPDATE
                this.ancienNomPersonnage = newSelection.getNom_personnage();
            }
        });

        // ÉCOUTEUR : Si on change l'histoire sélectionnée dans le ComboBox
        ComboHistoires.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            chargerPersonnagesPourHistoire(newSelection);
        });
    }

    // Liens techniques requis pour le MainController et l'HistoireController
    public void setHistoireController(HistoireController hc) { this.histoireController = hc; }
    public ComboBox<Histoire> getComboHistoires() { return this.ComboHistoires; }
    public ListView<Personnage> getListPersonnages() { return this.ListPersonnages; }

    /**
     * Synchronise la ListView des personnages en fonction de l'histoire passée en paramètre.
     */
    public void chargerPersonnagesPourHistoire(Histoire histoire) {
        if (histoire != null) {
            this.ListPersonnages.getItems().setAll(histoire.getListePersonnages());
        } else {
            this.ListPersonnages.getItems().clear();
        }
    }

    /**
     * Rafraîchit la liste des personnages (casting) pour l'histoire sélectionnée.
     */
    public void rafraichirPersonnages(Histoire histoire) {
        if (histoire != null) {
            // Si ton histoire contient déjà sa liste chargée en mémoire :
            if (histoire.getListePersonnages() != null) {
                ListPersonnages.getItems().setAll(histoire.getListePersonnages());
            } else {
                // Optionnel : si tu as un service pour les charger depuis la BDD par ID
                // ListPersonnages.getItems().setAll(personnageService.getPersonnagesByHistoire(histoire.getIdHisoire()));
                ListPersonnages.getItems().clear();
            }

            // Optionnel : Met à jour le ComboBox du casting pour refléter l'histoire active
            ComboHistoires.getSelectionModel().select(histoire);
        }
        viderChamps();
    }

    /**
     * Vide complètement la liste des personnages et les champs du formulaire.
     */
    public void viderInterface() {
        ListPersonnages.getItems().clear();
        viderChamps();
    }

    /**
     * Méthode utilitaire pour nettoyer les champs de saisie du formulaire personnage.
     */
    private void viderChamps() {
        if (txtFieldNomPerso != null) txtFieldNomPerso.clear();
        if (txtFieldRolePerso != null) txtFieldRolePerso.clear();
        if (txtAreaDescPerso != null) txtAreaDescPerso.clear();
    }

    // ==========================================
    // ACTIONS APPLICATIVES (CRUD)
    // ==========================================

    @FXML
    public void onAjouterPersonnage() {
        Histoire histoireSelectionnee = ComboHistoires.getSelectionModel().getSelectedItem();
        if (histoireSelectionnee == null) {
            afficherAlerteErreur("Sélection requise", "Veuillez associer le personnage à une histoire.");
            return;
        }

        try {
            // Le service valide les règles métier et appelle le DAO MySQL
            Personnage p = personnageService.creerPersonnage(
                    txtFieldNomPerso.getText(),
                    txtFieldRolePerso.getText(),
                    txtAreaDescPerso.getText(),
                    histoireSelectionnee
            );
            // Si aucune exception n'est levée, on l'ajoute à la liste de l'IHM
            ListPersonnages.getItems().add(p);
            viderChampsPersonnage();
        } catch (IllegalArgumentException e) {
            afficherAlerteErreur("Erreur de création", e.getMessage());
        }
    }

    @FXML
    public void onModifierPersonnage() {
        Personnage personnageSelectionne = ListPersonnages.getSelectionModel().getSelectedItem();
        Histoire histoireSelectionnee = ComboHistoires.getSelectionModel().getSelectedItem();

        if (personnageSelectionne == null || histoireSelectionnee == null) {
            afficherAlerteErreur("Sélection requise", "Veuillez sélectionner un personnage et son histoire.");
            return;
        }

        // 1. On capture le texte saisi sans toucher au personnage sélectionné
        String nouveauNom = txtFieldNomPerso.getText();
        String nouveauRole = txtFieldRolePerso.getText();
        String nouvelleDesc = txtAreaDescPerso.getText();

        try {
            // 2. OBJET TEMPORAIRE : On crée un clone de test pour effectuer les validations
            Personnage testPerso = new Personnage();
            testPerso.setId_personnage(personnageSelectionne.getId_personnage());
            testPerso.setNom_personnage(nouveauNom);
            testPerso.setRole_personnage(nouveauRole);
            testPerso.setDescription_personnage(nouvelleDesc);

            // On teste la mise à jour. Si doublon, ça saute directement au bloc catch !
            personnageService.modifierPersonnage(testPerso, ancienNomPersonnage, histoireSelectionnee);

            // 3. APPLICATION : Si on arrive ici, la BDD est modifiée avec succès.
            // On applique donc les modifications sur le vrai personnage de la liste
            personnageSelectionne.setNom_personnage(nouveauNom);
            personnageSelectionne.setRole_personnage(nouveauRole);
            personnageSelectionne.setDescription_personnage(nouvelleDesc);

            // Mise à jour de la référence pour le prochain clic/modification
            this.ancienNomPersonnage = nouveauNom;

            // Rafraîchissement de la vue JavaFX
            ListPersonnages.refresh();
            viderChampsPersonnage();
        } catch (IllegalArgumentException e) {
            // En cas d'erreur, le vrai "personnageSelectionne" n'a subi aucun changement métier
            afficherAlerteErreur("Erreur de modification", e.getMessage());
        }
    }

    @FXML
    public void onSupprimerPersonnage() {
        Personnage personnageSelectionne = ListPersonnages.getSelectionModel().getSelectedItem();
        Histoire histoireSelectionnee = ComboHistoires.getSelectionModel().getSelectedItem();

        if (personnageSelectionne == null || histoireSelectionnee == null) {
            afficherAlerteErreur("Sélection requise", "Veuillez sélectionner un personnage à supprimer.");
            return;
        }

        personnageService.supprimerPersonnage(personnageSelectionne, histoireSelectionnee);
        ListPersonnages.getItems().remove(personnageSelectionne);
        viderChampsPersonnage();
    }

    // ==========================================
    // OUTILS UTILITAIRES
    // ==========================================

    private void viderChampsPersonnage() {
        txtFieldNomPerso.clear();
        txtFieldRolePerso.clear();
        txtAreaDescPerso.clear();
        ListPersonnages.getSelectionModel().clearSelection();
    }

    private void afficherAlerteErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}