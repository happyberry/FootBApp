package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class InsertTransferController {

    public Connection connection;
    public Controller controller;

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

    public void saveHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String kwota = textFieldKwota.getText();
        Double kwotaTransferu = Double.parseDouble(kwota);
        String klubKupujacy = comboBoxBuy.getSelectionModel().getSelectedItem().toString();
        String klubSprzedajacy = comboBoxSell.getSelectionModel().getSelectedItem().toString();
        String year = (String) comboBoxYear.getSelectionModel().getSelectedItem();
        String month = (String) comboBoxMonth.getSelectionModel().getSelectedItem();
        String day = (String) comboBoxDay.getSelectionModel().getSelectedItem();
        String data = year + "-" + month + "-" + day;
        String idPilkarza = textFieldID.getText();
        Date dataTransferu = new java.sql.Date(Integer.valueOf(year)-1900, Integer.valueOf(month)-1, Integer.valueOf(day));
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO TRANSFERY VALUES(" + kwota + ", '" + klubSprzedajacy + "', "
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
        sfPlayerController.controller = this;
    }

}
