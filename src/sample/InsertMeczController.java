package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class InsertMeczController {

    public Connection connection;
    public Controller controller;
    public String sedziaId;
    @FXML
    public ComboBox comboBoxGosc;
    @FXML
    public ComboBox comboBoxGosp;
    @FXML
    public ComboBox comboBoxDay;
    @FXML
    public ComboBox comboBoxMonth;
    @FXML
    public ComboBox comboBoxYear;
    @FXML
    public TextField textFieldWynikGosp;
    @FXML
    public TextField textFieldWynikGosc;
    @FXML
    public TextField textFieldSedzia;
    @FXML
    public Label labelWarning;


    public void initializeOptions() {

        comboBoxGosc.getItems().clear();
        comboBoxGosp.getItems().clear();
        String SQL = "SELECT NAZWA_klubu from KLUBY ORDER BY NAZWA_KLUBU";

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        String nazwaKlubu = rs.getString(1);
                        comboBoxGosc.getItems().add(nazwaKlubu);
                        comboBoxGosp.getItems().add(nazwaKlubu);
                    }
                    rs.close();
                } catch (SQLRecoverableException e) {
                    controller.showConnectionLostDialogAndExitApp();
                } catch(SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Club Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void saveHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);

        String year = (String) comboBoxYear.getSelectionModel().getSelectedItem();
        String month = (String) comboBoxMonth.getSelectionModel().getSelectedItem();
        String day = (String) comboBoxDay.getSelectionModel().getSelectedItem();
        if (year == null || month == null || day == null) {
            labelWarning.setText("[DATA] Podaj pełną datę");
            labelWarning.setVisible(true);
            return;
        }
        Date date = new Date(Integer.valueOf(year)-1900, Integer.valueOf(month)-1, Integer.valueOf(day));
        String completeDate = year + '-' + month + '-' + day;

        String goscie = (String) comboBoxGosc.getSelectionModel().getSelectedItem();
        String gospodarze = (String) comboBoxGosp.getSelectionModel().getSelectedItem();
        if (goscie == null || goscie.equals("")) {
            labelWarning.setText("[GOŚCIE] Podaj nazwę drużyny gości");
            labelWarning.setVisible(true);
            return;
        }
        if (gospodarze == null || gospodarze.equals("")) {
            labelWarning.setText("[GOSPODARZE] Podaj nazwę drużyny gospodarzy");
            labelWarning.setVisible(true);
            return;
        }

        String wynikGosp = textFieldWynikGosp.getText();
        String wynikGosc = textFieldWynikGosc.getText();
        if (wynikGosc == null || wynikGosc.equals("")) {
            labelWarning.setText("[WYNIK_GOŚCI] Podaj wynik drużyny gości");
            labelWarning.setVisible(true);
            return;
        }
        if (wynikGosp == null || wynikGosp.equals("")) {
            labelWarning.setText("[WYNIK_GOSPODARZY] Podaj wynik drużyny gospodarzy");
            labelWarning.setVisible(true);
            return;
        }
        Integer wynikGospodarzy = null;
        Integer wynikGosci = null;
        try{
            wynikGospodarzy = Integer.parseInt(wynikGosp);
        } catch (Exception e) {
            labelWarning.setText("[WYNIK_GOSPODARZY] Podaj liczbę całkowitą");
            labelWarning.setVisible(true);
            return;
        }
        try{
            wynikGosci = Integer.parseInt(wynikGosc);
        } catch (Exception e) {
            labelWarning.setText("[WYNIK_GOŚCI] Podaj liczbę całkowitą");
            labelWarning.setVisible(true);
            return;
        }
        if (wynikGosci < 0) {
            labelWarning.setText("[WYNIK_GOŚCI] Podaj poprawny wynik");
            labelWarning.setVisible(true);
            return;
        }
        if (wynikGospodarzy < 0) {
            labelWarning.setText("[WYNIK_GOSPODARZY] Podaj poprawny wynik");
            labelWarning.setVisible(true);
            return;
        }

        if (Integer.parseInt(month) == 2) {
            if (Integer.parseInt(day) > 28) {
                if (Integer.parseInt(day) > 29 || Integer.parseInt(year) % 4 != 0) {
                    labelWarning.setText("[DATA] Luty ma mniej niż 30 dni");
                    labelWarning.setVisible(true);
                    return;
                }
            }
        }
        if (Integer.parseInt(month) == 4 || Integer.parseInt(month) == 6 || Integer.parseInt(month) == 9 || Integer.parseInt(month) == 11) {
            if (day.equals("31")) {
                labelWarning.setText("[DATA] Błędny dzień miesiąca");
                labelWarning.setVisible(true);
                return;
            }
        }

        if (sedziaId == null || sedziaId.equals("")) {
            sedziaId = null;
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO MECZE VALUES(null, DATE '" + completeDate + "', '" + gospodarze.replaceAll("'", "''") + "', '" +
                    goscie.replaceAll("'", "''") + "', " + wynikGospodarzy + ", " + wynikGosci + ", " + sedziaId + ")");
            ResultSet rs = statement.executeQuery("select MECZE_MECZ_ID_SEQ.currval from dual");
            rs.next();
            String idMeczu = rs.getString(1);
            String daneSedziego;
            rs.close();
            if (sedziaId == null) {
                daneSedziego = "";
                sedziaId = "-1";
            }
            else {
                rs = statement.executeQuery("select imie || ' ' || nazwisko from SEDZIOWIE where ID_SEDZIEGO = " + sedziaId);
                rs.next();
                daneSedziego = rs.getString(1);
                rs.close();
            }
            Mecze addedMecz = new Mecze(idMeczu, date, gospodarze, goscie,
                    wynikGospodarzy, wynikGosci, sedziaId, daneSedziego);

            controller.addToTable(controller.getTableMecze(), addedMecz);
        } catch (SQLRecoverableException e) {
            controller.showConnectionLostDialogAndExitApp();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().contains("ORA-02290")) {
                labelWarning.setText("Wybierz dwa różne kluby");
                labelWarning.setVisible(true);
                return;
            } else if (e.getMessage().contains("ORA-00001")){
                labelWarning.setText("[DATA, GOSPODARZE, GOŚCIE] Posiadasz już dane o tym meczu");
                labelWarning.setVisible(true);
                e.printStackTrace();
                return;
            } else {
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

    public void openSearch() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/searchForReferee.fxml"));

        Stage stage = new Stage();
        //stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene((AnchorPane) loader.load()));
        //stage.show();

        SFRefereeController sfRefereeController = loader.<SFRefereeController>getController();
        sfRefereeController.connection = connection;
        sfRefereeController.insertMeczController = this;
        sfRefereeController.operation = "Wstawianie";
        sfRefereeController.fetchInitialData();
        stage.showAndWait();
    }

}
