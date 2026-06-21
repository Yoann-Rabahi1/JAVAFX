package org.example.projetjavafx.model;

public class Personnage {

    private int id_personnage;
    private String nom_personnage;
    private String role_personnage;
    private String description_personnage;

    public Personnage(int id_personnage, String nom_personnage, String role_personnage, String description_personnage)
    {
        this.id_personnage = id_personnage;
        this.nom_personnage = nom_personnage;
        this.role_personnage = role_personnage;
        this.description_personnage = description_personnage;
    }

    public Personnage(String role_personnage, String nom_personnage, String description_personnage)
    {
        this.nom_personnage = nom_personnage;
        this.role_personnage = role_personnage;
        this.description_personnage = description_personnage;
    }

    public Personnage(int id_personnage, String role_personnage)
    {
        this.id_personnage = id_personnage;
        this.role_personnage = role_personnage;
    }

    public int getId_personnage() {
        return id_personnage;
    }

    public void setId_personnage(int id_personnage) {
        this.id_personnage = id_personnage;
    }

    public String getNom_personnage() {
        return nom_personnage;
    }

    public void setNom_personnage(String nom_personnage) {
        this.nom_personnage = nom_personnage;
    }

    public String getRole_personnage() {
        return role_personnage;
    }

    public void setRole_personnage(String role_personnage) {
        this.role_personnage = role_personnage;
    }

    public String getDescription_personnage() {
        return description_personnage;
    }

    public void setDescription_personnage(String description_personnage) {
        this.description_personnage = description_personnage;
    }


    @Override
    public String toString() {
        return "Personnage{" +
                "id_personnage=" + id_personnage +
                ", nom personnage=" + nom_personnage +
                ", role_personnage='" + role_personnage + '\'' +
                ", description_personnage='" + description_personnage + '\'' +
                '}';
    }
}
