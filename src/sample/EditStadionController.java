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

public class EditStadionController {

    public Connection connection;
    public Controller controller;
    public Stadiony stadion;

    @FXML
    public TextField firstTF;
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
        String SQL = "SELECT NAZWA_KLUBU from KLUBY ORDER BY NAZWA_KLUBU";
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

        labelWarning.setVisible(false);
        String nazwa = firstTF.getText();
        if (nazwa.equals("")) {
            labelWarning.setText("[NAZWA] Podaj nazwę stadionu");
            labelWarning.setVisible(true);
            return;
        }
        if (nazwa.length() > 40) {
            labelWarning.setText("[NAZWA] Nazwa zbyt długa");
            labelWarning.setVisible(true);
            return;
        }

        String strRokZbudowania = secondTF.getText();
        int rokZbudowania;
        try {
            rokZbudowania = Integer.parseInt(strRokZbudowania);
        }
        catch (NumberFormatException e) {
            labelWarning.setText("[ROK ZBUDOWANIA] Podaj liczbę naturalną");
            labelWarning.setVisible(true);
            return;
        }
        if (rokZbudowania < 1800 || rokZbudowania > 2050) {
            labelWarning.setText("[ROK ZBUDOWANIA] Podaj rok z przedziału 1800-2050");
            labelWarning.setVisible(true);
            return;
        }

        String strPojemnosc = thirdTF.getText();
        int pojemnosc;
        try {
            pojemnosc = Integer.parseInt(strPojemnosc);
        }
        catch (NumberFormatException e) {
            labelWarning.setText("[POJEMNOŚĆ] Podaj liczbę naturalną");
            labelWarning.setVisible(true);
            return;
        }
        if (pojemnosc < 0 || pojemnosc > 500000) {
            labelWarning.setText("[POJEMNOŚĆ] Podaj liczbę z przedziału 0-500000");
            labelWarning.setVisible(true);
            return;
        }

        String miasto = fourthTF.getText();
        if (miasto.equals("")) {
            labelWarning.setText("[MIASTO] Podaj miasto, w którym znajduje się stadion");
            labelWarning.setVisible(true);
            return;
        }
        if (miasto.length() > 40) {
            labelWarning.setText("[MIASTO] Nazwa miasta zbyt długa");
            labelWarning.setVisible(true);
            return;
        }

        String klub = (String) comboBoxClub.getSelectionModel().getSelectedItem();

        if (klub == null) {
            klub = comboBoxClub.getPromptText();
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE STADIONY SET NAZWA = '" + nazwa + "', ROK_ZBUDOWANIA = " + rokZbudowania +
                    ", POJEMNOSC = " + pojemnosc + ", miasto = '" + miasto + "', nazwa_klubu = '" + klub + "' " + "where NAZWA = '" + stadion.getNazwa() + "'");
            Stadiony nowyStadion = new Stadiony(nazwa, rokZbudowania, pojemnosc, miasto, klub);
            controller.removeFromTable(controller.getTableStadiony(), stadion);
            controller.addToTable(controller.getTableStadiony(), nowyStadion);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void deleteHandler(ActionEvent event) throws SQLException {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM STADIONY WHERE NAZWA = '" + stadion.getNazwa() + "'");
            controller.removeFromTable(controller.getTableStadiony(), stadion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}


