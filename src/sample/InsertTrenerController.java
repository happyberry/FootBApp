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

public class InsertTrenerController {

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
        String SQL = "SELECT NAZWA_KLUBU from KLUBY";
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

    public void saveHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String imie = secondTF.getText();
        if (imie.equals("")) {
            System.out.println("[IMIE] Podaj imię trenera");
            labelWarning.setText("[IMIE] Podaj imię trenera");
            labelWarning.setVisible(true);
            return;
        }
        if (imie.length() > 40) {
            System.out.println("[IMIE] Imię zbyt długie");
            labelWarning.setText("[IMIE] Imię zbyt długie");
            labelWarning.setVisible(true);
            return;
        }
        String nazwisko = thirdTF.getText();
        if (nazwisko.equals("")) {
            System.out.println("[NAZWISKO] Podaj nazwisko trenera");
            labelWarning.setText("[NAZWISKO] Podaj nazwisko trenera");
            labelWarning.setVisible(true);
            return;
        }
        if (nazwisko.length() > 40) {
            System.out.println("[NAZWISKO] Nazwisko zbyt długie");
            labelWarning.setText("[NAZWISKO] Nazwisko zbyt długie");
            labelWarning.setVisible(true);
            return;
        }

        String kraj = (String) comboBoxKraj.getSelectionModel().getSelectedItem();
        if (kraj == null) {
            System.out.println("[KRAJ] Wybierz kraj pochodzenia");
            labelWarning.setText("[KRAJ] Wybierz kraj pochodzenia");
            labelWarning.setVisible(true);
            return;
        }

        String klub = (String) comboBoxClub.getSelectionModel().getSelectedItem();
        if (klub == null) {
            System.out.println("[KLUB] Wybierz klub, który trenuje trener");
            labelWarning.setText("[KLUB] Wybierz klub, który trenuje trener");
            labelWarning.setVisible(true);
            return;
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO Trenerzy VALUES(null, '" + imie + "', '" + nazwisko + "', '"
                    + kraj + "', '" + klub + "')");
            ResultSet rs = statement.executeQuery("select ID_TRENERA_SEQ.currval from dual");
            rs.next();
            Trenerzy addedTrener = new Trenerzy(rs.getString(1), imie, nazwisko, kraj, klub);
            controller.addToTable(controller.getTableTrenerzy(), addedTrener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
