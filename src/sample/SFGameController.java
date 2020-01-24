package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;

public class SFGameController {

    public Connection connection;
    public InsertGolController insertGolController;
    public EditGolController editGolController;
    public String operation;
    @FXML
    public TextField textFieldNazwa;
    @FXML
    public TextField textFieldData;
    @FXML
    public TableView tableSearch;

    public void initializeOptions() {
        tableSearch.getItems().clear();

        String SQL = "SELECT MECZ_ID, DATA, GOSPODARZE, GOSCIE, WYNIK_GOSPODARZY, WYNIK_GOSCI, ID_SEDZIEGO, IMIE || ' ' || NAZWISKO from MECZE left outer join sedziowie using(id_sedziego)";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = connection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Mecze mecz = new Mecze(rs.getString(1), rs.getDate(2), rs.getString(3), rs.getString(4),
                                rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getString(8));
                        tableSearch.getItems().add(mecz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
        tableSearch.setPlaceholder(new Label("Nie znaleziono pasujących rekordów"));
    }

    public void search() {

        String nazwa = textFieldNazwa.getText();
        String data = textFieldData.getText();
        if (nazwa == null || nazwa.equals("")) {nazwa = "#123456789";}
        if (data == null || data.equals("")) {data = "1900-01-01";}

        tableSearch.getItems().clear();

        String SQL = "SELECT MECZ_ID, DATA, GOSPODARZE, GOSCIE, WYNIK_GOSPODARZY, WYNIK_GOSCI, ID_SEDZIEGO, IMIE || ' ' || NAZWISKO from MECZE left outer join sedziowie using(id_sedziego) where data = DATE '" + data + "' OR GOSCIE like '%" + nazwa +
                "%' OR GOSPODARZE like '%" + nazwa + "%'";
        try {
            ResultSet rs = connection.createStatement().executeQuery(SQL);
            while (rs.next()) {
                Mecze mecz = new Mecze(rs.getString(1), rs.getDate(2), rs.getString(3), rs.getString(4),
                        rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getString(8));
                tableSearch.getItems().add(mecz);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    public void saveSelected(ActionEvent event) {

        if(tableSearch.getSelectionModel().getSelectedItem() == null) return;

        Mecze mecz = (Mecze) tableSearch.getSelectionModel().getSelectedItem();
        if (operation.equals("Edycja")) {
            editGolController.mecz = mecz;
            editGolController.textFieldMecz.setText(mecz.getGospodarze() + "-" + mecz.getGoscie());
        }
        else {
            insertGolController.mecz = mecz;
            insertGolController.textFieldMecz.setText(mecz.getGospodarze() + "-" + mecz.getGoscie());
        }
        tableSearch.getItems().clear();

        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
