package org.example.projetjavafx.model;

import java.util.ArrayList;
import java.util.List;
import org.example.projetjavafx.model.Scene;



public class Histoire {
    private int idHisoire;
    private String titre;
    private String auteur;
    private String resumer;
    private List<Personnage> listePersonnages = new ArrayList<>();
    private List<Scene> listeScenes = new ArrayList<>();

    public Histoire() {}

    public int getIdHisoire() { return idHisoire; }
    public void setIdHisoire(int idHisoire) { this.idHisoire = idHisoire; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public String getResumer() { return resumer; }
    public void setResumer(String resumer) { this.resumer = resumer; }
    public List<Personnage> getListePersonnages() { return listePersonnages; }
    public List<Scene> getListeScenes() {return this.listeScenes;}
    public void setListeScenes(List<Scene> listeScenes) {this.listeScenes = listeScenes;}
    public void setListePersonnages(List<Personnage> listePersonnages) {this.listePersonnages = listePersonnages;}

    @Override
    public String toString() { return this.titre; }
}