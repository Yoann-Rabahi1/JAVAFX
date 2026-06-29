package org.example.projetjavafx.repository;

import org.example.projetjavafx.dao.SceneDAO;
import org.example.projetjavafx.dao.MySqlSceneDAO;
import org.example.projetjavafx.model.Scene;
import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.model.Histoire;
import java.util.List;

public interface SceneRepository{
    public void save(Scene scene, int idHistoire);
    public List<Scene> findByHistoire(int idHistoire);
    public void update(Scene scene);
    public void delete(int idScene);
    public void addCharacterToScene(int idScene, int idPersonnage);
    public void removeCharacterFromScene(int idScene, int idPersonnage);

}

/*

public class SceneRepository {

    private final SceneDAO sceneDAO;

    public SceneRepository() {
        this.sceneDAO = new MySqlSceneDAO();
    }

    public void save(Scene scene, int idHistoire) {
        sceneDAO.sauvegarder(scene, idHistoire);
    }

    public List<Scene> findByHistoire(int idHistoire) {
        return sceneDAO.chargerParHistoire(idHistoire);
    }

    public void update(Scene scene) {
        sceneDAO.mettreAJour(scene);
    }

    public void delete(int idScene) {
        sceneDAO.supprimer(idScene);
    }

    public void addCharacterToScene(int idScene, int idPersonnage) {
        sceneDAO.ajouterPersonnageAScene(idScene, idPersonnage);
    }

    public void removeCharacterFromScene(int idScene, int idPersonnage) {
        sceneDAO.retirerPersonnageDeScene(idScene, idPersonnage);
    }
}

*/
