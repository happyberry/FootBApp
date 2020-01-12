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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class EditTransferController {

    public Connection connection;
    public Controller controller;
    public String pilkarzId;
    public Transfery transfer;
    @FXML
    public TextField textFieldKwota;
    @FXML
    public TextField textFieldID;
    @FXML
    public ComboBox comboBoxDay;
    @FXML
    public ComboBox comboBoxMonth;
    @FXML
    public ComboBox comboBoxYear;
    @FXML
    public ComboBox comboBoxSell;
    @FXML
    public ComboBox comboBoxBuy;
    @FXML
    public Label labelWarning;



    public void initializeOptions() {

        comboBoxSell.getItems().clear();
        comboBoxSell.getItems().add("");
        comboBoxBuy.getItems().clear();
        String SQL = "SELECT NAZWA_KLUBU from KLUBY ORDER BY NAZWA_KLUBU";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ResultSet rs = null;
                try {
                    rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        String nazwa = rs.getString("nazwa_klubu");
                        comboBoxSell.getItems().add(nazwa);
                        comboBoxBuy.getItems().add(nazwa);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Clubs Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void deleteHandler(ActionEvent event) {
        String year = comboBoxYear.getPromptText();
        String month = comboBoxMonth.getPromptText();
        String day = comboBoxDay.getPromptText();
        String data = year + "-" + month + "-" + day;

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM TRANSFERY WHERE ID_PILKARZA = " + transfer.getIdPilkarza() + " AND DATA_TRANSFERU = DATE '" + data +"'");
            controller.removeFromTable(controller.getTableTransfery(), transfer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    public void editHandler(ActionEvent event) throws SQLException {


        labelWarning.setVisible(false);
        String kwota = textFieldKwota.getText().replaceAll(" ", "");
        kwota = kwota.replaceFirst(",", ".");
        int toCut = kwota.length() - kwota.indexOf(".") - 3;
        if (toCut > 0 && kwota.contains(".")) kwota = kwota.substring(0, kwota.length() - toCut);

        double kwotaTransferu;
        try {
            kwotaTransferu = Double.parseDouble(kwota);
        } catch (NumberFormatException e) {
            labelWarning.setText("[KWOTA] Błędny format kwoty transferu");
            labelWarning.setVisible(true);
            return;
        }
        if (kwotaTransferu > 9999999999.99 || kwotaTransferu < 0) {
            labelWarning.setText("[KWOTA] Niepoprawna wartość kwoty transferu");
            labelWarning.setVisible(true);
            return;
        }
        String klubKupujacy = (String) comboBoxBuy.getSelectionModel().getSelectedItem();
        if (klubKupujacy == null) {
            klubKupujacy = comboBoxBuy.getPromptText();
        }
        String klubSprzedajacy = (String) comboBoxSell.getSelectionModel().getSelectedItem();
        if (klubSprzedajacy == null) {
            klubSprzedajacy = comboBoxSell.getPromptText();
        } else if (klubSprzedajacy.equals("")){
            klubSprzedajacy = null;
        }
        if (klubSprzedajacy != null) klubSprzedajacy = "'" + klubSprzedajacy + "'";
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
        if (year == null || month == null || day == null) {
            labelWarning.setText("[DATA] Podaj pełną datę");
            labelWarning.setVisible(true);
            return;
        }
        String data = year + "-" + month + "-" + day;
        String idPilkarza = pilkarzId;
        if (idPilkarza == null) {
            idPilkarza = transfer.getIdPilkarza();
        }
        Date dataTransferu = new Date(Integer.valueOf(year)-1900, Integer.valueOf(month)-1, Integer.valueOf(day));
        String staraData = String.valueOf(transfer.getDataTransferu().getYear()+1900) + "-" + String.valueOf(transfer.getDataTransferu().getMonth()+1)
                + "-" + String.valueOf(transfer.getDataTransferu().getDay());
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM TRANSFERY WHERE ID_PILKARZA = " + transfer.getIdPilkarza() + " AND DATA_TRANSFERU = DATE '" + staraData +"'; commit;");
            controller.removeFromTable(controller.getTableTransfery(), transfer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO TRANSFERY VALUES(" + kwota + ", " + klubSprzedajacy + ", "
                    + idPilkarza + ", DATE '" + data + "', '" + klubKupujacy + "')");
            ResultSet rs = statement.executeQuery("select imie || ' ' || nazwisko from PILKARZE WHERE ID_PILKARZA = " + idPilkarza);
            rs.next();
            Transfery addedTransfer = new Transfery(kwotaTransferu, klubSprzedajacy, idPilkarza, dataTransferu, klubKupujacy, rs.getString(1));
            controller.addToTable(controller.getTableTransfery(), addedTransfer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    public void openSearch() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/searchForPlayer.fxml"));

        Stage stage = new Stage();
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.show();

        SFPlayerController sfPlayerController = loader.<SFPlayerController>getController();
        sfPlayerController.connection = connection;
        sfPlayerController.editTransferController = this;
        sfPlayerController.opcja = "edycjaTransfer";
        sfPlayerController.initialize();
    }

}
