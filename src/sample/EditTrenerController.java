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

public class EditTrenerController {

    public Connection connection;
    public Controller controller;
    public Trenerzy trener;

    @FXML
    public TextField secondTF;
    @FXML
    public TextField thirdTF;
    @FXML
    public ComboBox comboBoxKraj;
    @FXML
    public ComboBox comboBoxClub;
    @FXML
    public Label labelWarning;

    public void initializeOptions() {

        //System.out.println(connection);
        comboBoxClub.getItems().clear();
        comboBoxClub.getItems().add("");
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

    public void editHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String imie = secondTF.getText();
        if (imie.equals("")) {
            labelWarning.setText("[IMIE] Podaj imię trenera");
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
            labelWarning.setText("[NAZWISKO] Podaj nazwisko trenera");
            labelWarning.setVisible(true);
            return;
        }
        if (nazwisko.length() > 40) {
            labelWarning.setText("[NAZWISKO] Nazwisko zbyt długie");
            labelWarning.setVisible(true);
            return;
        }
        String kraj = (String) comboBoxKraj.getSelectionModel().getSelectedItem();

        if (kraj == null) {
            kraj = comboBoxKraj.getPromptText();
        }

        String klub = (String) comboBoxClub.getSelectionModel().getSelectedItem();

        if (klub == null) {
            klub = comboBoxClub.getPromptText();
        }
        klub = klub.replaceAll("'", "''");
        klub = "'" + klub + "'";
        if (klub.equals("''")) {
            klub = null;
        }


        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE TRENERZY SET imie = '" + imie.replaceAll("'", "''") + "', nazwisko = '" + nazwisko.replaceAll("'", "''") + "'" +
                    ", pochodzenie = '" + kraj + "', nazwa_klubu = " + klub + " where ID_TRENERA = " + trener.getIdTrenera());
            if (klub != null) klub = klub.substring(1, klub.length()-1);
            Trenerzy nowyTrener = new Trenerzy(trener.getIdTrenera(), imie, nazwisko, kraj, klub);
            controller.removeFromTable(controller.getTableTrenerzy(), trener);
            controller.addToTable(controller.getTableTrenerzy(), nowyTrener);
        } catch (SQLException e) {
            if (e.getMessage().contains("ORA-00001")) {
                labelWarning.setText("Ten klub ma już trenera. Usuń go i spróbuj ponownie");
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
            statement.executeUpdate("DELETE FROM TRENERZY WHERE ID_TRENERA = " + trener.getIdTrenera());
            controller.removeFromTable(controller.getTableTrenerzy(), trener);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}

