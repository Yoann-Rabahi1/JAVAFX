package org.example.projetjavafx.integration;

import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.model.Scene;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration : vérifient que la persistance fonctionne correctement.
 * Utilise une base H2 en mémoire (aucun MySQL requis, aucune donnée de prod touchée).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersistanceTest {

    private Connection conn;

    @BeforeAll
    static void initBase() throws SQLException {
        DatabaseConnectionTest.initSchema();
    }

    @BeforeEach
    void obtenirConnexion() throws SQLException {
        conn = DatabaseConnectionTest.getConnection();
        DatabaseConnectionTest.clearAll();
    }

    // -----------------------------------------------------------------------
    //  Helpers SQL locaux
    // -----------------------------------------------------------------------

    private int insererHistoire(String titre, String auteur, String resume) throws SQLException {
        String sql = "INSERT INTO histoire (titre, auteur, resume) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, titre);
            pstmt.setString(2, auteur);
            pstmt.setString(3, resume);
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) return gk.getInt(1);
            }
        }
        throw new SQLException("Impossible de récupérer l'id de l'histoire insérée.");
    }

    private int insererPersonnage(String nom, String role, String desc, int idHistoire) throws SQLException {
        String sql = "INSERT INTO personnage (nom_personnage, role_personnage, description_personnage, id_histoire) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, role);
            pstmt.setString(3, desc);
            pstmt.setInt(4, idHistoire);
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) return gk.getInt(1);
            }
        }
        throw new SQLException("Impossible de récupérer l'id du personnage inséré.");
    }

    private int insererScene(String titre, String contenu, int position, String statut, int idHistoire) throws SQLException {
        String sql = "INSERT INTO scene (titre, lieu, moment, contenu, position, statut, id_histoire) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, titre);
            pstmt.setString(2, "Lieu de test");
            pstmt.setString(3, "Nuit");
            pstmt.setString(4, contenu);
            pstmt.setInt(5, position);
            pstmt.setString(6, statut);
            pstmt.setInt(7, idHistoire);
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) return gk.getInt(1);
            }
        }
        throw new SQLException("Impossible de récupérer l'id de la scène insérée.");
    }

    // -----------------------------------------------------------------------
    //  Tests d'intégration : Personnage
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Sauvegarder un personnage puis le retrouver en base")
    void integration_SauvegarderPersonnage_PuisLeRetouver() throws SQLException {
        int idHistoire = insererHistoire("Le Hobbit", "Tolkien", "Un voyage inattendu");
        int idPersonnage = insererPersonnage("Bilbo Baggins", "Protagoniste", "Un hobbit de la Comté", idHistoire);

        String sql = "SELECT * FROM personnage WHERE id_personnage = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPersonnage);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Le personnage sauvegardé doit exister en base");

                Personnage retrouve = new Personnage();
                retrouve.setId_personnage(rs.getInt("id_personnage"));
                retrouve.setNom_personnage(rs.getString("nom_personnage"));
                retrouve.setRole_personnage(rs.getString("role_personnage"));
                retrouve.setDescription_personnage(rs.getString("description_personnage"));

                assertEquals("Bilbo Baggins",     retrouve.getNom_personnage());
                assertEquals("Protagoniste",       retrouve.getRole_personnage());
                assertEquals("Un hobbit de la Comté", retrouve.getDescription_personnage());
            }
        }
    }

    @Test
    @DisplayName("Sauvegarder une scène puis la recharger avec ses données complètes")
    void integration_SauvegarderScene_PuisLaRecharger() throws SQLException {
        int idHistoire = insererHistoire("Le Silmarillion", "Tolkien", "Les âges anciens");
        int idScene    = insererScene("La chute de Númenor", "Un grand tremblement secoua les mers...", 1, "Brouillon", idHistoire);

        String sql = "SELECT * FROM scene WHERE id_scene = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idScene);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "La scène sauvegardée doit exister en base");

                Scene retrouvee = new Scene();
                retrouvee.setIdScene(rs.getInt("id_scene"));
                retrouvee.setTitre(rs.getString("titre"));
                retrouvee.setContenu(rs.getString("contenu"));
                retrouvee.setPosition(rs.getInt("position"));
                retrouvee.setStatut(rs.getString("statut"));

                assertEquals("La chute de Númenor",                    retrouvee.getTitre());
                assertEquals("Un grand tremblement secoua les mers...", retrouvee.getContenu());
                assertEquals(1,           retrouvee.getPosition());
                assertEquals("Brouillon", retrouvee.getStatut());
            }
        }
    }

    @Test
    @DisplayName("Vérifier qu'une suppression de personnage est bien prise en compte en base")
    void integration_SupprimerPersonnage_PuisVerifierAbsence() throws SQLException {
        int idHistoire   = insererHistoire("Les Deux Tours", "Tolkien", "La quête continue");
        int idPersonnage = insererPersonnage("Gollum", "Antagoniste", "Ancien hobbit corrompu", idHistoire);

        String sqlSelect = "SELECT COUNT(*) FROM personnage WHERE id_personnage = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setInt(1, idPersonnage);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                assertEquals(1, rs.getInt(1), "Le personnage doit exister avant la suppression");
            }
        }

        String sqlDelete = "DELETE FROM personnage WHERE id_personnage = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setInt(1, idPersonnage);
            int lignesAffectees = pstmt.executeUpdate();
            assertEquals(1, lignesAffectees, "Une ligne doit être supprimée");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setInt(1, idPersonnage);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                assertEquals(0, rs.getInt(1), "Le personnage supprimé ne doit plus exister en base");
            }
        }
    }

    @Test
    @DisplayName("Vérifier qu'une suppression de scène est bien prise en compte en base")
    void integration_SupprimerScene_PuisVerifierAbsence() throws SQLException {
        int idHistoire = insererHistoire("Le Retour du Roi", "Tolkien", "La fin de la guerre");
        int idScene    = insererScene("La destruction de l'Anneau", "Frodo jeta l'Anneau dans le feu.", 1, "Publiée", idHistoire);

        String sqlDelete = "DELETE FROM scene WHERE id_scene = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setInt(1, idScene);
            pstmt.executeUpdate();
        }

        String sqlSelect = "SELECT COUNT(*) FROM scene WHERE id_scene = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setInt(1, idScene);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                assertEquals(0, rs.getInt(1), "La scène supprimée ne doit plus exister en base");
            }
        }
    }

    @Test
    @DisplayName("La suppression en cascade d'une histoire supprime ses personnages et scènes")
    void integration_SupprimerHistoire_CascadeSurPersonnagesEtScenes() throws SQLException {
        int idHistoire   = insererHistoire("Les Aventures Test", "Auteur", "Résumé");
        int idPersonnage = insererPersonnage("Héros", "Protagoniste", "Desc", idHistoire);
        int idScene      = insererScene("Scène initiale", "Le héros entre en scène.", 1, "Brouillon", idHistoire);

        String sqlDelete = "DELETE FROM histoire WHERE id_histoire = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setInt(1, idHistoire);
            pstmt.executeUpdate();
        }

        try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM personnage WHERE id_personnage = ?")) {
            pstmt.setInt(1, idPersonnage);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                assertEquals(0, rs.getInt(1), "Les personnages doivent être supprimés en cascade");
            }
        }

        try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM scene WHERE id_scene = ?")) {
            pstmt.setInt(1, idScene);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                assertEquals(0, rs.getInt(1), "Les scènes doivent être supprimées en cascade");
            }
        }
    }
}