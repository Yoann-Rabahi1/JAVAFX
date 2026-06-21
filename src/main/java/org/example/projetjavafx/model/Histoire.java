package org.example.projetjavafx.model;

import java.util.ArrayList;
import java.util.List;

public class Histoire {

    private int idHisoire;
    private String titre;
    private String auteur;
    private String resumer;
    private List<Personnage> ListPersonnage;

    public Histoire(int idHisoire, String titre, List<Personnage> listPersonnage, String auteur, String resumer) {
        this.idHisoire = idHisoire;
        this.titre = titre;
        this.auteur = auteur;
        this.resumer = resumer;
        ListPersonnage = new ArrayList<Personnage>();
    }

    public Histoire(int idHisoire, String titre, List<Personnage> listPersonnage, String auteur) {
        this.idHisoire = idHisoire;
        this.titre = titre;
        this.auteur = auteur;
        ListPersonnage = new ArrayList<Personnage>();
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
        return ListPersonnage;
    }

    public void addPersonnage(Personnage personnage)
    {
        if(personnage != null)
            this.ListPersonnage.add(personnage);
    }

    public void addPersonnage(String nom, String role, String description)
    {
        Personnage newPersonnage = new Personnage(nom, role, description);
        this.ListPersonnage.add(newPersonnage);
    }

    @Override
    public String toString() {
        return "Histoire{" +
                "idHisoire=" + idHisoire +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", resumer='" + resumer + '\'' +
                ", ListPersonnage=" + ListPersonnage +
                '}';
    }
}
