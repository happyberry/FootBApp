package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;

public class EditWlascicielController {

    public Connection connection;
    public Controller controller;
    public Wlasciciele wlasciciel;

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
                    rs.close();
                } catch (SQLRecoverableException e) {
                    controller.showConnectionLostDialogAndExitApp();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Clubs Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void editHandler(ActionEvent event) throws SQLException {

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

        //System.out.println(majatek);
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
        if(klub == null){
            klub = comboBoxClub.getPromptText();
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE Wlasciciele SET imie = '" + imie.replaceAll("'", "''") + "', nazwisko = '" + nazwisko.replaceAll("'", "''") + "'" +
                    ", majatek = " + doubleMajatek + ", nazwa_klubu = '" + klub.replaceAll("'", "''") + "' " + "where ID_Wlasciciela = " + wlasciciel.getIdWlasciciela());
            Wlasciciele nowyWlasciciel = new Wlasciciele(wlasciciel.getIdWlasciciela(), imie, nazwisko, doubleMajatek, klub);
            controller.removeFromTable(controller.getTableWlasciciele(), wlasciciel);
            controller.addToTable(controller.getTableWlasciciele(), nowyWlasciciel);
        } catch (SQLRecoverableException e) {
            controller.showConnectionLostDialogAndExitApp();
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

    public void deleteHandler(ActionEvent event) throws SQLException {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM Wlasciciele WHERE ID_Wlasciciela = " + wlasciciel.getIdWlasciciela());
            controller.removeFromTable(controller.getTableWlasciciele(), wlasciciel);
        } catch (SQLRecoverableException e) {
            controller.showConnectionLostDialogAndExitApp();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}