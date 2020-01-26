package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;
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
        String country = (String) comboBoxKraj.getSelectionModel().getSelectedItem();

        if (country == null) {
            labelWarning.setText("[KRAJ] Podaj kraj rozgrywek");
            labelWarning.setVisible(true);
            return;
        }

        try {
            Ligi addedLiga = new Ligi(name, country);
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO LIGI VALUES('" + name.replaceAll("'", "''") + "', '" + country + "')");
            controller.addToTable(controller.getTableLigi(), addedLiga);
        } catch (SQLRecoverableException e) {
            controller.showConnectionLostDialogAndExitApp();
        } catch (SQLException e) {
            labelWarning.setText("Dane nieprawidłowe. Spróbuj ponownie");
            labelWarning.setVisible(true);
            e.printStackTrace();
            return;
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
