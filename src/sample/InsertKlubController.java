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

public class InsertKlubController {

    public Connection connection;
    public Controller controller;
    @FXML
    public ComboBox comboBoxLeague;
    @FXML
    public TextField clubName;
    @FXML
    public TextField clubYear;
    @FXML
    public Label labelWarning;

    public void initializeOptions() {

        comboBoxLeague.getItems().clear();
        comboBoxLeague.getItems().add("");
        String SQL = "SELECT NAZWA_LIGI from LIGI ORDER BY NAZWA_LIGI";

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        comboBoxLeague.getItems().add(rs.getString("nazwa_ligi"));
                    }
                }
                catch(SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Ligue Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void saveHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String name = clubName.getText();
        String year = clubYear.getText();
        String league = (String) comboBoxLeague.getSelectionModel().getSelectedItem();
        System.out.println(name + " " + year + " " + league);
        int yearInt;
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            labelWarning.setText("[ROK ZALOZENIA] Podaj liczbę całkowitą");
            labelWarning.setVisible(true);
            return;
        }
        if (yearInt <= 1800) {
            labelWarning.setText("[ROK ZALOZENIA] Podaj rok 1801 lub późniejszy");
            labelWarning.setVisible(true);
            return;
        }
        if (name.length() > 40) {
            labelWarning.setText("[NAZWA KLUBU] Podaj krótszą nazwę klubu");
            labelWarning.setVisible(true);
            return;
        }
        if (league != null) {
            if (league.equals("")) {league = null;}
            else { league = "'" + league + "'";}
            //labelWarning.setText("[NAZWA LIGI] Wybierz ligę, do której należy klub");
            //labelWarning.setVisible(true);
            //return;
        }
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO KLUBY VALUES('" + name + "', " + year + ", " + league + ")");
            if (league != null) league = league.substring(1, league.length()-1);
            Kluby addedClub = new Kluby(name, yearInt, league);
            controller.addToTable(controller.getTableKluby(), addedClub);
        } catch (SQLException e) {
            if (e.getMessage().contains("ORA-00001")) {
                labelWarning.setText("Taki klub już istnieje. Zmień dane i spróbuj ponownie");
                labelWarning.setVisible(true);
                return;
            }
            else {
                labelWarning.setText("Dane nieprawidłowe. Spróbuj ponownie");
                labelWarning.setVisible(true);
                e.printStackTrace();
                return;
            }
        }
        //controller.fillKluby();
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
