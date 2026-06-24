package org.example.projetjavafx.dao;

import org.example.projetjavafx.model.Scene;
import java.util.List;

public interface SceneDAO {
    void sauvegarder(Scene scene, int idHistoire);
    List<Scene> chargerParHistoire(int idHistoire);
    void mettreAJour(Scene scene);
    void supprimer(int idScene);

    // Pour la gestion de la table de jointure Many-To-Many (Scène <-> Personnage)
    void ajouterPersonnageAScene(int idScene, int idPersonnage);
    void retirerPersonnageDeScene(int idScene, int idPersonnage);
}