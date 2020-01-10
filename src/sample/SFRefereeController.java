package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;

public class SFRefereeController {

    public Connection connection;
    public InsertMeczController insertMeczController;
    public EditMeczController editMeczController;
    public String operation;
    @FXML
    public TextField textFieldNazwisko;
    @FXML
    public TextField textFieldKraj;
    @FXML
    public TableView tableSearch;

    public void search() {

        String nazwisko = textFieldNazwisko.getText();
        String kraj = textFieldKraj.getText();
        if (nazwisko == null || nazwisko.equals("")) {nazwisko = "#123456789";}
        if (kraj == null || kraj.equals("")) {kraj = "#123456789";}

        tableSearch.getItems().clear();

        String SQL = "SELECT * from SEDZIOWIE where POCHODZENIE like '%" + kraj +
                "%' OR nazwisko like '%" + nazwisko + "%'";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Sedziowie sedzia = new Sedziowie(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5));
                        tableSearch.getItems().add(sedzia);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void saveSelected(ActionEvent event) {

        if(tableSearch.getSelectionModel().getSelectedItem() == null) return;

        Sedziowie sedzia = (Sedziowie) tableSearch.getSelectionModel().getSelectedItem();
        if (operation.equals("Edycja")) {
            editMeczController.textFieldSedzia.setText(sedzia.getIdSedziego());
        }
        else {
            insertMeczController.textFieldSedzia.setText(sedzia.getIdSedziego());
        }
        tableSearch.getItems().clear();

        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
