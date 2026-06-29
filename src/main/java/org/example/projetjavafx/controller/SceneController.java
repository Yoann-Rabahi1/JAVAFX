package org.example.projetjavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.model.Scene;
import org.example.projetjavafx.repository.MySqlSceneRepository;
import org.example.projetjavafx.service.SceneService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SceneController {

    // Initialisation des dépendances métier
    private final SceneService sceneService = new SceneService(new MySqlSceneRepository());
    private HistoireController histoireController;

    // Éléments de l'interface graphique (FXID)
    @FXML private ListView<Scene> ListScenes;
    @FXML private ListView<Personnage> ListPersonnagesPresents;
    @FXML private ComboBox<Personnage> ComboAjoutPersonnage;

    @FXML private TextField txtFieldTitre;
    @FXML private TextField txtFieldLieu;
    @FXML private TextField txtFieldMoment;
    @FXML private TextField txtFieldPosition;
    @FXML private ComboBox<String> ComboStatut;
    @FXML private TextArea txtAreaContenu;

    @FXML
    public void initialize() {
        // Configuration initiale des choix de statuts obligatoires
        ComboStatut.getItems().setAll("Planifiée", "En cours", "Rédigée", "Validée");

        // --- CONFIGURATION DU RENDU VISUEL (Évite les adresses mémoire @1a2b3c) ---
        ListScenes.setCellFactory(lv -> new ListCell<Scene>() {
            @Override
            protected void updateItem(Scene item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitre() + " [" + item.getStatut() + "]");
            }
        });

        ListPersonnagesPresents.setCellFactory(lv -> new ListCell<Personnage>() {
            @Override
            protected void updateItem(Personnage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom_personnage());
            }
        });

        ComboAjoutPersonnage.setCellFactory(lv -> new ListCell<Personnage>() {
            @Override
            protected void updateItem(Personnage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom_personnage());
            }
        });
        ComboAjoutPersonnage.setButtonCell(new ListCell<Personnage>() {
            @Override
            protected void updateItem(Personnage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom_personnage());
            }
        });

        // ÉCOUTEUR : Met à jour les champs textuels et le casting dès qu'une scène est sélectionnée
        ListScenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSec, newSec) -> {
            if (newSec != null) {
                txtFieldTitre.setText(newSec.getTitre());
                txtFieldLieu.setText(newSec.getLieu());
                txtFieldMoment.setText(newSec.getMoment());
                txtFieldPosition.setText(String.valueOf(newSec.getPosition()));
                ComboStatut.getSelectionModel().select(newSec.getStatut());
                txtAreaContenu.setText(newSec.getContenu());

                // Affichage sécurisé des personnages associés à cette scène précise
                if (newSec.getPersonnagesPresents() != null) {
                    ListPersonnagesPresents.getItems().setAll(newSec.getPersonnagesPresents());
                } else {
                    ListPersonnagesPresents.getItems().clear();
                }
            } else {
                viderChamps();
            }
        });
    }

    /**
     * Injection du contrôleur des histoires pour connaître l'élément sélectionné globalement.
     */
    public void setHistoireController(HistoireController hc) {
        this.histoireController = hc;
    }

    /**
     * Rechargement complet de la vue basé sur l'histoire active sélectionnée à gauche.
     */
    public void rafraichirScenes(Histoire histoire) {
        if (histoire != null) {
            // Récupération des données depuis la BDD
            List<Scene> list = sceneService.getScenesByHistoire(histoire.getIdHisoire());

            // FIX : On va chercher en BDD les personnages associés à CHAQUE scène chargée
            if (list != null) {
                try (Connection conn = org.example.projetjavafx.dao.DatabaseConnection.getConnection()) {
                    String sql = "SELECT p.* FROM personnage p " +
                            "JOIN scene_personnage sp ON p.id_personnage = sp.id_personnage " +
                            "WHERE sp.id_scene = ?";

                    for (Scene s : list) {
                        List<Personnage> presents = new ArrayList<>();
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setInt(1, s.getIdScene());
                            try (ResultSet rs = pstmt.executeQuery()) {
                                while (rs.next()) {
                                    Personnage p = new Personnage();
                                    p.setId_personnage(rs.getInt("id_personnage"));
                                    p.setNom_personnage(rs.getString("nom_personnage"));
                                    presents.add(p);
                                }
                            }
                        }
                        s.setPersonnagesPresents(presents);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            ListScenes.getItems().setAll(list);

            // Met à jour la liste des personnages éligibles à l'association
            if (histoire.getListePersonnages() != null) {
                ComboAjoutPersonnage.getItems().setAll(histoire.getListePersonnages());
            } else {
                ComboAjoutPersonnage.getItems().clear();
            }
        } else {
            viderInterface();
        }
        viderChamps();
    }

    /**
     * Action : Bouton Ajouter
     */
    @FXML
    public void onAjouterScene() {
        Histoire histoireActive = getHistoireActiveSecurisee();
        if (histoireActive == null) return;

        try {
            int pos = Integer.parseInt(txtFieldPosition.getText().trim());

            Scene nouvelleScene = sceneService.creerScene(
                    txtFieldTitre.getText(),
                    txtFieldLieu.getText(),
                    txtFieldMoment.getText(),
                    txtAreaContenu.getText(),
                    pos,
                    ComboStatut.getSelectionModel().getSelectedItem(),
                    histoireActive
            );

            if (nouvelleScene.getPersonnagesPresents() == null) {
                nouvelleScene.setPersonnagesPresents(new ArrayList<>());
            }

            ListScenes.getItems().add(nouvelleScene);
            ListScenes.getSelectionModel().select(nouvelleScene);
            viderChamps();

        } catch (NumberFormatException e) {
            afficherAlert("Erreur de saisie", "La position doit obligatoirement être un entier numérique.");
        } catch (Exception e) {
            afficherAlert("Erreur d'insertion", e.getMessage());
        }
    }

    /**
     * Action : Bouton Modifier
     */
    @FXML
    public void onModifierScene() {
        Scene sceneSelectionnee = ListScenes.getSelectionModel().getSelectedItem();
        Histoire histoireActive = getHistoireActiveSecurisee();

        if (sceneSelectionnee == null || histoireActive == null) {
            afficherAlert("Sélection requise", "Veuillez sélectionner la scène à modifier dans la liste.");
            return;
        }

        try {
            int anciennePosition = sceneSelectionnee.getPosition();

            sceneSelectionnee.setTitre(txtFieldTitre.getText());
            sceneSelectionnee.setLieu(txtFieldLieu.getText());
            sceneSelectionnee.setMoment(txtFieldMoment.getText());
            sceneSelectionnee.setContenu(txtAreaContenu.getText());
            sceneSelectionnee.setPosition(Integer.parseInt(txtFieldPosition.getText().trim()));
            sceneSelectionnee.setStatut(ComboStatut.getSelectionModel().getSelectedItem());

            sceneService.modifierScene(sceneSelectionnee, anciennePosition, histoireActive);

            ListScenes.refresh();
            afficherAlert("Succès", "La scène a bien été mise à jour.");

        } catch (NumberFormatException e) {
            afficherAlert("Erreur de saisie", "La position doit être un nombre entier.");
        } catch (Exception e) {
            afficherAlert("Erreur de modification", e.getMessage());
        }
    }

    /**
     * Action : Bouton Supprimer
     */
    @FXML
    public void onSupprimerScene() {
        Scene sceneSelectionnee = ListScenes.getSelectionModel().getSelectedItem();
        Histoire histoireActive = getHistoireActiveSecurisee();

        if (sceneSelectionnee == null) {
            afficherAlert("Sélection requise", "Veuillez sélectionner la scène à supprimer.");
            return;
        }

        sceneService.supprimerScene(sceneSelectionnee, histoireActive);
        ListScenes.getItems().remove(sceneSelectionnee);
        viderChamps();
    }

    /**
     * Action : Bouton Associer Personnage
     */
    @FXML
    public void onAssocierPersonnage() {
        Scene sceneSel = ListScenes.getSelectionModel().getSelectedItem();
        Personnage persoSel = ComboAjoutPersonnage.getSelectionModel().getSelectedItem();
        Histoire histoireActive = getHistoireActiveSecurisee();

        if (sceneSel == null || persoSel == null) {
            afficherAlert("Sélection manquante", "Sélectionnez une scène ET un personnage du menu déroulant.");
            return;
        }

        try {
            sceneService.associerPersonnageAScene(sceneSel, persoSel, histoireActive);
            ListPersonnagesPresents.getItems().setAll(sceneSel.getPersonnagesPresents());
        } catch (Exception e) {
            afficherAlert("Erreur d'association", e.getMessage());
        }
    }

    /**
     * Action : Bouton Retirer Personnage
     */
    @FXML
    public void onRetirerPersonnage() {
        Scene sceneSel = ListScenes.getSelectionModel().getSelectedItem();
        Personnage persoSel = ListPersonnagesPresents.getSelectionModel().getSelectedItem();

        if (sceneSel == null || persoSel == null) {
            afficherAlert("Sélection manquante", "Sélectionnez une scène et le personnage présent à exclure.");
            return;
        }

        sceneService.retirerPersonnageDeScene(sceneSel, persoSel);
        ListPersonnagesPresents.getItems().remove(persoSel);
    }

    /**
     * Remet à zéro l'ensemble des conteneurs de listes de l'onglet.
     */
    public void viderInterface() {
        ListScenes.getItems().clear();
        ComboAjoutPersonnage.getItems().clear();
        ListPersonnagesPresents.getItems().clear();
        viderChamps();
    }

    /**
     * Efface les inputs du formulaire d'édition.
     */
    private void viderChamps() {
        txtFieldTitre.clear();
        txtFieldLieu.clear();
        txtFieldMoment.clear();
        txtFieldPosition.clear();
        txtAreaContenu.clear();
        ComboStatut.getSelectionModel().clearSelection();
        ListPersonnagesPresents.getItems().clear();
    }

    /**
     * Centralisation de la récupération de l'histoire active avec alerte d'erreur intégrée.
     */
    private Histoire getHistoireActiveSecurisee() {
        if (histoireController == null) return null;
        Histoire active = histoireController.getListHistoires().getSelectionModel().getSelectedItem();
        if (active == null) {
            afficherAlert("Histoire manquante", "Veuillez d'abord sélectionner une histoire dans le panneau de gauche.");
        }
        return active;
    }

    private void afficherAlert(String titre, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}