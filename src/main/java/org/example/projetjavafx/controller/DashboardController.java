package org.example.projetjavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.model.Scene;
import org.example.projetjavafx.repository.MySqlHistoireRepository;
import org.example.projetjavafx.repository.MySqlSceneRepository;
import org.example.projetjavafx.service.SceneService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    private final SceneService sceneService = new SceneService(new MySqlSceneRepository());
    private final MySqlHistoireRepository histoireRepository = new MySqlHistoireRepository();

    private Histoire histoireActuelle;

    @FXML private ComboBox<Histoire> comboChoixHistoire;
    @FXML private ComboBox<String> comboFiltreStatut;
    @FXML private ComboBox<Personnage> comboFiltrePersonnage;
    @FXML private TextField txtRechercheMotCle;
    @FXML private ListView<Scene> listScenesFiltrees;

    @FXML private Label lblTotalPersonnages;
    @FXML private Label lblTotalScenes;
    @FXML private Label lblStatutPlanifiee;
    @FXML private Label lblStatutEnCours;
    @FXML private Label lblStatutRedigee;
    @FXML private Label lblStatutValidee;

    @FXML
    public void initialize() {
        comboFiltreStatut.getItems().setAll("Planifiée", "En cours", "Rédigée", "Validée");

        comboChoixHistoire.setCellFactory(lv -> new ListCell<Histoire>() {
            @Override
            protected void updateItem(Histoire item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitre());
            }
        });
        comboChoixHistoire.setButtonCell(new ListCell<Histoire>() {
            @Override
            protected void updateItem(Histoire item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitre());
            }
        });

        comboFiltrePersonnage.setCellFactory(lv -> new ListCell<Personnage>() {
            @Override
            protected void updateItem(Personnage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom_personnage());
            }
        });
        comboFiltrePersonnage.setButtonCell(new ListCell<Personnage>() {
            @Override
            protected void updateItem(Personnage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom_personnage());
            }
        });

        listScenesFiltrees.setCellFactory(lv -> new ListCell<Scene>() {
            @Override
            protected void updateItem(Scene item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getTitre() + " [" + item.getStatut() + "]");
                }
            }
        });

        comboChoixHistoire.valueProperty().addListener((obs, oldHist, newHist) -> {
            this.histoireActuelle = newHist;
            if (newHist != null) {
                if (newHist.getListePersonnages() != null) {
                    comboFiltrePersonnage.getItems().setAll(newHist.getListePersonnages());
                } else {
                    comboFiltrePersonnage.getItems().clear();
                }
                onReinitialiserFiltres();
            } else {
                viderDashboard();
            }
        });

        comboFiltreStatut.valueProperty().addListener((obs, oldV, newV) -> appliquerFiltresEtMiseAJour());
        comboFiltrePersonnage.valueProperty().addListener((obs, oldV, newV) -> appliquerFiltresEtMiseAJour());
        txtRechercheMotCle.textProperty().addListener((obs, oldV, newV) -> appliquerFiltresEtMiseAJour());
    }

    public void chargerHistoiresDepuisBdd() {
        try {
            List<Histoire> toutesLesHistoires = histoireRepository.chargerTout();

            if (toutesLesHistoires != null) {
                try (Connection conn = org.example.projetjavafx.dao.DatabaseConnection.getConnection()) {
                    for (Histoire h : toutesLesHistoires) {
                        int idHist = h.getIdHisoire();

                        List<Personnage> personnages = new ArrayList<>();
                        String sqlPerso = "SELECT * FROM personnage WHERE id_histoire = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlPerso)) {
                            pstmt.setInt(1, idHist);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                while (rs.next()) {
                                    Personnage p = new Personnage();
                                    p.setId_personnage(rs.getInt("id_personnage"));
                                    p.setNom_personnage(rs.getString("nom_personnage"));
                                    personnages.add(p);
                                }
                            }
                        }
                        h.setListePersonnages(personnages);

                        List<Scene> scenes = new ArrayList<>();
                        String sqlScene = "SELECT * FROM scene WHERE id_histoire = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlScene)) {
                            pstmt.setInt(1, idHist);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                while (rs.next()) {
                                    Scene s = new Scene();
                                    s.setIdScene(rs.getInt("id_scene"));
                                    s.setTitre(rs.getString("titre"));
                                    s.setStatut(rs.getString("statut"));
                                    scenes.add(s);
                                }
                            }
                        }
                        h.setListeScenes(scenes);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                comboChoixHistoire.getItems().setAll(toutesLesHistoires);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de récupération BDD dans le DashboardController.");
        }
    }

    private void appliquerFiltresEtMiseAJour() {
        if (histoireActuelle == null) return;

        String statut = comboFiltreStatut.getSelectionModel().getSelectedItem();
        Personnage perso = comboFiltrePersonnage.getSelectionModel().getSelectedItem();
        String motCle = txtRechercheMotCle.getText();

        // 1. Filtrage de base (Statut et Mot-clé)
        List<Scene> resultats = sceneService.filtrerEtRechercherScenes(histoireActuelle, statut, null, motCle);

        // 2. Filtrage par Personnage
        if (perso != null) {

            // --- OPTION A : SI TU AS UNE TABLE DE LIAISON DANS TA BDD ---
            // (Vérifie bien que le nom de la table 'scene_personnage' et des colonnes sont corrects)
            List<Integer> idsScenesDuPersonnage = new ArrayList<>();
            String sqlLiaison = "SELECT id_scene FROM scene_personnage WHERE id_personnage = ?";

            try (Connection conn = org.example.projetjavafx.dao.DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sqlLiaison)) {
                pstmt.setInt(1, perso.getId_personnage());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        idsScenesDuPersonnage.add(rs.getInt("id_scene"));
                    }
                }
            } catch (SQLException e) {
                // Si la table n'existe pas ou a un autre nom, l'erreur s'affichera dans la console
                System.err.println("Erreur table liaison: " + e.getMessage());
            }

            // Si on a trouvé des liaisons en BDD, on filtre dessus
            if (!idsScenesDuPersonnage.isEmpty()) {
                resultats = resultats.stream()
                        .filter(s -> idsScenesDuPersonnage.contains(s.getIdScene()))
                        .collect(Collectors.toList());
            } else {
                // OPTION B : SI CHAQUE SCÈNE A DIRECTEMENT UNE CLÉ ÉTRANGÈRE 'id_personnage' (Relation 1 à N)
                // Décommente la ligne ci-dessous si ton modèle Scene possède un getId_personnage() ou similaire :
                // resultats = resultats.stream().filter(s -> s.getId_personnage() == perso.getId_personnage()).collect(Collectors.toList());
            }
        }

        listScenesFiltrees.getItems().setAll(resultats);
        mettreAJourStatistiques();
    }

    private void mettreAJourStatistiques() {
        if (histoireActuelle == null) return;

        int totalPersos = histoireActuelle.getListePersonnages() != null ? histoireActuelle.getListePersonnages().size() : 0;
        int totalScenes = histoireActuelle.getListeScenes() != null ? histoireActuelle.getListeScenes().size() : 0;

        lblTotalPersonnages.setText("Nombre total de personnages : " + totalPersos);
        lblTotalScenes.setText("Nombre total de scènes : " + totalScenes);

        Map<String, Long> statsStatuts = sceneService.getNombreScenesParStatut(histoireActuelle);
        lblStatutPlanifiee.setText("Planifiées : " + statsStatuts.getOrDefault("Planifiée", 0L));
        lblStatutEnCours.setText("En cours : " + statsStatuts.getOrDefault("En cours", 0L));
        lblStatutRedigee.setText("Rédigées : " + statsStatuts.getOrDefault("Rédigée", 0L));
        lblStatutValidee.setText("Validées : " + statsStatuts.getOrDefault("Validée", 0L));
    }

    @FXML
    public void onReinitialiserFiltres() {
        comboFiltreStatut.getSelectionModel().clearSelection();
        comboFiltrePersonnage.getSelectionModel().clearSelection();
        txtRechercheMotCle.clear();
        appliquerFiltresEtMiseAJour();
    }

    private void viderDashboard() {
        listScenesFiltrees.getItems().clear();
        comboFiltrePersonnage.getItems().clear();
        comboFiltreStatut.getSelectionModel().clearSelection();
        txtRechercheMotCle.clear();

        lblTotalPersonnages.setText("Nombre total de personnages : 0");
        lblTotalScenes.setText("Nombre total de scènes : 0");
        lblStatutPlanifiee.setText("Planifiées : 0");
        lblStatutEnCours.setText("En cours : 0");
        lblStatutRedigee.setText("Rédigées : 0");
        lblStatutValidee.setText("Validées : 0");
    }
}