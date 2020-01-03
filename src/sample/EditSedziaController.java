package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class EditSedziaController {

    public Connection connection;
    public Controller controller;
    public Sedziowie sedzia;

    @FXML
    public TextField textFieldImie;
    @FXML
    public TextField textFieldNazwisko;
    @FXML
    public TextField textFieldWiek;
    @FXML
    public ComboBox comboBoxKraj;
    @FXML
    public Label labelWarning;

    public void editHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String imie = textFieldImie.getText();
        if (imie.equals("")) {
            System.out.println("[IMIE] Podaj imię sędziego");
            return;
        }
        if (imie.length() > 40) {
            System.out.println("[IMIE] Imię zbyt długie");
            return;
        }
        String nazwisko = textFieldNazwisko.getText();
        if (nazwisko.equals("")) {
            System.out.println("[NAZWISKO] Podaj nazwisko sędziego");
            return;
        }
        if (nazwisko.length() > 40) {
            System.out.println("[NAZWISKO] Nazwisko zbyt długie");
            return;
        }
        String wiek = textFieldWiek.getText();
        int wiekInt;
        try {
            wiekInt = Integer.parseInt(wiek);
        } catch (NumberFormatException e) {
            System.out.println("[WIEK] Podaj wiek jako liczbę całkowitą z zakresu 20-60");
            return;
        }
        if (wiekInt < 20 || wiekInt > 60) {
            System.out.println("[WIEK] Podaj wiek jako liczbę całkowitą z zakresu 20-60");
            return;
        }
        String kraj = (String) comboBoxKraj.getSelectionModel().getSelectedItem();

        if (kraj == null) {
            kraj = comboBoxKraj.getPromptText();
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE SEDZIOWIE SET imie = '" + imie + "', nazwisko = '" + nazwisko + "', wiek = "
                    + wiek + ", pochodzenie = '" + kraj + "' where ID_SEDZIEGO = " + sedzia.getIdSedziego());
            Sedziowie nowySedzia = new Sedziowie(sedzia.getIdSedziego(), imie, nazwisko, Integer.parseInt(wiek), kraj);
            controller.removeFromTable(controller.getTableSedziowie(), sedzia);
            controller.addToTable(controller.getTableSedziowie(), nowySedzia);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void deleteHandler(ActionEvent event) throws SQLException {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM SEDZIOWIE WHERE ID_SEDZIEGO = " + sedzia.getIdSedziego());
            controller.removeFromTable(controller.getTableSedziowie(), sedzia);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
