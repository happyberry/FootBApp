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

public class InsertStadionController {

    public Connection connection;
    public Controller controller;

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

    public void saveHandler(ActionEvent event) throws SQLException {

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
            labelWarning.setText("[KLUB] Wybierz klub, do którego nalezy stadion. Jeśli nie ma go na liście, najpierw dodaj klub.");
            labelWarning.setVisible(true);
            return;
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO STADIONY VALUES('" + nazwa + "', " + rokZbudowania + ", " + pojemnosc + ", '"
                    + miasto + "', '" + klub + "')");
            Stadiony addedStadion = new Stadiony(nazwa, rokZbudowania, pojemnosc, miasto, klub);
            controller.addToTable(controller.getTableStadiony(), addedStadion);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}

