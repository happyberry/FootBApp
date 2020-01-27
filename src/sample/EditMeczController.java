package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class EditMeczController {

    public Connection connection;
    public Controller controller;
    public Mecze mecz;
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
        if (year == null) {
            year = comboBoxYear.getPromptText();
        }
        if (month == null) {
            month = comboBoxMonth.getPromptText();
        }
        if (day == null) {
            day = comboBoxDay.getPromptText();
        }
        //System.out.println(year + " " + month + " " + day);
        Date date = new Date(Integer.parseInt(year)-1900, Integer.parseInt(month)-1, Integer.parseInt(day));
        String completeDate = year + '-' + month + '-' + day;

        String goscie = (String) comboBoxGosc.getSelectionModel().getSelectedItem();
        String gospodarze = (String) comboBoxGosp.getSelectionModel().getSelectedItem();
        if (goscie == null) {
            goscie = comboBoxGosc.getPromptText();
        }
        if (gospodarze == null) {
            gospodarze = comboBoxGosp.getPromptText();
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

        if(sedziaId == null || sedziaId.equals("") || sedziaId.equals("-1")) {
            sedziaId = null;
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE MECZE SET DATA = DATE '" + completeDate + "',GOSPODARZE = '" + gospodarze + "', GOSCIE = '" +
                    goscie + "', WYNIK_GOSPODARZY = " + wynikGospodarzy + ", WYNIK_GOSCI = " + wynikGosci + ",ID_SEDZIEGO = " + sedziaId +
                    " WHERE MECZ_ID = " + mecz.getMeczId());
            String daneSedziego;
            if (sedziaId == null) {
                daneSedziego = "";
                sedziaId = "-1";
            }
            else {
                ResultSet rs = statement.executeQuery("select imie || ' ' || nazwisko from SEDZIOWIE where ID_SEDZIEGO = " + sedziaId);
                rs.next();
                daneSedziego = rs.getString(1);
                rs.close();
            }
            Mecze nowyMecz = new Mecze(mecz.getMeczId(), date, gospodarze, goscie,
                    wynikGospodarzy, wynikGosci, sedziaId, daneSedziego);
            controller.removeFromTable(controller.getTableMecze(), mecz);
            controller.addToTable(controller.getTableMecze(), nowyMecz);
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

    public void deleteHandler(ActionEvent event) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Czy jesteś pewien?");
        alert.setHeaderText("Czy na pewno chcesz usunąć ten mecz?\n" +
                "Wraz z nim usunięte zostaną informacje o golach w nim strzelonych.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {
            return;
        }
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM MECZE WHERE MECZ_ID = " + mecz.getMeczId());
            controller.removeFromTable(controller.getTableMecze(), mecz);
            controller.goleJuzWczytane = false;
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

    public void openSearch() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/searchForReferee.fxml"));

        Stage stage = new Stage();
        //stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene((AnchorPane) loader.load()));
        //stage.show();

        SFRefereeController sfRefereeController = loader.<SFRefereeController>getController();
        sfRefereeController.connection = connection;
        sfRefereeController.editMeczController = this;
        sfRefereeController.operation = "Edycja";
        sfRefereeController.fetchInitialData();
        stage.showAndWait();
    }


}
