package org.example.projetjavafx.model;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    public static final String[] STATUTS_POSSIBLES = {"Brouillon", "En cours", "Prêt à publier", "Publiée"};

    private int idScene;
    private String titre;
    private String lieu;
    private String moment;
    private String contenu;
    private int position;
    private String statut; // Stocké directement en String pour simplifier
    private List<Personnage> personnagesPresents = new ArrayList<>();

    public Scene() {}

    // Getters et Setters
    public int getIdScene() { return idScene; }
    public void setIdScene(int idScene) { this.idScene = idScene; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getMoment() { return moment; }
    public void setMoment(String moment) { this.moment = moment; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public List<Personnage> getPersonnagesPresents() { return personnagesPresents; }
    public void setPersonnagesPresents(List<Personnage> personnagesPresents) { this.personnagesPresents = personnagesPresents; }

    @Override
    public String toString() {
        return "N°" + position + " - " + titre + " (" + statut + ")";
    }
}