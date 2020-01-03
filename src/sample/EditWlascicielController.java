package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EditWlascicielController {

    public Connection connection;
    public Controller controller;
    public Wlasciciele wlasciciel;

    @FXML
    public TextField secondTF;
    @FXML
    public TextField thirdTF;
    @FXML
    public TextField fourthTF;
    @FXML
    public ComboBox comboBoxClub;
    @FXML
    public Label labelWarning;

    public void initializeOptions() {

        comboBoxClub.getItems().clear();
        String SQL = "SELECT NAZWA_KLUBU from KLUBY";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ResultSet rs = null;
                try {
                    rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        comboBoxClub.getItems().add(rs.getString("nazwa_klubu"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Clubs Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void editHandler(ActionEvent event) throws SQLException {

        String imie = secondTF.getText();
        if (imie.equals("")) {
            System.out.println("[IMIE] Podaj imię wlasciciela");
            return;
        }
        if (imie.length() > 40) {
            System.out.println("[IMIE] Imię zbyt długie");
            return;
        }
        String nazwisko = thirdTF.getText();
        if (nazwisko.equals("")) {
            System.out.println("[NAZWISKO] Podaj nazwisko wlasciciela");
            return;
        }
        if (nazwisko.length() > 40) {
            System.out.println("[NAZWISKO] Nazwisko zbyt długie");
            return;
        }

        String majatek = (String) fourthTF.getText().replaceAll(" ", "");
        majatek.replaceFirst(",", ".");
        int toCut = majatek.length() - majatek.indexOf(".") - 3;
        if (toCut > 0 && majatek.contains(".")) majatek = majatek.substring(0, majatek.length() - toCut);

        double doubleMajatek;
        try {
            doubleMajatek = Double.parseDouble(majatek);
        } catch (NumberFormatException e) {
            System.out.println("[MAJĄTEK] Podaj wartość majątku właściciela");
            return;
        }
        if (doubleMajatek > 9999999999.99 || doubleMajatek < -9999999999.99) {
            System.out.println("[MAJĄTEK] Niepoprawna wartość majątku.");
            return;
        }

        String klub = (String) comboBoxClub.getSelectionModel().getSelectedItem();
        if(klub == null){
            klub = comboBoxClub.getPromptText();
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE Wlasciciele SET imie = '" + imie + "', nazwisko = '" + nazwisko + "'" +
                    ", majatek = " + doubleMajatek + ", nazwa_klubu = '" + klub + "' " + "where ID_Wlasciciela = " + wlasciciel.getIdWlasciciela());
            Wlasciciele nowyWlasciciel = new Wlasciciele(wlasciciel.getIdWlasciciela(), imie, nazwisko, doubleMajatek, klub);
            controller.removeFromTable(controller.getTableWlasciciele(), wlasciciel);
            controller.addToTable(controller.getTableWlasciciele(), nowyWlasciciel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void deleteHandler(ActionEvent event) throws SQLException {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM Wlasciciele WHERE ID_Wlasciciela = " + wlasciciel.getIdWlasciciela());
            controller.removeFromTable(controller.getTableWlasciciele(), wlasciciel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}