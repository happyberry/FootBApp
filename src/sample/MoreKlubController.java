package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;


public class MoreKlubController {

    @FXML
    public TableView tablePilkarze;
    @FXML
    public Label labelStadion, labelMiasto, labelWlasciciel, labelTrener, labelRok, labelLiga, labelNazwa;

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
