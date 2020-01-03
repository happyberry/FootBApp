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

public class EditLigaController {

    public Connection connection;
    public Controller controller;
    Ligi liga;

    @FXML
    public TextField textFieldName;
    @FXML
    public ComboBox comboBoxKraj;
    @FXML
    public Label labelWarning;


    public void deleteHandler(ActionEvent event) throws SQLException {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM LIGI WHERE NAZWA_LIGI = '" + liga.getNazwaLigi() + "'");
            controller.removeFromTable(controller.getTableLigi(), liga);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void editHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String name = textFieldName.getText();
        if (!name.equals(liga.getNazwaLigi())) {
            if (name.equals("")) {
                labelWarning.setText("Podaj nazwę ligi");
                labelWarning.setVisible(true);
                return;
            }
            if (name.length() > 40) {
                labelWarning.setText("Nazwa ligi powinna być krótsza niż 40 znaków");
                labelWarning.setVisible(true);
                return;
            }
            for (Object o: controller.getTableLigi().getItems()) {
                Ligi liga = (Ligi) o;
                if (name.equals(liga.getNazwaLigi())) {
                    labelWarning.setText("Taka liga już istnieje!");
                    labelWarning.setVisible(true);
                    return;
                }
            }
        }

        String country = (String) comboBoxKraj.getSelectionModel().getSelectedItem();

        if (country == null) {
            country = comboBoxKraj.getPromptText();
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE LIGI SET nazwa_ligi = '" + name + "', kraj = '" + country + "'"
                    + " WHERE nazwa_ligi = '" + liga.getNazwaLigi() + "'");
            Ligi nowaLiga = new Ligi(name, country);
            controller.removeFromTable(controller.getTableLigi(), liga);
            controller.addToTable(controller.getTableLigi(), nowaLiga);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
