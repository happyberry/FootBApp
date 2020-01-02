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

    public void initializeOptions() {

        //System.out.println(connection);
        comboBoxLeague.getItems().clear();
        String SQL = "SELECT DISTINCT NAZWA_LIGI from LIGI";
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

    public void deleteHandler(ActionEvent event) throws SQLException {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM KLUBY WHERE nazwa_klubu = '" + oldName + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //controller.fillKluby();
        controller.removeFromTable(controller.getTableKluby(), klub);

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void editHandler(ActionEvent event) throws SQLException {

        String name = textFieldClubName.getText();
        String year = textFieldYear.getText();
        String league = (String) comboBoxLeague.getSelectionModel().getSelectedItem();
        if(league == null) league = comboBoxLeague.getPromptText();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE KLUBY SET nazwa_klubu = '" + name + "', rok_zalozenia = " + year + ", nazwa_ligi = '" + league + "'"
                    + " WHERE nazwa_klubu = '" + oldName + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //controller.fillKluby();
        Kluby nowyKlub = new Kluby(name, year, league);
        controller.removeFromTable(controller.getTableKluby(), klub);
        controller.addToTable(controller.getTableKluby(), nowyKlub);
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
