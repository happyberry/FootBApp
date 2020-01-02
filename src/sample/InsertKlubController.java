package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
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

    public void initializeOptions() {

        System.out.println(connection);
        comboBoxLeague.getItems().clear();
        String SQL = "SELECT NAZWA_LIGI from LIGI";

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

        String name = clubName.getText();
        String year = clubYear.getText();
        String league = (String) comboBoxLeague.getSelectionModel().getSelectedItem();
        System.out.println(name + " " + year + " " + league);
        int yearInt;
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            System.out.println("[ROK ZALOZENIA] Podaj liczbę całkowitą");
            return;
        }
        if (yearInt <= 1800) {
            System.out.println("[ROK ZALOZENIA] Podaj rok 1801 lub późniejszy");
            return;
        }
        if (name.length() > 40) {
            System.out.println("[NAZWA KLUBU] Podaj krótszą nazwę klubu");
            return;
        }
        if (league == null) {
            System.out.println("[NAZWA LIGI] Wybierz ligę, do której należy klub");
            return;
        }
        try {
            Kluby addedClub = new Kluby(name, year, league);
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO KLUBY VALUES ('" + name + "', " + year + ", '" + league + "')");
            controller.addToTable(controller.getTableKluby(), addedClub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //controller.fillKluby();
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
