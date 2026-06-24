package org.example.projetjavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.projetjavafx.model.Histoire;
import org.example.projetjavafx.dao.MySqlHistoireDAO;
import org.example.projetjavafx.dao.MySqlPersonnageDAO;
import org.example.projetjavafx.service.HistoireService;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HistoireController implements Initializable {

    private final HistoireService histoireService = new HistoireService(new MySqlHistoireDAO(), new MySqlPersonnageDAO());
    private PersonnageController personnageController;

    @FXML private ListView<Histoire> ListHistoires;
    @FXML private TextField txtFieldTitre;
    @FXML private TextField txtFieldAuteur;
    @FXML private TextArea txtAreaResumer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Histoire> histoiresBDD = histoireService.recupererToutesLesHistoires();
        ListHistoires.getItems().addAll(histoiresBDD);

        ListHistoires.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayHistoireDetails(newSelection);
                if (personnageController != null) {
                    if (personnageController.getComboHistoires() != null) {
                        personnageController.getComboHistoires().getSelectionModel().select(newSelection);
                    }
                    personnageController.chargerPersonnagesPourHistoire(newSelection);
                }
            }
        });
    }

    public void setPersonnageController(PersonnageController personnageController) { this.personnageController = personnageController; }
    public ListView<Histoire> getListHistoires() { return this.ListHistoires; }

    @FXML
    public void onAjouterHistoire() {
        try {
            Histoire h = histoireService.creerHistoire(txtFieldTitre.getText(), txtFieldAuteur.getText(), txtAreaResumer.getText());
            ListHistoires.getItems().add(h);
            viderChampsHistoire();
        } catch (IllegalArgumentException e) { afficherAlerteErreur("Erreur", e.getMessage()); }
    }

    @FXML
    public void onModifierHistoire() {
        Histoire h = ListHistoires.getSelectionModel().getSelectedItem();
        if (h == null) return;
        h.setTitre(txtFieldTitre.getText());
        h.setAuteur(txtFieldAuteur.getText());
        h.setResumer(txtAreaResumer.getText());
        try {
            histoireService.modifierHistoire(h);
            ListHistoires.refresh();
        } catch (IllegalArgumentException e) { afficherAlerteErreur("Erreur", e.getMessage()); }
    }

    @FXML
    public void onSupprimerHistoire() {
        Histoire h = ListHistoires.getSelectionModel().getSelectedItem();
        if (h == null) return;
        histoireService.supprimerHistoire(h);
        ListHistoires.getItems().remove(h);
        if (personnageController != null) personnageController.chargerPersonnagesPourHistoire(null);
        viderChampsHistoire();
    }

    private void displayHistoireDetails(Histoire h) {
        txtFieldAuteur.setText(h.getAuteur());
        txtFieldTitre.setText(h.getTitre());
        txtAreaResumer.setText(h.getResumer());
    }

    private void viderChampsHistoire() {
        txtFieldTitre.clear(); txtFieldAuteur.clear(); txtAreaResumer.clear();
        ListHistoires.getSelectionModel().clearSelection();
    }

    private void afficherAlerteErreur(String t, String m) {
        Alert a = new Alert(Alert.AlertType.ERROR); a.setTitle(t); a.setContentText(m); a.showAndWait();
    }
}