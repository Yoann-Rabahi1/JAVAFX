
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE scene_personnage;
TRUNCATE TABLE scene;
TRUNCATE TABLE personnage;
TRUNCATE TABLE histoire;
SET FOREIGN_KEY_CHECKS = 1;



INSERT INTO histoire (id_histoire, titre, auteur, resume) VALUES
(1001, 'Les Chroniques d''Éternia', 'Auteur Fantastique', 'Une quête épique dans un monde médiéval-fantastique où la magie se meurt.'),
(1002, 'Cyber-Neo 2084', 'Auteur Cyberpunk', 'Une plongée dystopique au cœur d''une mégapole gouvernée par une IA omnipotente.'),
(1003, 'L''Ombre du Quai 9', 'Auteur Policier', 'Une enquête policière sombre et complexe suite à une série de disparitions mystérieuses.');



INSERT INTO personnage (id_personnage, nom_personnage, id_histoire) VALUES
-- Personnages de l'Histoire 1001 (Éternia)
(2001, 'Eldrin le Sage', 1001),
(2002, 'Kaelen l''Archer', 1001),
(2003, 'Lumina la Magicienne', 1001),

-- Personnages de l'Histoire 1002 (Cyber-Neo)
(2004, 'Nova_X7', 1002),
(2005, 'Inspecteur Vance', 1002),

-- Personnages de l'Histoire 1003 (L'Ombre du Quai)
(2006, 'Marc Dupont', 1003),
(2007, 'Lucie Keller', 1003);



INSERT INTO scene (id_scene, titre, statut, id_histoire, contenu, position) VALUES 
-- Scènes de l'Histoire 1001
(3001, 'Prologue : La découverte de la carte mystérieuse', 'Validée', 1001, 'Texte de la scène 3001...', 1),
(3002, 'Rencontre inattendue dans la forêt de l''exil', 'En cours', 1001, 'Texte de la scène 3002...', 2),
(3003, 'L''embuscade des brigands au col de montagne', 'Planifiée', 1001, 'Texte de la scène 3003...', 3),
(3004, 'Révélation du secret de la citadelle', 'Rédigée', 1001, 'Texte de la scène 3004...', 4),

-- Scènes de l'Histoire 1002
(3005, 'Réveil brutal dans la matrice néon', 'Validée', 1002, 'Texte de la scène 3005...', 1),
(3006, 'Infiltration du serveur central d''Amphion', 'En cours', 1002, 'Texte de la scène 3006...', 2),
(3007, 'Course-poursuite sur les toits de Paris', 'Planifiée', 1002, 'Texte de la scène 3007...', 3),
(3008, 'Confrontation finale avec l''Intelligence Artificielle', 'Planifiée', 1002, 'Texte de la scène 3008...', 4),

-- Scènes de l'Histoire 1003
(3009, 'La première disparition au quai numéro 9', 'Rédigée', 1003, 'Texte de la scène 3009...', 1),
(3010, 'Interrogatoire tendu au commissariat principal', 'En cours', 1003, 'Texte de la scène 3010...', 2);



INSERT INTO scene_personnage (id_scene, id_personnage) VALUES 
-- Casting de l'Histoire 1001
(3001, 2001), 
(3002, 2001), 
(3002, 2002), 
(3003, 2002), 
(3003, 2003), 
(3004, 2001), 
(3004, 2003), 

-- Casting de l'Histoire 1002
(3005, 2004), 
(3006, 2004), 
(3007, 2004), 
(3007, 2005), 
(3008, 2004), 
(3008, 2005), 

-- Casting de l'Histoire 1003
(3009, 2006), 
(3010, 2007);