package org.example.projetjavafx.service;

import org.example.projetjavafx.model.Histoire;

public class HistoireService {

    public Histoire creerHistoire(String titre, String auteur, String resume) {
        // Validation des contraintes de l'énoncé
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de l'histoire ne peut pas être vide !");
        }
        if (auteur == null || auteur.trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur de l'histoire ne peut pas être vide !");
        }

        // Si c'est valide, on crée l'objet
        return new Histoire(titre, auteur, resume);
    }

    public void modifierHistoire(Histoire histoire) {
        if (histoire.getTitre() == null || histoire.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre modifié ne peut pas être vide !");
        }
        if (histoire.getAuteur() == null || histoire.getAuteur().trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur modifié ne peut pas être vide !");
        }
    }

    public void supprimerHistoire(Histoire histoire) {
        // En mémoire (Étape 1), le fait de la retirer de la liste du contrôleur suffit.
        // À l'Étape 2, c'est ici qu'on appellera histoireRepository.delete(histoire.getId_histoire());
    }
}