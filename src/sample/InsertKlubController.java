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
        String SQL = "SELECT DISTINCT NAZWA_LIGI from KLUBY";
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

    public void saveHandler(ActionEvent event) throws SQLException {

        String name = clubName.getText();
        String year = clubYear.getText();
        String league = (String) comboBoxLeague.getSelectionModel().getSelectedItem();
        System.out.println(name + " " + year + " " + league);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO KLUBY VALUES ('" + name + "', " + year + ", '" + league + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        controller.fillKluby();
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
