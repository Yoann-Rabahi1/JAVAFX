package org.example.projetjavafx.repository;

import org.example.projetjavafx.model.Histoire;
import java.util.List;

public interface HistoireRepository {
    void sauvegarder(Histoire histoire);
    List<Histoire> chargerTout();
    void mettreAJour(Histoire histoire);
    void supprimer(int idHistoire);
}