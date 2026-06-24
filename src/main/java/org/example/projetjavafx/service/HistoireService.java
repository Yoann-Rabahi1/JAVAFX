package org.example.projetjavafx.service;

import org.example.projetjavafx.dao.HistoireDAO;
import org.example.projetjavafx.dao.PersonnageDAO;
import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.model.Personnage;
import java.util.List;

public class HistoireService {

    private final HistoireDAO histoireDAO;
    private final PersonnageDAO personnageDAO;

    public HistoireService(HistoireDAO histoireDAO, PersonnageDAO personnageDAO) {
        this.histoireDAO = histoireDAO;
        this.personnageDAO = personnageDAO;
    }

    public List<Histoire> recupererToutesLesHistoires() {
        List<Histoire> histoires = histoireDAO.chargerTout();
        for (Histoire h : histoires) {
            List<Personnage> deLaBDD = personnageDAO.chargerParHistoire(h.getIdHisoire());
            h.getListePersonnages().clear();
            h.getListePersonnages().addAll(deLaBDD);
        }
        return histoires;
    }

    public Histoire creerHistoire(String titre, String auteur, String resume) {
        if (titre == null || titre.trim().isEmpty()) throw new IllegalArgumentException("Titre requis.");
        if (auteur == null || auteur.trim().isEmpty()) throw new IllegalArgumentException("Auteur requis.");

        Histoire h = new Histoire();
        h.setTitre(titre);
        h.setAuteur(auteur);
        h.setResumer(resume);
        histoireDAO.sauvegarder(h);
        return h;
    }

    public void modifierHistoire(Histoire histoire) {
        if (histoire.getTitre() == null || histoire.getTitre().trim().isEmpty()) throw new IllegalArgumentException("Titre requis.");
        histoireDAO.mettreAJour(histoire);
    }

    public void supprimerHistoire(Histoire histoire) {
        if (histoire != null) histoireDAO.supprimer(histoire.getIdHisoire());
    }
}