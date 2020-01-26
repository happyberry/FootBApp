package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class EditGolController {

    public Connection connection;
    public Controller controller;
    public Gole gol;
    public Mecze mecz;
    public String idPilkarza;

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

    public void editHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        if (mecz == null) {
            labelWarning.setText("[MECZ] Wyszukaj spotkanie");
            labelWarning.setVisible(true);
            return;
        }
        if (idPilkarza == null) {
            labelWarning.setText("[PIŁKARZ] Wyszukaj piłkarza");
            labelWarning.setVisible(true);
            return;
        }

        Integer minuta = 0;
        String min = textFieldMinuta.getText();
        if (min == null || min.equals("")) {
            labelWarning.setText("[MINUTA] Podaj minutę, w której padł gol");
            labelWarning.setVisible(true);
            return;
        }
        else {
            try {
                minuta = Integer.parseInt(min);
            } catch (Exception e) {
                labelWarning.setText("[MINUTA] Podaj liczbę całkowitą");
                labelWarning.setVisible(true);
                return;
            }
        }

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
        if (!radioButtonGospodarze.isSelected() && !radioButtonGoscie.isSelected()) {
            labelWarning.setText("Wybierz stronę, która strzeliła");
            labelWarning.setVisible(true);
            return;
        }

        String okolicznosci = null;
        if (czySamobojczy == 0) {
            okolicznosci = "Nie";
        } else {
            okolicznosci = "Tak";
        }
        if (dlaGospodarzy == 1 && gol.getCzyDlaGospodarzy() == 0) {
            Integer policzoneGole = 0;
            Statement policzGole = connection.createStatement();
            try {
                ResultSet liczbaGoli = policzGole.executeQuery("Select count(*) from gole where CZY_DLA_GOSPODARZY = 1 AND MECZ_ID = " + mecz.getMeczId());
                while (liczbaGoli.next()) {
                    policzoneGole = liczbaGoli.getInt(1);
                }
                if (policzoneGole == mecz.getWynikGospodarzy()) {
                    labelWarning.setText("Wszystkie gole gospodarzy są już wpisane");
                    labelWarning.setVisible(true);
                    return;
                }
                liczbaGoli.close();
            } catch (SQLRecoverableException e) {
                controller.showConnectionLostDialogAndExitApp();
            }

        } else if (dlaGospodarzy == 0 && gol.getCzyDlaGospodarzy() == 1){
            Integer policzoneGole = 0;
            Statement policzGole = connection.createStatement();
            try {
                ResultSet liczbaGoli = policzGole.executeQuery("Select count(*) from gole where CZY_DLA_GOSPODARZY = 0 AND MECZ_ID = " + mecz.getMeczId());
                while (liczbaGoli.next()) {
                    policzoneGole = liczbaGoli.getInt(1);
                }
                if (policzoneGole == mecz.getWynikGosci()) {
                    labelWarning.setText("Wszystkie gole gosci są już wpisane");
                    labelWarning.setVisible(true);
                    return;
                }
                liczbaGoli.close();
            } catch (SQLRecoverableException e) {
                controller.showConnectionLostDialogAndExitApp();
            }
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE GOLE SET MECZ_ID = " + mecz.getMeczId() + ", ID_PILKARZA = " + idPilkarza + ", MINUTA = " +
                    minuta + ", CZY_SAMOBOJCZY = " + czySamobojczy + ", CZY_DLA_GOSPODARZY = " + dlaGospodarzy + " WHERE GOL_ID = " + gol.getGolId());

            ResultSet rs = statement.executeQuery("select imie || ' ' || nazwisko from PILKARZE where ID_PILKARZA = " + idPilkarza);
            rs.next();
            String pilkarz = rs.getString(1);
            controller.removeFromTable(controller.getTableGole(), gol);
            Gole nowyGol = new Gole(gol.getGolId(), mecz.getMeczId(), idPilkarza, minuta, czySamobojczy, dlaGospodarzy, pilkarz,
                    okolicznosci, mecz.getGospodarze(), mecz.getGoscie(), mecz.getData());
            controller.addToTable(controller.getTableGole(), nowyGol);
            rs.close();
        } catch (SQLRecoverableException e) {
            controller.showConnectionLostDialogAndExitApp();
        } catch (SQLException e) {
            if (e.getMessage().contains("ORA-02290") && (minuta < 1 || minuta > 130)) {
                labelWarning.setText("[MINUTA] Podaj poprawną wartość");
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
            statement.executeUpdate("DELETE FROM GOLE WHERE GOL_ID = " + gol.getGolId());
            controller.removeFromTable(controller.getTableGole(), gol);
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

    public void findPilkarz(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/searchForPlayer.fxml"));

        Stage stage = new Stage();
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.show();

        SFPlayerController sfPlayerController = loader.<SFPlayerController>getController();
        sfPlayerController.connection = connection;
        sfPlayerController.editGolController = this;
        sfPlayerController.opcja = "edycjaGol";
        controller.<Pilkarze>reformatDoubleCell(sfPlayerController.tableColumnWartosc);
        controller.<Pilkarze>reformatDoubleCell(sfPlayerController.tableColumnPensja);
        sfPlayerController.fetchInitialData();
        stage.showAndWait();
    }

    public void findMecz(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/searchForGame.fxml"));

        Stage stage = new Stage();
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.show();

        SFGameController sfGameController = loader.<SFGameController>getController();
        sfGameController.connection = connection;
        sfGameController.editGolController = this;
        sfGameController.initializeOptions();
        sfGameController.operation = "Edycja";
        stage.showAndWait();
    }

}
