package org.example.projetjavafx.dao;

import org.example.projetjavafx.dao.DatabaseConnection;
import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.model.Scene;
import org.example.projetjavafx.dao.SceneDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlSceneDAO implements SceneDAO {

    @Override
    public void sauvegarder(Scene scene, int idHistoire) {
        String sql = "INSERT INTO scene (titre, lieu, moment, contenu, position, statut, id_histoire) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, scene.getTitre());
            pstmt.setString(2, scene.getLieu());
            pstmt.setString(3, scene.getMoment());
            pstmt.setString(4, scene.getContenu());
            pstmt.setInt(5, scene.getPosition());
            pstmt.setString(6, scene.getStatut()); // Stockage direct en String
            pstmt.setInt(7, idHistoire);
            pstmt.executeUpdate();

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) scene.setIdScene(gk.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Scene> chargerParHistoire(int idHistoire) {
        List<Scene> scenes = new ArrayList<>();
        String sql = "SELECT * FROM scene WHERE id_histoire = ? ORDER BY position";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, idHistoire);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Scene s = new Scene();
                    s.setIdScene(rs.getInt("id_scene"));
                    s.setTitre(rs.getString("titre"));
                    s.setLieu(rs.getString("lieu"));
                    s.setMoment(rs.getString("moment"));
                    s.setContenu(rs.getString("contenu"));
                    s.setPosition(rs.getInt("position"));
                    s.setStatut(rs.getString("statut"));

                    // Chargement en cascade des personnages de la scène (Many-To-Many)
                    s.setPersonnagesPresents(chargerPersonnagesDeScene(s.getIdScene()));
                    scenes.add(s);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return scenes;
    }

    private List<Personnage> chargerPersonnagesDeScene(int idScene) {
        List<Personnage> personnages = new ArrayList<>();
        String sql = "SELECT p.* FROM personnage p " +
                "JOIN scene_personnage sp ON p.id_personnage = sp.id_personnage " +
                "WHERE sp.id_scene = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, idScene);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Personnage p = new Personnage();
                    p.setId_personnage(rs.getInt("id_personnage"));
                    p.setNom_personnage(rs.getString("nom_personnage"));
                    p.setRole_personnage(rs.getString("role_personnage"));
                    p.setDescription_personnage(rs.getString("description_personnage"));
                    personnages.add(p);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return personnages;
    }

    @Override
    public void mettreAJour(Scene scene) {
        String sql = "UPDATE scene SET titre = ?, lieu = ?, moment = ?, contenu = ?, position = ?, statut = ? WHERE id_scene = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, scene.getTitre());
            pstmt.setString(2, scene.getLieu());
            pstmt.setString(3, scene.getMoment());
            pstmt.setString(4, scene.getContenu());
            pstmt.setInt(5, scene.getPosition());
            pstmt.setString(6, scene.getStatut());
            pstmt.setInt(7, scene.getIdScene());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void supprimer(int idScene) {
        String sql = "DELETE FROM scene WHERE id_scene = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, idScene);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void ajouterPersonnageAScene(int idScene, int idPersonnage) {
        String sql = "INSERT IGNORE INTO scene_personnage (id_scene, id_personnage) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, idScene);
            pstmt.setInt(2, idPersonnage);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void retirerPersonnageDeScene(int idScene, int idPersonnage) {
        String sql = "DELETE FROM scene_personnage WHERE id_scene = ? AND id_personnage = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, idScene);
            pstmt.setInt(2, idPersonnage);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}