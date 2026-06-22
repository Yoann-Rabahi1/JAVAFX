package org.example.projetjavafx.service;

import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.model.Personnage;

public class PersonnageService {

    public Personnage creerPersonnage(String nom, String role, String description, Histoire histoire) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du personnage ne peut pas être vide !");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Le rôle du personnage ne peut pas être vide !");
        }

        for (Personnage p : histoire.getListePersonnages()) {
            if (p.getNom_personnage().equalsIgnoreCase(nom.trim())) {
                throw new IllegalArgumentException("Un personnage nommé '" + nom + "' existe déjà dans cette histoire !");
            }
        }

        Personnage nouveau = new Personnage(nom.trim(), role.trim(), description);
        histoire.addPersonnage(nouveau);
        return nouveau;
    }

    public void modifierPersonnage(Personnage personnage, String ancienNom, Histoire histoire) {
        if (personnage.getNom_personnage() == null || personnage.getNom_personnage().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide !");
        }
        if (personnage.getRole_personnage() == null || personnage.getRole_personnage().trim().isEmpty()) {
            throw new IllegalArgumentException("Le rôle ne peut pas être vide !");
        }

        if (!personnage.getNom_personnage().equalsIgnoreCase(ancienNom)) {
            for (Personnage p : histoire.getListePersonnages()) {
                if (p.getNom_personnage().equalsIgnoreCase(personnage.getNom_personnage().trim())) {
                    throw new IllegalArgumentException("Le nom '" + personnage.getNom_personnage() + "' est déjà pris !");
                }
            }
        }
    }

    public void supprimerPersonnage(Personnage personnage, Histoire histoire) {
        histoire.getListePersonnages().remove(personnage);
    }
}