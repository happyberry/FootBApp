package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLRecoverableException;
import java.util.Random;

public class SFPlayerController {

    public Connection connection;
    public InsertTransferController insertTransferController;
    public InsertGolController insertGolController;

    public String opcja;
    @FXML
    public TextField textFieldNazwisko;
    @FXML
    public TextField textFieldKlub;
    @FXML
    public ComboBox comboBoxPozycja;
    @FXML
    public TableView tableSearch;
    @FXML
    public TableColumn tableColumnWartosc;
    @FXML
    public TableColumn tableColumnPensja;
    public EditGolController editGolController;
    public EditTransferController editTransferController;

    public void fetchInitialData(){
        tableSearch.getItems().clear();
        String SQL = "SELECT * from PILKARZE ORDER BY POZYCJA";
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
                    rs.close();
                } catch (SQLRecoverableException e) {
                    insertGolController.controller.showConnectionLostDialogAndExitApp();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
        tableSearch.setPlaceholder(new Label("Nie znaleziono pasujących piłkarzy"));
    }

    public void search() {

        String nazwisko = textFieldNazwisko.getText();
        String nazwaKlubu = textFieldKlub.getText();
        String pozycja = (String) comboBoxPozycja.getSelectionModel().getSelectedItem();
        if (nazwisko == null || nazwisko.equals("")) {nazwisko = "#123456789";}
        if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}
        if (pozycja == null || pozycja.equals("")) {pozycja = "#123456789";}

        tableSearch.getItems().clear();

        String SQL = "SELECT * from PILKARZE where NAZWA_KLUBU like '%" + nazwaKlubu.replaceAll("'", "''") +
                "%' OR nazwisko like '%" + nazwisko.replaceAll("'", "''") + "%' OR POZYCJA like '%" + pozycja + "%' ORDER BY POZYCJA";
        try {
            ResultSet rs = connection.createStatement().executeQuery(SQL);
            while (rs.next()) {
                Pilkarze pilkarz = new Pilkarze(rs.getString("id_pilkarza"), rs.getString("imie"),
                        rs.getString("nazwisko"), rs.getDate("data_urodzenia"), rs.getString("pozycja"),
                        rs.getDouble("wartosc_rynkowa"), rs.getDouble("pensja"), rs.getString("nazwa_klubu"));
                tableSearch.getItems().add(pilkarz);
            }
            rs.close();
        } catch (SQLRecoverableException e) {
            if (opcja.equals("wstawianieTransfer")) {
                insertTransferController.controller.showConnectionLostDialogAndExitApp();
            } else if (opcja.equals("wstawianieGol")) {
                insertGolController.controller.showConnectionLostDialogAndExitApp();
            } else if (opcja.equals("edycjaGol")) {
                editGolController.controller.showConnectionLostDialogAndExitApp();
            } else {
                editTransferController.controller.showConnectionLostDialogAndExitApp();
            }
        } catch (Exception e) {
            Label label = new Label("Brak wyników");
            String[] kek = new String[7];
            kek[0] = "red"; kek[1] = "blue"; kek[2] = "green"; kek[3] = "black"; kek[4] = "yellow"; kek[5] = "brown"; kek[6] = "magenta";
            Random r = new Random();
            int ind = r.nextInt(7);
            label.setStyle("-fx-text-fill: " + kek[ind]);
            tableSearch.setPlaceholder(label);
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    public void saveSelected(ActionEvent event) {

        if (tableSearch.getSelectionModel().getSelectedItem() == null) return;

        Pilkarze pilkarz = (Pilkarze) tableSearch.getSelectionModel().getSelectedItem();

        if (opcja.equals("wstawianieTransfer")) {
            insertTransferController.pilkarzId = pilkarz.getIdPilkarza();
            insertTransferController.textFieldID.setText(pilkarz.getImie() + " " + pilkarz.getNazwisko());
        } else if (opcja.equals("wstawianieGol")) {
            insertGolController.idPilkarza = pilkarz.getIdPilkarza();
            insertGolController.textFieldPilkarz.setText(pilkarz.getImie() + " " + pilkarz.getNazwisko());
        } else if (opcja.equals("edycjaGol")) {
            editGolController.idPilkarza = pilkarz.getIdPilkarza();
            editGolController.textFieldPilkarz.setText(pilkarz.getImie() + " " + pilkarz.getNazwisko());
        } else {
            editTransferController.pilkarzId = pilkarz.getIdPilkarza();
            editTransferController.textFieldID.setText(pilkarz.getImie() + " " + pilkarz.getNazwisko());
        }

        tableSearch.getItems().clear();

        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
