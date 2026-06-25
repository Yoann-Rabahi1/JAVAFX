CREATE DATABASE IF NOT EXISTS story_forge;

USE story_forge;

CREATE TABLE IF NOT EXISTS histoire (
    id_histoire INT AUTO_INCREMENT,
    titre VARCHAR(150) NOT NULL,
    auteur VARCHAR(100) NOT NULL,
    resume TEXT,
    CONSTRAINT pk_histoire PRIMARY KEY (id_histoire)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS personnage (
    id_personnage INT AUTO_INCREMENT,
    nom_personnage VARCHAR(100) NOT NULL,
    role_personnage VARCHAR(100),
    description_personnage TEXT,
    id_histoire INT NOT NULL, -- Clé étrangère vers l'histoire associée
    CONSTRAINT pk_personnage PRIMARY KEY (id_personnage),
    CONSTRAINT fk_personnage_histoire FOREIGN KEY (id_histoire) 
        REFERENCES histoire(id_histoire) 
        ON DELETE CASCADE -- Si on supprime une histoire, ses personnages partent avec
) ENGINE=InnoDB;