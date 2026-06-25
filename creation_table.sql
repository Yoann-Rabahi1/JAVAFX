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

CREATE TABLE IF NOT EXISTS scene (
    id_scene INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    lieu VARCHAR(255),
    moment VARCHAR(255),
    contenu TEXT NOT NULL,
    position INT NOT NULL,
    statut VARCHAR(50) NOT NULL,
    id_histoire INT NOT NULL,
    FOREIGN KEY (id_histoire) REFERENCES histoire(id_histoire) ON DELETE CASCADE,
    UNIQUE(id_histoire, position) -- Garantit la contrainte de position unique par histoire
);

CREATE TABLE IF NOT EXISTS scene_personnage (
    id_scene INT NOT NULL,
    id_personnage INT NOT NULL,
    PRIMARY KEY (id_scene, id_personnage),
    FOREIGN KEY (id_scene) REFERENCES scene(id_scene) ON DELETE CASCADE,
    FOREIGN KEY (id_personnage) REFERENCES personnage(id_personnage) ON DELETE CASCADE
);