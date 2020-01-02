package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.*;

public class InsertPilkarzController {

    public Connection connection;
    public Controller controller;
    @FXML
    public ComboBox comboBoxClub;
    @FXML
    public ComboBox comboBoxPos;
    @FXML
    public ComboBox comboBoxBDay;
    @FXML
    public ComboBox comboBoxBMonth;
    @FXML
    public ComboBox comboBoxBYear;
    @FXML
    public TextField secondTF;
    @FXML
    public TextField thirdTF;
    @FXML
    public TextField sixthTF;
    @FXML
    public TextField seventhTF;


    public void initializeOptions() {

        System.out.println(connection);
        comboBoxClub.getItems().clear();
        String SQL = "SELECT NAZWA_klubu from KLUBY";

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        comboBoxClub.getItems().add(rs.getString("nazwa_klubu"));
                    }
                }
                catch(SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Club Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void saveHandler(ActionEvent event) throws SQLException {

        String second = secondTF.getText();
        String third = thirdTF.getText();
        String year = comboBoxBYear.getSelectionModel().getSelectedItem().toString();
        String month = comboBoxBMonth.getSelectionModel().getSelectedItem().toString();
        String day = comboBoxBDay.getSelectionModel().getSelectedItem().toString();
        Date fourth = new java.sql.Date(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
        String fifth = comboBoxPos.getSelectionModel().getSelectedItem().toString();
        String sixth = sixthTF.getText();
        String seventh = seventhTF.getText();
        String eighth = comboBoxClub.getSelectionModel().getSelectedItem().toString();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO PILKARZE VALUES (null, '" + second + "', '" + third + "', Date '" +
                    year + "-" + month + "-" + day + "', '" + fifth + "', " + sixth + ", " + seventh + ", '" + eighth + "')");
            ResultSet rs = statement.executeQuery("select id_pilkarza_seq.currval from dual");
            rs.next();
            Pilkarze addedPilkarz = new Pilkarze(rs.getString(1), second, third, fourth, fifth, sixth, seventh, eighth);
            controller.addToTable(controller.getTablePilkarze(), addedPilkarz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
