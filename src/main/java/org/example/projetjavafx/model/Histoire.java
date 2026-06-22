package org.example.projetjavafx.model;

import java.util.ArrayList;
import java.util.List;

public class Histoire {

    private int idHisoire;
    private String titre;
    private String auteur;
    private String resumer;
    private List<Personnage> listePersonnages;

    public Histoire(int idHisoire, String titre, String auteur, String resumer) {
        this.idHisoire = idHisoire;
        this.titre = titre;
        this.auteur = auteur;
        this.resumer = resumer;
        listePersonnages = new ArrayList<Personnage>();
    }

    public Histoire( String titre, String auteur, String resumer) {
        this.titre = titre;
        this.auteur = auteur;
        this.resumer = resumer;
        listePersonnages = new ArrayList<Personnage>();
    }

    public int getIdHisoire() {
        return idHisoire;
    }

    public void setIdHisoire(int idHisoire) {
        this.idHisoire = idHisoire;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getResumer() {
        return resumer;
    }

    public void setResumer(String resumer) {
        this.resumer = resumer;
    }

    public List<Personnage> getListPersonnage() {
        return listePersonnages;
    }

    public void addPersonnage(Personnage personnage)
    {
        if(personnage != null)
            this.listePersonnages.add(personnage);
    }

    public void addPersonnage(String nom, String role, String description)
    {
        Personnage newPersonnage = new Personnage(nom, role, description);
        this.listePersonnages.add(newPersonnage);
    }


    public List<Personnage> getListePersonnages() {
        if (this.listePersonnages == null) {
            this.listePersonnages = new ArrayList<>();
        }
        return this.listePersonnages;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.titre).append(" (par ").append(this.auteur).append(")\n");
        sb.append("Résumé : ").append(this.resumer).append("\n");
        sb.append("Casting :");

        if (this.listePersonnages == null || this.listePersonnages.isEmpty()) {
            sb.append(" Aucun personnage pour le moment.");
        } else {
            for (Personnage p : this.listePersonnages) {
                sb.append("\n  - ").append(p.getNom_personnage()).append(" (").append(p.getRole_personnage()).append(")");
            }
        }
        return sb.toString();
    }
}
