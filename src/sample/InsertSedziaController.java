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

public class InsertSedziaController {

    public Connection connection;
    public Controller controller;

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

    public void saveHandler(ActionEvent event) throws SQLException {

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
            System.out.println("[KRAJ] Wybierz kraj pochodzenia");
            return;
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO SEDZIOWIE VALUES(null, '" + imie + "', '" + nazwisko + "', "
                    + wiek + ", '" + kraj + "')");
            ResultSet rs = statement.executeQuery("select ID_SEDZIEGO_SEQ.currval from dual");
            rs.next();
            Sedziowie addedSedzia = new Sedziowie(rs.getString(1), imie, nazwisko, Integer.parseInt(wiek), kraj);
            controller.addToTable(controller.getTableSedziowie(), addedSedzia);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
