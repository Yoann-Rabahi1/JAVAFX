package org.example.projetjavafx.repository;

import org.example.projetjavafx.dao.DatabaseConnection;
import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.repository.PersonnageRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlPersonnageRepository implements PersonnageRepository {

    @Override
    public void sauvegarder(Personnage personnage, int idHistoire) {
        String sql = "INSERT INTO personnage (nom_personnage, role_personnage, description_personnage, id_histoire) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, personnage.getNom_personnage());
            pstmt.setString(2, personnage.getRole_personnage());
            pstmt.setString(3, personnage.getDescription_personnage());
            pstmt.setInt(4, idHistoire);
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) personnage.setId_personnage(gk.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Personnage> chargerParHistoire(int idHistoire) {
        List<Personnage> list = new ArrayList<>();
        String sql = "SELECT * FROM personnage WHERE id_histoire = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, idHistoire);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Personnage p = new Personnage();
                    p.setId_personnage(rs.getInt("id_personnage"));
                    p.setNom_personnage(rs.getString("nom_personnage"));
                    p.setRole_personnage(rs.getString("role_personnage"));
                    p.setDescription_personnage(rs.getString("description_personnage"));
                    list.add(p);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void mettreAJour(Personnage personnage) {
        String sql = "UPDATE personnage SET nom_personnage = ?, role_personnage = ?, description_personnage = ? WHERE id_personnage = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, personnage.getNom_personnage());
            pstmt.setString(2, personnage.getRole_personnage());
            pstmt.setString(3, personnage.getDescription_personnage());
            pstmt.setInt(4, personnage.getId_personnage());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void supprimer(int idPersonnage) {
        String sql = "DELETE FROM personnage WHERE id_personnage = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, idPersonnage);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}