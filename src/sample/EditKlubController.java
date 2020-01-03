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

public class EditKlubController {

    public Connection connection;
    public Controller controller;
    public String oldName;
    public Kluby klub;

    @FXML
    public ComboBox comboBoxLeague;
    @FXML
    public TextField textFieldClubName;
    @FXML
    public TextField textFieldYear;
    @FXML
    public Label labelWarning;

    public void initializeOptions() {

        //System.out.println(connection);
        comboBoxLeague.getItems().clear();
        String SQL = "SELECT NAZWA_LIGI from LIGI";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ResultSet rs = null;
                try {
                    rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        comboBoxLeague.getItems().add(rs.getString("nazwa_ligi"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Ligue Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void deleteHandler(ActionEvent event) throws SQLException {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM KLUBY WHERE nazwa_klubu = '" + oldName + "'");
            controller.removeFromTable(controller.getTableKluby(), klub);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //controller.fillKluby();

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void editHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String name = textFieldClubName.getText();
        String year = textFieldYear.getText();
        String league = (String) comboBoxLeague.getSelectionModel().getSelectedItem();
        if (league == null) league = comboBoxLeague.getPromptText();
        int yearInt;
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            System.out.println("[ROK ZALOZENIA] Podaj liczbę całkowitą");
            labelWarning.setText("[ROK ZALOZENIA] Podaj liczbę całkowitą");
            labelWarning.setVisible(true);
            return;
        }
        if (yearInt <= 1800) {
            System.out.println("[ROK ZALOZENIA] Podaj rok 1801 lub późniejszy");
            labelWarning.setText("[ROK ZALOZENIA] Podaj rok 1801 lub późniejszy");
            labelWarning.setVisible(true);
            return;
        }
        if (name.length() > 40) {
            System.out.println("[NAZWA KLUBU] Podaj krótszą nazwę klubu");
            labelWarning.setText("[NAZWA KLUBU] Podaj krótszą nazwę klubu");
            labelWarning.setVisible(true);
            return;
        }
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE KLUBY SET nazwa_klubu = '" + name + "', rok_zalozenia = " + year + ", nazwa_ligi = '" + league + "'"
                    + " WHERE nazwa_klubu = '" + oldName + "'");
            Kluby nowyKlub = new Kluby(name, yearInt, league);
            controller.removeFromTable(controller.getTableKluby(), klub);
            controller.addToTable(controller.getTableKluby(), nowyKlub);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //controller.fillKluby();
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
