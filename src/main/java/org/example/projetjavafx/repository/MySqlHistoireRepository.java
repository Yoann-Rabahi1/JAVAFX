package org.example.projetjavafx.repository;

import org.example.projetjavafx.dao.DatabaseConnection;
import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.repository.HistoireRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlHistoireRepository implements HistoireRepository {

    @Override
    public void sauvegarder(Histoire histoire) {
        String sql = "INSERT INTO histoire (titre, auteur, resume) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, histoire.getTitre());
            pstmt.setString(2, histoire.getAuteur());
            pstmt.setString(3, histoire.getResumer());
            pstmt.executeUpdate();
            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) histoire.setIdHisoire(gk.getInt(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Histoire> chargerTout() {
        List<Histoire> histoires = new ArrayList<>();
        String sql = "SELECT * FROM histoire";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Histoire h = new Histoire();
                h.setIdHisoire(rs.getInt("id_histoire"));
                h.setTitre(rs.getString("titre"));
                h.setAuteur(rs.getString("auteur"));
                h.setResumer(rs.getString("resume"));
                histoires.add(h);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return histoires;
    }

    @Override
    public void mettreAJour(Histoire histoire) {
        String sql = "UPDATE histoire SET titre = ?, auteur = ?, resume = ? WHERE id_histoire = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, histoire.getTitre());
            pstmt.setString(2, histoire.getAuteur());
            pstmt.setString(3, histoire.getResumer());
            pstmt.setInt(4, histoire.getIdHisoire());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void supprimer(int idHistoire) {
        String sql = "DELETE FROM histoire WHERE id_histoire = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, idHistoire);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}