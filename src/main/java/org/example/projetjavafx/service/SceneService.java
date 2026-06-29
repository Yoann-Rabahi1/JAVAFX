package org.example.projetjavafx.service;

import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.model.Scene;
import org.example.projetjavafx.repository.MySqlSceneRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SceneService {

    private final MySqlSceneRepository sceneRepository;

    // Injection du repository MySQL par le constructeur
    public SceneService(MySqlSceneRepository sceneRepository) {
        this.sceneRepository = sceneRepository;
    }

    /**
     * STATISTIQUES : Compte le nombre de scènes par statut à l'aide d'un Stream Collector (groupingBy)
     */
    public Map<String, Long> getNombreScenesParStatut(Histoire histoire) {
        if (histoire == null || histoire.getListeScenes() == null) {
            return Map.of();
        }

        return histoire.getListeScenes().stream()
                .filter(scene -> scene.getStatut() != null)
                .collect(Collectors.groupingBy(Scene::getStatut, Collectors.counting()));
    }

    /**
     * RECHERCHE & FILTRAGE : Combine le filtrage par statut, par personnage présent et la recherche par mot-clé
     */
    public List<Scene> filtrerEtRechercherScenes(Histoire histoire, String statutFiltre, Personnage personnageFiltre, String motCle) {
        if (histoire == null || histoire.getListeScenes() == null) {
            return List.of();
        }

        return histoire.getListeScenes().stream()
                .filter(scene -> {
                    // 1. Filtrage par statut (si un statut est sélectionné)
                    if (statutFiltre != null && !statutFiltre.isEmpty()) {
                        return scene.getStatut() != null && scene.getStatut().equalsIgnoreCase(statutFiltre);
                    }
                    return true;
                })
                .filter(scene -> {
                    // 2. Filtrage par personnage présent (si un personnage est sélectionné)
                    if (personnageFiltre != null) {
                        if (scene.getPersonnagesPresents() == null) {
                            return false;
                        }
                        return scene.getPersonnagesPresents().stream()
                                .anyMatch(p -> p.getId_personnage() == personnageFiltre.getId_personnage());
                    }
                    return true;
                })
                .filter(scene -> {
                    // 3. Recherche par mot-clé sur le titre OU le contenu (si saisi)
                    if (motCle != null && !motCle.trim().isEmpty()) {
                        String lowerMot = motCle.toLowerCase().trim();
                        boolean matchesTitre = scene.getTitre() != null && scene.getTitre().toLowerCase().contains(lowerMot);
                        boolean matchesContenu = scene.getContenu() != null && scene.getContenu().toLowerCase().contains(lowerMot);
                        return matchesTitre || matchesContenu;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Crée et persiste une nouvelle scène après validation des contraintes métier.
     */
    public Scene creerScene(String titre, String lieu, String moment, String contenu, int position, String statut, Histoire histoire) {
        // 1. Validation des contraintes de base
        if (histoire == null) {
            throw new IllegalArgumentException("Une scène doit obligatoirement appartenir à une histoire.");
        }
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de la scène ne peut pas être vide.");
        }
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu de la scène ne peut pas être vide.");
        }

        // 2. Validation du statut par rapport aux modalités autorisées
        if (statut == null || !Arrays.asList(Scene.STATUTS_POSSIBLES).contains(statut)) {
            throw new IllegalArgumentException("Le statut fourni est invalide.");
        }

        // 3. Vérification de la contrainte d'unicité de la position dans l'histoire
        if (histoire.getListeScenes() != null) {
            for (Scene s : histoire.getListeScenes()) {
                if (s.getPosition() == position) {
                    throw new IllegalArgumentException("La position " + position + " est déjà occupée dans cette histoire.");
                }
            }
        }

        // Instanciation et hydratation de l'objet métier via les setters (pas de constructeur surchargé)
        Scene scene = new Scene();
        scene.setTitre(titre.trim());
        scene.setLieu(lieu);
        scene.setMoment(moment);
        scene.setContenu(contenu.trim());
        scene.setPosition(position);
        scene.setStatut(statut);

        // Persistance via le repository et mise à jour de la liste en mémoire (One-To-Many)
        sceneRepository.sauvegarder(scene, histoire.getIdHisoire());
        if (histoire.getListeScenes() != null) {
            histoire.getListeScenes().add(scene);
        }

        return scene;
    }

    /**
     * Modifie les données d'une scène existante et gère le changement de position.
     */
    public void modifierScene(Scene scene, int anciennePosition, Histoire histoire) {
        if (scene.getTitre() == null || scene.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        }
        if (scene.getContenu() == null || scene.getContenu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu ne peut pas être vide.");
        }

        // Si l'utilisateur change le numéro d'ordre (position), on vérifie que le nouveau n'est pas pris
        if (scene.getPosition() != anciennePosition && histoire.getListeScenes() != null) {
            for (Scene s : histoire.getListeScenes()) {
                if (s.getPosition() == scene.getPosition() && s.getIdScene() != scene.getIdScene()) {
                    throw new IllegalArgumentException("La position " + scene.getPosition() + " est déjà occupée par une autre scène.");
                }
            }
        }

        sceneRepository.mettreAJour(scene);
    }

    /**
     * Supprime une scène de la base de données et de l'histoire associée.
     */
    public void supprimerScene(Scene scene, Histoire histoire) {
        if (scene != null) {
            sceneRepository.supprimer(scene.getIdScene());
            if (histoire != null && histoire.getListeScenes() != null) {
                histoire.getListeScenes().remove(scene);
            }
        }
    }

    /**
     * Associe un personnage existant à la scène (Relation Many-To-Many) après vérification d'éligibilité.
     */
    public void associerPersonnageAScene(Scene scene, Personnage personnage, Histoire histoire) {
        if (scene == null || personnage == null || histoire == null) return;

        // RÈGLE IMPORTANTE : Le personnage doit obligatoirement faire partie des personnages de l'histoire
        boolean appartientALHistoire = false;
        if (histoire.getListePersonnages() != null) {
            for (Personnage p : histoire.getListePersonnages()) {
                if (p.getId_personnage() == personnage.getId_personnage()) {
                    appartientALHistoire = true;
                    break;
                }
            }
        }

        if (!appartientALHistoire) {
            throw new IllegalArgumentException("Erreur : Ce personnage ne fait pas partie des personnages déclarés dans cette histoire.");
        }

        // Si le personnage n'est pas déjà ajouté à la scène, on le lie en BDD et en mémoire
        boolean dejaPresent = false;
        if (scene.getPersonnagesPresents() != null) {
            for (Personnage p : scene.getPersonnagesPresents()) {
                if (p.getId_personnage() == personnage.getId_personnage()) {
                    dejaPresent = true;
                    break;
                }
            }
        }

        if (!dejaPresent) {
            sceneRepository.ajouterPersonnageAScene(scene.getIdScene(), personnage.getId_personnage());
            if (scene.getPersonnagesPresents() != null) {
                scene.getPersonnagesPresents().add(personnage);
            }
        }
    }

    /**
     * Retire un personnage de la scène (suppression de l'association Many-To-Many).
     */
    public void retirerPersonnageDeScene(Scene scene, Personnage personnage) {
        if (scene == null || personnage == null || scene.getPersonnagesPresents() == null) return;

        Personnage cible = null;
        for (Personnage p : scene.getPersonnagesPresents()) {
            if (p.getId_personnage() == personnage.getId_personnage()) {
                cible = p;
                break;
            }
        }

        if (cible != null) {
            sceneRepository.retirerPersonnageDeScene(scene.getIdScene(), personnage.getId_personnage());
            scene.getPersonnagesPresents().remove(cible);
        }
    }

    /**
     * Récupère la liste brute des scènes d'une histoire depuis la base de données.
     */
    public List<Scene> getScenesByHistoire(int idHistoire) {
        List<Scene> liste = new ArrayList<>();
        String query = "SELECT * FROM scene WHERE id_histoire = ?";

        try (Connection conn = org.example.projetjavafx.dao.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idHistoire);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Scene scene = new Scene();

                    scene.setIdScene(rs.getInt("id_scene"));
                    scene.setTitre(rs.getString("titre"));
                    scene.setLieu(rs.getString("lieu"));
                    scene.setMoment(rs.getString("moment"));
                    scene.setContenu(rs.getString("contenu"));
                    scene.setPosition(rs.getInt("position"));
                    scene.setStatut(rs.getString("statut"));

                    liste.add(scene);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
}