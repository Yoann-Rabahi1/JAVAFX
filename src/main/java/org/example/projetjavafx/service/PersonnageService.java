package org.example.projetjavafx.service;

import org.example.projetjavafx.dao.PersonnageDAO;
import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.model.Personnage;

public class PersonnageService {

    private final PersonnageDAO personnageDAO;

    public PersonnageService(PersonnageDAO personnageDAO) {
        this.personnageDAO = personnageDAO;
    }

    public Personnage creerPersonnage(String nom, String role, String description, Histoire histoire) {
        if (nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Nom requis.");
        if (role == null || role.trim().isEmpty()) throw new IllegalArgumentException("Rôle requis.");

        for (Personnage p : histoire.getListePersonnages()) {
            if (p.getNom_personnage().equalsIgnoreCase(nom.trim())) {
                throw new IllegalArgumentException("Un personnage nommé '" + nom + "' existe déjà !");
            }
        }

        Personnage n = new Personnage();
        n.setNom_personnage(nom.trim());
        n.setRole_personnage(role.trim());
        n.setDescription_personnage(description);

        personnageDAO.sauvegarder(n, histoire.getIdHisoire());
        histoire.getListePersonnages().add(n);
        return n;
    }

    public void modifierPersonnage(Personnage personnage, String ancienNom, Histoire histoire) {
        if (personnage.getNom_personnage() == null || personnage.getNom_personnage().trim().isEmpty()) throw new IllegalArgumentException("Nom requis.");

        if (!personnage.getNom_personnage().equalsIgnoreCase(ancienNom)) {
            for (Personnage p : histoire.getListePersonnages()) {
                if (p.getNom_personnage().equalsIgnoreCase(personnage.getNom_personnage().trim())) {
                    throw new IllegalArgumentException("Le nom '" + personnage.getNom_personnage() + "' est déjà pris !");
                }
            }
        }
        personnageDAO.mettreAJour(personnage);
    }

    public void supprimerPersonnage(Personnage personnage, Histoire histoire) {
        if (personnage != null) {
            personnageDAO.supprimer(personnage.getId_personnage());
            histoire.getListePersonnages().remove(personnage);
        }
    }
}