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
import java.util.Arrays;
import java.util.List;

public class InsertLigaController {

    public Connection connection;
    public Controller controller;

    @FXML
    public TextField textFieldName;
    @FXML
    public ComboBox comboBoxKraj;
    @FXML
    public Label labelWarning;

    public void saveHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String name = textFieldName.getText();
        String country = (String) comboBoxKraj.getSelectionModel().getSelectedItem();

        if (country == null) {
            labelWarning.setText("[KRAJ] Podaj kraj rozgrywek");
            labelWarning.setVisible(true);
            return;
        }

        try {
            Ligi addedLiga = new Ligi(name, country);
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO LIGI VALUES('" + name + "', '" + country + "')");
            controller.addToTable(controller.getTableLigi(), addedLiga);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
