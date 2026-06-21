package org.example.projetjavafx.controller;

import com.dlsc.formsfx.model.util.ResourceBundleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.projetjavafx.model.Personnage;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PersonnageController implements Serializable, Initializable {

    private final ObservableList<Personnage> personnageList = FXCollections.observableArrayList();

    @FXML
    private ListView<Personnage> ListPersonnage;

    @FXML
    private TextField txtFieldNomPersonnage;

    @FXML
    private TextField txtFieldRolePersonnage;

    @FXML
    private TextField txtFieldDescriptionPersonnage;


    private void displayPersonnageDetails(Personnage personnageSelectionne)
    {
        if(personnageSelectionne!=null)
        {
            txtFieldNomPersonnage.setText(personnageSelectionne.getNom_personnage());
            txtFieldRolePersonnage.setText(personnageSelectionne.getRole_personnage());
            txtFieldDescriptionPersonnage.setText(personnageSelectionne.getDescription_personnage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        ListPersonnage.setItems(personnageList);
        ListPersonnage.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> displayPersonnageDetails(newSelection)
        );
    }

}
