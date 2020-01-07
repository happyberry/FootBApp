package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;

public class SFPlayerController {

    public Connection connection;
    public InsertTransferController controller;
    @FXML
    public TextField textFieldNazwisko;
    @FXML
    public TextField textFieldKlub;
    @FXML
    public TextField textFieldData;
    @FXML
    public TextField textFieldPozycja;
    @FXML
    public TableView tableSearch;

    public void search() {

        String nazwisko = textFieldNazwisko.getText();
        String nazwaKlubu = textFieldKlub.getText();
        String dataUrodzenia = textFieldData.getText();
        String pozycja = textFieldPozycja.getText();
        if (nazwisko == null || nazwisko.equals("")) {nazwisko = "#123456789";}
        if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}
        if (dataUrodzenia == null || dataUrodzenia.equals("")) {dataUrodzenia = "1800-01-01";}
        if (pozycja == null || pozycja.equals("")) {pozycja = "#123456789";}

        tableSearch.getItems().clear();

        String SQL = "SELECT * from PILKARZE where DATA_URODZENIA = DATE '" + dataUrodzenia + "' OR NAZWA_KLUBU like '%" + nazwaKlubu +
                "%' OR nazwisko like '%" + nazwisko + "%' OR POZYCJA like '%" + pozycja + "%'";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Pilkarze pilkarz = new Pilkarze(rs.getString("id_pilkarza"), rs.getString("imie"),
                                rs.getString("nazwisko"), rs.getDate("data_urodzenia"), rs.getString("pozycja"),
                                rs.getDouble("wartosc_rynkowa"), rs.getDouble("pensja"), rs.getString("nazwa_klubu"));
                        tableSearch.getItems().add(pilkarz);
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

        Pilkarze pilkarz = (Pilkarze) tableSearch.getSelectionModel().getSelectedItem();
        controller.textFieldID.setText(pilkarz.getIdPilkarza());

        tableSearch.getItems().clear();

        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
