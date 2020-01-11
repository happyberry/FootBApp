package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class InsertGolController {

    public Connection connection;
    public Controller controller;
    public String idPilkarza;
    public Mecze mecz;

    @FXML
    public TextField textFieldPilkarz;
    @FXML
    public TextField textFieldMecz;
    @FXML
    public TextField textFieldMinuta;
    @FXML
    public RadioButton radioButtonGospodarze;
    @FXML
    public RadioButton radioButtonGoscie;
    @FXML
    public CheckBox checkBoxSamobojczy;
    @FXML
    public Label labelWarning;

    public void saveHandler(ActionEvent event) throws SQLException {

        String min = textFieldMinuta.getText();
        Integer minuta = Integer.parseInt(min);

        Integer czySamobojczy;
        if (checkBoxSamobojczy.isSelected()) {
            czySamobojczy = 1;
        } else {
            czySamobojczy = 0;
        }

        Integer dlaGospodarzy;
        if (radioButtonGospodarze.isSelected()) {
            dlaGospodarzy = 1;
        } else {
            dlaGospodarzy = 0;
        }
        String okolicznosci = null;
        if (czySamobojczy == 0) {
            okolicznosci = "Nie";
        } else {
            okolicznosci = "Tak";
        }


        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO GOLE VALUES (null, " + mecz.getMeczId() + ", " + idPilkarza + ", " +
                    minuta + ", " + czySamobojczy + ", " + dlaGospodarzy + ")");
            ResultSet rs = statement.executeQuery("select GOLE_GOL_ID_SEQ.currval from dual");
            rs.next();
            String id = rs.getString(1);
            rs = statement.executeQuery("select imie || ' ' || nazwisko from PILKARZE where ID_PILKARZA = " + idPilkarza);
            rs.next();
            Gole addedGol = new Gole(id, mecz.getMeczId(), idPilkarza, minuta, czySamobojczy, dlaGospodarzy, rs.getString(1), okolicznosci, mecz.getGospodarze(), mecz.getGoscie(), mecz.getData());
            controller.addToTable(controller.getTableGole(), addedGol);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    public void findPilkarz(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/searchForPlayer.fxml"));

        Stage stage = new Stage();
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.show();

        SFPlayerController sfPlayerController = loader.<SFPlayerController>getController();
        sfPlayerController.connection = connection;
        sfPlayerController.insertGolController = this;
        sfPlayerController.opcja = "wstawianieGol";
    }

    public void findMecz(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/searchForGame.fxml"));

        Stage stage = new Stage();
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.show();

        SFGameController sfGameController = loader.<SFGameController>getController();
        sfGameController.connection = connection;
        sfGameController.insertGolController = this;
        sfGameController.operation = "wstawianie";
    }

}
