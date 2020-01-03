package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;

public class EditPilkarzController {

    public Connection connection;
    public Controller controller;
    public Pilkarze pilkarz;

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

        System.out.println(connection);
        comboBoxClub.getItems().clear();
        String SQL = "SELECT NAZWA_klubu from KLUBY";

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        comboBoxClub.getItems().add(rs.getString("nazwa_klubu"));
                    }
                }
                catch(SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error on Picking Club Names");
                }
            }
        };
        new Thread(r).start();
    }

    public void deleteHandler(ActionEvent event) throws SQLException {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM PILKARZE WHERE ID_PILKARZA = " + pilkarz.getIdPilkarza());
            controller.removeFromTable(controller.getTablePilkarze(), pilkarz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();

    }

    public void editHandler(ActionEvent event) throws SQLException {

        labelWarning.setVisible(false);
        String imie = textFieldImie.getText();
        if (imie.equals("")) {
            System.out.println("[IMIE] Podaj imie zawodnika");
            return;
        }
        String nazwisko = textFieldNazwisko.getText();
        if (nazwisko.equals("")) {
            System.out.println("[NAZWISKO] Podaj nazwisko zawodnika");
            return;
        }
        String year = (String) comboBoxBYear.getSelectionModel().getSelectedItem();
        String month = (String) comboBoxBMonth.getSelectionModel().getSelectedItem();
        String day = (String) comboBoxBDay.getSelectionModel().getSelectedItem();
        if (year == null) {
            year = comboBoxBYear.getPromptText();
        }
        if (month == null) {
            month = comboBoxBMonth.getPromptText();
            if(month.length() == 1){month = '0' + month;}
        }
        if (day == null) {
            day = comboBoxBDay.getPromptText();
            if(day.length() == 1){day = '0' + day;}
        }
        Date date = new java.sql.Date(Integer.valueOf(year)-1900, Integer.valueOf(month)-1, Integer.valueOf(day));
        String pozycja = (String) comboBoxPos.getSelectionModel().getSelectedItem();
        if(pozycja == null){
            pozycja = comboBoxPos.getPromptText();
        }
        String wartosc = textFieldWartosc.getText().replaceAll(" ", "");
        wartosc = wartosc.replaceFirst(",", ".");
        String pensja = null;
        if (!textFieldPensja.getText().equals("")) {
            pensja = textFieldPensja.getText().replaceAll(" ", "");
            pensja = pensja.replaceFirst(",", ".");
        }
        String klub = (String) comboBoxClub.getSelectionModel().getSelectedItem();
        if(klub == null){
            klub = comboBoxClub.getPromptText();
        }
        klub = "'" + klub + "'";

        if (imie.length() > 40) {
            System.out.println("[IMIE] Imię zbyt długie, skróć do 40 znaków");
            return;
        }
        if (nazwisko.length() > 40) {
            System.out.println("[NAZWISKO] Nazwisko zbyt długie, skróć do 40 znaków");
            return;
        }
        if (Integer.parseInt(month) == 2) {
            if (Integer.parseInt(day) > 28) {
                if (Integer.parseInt(day) > 29 || Integer.parseInt(year) % 4 != 0) {
                    System.out.println("[DATA URODZENIA] Luty ma mniej niż 30 dni");
                    return;
                }
            }
        }
        if (Integer.parseInt(month) == 4 || Integer.parseInt(month) == 6 || Integer.parseInt(month) == 9 || Integer.parseInt(month) == 11) {
            if (day.equals("31")) {
                System.out.println("[DATA URODZENIA] Błędny dzień miesiąca");
                return;
            }
        }
        double wartoscRynkowa;
        try {
            wartoscRynkowa = Double.parseDouble(wartosc);
        } catch (NumberFormatException e) {
            System.out.println("[WARTOŚĆ RYNKOWA] Błędny format wartości rynkowej");
            return;
        }
        if (wartoscRynkowa > 9999999999.99 || wartoscRynkowa < 0) {
            System.out.println("[WARTOŚĆ RYNKOWA] Błędna wartość rynkowa");
            return;
        }
        if (pensja != null) {
            double doublePensja;
            try {
                doublePensja = Double.parseDouble(pensja);
            } catch (NumberFormatException e) {
                System.out.println("[PENSJA] Błędny format pensji");
                return;
            }
            if (doublePensja > 9999999999.99 || doublePensja < 0) {
                System.out.println("[PENSJA] Błędna wartość pensji");
                return;
            }
        }

        System.out.println(imie + " " + nazwisko + " " + year + "-" + month + "-" + day + " " + pozycja + " " + wartosc + " " + pensja + " " + klub);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE PILKARZE SET imie = '" + imie + "', nazwisko = '" + nazwisko + "', DATA_URODZENIA = DATE '"
                    + year + "-" + month + "-" + day + "', POZYCJA = '" + pozycja + "', WARTOSC_RYNKOWA = " + wartosc + ", PENSJA = " + pensja
                    + ", NAZWA_KLUBU = " + klub + "  WHERE ID_PILKARZA = " + pilkarz.getIdPilkarza());
            Pilkarze nowyPilkarz = new Pilkarze(pilkarz.getIdPilkarza(), imie, nazwisko, date, pozycja, wartoscRynkowa, Double.parseDouble(pensja), klub.substring(1,klub.length()-1));
            controller.removeFromTable(controller.getTablePilkarze(), pilkarz);
            controller.addToTable(controller.getTablePilkarze(), nowyPilkarz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
