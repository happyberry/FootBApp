package sample;

import com.sun.org.apache.xpath.internal.operations.Bool;
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

public class InsertWlascicielController {

    public Connection connection;
    public Controller controller;

    @FXML
    public TextField secondTF;
    @FXML
    public TextField thirdTF;
    @FXML
    public TextField fourthTF;
    @FXML
    public ComboBox comboBoxClub;
    @FXML
    public Label labelWarning;


    public void initializeOptions() {

        comboBoxClub.getItems().clear();
        String SQL = "SELECT NAZWA_KLUBU from KLUBY ORDER BY NAZWA_KLUBU";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ResultSet rs = null;
                try {
                    rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        comboBoxClub.getItems().add(rs.getString("nazwa_klubu"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Clubs Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void saveHandler(ActionEvent event) {

        Boolean out = true;
        labelWarning.setVisible(false);
        String imie = secondTF.getText();
        if (imie.equals("")) {
            labelWarning.setText("[IMIE] Podaj imię wlasciciela");
            labelWarning.setVisible(true);
            return;
        }
        if (imie.length() > 40) {
            labelWarning.setText("[IMIE] Imię zbyt długie");
            labelWarning.setVisible(true);
            return;
        }
        String nazwisko = thirdTF.getText();
        if (nazwisko.equals("")) {
            labelWarning.setText("[NAZWISKO] Podaj nazwisko wlasciciela");
            labelWarning.setVisible(true);
            return;
        }
        if (nazwisko.length() > 40) {
            labelWarning.setText("[NAZWISKO] Nazwisko zbyt długie");
            labelWarning.setVisible(true);
            return;
        }

        String majatek = fourthTF.getText().replaceAll(" ", "");
        majatek = majatek.replaceFirst(",", ".");
        int toCut = majatek.length() - majatek.indexOf(".") - 3;
        if (toCut > 0 && majatek.contains(".")) majatek = majatek.substring(0, majatek.length() - toCut);

        double doubleMajatek;
        try {
            doubleMajatek = Double.parseDouble(majatek);
        } catch (NumberFormatException e) {
            labelWarning.setText("[MAJĄTEK] Błędny format wartości majątku właściciela");
            labelWarning.setVisible(true);
            return;
        }
        if (doubleMajatek > 9999999999.99 || doubleMajatek < -9999999999.99) {
            labelWarning.setText("[MAJĄTEK] Niepoprawna wartość majątku");
            labelWarning.setVisible(true);
            return;
        }

        String klub = (String) comboBoxClub.getSelectionModel().getSelectedItem();
        if (klub == null) {
            labelWarning.setText("[KLUB] Wybierz klub, który posiada właściciel");
            labelWarning.setVisible(true);
            return;
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO Wlasciciele VALUES(null, '" + imie + "', '" + nazwisko + "', "
                    + majatek + ", '" + klub + "')");
            ResultSet rs = statement.executeQuery("select ID_wlasciciela_SEQ.currval from dual");
            rs.next();
            Wlasciciele addedWlasciciel = new Wlasciciele(rs.getString(1), imie, nazwisko, doubleMajatek, klub);
            controller.addToTable(controller.getTableWlasciciele(), addedWlasciciel);
        } catch (SQLException e) {
            if (e.getMessage().contains("ORA-00001")) {
                labelWarning.setText("Ten klub ma już właściciela. Usuń go i spróbuj ponownie");
                labelWarning.setVisible(true);
                return;
            }
            else {
                labelWarning.setText("Dane nieprawidłowe. Spróbuj ponownie");
                labelWarning.setVisible(true);
                e.printStackTrace();
                return;
            }
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
