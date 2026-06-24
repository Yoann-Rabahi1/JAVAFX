package org.example.projetjavafx.repository;

import org.example.projetjavafx.model.Personnage;
import java.util.List;

public interface PersonnageRepository {
    void sauvegarder(Personnage personnage, int idHistoire);
    List<Personnage> chargerParHistoire(int idHistoire);
    void mettreAJour(Personnage personnage);
    void supprimer(int idPersonnage);
}