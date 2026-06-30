package org.example.projetjavafx.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Connexion vers une base H2 en mémoire utilisée exclusivement pour les tests d'intégration.
 * Aucune donnée de production n'est modifiée.
 */
public class DatabaseConnectionTest {

    private static final String URL      = "jdbc:h2:mem:story_forge_test;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String USER     = "sa";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    private DatabaseConnectionTest() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /** Crée le schéma complet (identique à creation_table.sql) dans la base de test. */
    public static void initSchema() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS histoire (
                    id_histoire   INT AUTO_INCREMENT PRIMARY KEY,
                    titre         VARCHAR(150) NOT NULL,
                    auteur        VARCHAR(100) NOT NULL,
                    resume        TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS personnage (
                    id_personnage           INT AUTO_INCREMENT PRIMARY KEY,
                    nom_personnage          VARCHAR(100) NOT NULL,
                    role_personnage         VARCHAR(100),
                    description_personnage  TEXT,
                    id_histoire             INT NOT NULL,
                    FOREIGN KEY (id_histoire) REFERENCES histoire(id_histoire) ON DELETE CASCADE
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS scene (
                    id_scene    INT AUTO_INCREMENT PRIMARY KEY,
                    titre       VARCHAR(255) NOT NULL,
                    lieu        VARCHAR(255),
                    moment      VARCHAR(255),
                    contenu     TEXT NOT NULL,
                    position    INT NOT NULL,
                    statut      VARCHAR(50) NOT NULL,
                    id_histoire INT NOT NULL,
                    FOREIGN KEY (id_histoire) REFERENCES histoire(id_histoire) ON DELETE CASCADE
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS scene_personnage (
                    id_scene      INT NOT NULL,
                    id_personnage INT NOT NULL,
                    PRIMARY KEY (id_scene, id_personnage),
                    FOREIGN KEY (id_scene)      REFERENCES scene(id_scene)           ON DELETE CASCADE,
                    FOREIGN KEY (id_personnage) REFERENCES personnage(id_personnage)  ON DELETE CASCADE
                )
            """);
        }
    }

    /** Vide toutes les tables entre les tests pour garantir l'isolation. */
    public static void clearAll() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("DELETE FROM scene_personnage");
            stmt.execute("DELETE FROM scene");
            stmt.execute("DELETE FROM personnage");
            stmt.execute("DELETE FROM histoire");
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}