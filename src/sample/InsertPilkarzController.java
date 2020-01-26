package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;

public class InsertPilkarzController {

    public Connection connection;
    public Controller controller;
    @FXML
    public ComboBox comboBoxClub;
    @FXML
    public ComboBox comboBoxPos;
    @FXML
    public ComboBox comboBoxBDay;
    @FXML
    public ComboBox comboBoxBMonth;
    @FXML
    public ComboBox comboBoxBYear;
    @FXML
    public TextField textFieldImie;
    @FXML
    public TextField textFieldNazwisko;
    @FXML
    public TextField textFieldWartosc;
    @FXML
    public TextField textFieldPensja;
    @FXML
    public Label labelWarning;


    public void initializeOptions() {

        comboBoxClub.getItems().clear();
        comboBoxClub.getItems().add("");
        String SQL = "SELECT NAZWA_klubu from KLUBY ORDER BY NAZWA_KLUBU";

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        comboBoxClub.getItems().add(rs.getString("nazwa_klubu"));
                    }
                    rs.close();
                } catch (SQLRecoverableException e) {
                    controller.showConnectionLostDialogAndExitApp();
                }
                catch(SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Club Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void saveHandler(ActionEvent event) throws SQLException {

        String imie = textFieldImie.getText();
        if (imie.equals("")) {
            labelWarning.setText("[IMIE] Podaj imie zawodnika");
            labelWarning.setVisible(true);
            return;
        }
        String nazwisko = textFieldNazwisko.getText();
        if (nazwisko.equals("")) {
            labelWarning.setText("[NAZWISKO] Podaj nazwisko zawodnika");
            labelWarning.setVisible(true);
            return;
        }
        String year = (String) comboBoxBYear.getSelectionModel().getSelectedItem();
        String month = (String) comboBoxBMonth.getSelectionModel().getSelectedItem();
        String day = (String) comboBoxBDay.getSelectionModel().getSelectedItem();
        if (year == null || month == null || day == null) {
            labelWarning.setText("[DATA URODZENIA] Podaj pełną datę urodzenia");
            labelWarning.setVisible(true);
            return;
        }
        Date date = new java.sql.Date(Integer.valueOf(year)-1900, Integer.valueOf(month)-1, Integer.valueOf(day));
        String pozycje = (String) comboBoxPos.getSelectionModel().getSelectedItem();
        String wartosc = textFieldWartosc.getText().replaceAll(" ", "");
        wartosc = wartosc.replaceFirst(",", ".");
        String pensja = null;
        if (!textFieldPensja.getText().equals("")) {
            pensja = textFieldPensja.getText().replaceAll(" ", "");
            pensja = pensja.replaceFirst(",", ".");
        }
        String klub = null;
        if (comboBoxClub.getSelectionModel().getSelectedItem() != null) {
            if (comboBoxClub.getSelectionModel().getSelectedItem().equals("")) {klub = null;}
            else {
                klub = comboBoxClub.getSelectionModel().getSelectedItem().toString();
                klub = klub.replaceAll("'", "''");
                klub = "'" + klub + "'";
            }
        }
        if (imie.length() > 40) {
            labelWarning.setText("[IMIE] Imię zbyt długie, skróć do 40 znaków");
            labelWarning.setVisible(true);
            return;
        }
        if (nazwisko.length() > 40) {
            labelWarning.setText("[NAZWISKO] Nazwisko zbyt długie, skróć do 40 znaków");
            labelWarning.setVisible(true);
            return;
        }
        if (Integer.parseInt(month) == 2) {
            if (Integer.parseInt(day) > 28) {
                if (Integer.parseInt(day) > 29 || Integer.parseInt(year) % 4 != 0) {
                    labelWarning.setText("[DATA URODZENIA] Luty ma mniej niż 30 dni");
                    labelWarning.setVisible(true);
                    return;
                }
            }
        }
        if (Integer.parseInt(month) == 4 || Integer.parseInt(month) == 6 || Integer.parseInt(month) == 9 || Integer.parseInt(month) == 11) {
            if (day.equals("31")) {
                labelWarning.setText("[DATA URODZENIA] Błędny dzień miesiąca");
                labelWarning.setVisible(true);
                return;
            }
        }
        if (pozycje == null) {
            labelWarning.setText("[POZYCJA] Podaj pozycję, na której gra piłkarz");
            labelWarning.setVisible(true);
            return;
        }

        if (wartosc.equals("")) {
            labelWarning.setText("[WARTOŚĆ RYNKOWA] Podaj wartość rynkową piłkarza");
            labelWarning.setVisible(true);
            return;
        }

        double wartoscRynkowa;
        try {
            wartoscRynkowa = Double.parseDouble(wartosc);
        } catch (NumberFormatException e) {
            labelWarning.setText("[WARTOŚĆ RYNKOWA] Błędny format wartości rynkowej");
            labelWarning.setVisible(true);
            return;
        }
        if (wartoscRynkowa > 9999999999.99 || wartoscRynkowa < 0) {
            labelWarning.setText("[WARTOŚĆ RYNKOWA] Błędna wartość rynkowa");
            labelWarning.setVisible(true);
            return;
        }
        if (pensja != null) {
            double doublePensja;
            try {
                doublePensja = Double.parseDouble(pensja);
            } catch (NumberFormatException e) {
                labelWarning.setText("[PENSJA] Błędny format pensji");
                labelWarning.setVisible(true);
                return;
            }
            if (doublePensja > 9999999999.99 || doublePensja < 0) {
                labelWarning.setText("[PENSJA] Błędna wartość pensji");
                labelWarning.setVisible(true);
                return;
            }
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO PILKARZE VALUES (null, '" + imie.replaceAll("'", "''") + "', '" + nazwisko.replaceAll("'", "''") + "', Date '" +
                    year + "-" + month + "-" + day + "', '" + pozycje + "', " + wartosc + ", " + pensja + ", " + klub + ")");
            ResultSet rs = statement.executeQuery("select id_pilkarza_seq.currval from dual");
            rs.next();
            if (pensja == null) pensja = "0";
            if (klub == null) klub = "  ";
            Pilkarze addedPilkarz = new Pilkarze(rs.getString(1), imie, nazwisko, date, pozycje, Double.parseDouble(wartosc),
                    Double.parseDouble(pensja), klub.substring(1, klub.length() - 1));
            controller.addToTable(controller.getTablePilkarze(), addedPilkarz);
            rs.close();
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
