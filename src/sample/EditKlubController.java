package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.sql.*;
import java.util.Optional;

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
    @FXML
    public Label labelWarning;

    public void initializeOptions() {

        comboBoxLeague.getItems().clear();
        comboBoxLeague.getItems().add("");
        String SQL = "SELECT NAZWA_LIGI from LIGI ORDER BY NAZWA_LIGI";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ResultSet rs = null;
                try {
                    rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        comboBoxLeague.getItems().add(rs.getString("nazwa_ligi"));
                    }
                    rs.close();
                } catch (SQLRecoverableException e) {
                    controller.showConnectionLostDialogAndExitApp();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Ligue Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void deleteHandler(ActionEvent event) throws SQLException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Czy jesteś pewien?");
        alert.setHeaderText("Czy na pewno chcesz usunąć ten klub?\n" +
                "Usuwając go, usuniesz także dane o rozegranych meczach,\n" +
                "stadionie, właścicielu i transferach");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {
            return;
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM KLUBY WHERE nazwa_klubu = '" + oldName.replaceAll("'", "''") + "'");
            controller.removeFromTable(controller.getTableKluby(), klub);
            controller.goleJuzWczytane = false;
            controller.wlascicieleJuzWczytani = false;
            controller.meczeJuzWczytane = false;
            controller.transferyJuzWczytane = false;
            controller.stadionyJuzWczytane = false;
            controller.trenerzyJuzWczytani = false;
            controller.pilkarzeJuzWczytani = false;
        } catch (SQLRecoverableException e) {
            controller.showConnectionLostDialogAndExitApp();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void editHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String name = textFieldClubName.getText();
        String year = textFieldYear.getText();
        String league = (String) comboBoxLeague.getSelectionModel().getSelectedItem();
        if (league == null) league = comboBoxLeague.getPromptText();
        if (league.equals("")) {
            league = null;
        } else {
            league = league.replaceAll("'", "''");
            league = "'" + league + "'";
        }
        int yearInt;
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            labelWarning.setText("[ROK ZALOZENIA] Podaj liczbę całkowitą");
            labelWarning.setVisible(true);
            return;
        }
        if (yearInt <= 1800) {
            labelWarning.setText("[ROK ZALOZENIA] Podaj rok 1801 lub późniejszy");
            labelWarning.setVisible(true);
            return;
        }
        if (name.length() > 40) {
            labelWarning.setText("[NAZWA KLUBU] Podaj krótszą nazwę klubu");
            labelWarning.setVisible(true);
            return;
        }
        try {
            if (!name.equals(oldName)) {
                connection.setAutoCommit(false);
            }
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE KLUBY SET nazwa_klubu = '" + name.replaceAll("'", "''") + "', rok_zalozenia = " + year + ", nazwa_ligi = " + league +
                    " WHERE nazwa_klubu = '" + oldName.replaceAll("'", "''") + "'");
            if (!name.equals(oldName)) {
                statement.executeUpdate("UPDATE MECZE SET GOSPODARZE = '" + name.replaceAll("'", "''") + "'" +
                        " WHERE GOSPODARZE = '" + oldName.replaceAll("'", "''") + "'");
                statement.executeUpdate("UPDATE MECZE SET GOSCIE = '" + name.replaceAll("'", "''") + "'" +
                        " WHERE GOSCIE = '" + oldName.replaceAll("'", "''") + "'");
                statement.executeUpdate("UPDATE TRENERZY SET NAZWA_KLUBU = '" + name.replaceAll("'", "''") + "'" +
                        " WHERE NAZWA_KLUBU = '" + oldName.replaceAll("'", "''") + "'");
                statement.executeUpdate("UPDATE WLASCICIELE SET NAZWA_KLUBU = '" + name.replaceAll("'", "''") + "'" +
                        " WHERE NAZWA_KLUBU = '" + oldName.replaceAll("'", "''") + "'");
                statement.executeUpdate("UPDATE STADIONY SET NAZWA_KLUBU = '" + name.replaceAll("'", "''") + "'" +
                        " WHERE NAZWA_KLUBU = '" + oldName.replaceAll("'", "''") + "'");
                statement.executeUpdate("UPDATE PILKARZE SET NAZWA_KLUBU = '" + name.replaceAll("'", "''") + "'" +
                        " WHERE NAZWA_KLUBU = '" + oldName.replaceAll("'", "''") + "'");
                statement.executeUpdate("UPDATE TRANSFERY SET KLUB_KUPUJACY = '" + name.replaceAll("'", "''") + "'" +
                        " WHERE KLUB_KUPUJACY = '" + oldName.replaceAll("'", "''") + "'");
                statement.executeUpdate("UPDATE TRANSFERY SET KLUB_SPRZEDAJACY = '" + name.replaceAll("'", "''") + "'" +
                        " WHERE KLUB_SPRZEDAJACY = '" + oldName.replaceAll("'", "''") + "'");
                connection.commit();
                connection.setAutoCommit(true);
                controller.meczeJuzWczytane = false;
                controller.trenerzyJuzWczytani = false;
                controller.wlascicieleJuzWczytani = false;
                controller.stadionyJuzWczytane = false;
                controller.pilkarzeJuzWczytani = false;
                controller.transferyJuzWczytane = false;
            }
            if (league != null) league = league.substring(1, league.length()-1);
            Kluby nowyKlub = new Kluby(name, yearInt, league);
            controller.removeFromTable(controller.getTableKluby(), klub);
            controller.addToTable(controller.getTableKluby(), nowyKlub);
        } catch (SQLRecoverableException e) {
            controller.showConnectionLostDialogAndExitApp();
        } catch (SQLException e) {
            if (e.getMessage().contains("ORA-00001")) {
                labelWarning.setText("Taki klub już istnieje. Zmień dane i spróbuj ponownie");
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
        //controller.fillKluby();
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
