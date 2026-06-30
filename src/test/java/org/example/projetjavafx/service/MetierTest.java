package org.example.projetjavafx.service;

import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.model.Personnage;
import org.example.projetjavafx.model.Scene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires des règles métier (aucune base de données requise).
 * Les services sont instanciés avec des implémentations DAO fictives (stubs)
 * qui stockent les données en mémoire.
 */
class MetierTest {

    // -----------------------------------------------------------------------
    //  Stubs DAO en mémoire
    // -----------------------------------------------------------------------

    /** HistoireDAO stub : sauvegarde en mémoire et attribue un id fictif. */
    private static class HistoireDAOStub implements org.example.projetjavafx.dao.HistoireDAO {
        private final List<Histoire> store = new ArrayList<>();
        private int nextId = 1;

        @Override public void sauvegarder(Histoire h)        { h.setIdHisoire(nextId++); store.add(h); }
        @Override public List<Histoire> chargerTout()        { return new ArrayList<>(store); }
        @Override public void mettreAJour(Histoire h)        {}
        @Override public void supprimer(int id)              { store.removeIf(h -> h.getIdHisoire() == id); }
    }

    /** PersonnageDAO stub : sauvegarde en mémoire. */
    private static class PersonnageDAOStub implements org.example.projetjavafx.dao.PersonnageDAO {
        private final List<Personnage> store = new ArrayList<>();
        private int nextId = 1;

        @Override public void sauvegarder(Personnage p, int idHistoire) { p.setId_personnage(nextId++); store.add(p); }
        @Override public List<Personnage> chargerParHistoire(int id)    { return new ArrayList<>(store); }
        @Override public void mettreAJour(Personnage p)                 {}
        @Override public void supprimer(int id)                         { store.removeIf(p -> p.getId_personnage() == id); }
    }

    /** MySqlSceneRepository stub : sauvegarde en mémoire. */
    private static class SceneRepositoryStub extends org.example.projetjavafx.repository.MySqlSceneRepository {
        private final List<Scene> store = new ArrayList<>();
        private int nextId = 1;

        @Override public void sauvegarder(Scene s, int idHistoire)     { s.setIdScene(nextId++); store.add(s); }
        @Override public void mettreAJour(Scene s)                     {}
        @Override public void supprimer(int id)                        { store.removeIf(s -> s.getIdScene() == id); }
        @Override public void ajouterPersonnageAScene(int is, int ip)  {}
        @Override public void retirerPersonnageDeScene(int is, int ip) {}
        @Override public List<Scene> chargerParHistoire(int id)        { return new ArrayList<>(store); }
    }

    // -----------------------------------------------------------------------
    //  Initialisation
    // -----------------------------------------------------------------------

    private HistoireService   histoireService;
    private PersonnageService personnageService;
    private SceneService      sceneService;
    private Histoire          histoireTest;

    @BeforeEach
    void setUp() {
        histoireService   = new HistoireService(new HistoireDAOStub(), new PersonnageDAOStub());
        personnageService = new PersonnageService(new PersonnageDAOStub());
        sceneService      = new SceneService(new SceneRepositoryStub());

        // Histoire de base utilisée dans plusieurs tests
        histoireTest = new Histoire();
        histoireTest.setIdHisoire(1);
        histoireTest.setTitre("Le Seigneur des Tests");
        histoireTest.setAuteur("JUnit");
    }

    // -----------------------------------------------------------------------
    //  Tests : Histoire
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Une histoire ne peut pas avoir un titre vide")
    void histoire_TitreVide_LanceException() {
        assertThrows(IllegalArgumentException.class,
                () -> histoireService.creerHistoire("", "Auteur", "Résumé"),
                "Un titre vide doit lever une IllegalArgumentException");
    }

    @Test
    @DisplayName("Une histoire ne peut pas avoir un titre null")
    void histoire_TitreNull_LanceException() {
        assertThrows(IllegalArgumentException.class,
                () -> histoireService.creerHistoire(null, "Auteur", "Résumé"),
                "Un titre null doit lever une IllegalArgumentException");
    }

    @Test
    @DisplayName("Une histoire valide est créée sans erreur")
    void histoire_TitreValide_CreeSansErreur() {
        Histoire h = histoireService.creerHistoire("Titre valide", "Auteur", "Résumé");
        assertEquals("Titre valide", h.getTitre());
    }

    // -----------------------------------------------------------------------
    //  Tests : Personnage
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Un personnage ne peut pas avoir un nom vide")
    void personnage_NomVide_LanceException() {
        assertThrows(IllegalArgumentException.class,
                () -> personnageService.creerPersonnage("", "Héros", "Desc", histoireTest),
                "Un nom vide doit lever une IllegalArgumentException");
    }

    @Test
    @DisplayName("Un personnage ne peut pas avoir un nom null")
    void personnage_NomNull_LanceException() {
        assertThrows(IllegalArgumentException.class,
                () -> personnageService.creerPersonnage(null, "Héros", "Desc", histoireTest),
                "Un nom null doit lever une IllegalArgumentException");
    }

    @Test
    @DisplayName("Deux personnages d'une même histoire ne peuvent pas avoir le même nom")
    void personnage_NomDuplique_LanceException() {
        // On ajoute un premier personnage à l'histoire
        Personnage frodo = new Personnage();
        frodo.setNom_personnage("Frodo");
        frodo.setRole_personnage("Héros");
        histoireTest.getListePersonnages().add(frodo);

        // Tenter d'ajouter un deuxième avec le même nom doit échouer
        assertThrows(IllegalArgumentException.class,
                () -> personnageService.creerPersonnage("Frodo", "Méchant", "Desc", histoireTest),
                "Deux personnages du même nom dans la même histoire doivent lever une exception");
    }

    @Test
    @DisplayName("La comparaison de noms est insensible à la casse")
    void personnage_NomDupliqueCasse_LanceException() {
        Personnage gandalf = new Personnage();
        gandalf.setNom_personnage("Gandalf");
        gandalf.setRole_personnage("Mage");
        histoireTest.getListePersonnages().add(gandalf);

        assertThrows(IllegalArgumentException.class,
                () -> personnageService.creerPersonnage("GANDALF", "Imposteur", "Desc", histoireTest));
    }

    @Test
    @DisplayName("Un personnage valide est créé sans erreur")
    void personnage_NomValide_CreeSansErreur() {
        Personnage p = personnageService.creerPersonnage("Aragorn", "Roi", "Héritier du trône", histoireTest);
        assertEquals("Aragorn", p.getNom_personnage());
    }

    // -----------------------------------------------------------------------
    //  Tests : Scène
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Une scène ne peut pas avoir un titre vide")
    void scene_TitreVide_LanceException() {
        assertThrows(IllegalArgumentException.class,
                () -> sceneService.creerScene("", "Forêt", "Nuit", "Contenu...", 1, "Brouillon", histoireTest),
                "Un titre vide doit lever une IllegalArgumentException");
    }

    @Test
    @DisplayName("Une scène ne peut pas avoir un titre null")
    void scene_TitreNull_LanceException() {
        assertThrows(IllegalArgumentException.class,
                () -> sceneService.creerScene(null, "Forêt", "Nuit", "Contenu...", 1, "Brouillon", histoireTest));
    }

    @Test
    @DisplayName("Une scène doit posséder un statut valide")
    void scene_StatutInvalide_LanceException() {
        assertThrows(IllegalArgumentException.class,
                () -> sceneService.creerScene("Titre", "Lieu", "Nuit", "Contenu...", 1, "INVALIDE", histoireTest),
                "Un statut invalide doit lever une IllegalArgumentException");
    }

    @Test
    @DisplayName("Une scène doit posséder un statut (null interdit)")
    void scene_StatutNull_LanceException() {
        assertThrows(IllegalArgumentException.class,
                () -> sceneService.creerScene("Titre", "Lieu", "Nuit", "Contenu...", 1, null, histoireTest),
                "Un statut null doit lever une IllegalArgumentException");
    }

    @Test
    @DisplayName("Une scène ne peut pas contenir un personnage étranger à l'histoire")
    void scene_PersonnageHorsHistoire_LanceException() {
        Scene scene = new Scene();
        scene.setIdScene(10);
        scene.setPersonnagesPresents(new ArrayList<>());

        // Personnage qui n'appartient PAS à histoireTest
        Personnage etranger = new Personnage();
        etranger.setId_personnage(999);
        etranger.setNom_personnage("Sauron");

        assertThrows(IllegalArgumentException.class,
                () -> sceneService.associerPersonnageAScene(scene, etranger, histoireTest),
                "Associer un personnage étranger doit lever une IllegalArgumentException");
    }

    @Test
    @DisplayName("Un personnage appartenant à l'histoire peut être associé à une scène")
    void scene_PersonnageAppartientHistoire_AssocieSansErreur() {
        Personnage bilbo = new Personnage();
        bilbo.setId_personnage(1);
        bilbo.setNom_personnage("Bilbo");
        histoireTest.getListePersonnages().add(bilbo);

        Scene scene = new Scene();
        scene.setIdScene(5);
        scene.setPersonnagesPresents(new ArrayList<>());

        // Ne doit pas lever d'exception
        assertDoesNotThrow(() -> sceneService.associerPersonnageAScene(scene, bilbo, histoireTest));
        assertTrue(scene.getPersonnagesPresents().contains(bilbo));
    }

    // -----------------------------------------------------------------------
    //  Tests : Filtrage et recherche
    // -----------------------------------------------------------------------

    private Histoire histoireAvecScenes() {
        Histoire h = new Histoire();
        h.setIdHisoire(2);
        h.setTitre("Histoires de test");

        Personnage sam   = creerPersonnage(1, "Sam");
        Personnage pippin = creerPersonnage(2, "Pippin");
        h.getListePersonnages().addAll(Arrays.asList(sam, pippin));

        Scene s1 = creerScene(1, "La Comté",     "Brouillon",        "Le départ de la Comté", 1, sam);
        Scene s2 = creerScene(2, "Rivendell",    "En cours",          "Le conseil d'Elrond",  2, sam, pippin);
        Scene s3 = creerScene(3, "Moria",        "Prêt à publier",    "Les mines de la Moria",3, pippin);

        h.getListeScenes().addAll(Arrays.asList(s1, s2, s3));
        return h;
    }

    private Personnage creerPersonnage(int id, String nom) {
        Personnage p = new Personnage();
        p.setId_personnage(id);
        p.setNom_personnage(nom);
        return p;
    }

    private Scene creerScene(int id, String titre, String statut, String contenu, int position, Personnage... persos) {
        Scene s = new Scene();
        s.setIdScene(id);
        s.setTitre(titre);
        s.setStatut(statut);
        s.setContenu(contenu);
        s.setPosition(position);
        s.setPersonnagesPresents(new ArrayList<>(Arrays.asList(persos)));
        return s;
    }

    @Test
    @DisplayName("Le filtrage par statut retourne uniquement les scènes du statut demandé")
    void filtrage_ParStatut_RetourneSceneAttendue() {
        Histoire h = histoireAvecScenes();

        List<Scene> resultat = sceneService.filtrerEtRechercherScenes(h, "En cours", null, null);

        assertEquals(1, resultat.size());
        assertEquals("Rivendell", resultat.get(0).getTitre());
    }

    @Test
    @DisplayName("Le filtrage par personnage retourne les scènes où il apparaît")
    void filtrage_ParPersonnage_RetourneScenesAttendue() {
        Histoire h = histoireAvecScenes();
        Personnage sam = h.getListePersonnages().get(0); // id=1

        List<Scene> resultat = sceneService.filtrerEtRechercherScenes(h, null, sam, null);

        assertEquals(2, resultat.size(), "Sam apparaît dans 2 scènes");
        assertTrue(resultat.stream().anyMatch(s -> s.getTitre().equals("La Comté")));
        assertTrue(resultat.stream().anyMatch(s -> s.getTitre().equals("Rivendell")));
    }

    @Test
    @DisplayName("La recherche par mot-clé sur le titre retourne les scènes correspondantes")
    void recherche_ParMotCle_RetourneScenesCorrespondantes() {
        Histoire h = histoireAvecScenes();

        List<Scene> resultat = sceneService.filtrerEtRechercherScenes(h, null, null, "Comté");

        assertEquals(1, resultat.size());
        assertEquals("La Comté", resultat.get(0).getTitre());
    }

    @Test
    @DisplayName("La recherche par mot-clé est insensible à la casse")
    void recherche_ParMotCleCasse_RetourneScenesCorrespondantes() {
        Histoire h = histoireAvecScenes();

        List<Scene> resultat = sceneService.filtrerEtRechercherScenes(h, null, null, "moria");

        assertEquals(1, resultat.size());
        assertEquals("Moria", resultat.get(0).getTitre());
    }

    @Test
    @DisplayName("La recherche par mot-clé sur le contenu retourne les scènes correspondantes")
    void recherche_ParMotCleSurContenu_RetourneScenesCorrespondantes() {
        Histoire h = histoireAvecScenes();

        List<Scene> resultat = sceneService.filtrerEtRechercherScenes(h, null, null, "conseil");

        assertEquals(1, resultat.size());
        assertEquals("Rivendell", resultat.get(0).getTitre());
    }

    @Test
    @DisplayName("Un filtre sans résultat retourne une liste vide")
    void filtrage_AucunResultat_RetourneListeVide() {
        Histoire h = histoireAvecScenes();

        List<Scene> resultat = sceneService.filtrerEtRechercherScenes(h, "Publiée", null, null);

        assertTrue(resultat.isEmpty());
    }
}
