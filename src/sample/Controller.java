package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {

    public Connection mainConnection = null;
    @FXML
    private TableView tablePilkarze;
    @FXML
    private TableView tableKluby;


    public void initializeKluby() throws SQLException {

        TableColumn nameColumn = new TableColumn("Nazwa klubu");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwaKlubu"));
        nameColumn.setPrefWidth(220.0);

        TableColumn yearColumn = new TableColumn("Rok zalozenia");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("rokZalozenia"));
        yearColumn.setPrefWidth(110.0);

        TableColumn ligueColumn = new TableColumn("Nazwa Ligi");
        ligueColumn.setCellValueFactory(new PropertyValueFactory<>("nazwaLigi"));
        ligueColumn.setPrefWidth(290.0);

        tableKluby.getColumns().addAll(nameColumn, yearColumn, ligueColumn);

    }

    public void fillKluby() throws SQLException {

        tableKluby.getItems().clear();
        String SQL = "SELECT * from KLUBY";
        ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
        try {
            while (rs.next()) {
                //Iterate Row
                Kluby klub = new Kluby(rs.getString("nazwa_klubu"), rs.getString("rok_zalozenia"), rs.getString("nazwa_ligi"));
                //String rowdata[] = {klub.getNazwaKlubu(), klub.getRokZalozenia(), klub.getNazwaLigi()};
                //System.out.println(Arrays.toString(rowdata));
                tableKluby.getItems().add(klub);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    public void showKluby(ActionEvent event) throws SQLException {
        fillKluby();
        hideAll();
        tableKluby.setVisible(true);
    }

    public void hideAll() {
        tablePilkarze.setVisible(false);
        tableKluby.setVisible(false);
    }

    public void openEditKlub(ActionEvent event) throws IOException {

        if(tableKluby.getSelectionModel().getSelectedItem() != null) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("editKlub.fxml"));

            Stage stage = new Stage();
            stage.setTitle("Edytuj");
            stage.setScene(new Scene((AnchorPane) loader.load()));
            EditKlubController editKlubController = loader.<EditKlubController>getController();

            editKlubController.connection = mainConnection;
            editKlubController.controller = this;

            Kluby klub = (Kluby) tableKluby.getSelectionModel().getSelectedItem();

            editKlubController.textFieldClubName.setText(klub.getNazwaKlubu());
            editKlubController.oldName = klub.getNazwaKlubu();
            editKlubController.textFieldYear.setText(klub.getRokZalozenia());
            editKlubController.comboBoxLeague.setPromptText(klub.getNazwaLigi());

            stage.show();
        }
    }

    public void hideWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    public void openInsertKlub(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("insertKlub.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertKlubController insertKlubController = loader.<InsertKlubController>getController();

        insertKlubController.connection = mainConnection;
        insertKlubController.controller = this;
        //System.out.println(mainConnection);
        //System.out.println(insertKlubController.connection);

        stage.show();

        /*Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("insertKlub.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Dodaj");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void openMore(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("moreKlub.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Real Madryt");
            stage.setScene(new Scene(root, 600, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializePilkarze() throws SQLException {
        TableColumn idColumn = new TableColumn("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idPilkarza"));
        idColumn.setPrefWidth(5.0);
        idColumn.setVisible(false);

        TableColumn nameColumn = new TableColumn("Imie");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("imie"));
        nameColumn.setPrefWidth(100.0);

        TableColumn surnameColumn = new TableColumn("Nazwisko");
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        surnameColumn.setPrefWidth(100.0);

        TableColumn birthDateColumn = new TableColumn("Data urodzenia");
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("dataUrodzenia"));
        birthDateColumn.setPrefWidth(100.0);

        TableColumn posColumn = new TableColumn("Pozycja");
        posColumn.setCellValueFactory(new PropertyValueFactory<>("pozycja"));
        posColumn.setPrefWidth(80.0);

        TableColumn priceColumn = new TableColumn("Wartość rynkowa");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("wartoscRynkowa"));
        priceColumn.setPrefWidth(100.0);

        TableColumn salaryColumn = new TableColumn("Pensja");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("pensja"));
        salaryColumn.setPrefWidth(80.0);

        TableColumn clubColumn = new TableColumn("Nazwa klubu");
        clubColumn.setCellValueFactory(new PropertyValueFactory<>("nazwaKlubu"));
        clubColumn.setPrefWidth(100.0);

        tablePilkarze.getColumns().addAll(idColumn, nameColumn, surnameColumn, birthDateColumn, posColumn, priceColumn, salaryColumn, clubColumn);
    }

    public void fillPilkarze() throws SQLException {

        tablePilkarze.getItems().clear();
        String SQL = "SELECT * from PILKARZE";
        ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
        try {
            while (rs.next()) {

                Pilkarze kopacz = new Pilkarze(rs.getString("id_pilkarza"), rs.getString("imie"),
                        rs.getString("nazwisko"), rs.getDate("data_urodzenia"), rs.getString("pozycja"),
                        rs.getString("wartosc_rynkowa"), rs.getString("pensja"), rs.getString("nazwa_klubu"));

                tablePilkarze.getItems().add(kopacz);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }

        /*tablePilkarze.getItems().clear();
        ObservableList<ObservableList> data;
        data = FXCollections.observableArrayList();
        String SQL = "SELECT * from PILKARZE";
        //ResultSet
        ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
        try {
            /********************************
             * Data added to ObservableList *
             ********************************/
        /*
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate
                    String oneRow = rs.getString(i);
                    if (oneRow == null) oneRow = "null";
                    row.add(oneRow);
                }
                System.out.println("Row [1] added " + row);
                data.add(row);
            }
            tablePilkarze.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }*/
    }

    public void showPilkarze(ActionEvent event) throws SQLException {
        fillPilkarze();
        hideAll();
        tablePilkarze.setVisible(true);
    }

}
