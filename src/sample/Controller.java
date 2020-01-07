package sample;

import javafx.beans.binding.Bindings;
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
    @FXML
    private TableView tableGole;
    @FXML
    private TableView tableMecze;
    @FXML
    private TableView tableSedziowie;
    @FXML
    private TableView tableStadiony;
    @FXML
    private TableView tableTransfery;
    @FXML
    private TableView tableTrenerzy;
    @FXML
    private TableView tableWlasciciele;
    @FXML
    private TableView tableLigi;
    @FXML
    private TableView tableSearch;
    @FXML
    private TableColumn tableColumnWartosc;
    @FXML
    private ComboBox comboBoxTable;
    @FXML
    private AnchorPane anchorPaneSearch;
    @FXML
    private Label labelA, labelB, labelC, labelD;
    @FXML
    private TextField textFieldA, textFieldB, textFieldC, textFieldD;

    private boolean pilkarzeJuzWczytani = false;
    private boolean klubyJuzWczytane = false;
    private boolean ligiJuzWczytane = false;
    private boolean goleJuzWczytane = false;
    private boolean meczeJuzWczytane = false;
    private boolean sedziowieJuzWczytani = false;
    private boolean trenerzyJuzWczytani = false;
    private boolean stadionyJuzWczytane = false;
    private boolean transferyJuzWczytane = false;
    private boolean wlascicieleJuzWczytani = false;

    public TableView getTableKluby() {
        return tableKluby;
    }

    public TableView getTablePilkarze() {
        return tablePilkarze;
    }

    public TableView getTableGole() {
        return tableGole;
    }

    public TableView getTableMecze() {
        return tableMecze;
    }

    public TableView getTableSedziowie() {
        return tableSedziowie;
    }

    public TableView getTableStadiony() {
        return tableStadiony;
    }

    public TableView getTableTransfery() {
        return tableTransfery;
    }

    public TableView getTableTrenerzy() {
        return tableTrenerzy;
    }

    public TableView getTableWlasciciele() {
        return tableWlasciciele;
    }

    public TableView getTableLigi() {
        return tableLigi;
    }

    public void fillKluby() throws SQLException {

        if (klubyJuzWczytane) return;
        klubyJuzWczytane = true;
        String SQL = "SELECT * from KLUBY";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        //Iterate Row
                        Kluby klub = new Kluby(rs.getString("nazwa_klubu"), rs.getInt("rok_zalozenia"), rs.getString("nazwa_ligi"));
                        //String rowdata[] = {klub.getNazwaKlubu(), klub.getRokZalozenia(), klub.getNazwaLigi()};
                        //System.out.println(Arrays.toString(rowdata));
                        tableKluby.getItems().add(klub);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void openEditKlub(ActionEvent event) throws IOException {

        if(tableKluby.getSelectionModel().getSelectedItem() == null) {return;}


        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editKlub.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Edytuj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        EditKlubController editKlubController = loader.<EditKlubController>getController();

        editKlubController.connection = mainConnection;
        editKlubController.controller = this;

        Kluby klub = (Kluby) tableKluby.getSelectionModel().getSelectedItem();
        editKlubController.klub = klub;
        editKlubController.initializeOptions();

        editKlubController.textFieldClubName.setText(klub.getNazwaKlubu());
        editKlubController.oldName = klub.getNazwaKlubu();
        editKlubController.textFieldYear.setText(String.valueOf(klub.getRokZalozenia()));
        editKlubController.comboBoxLeague.setPromptText(klub.getNazwaLigi());

        stage.show();

    }

    public void openInsertKlub(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertKlub.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertKlubController insertKlubController = loader.<InsertKlubController>getController();

        insertKlubController.connection = mainConnection;
        insertKlubController.controller = this;
        insertKlubController.initializeOptions();
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

    public void openMoreKlub(ActionEvent event) throws IOException {
        if (tableKluby.getSelectionModel().getSelectedItem() == null) {return;}

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/moreKlub.fxml"));

        Stage stage = new Stage();
        Kluby klub = (Kluby) tableKluby.getSelectionModel().getSelectedItem();
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.setTitle(klub.getNazwaKlubu() + " - dodatkowe informacje");
        stage.show();

        MoreKlubController moreKlubController = loader.<MoreKlubController>getController();

        String SQL = "SELECT * from PILKARZE where NAZWA_KLUBU = '" + klub.getNazwaKlubu() + "'";
        moreKlubController.tablePilkarze.getItems().clear();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Pilkarze kopacz = new Pilkarze(rs.getString("id_pilkarza"), rs.getString("imie"),
                                rs.getString("nazwisko"), rs.getDate("data_urodzenia"), rs.getString("pozycja"),
                                rs.getDouble("wartosc_rynkowa"), rs.getDouble("pensja"), rs.getString("nazwa_klubu"));

                        moreKlubController.tablePilkarze.getItems().add(kopacz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();

    }

    public void fillPilkarze() throws SQLException {

        if (pilkarzeJuzWczytani) return;
        pilkarzeJuzWczytani = true;

        String SQL = "SELECT * from PILKARZE";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Pilkarze kopacz = new Pilkarze(rs.getString("id_pilkarza"), rs.getString("imie"),
                                rs.getString("nazwisko"), rs.getDate("data_urodzenia"), rs.getString("pozycja"),
                                rs.getDouble("wartosc_rynkowa"), rs.getDouble("pensja"), rs.getString("nazwa_klubu"));

                        tablePilkarze.getItems().add(kopacz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();


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

    public void openEditPilkarz(ActionEvent event) throws IOException {

        if(tablePilkarze.getSelectionModel().getSelectedItem() != null) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editPilkarz.fxml"));

            Stage stage = new Stage();
            stage.setTitle("Edytuj");
            stage.setScene(new Scene((AnchorPane) loader.load()));
            EditPilkarzController editPilkarzController = loader.<EditPilkarzController>getController();

            editPilkarzController.connection = mainConnection;
            editPilkarzController.controller = this;

            Pilkarze pilkarz = (Pilkarze) tablePilkarze.getSelectionModel().getSelectedItem();
            editPilkarzController.pilkarz = pilkarz;
            editPilkarzController.initializeOptions();

            editPilkarzController.textFieldImie.setText(pilkarz.getImie());
            editPilkarzController.textFieldNazwisko.setText(pilkarz.getNazwisko());
            editPilkarzController.comboBoxBYear.setPromptText(String.valueOf(pilkarz.getDataUrodzenia().getYear()+1900));
            editPilkarzController.comboBoxBMonth.setPromptText(String.valueOf(pilkarz.getDataUrodzenia().getMonth()+1));
            editPilkarzController.comboBoxBDay.setPromptText(String.valueOf(pilkarz.getDataUrodzenia().getDate()));
            editPilkarzController.comboBoxPos.setPromptText(pilkarz.getPozycja());
            editPilkarzController.textFieldWartosc.setText(String.valueOf(pilkarz.getWartoscRynkowa()));
            editPilkarzController.textFieldPensja.setText(String.valueOf(pilkarz.getPensja().toString()));
            editPilkarzController.comboBoxClub.setPromptText(pilkarz.getNazwaKlubu());

            stage.show();
        }
    }

    public void openInsertPilkarz(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertPilkarz.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertPilkarzController insertPilkarzController = loader.<InsertPilkarzController>getController();

        insertPilkarzController.connection = mainConnection;
        insertPilkarzController.controller = this;
        insertPilkarzController.initializeOptions();

        stage.show();
    }

    public void fillMecze() throws SQLException {

        if (meczeJuzWczytane) return;
        meczeJuzWczytane = true;
        String SQL = "SELECT * from MECZE";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        //Iterate Row
                        Mecze mecz = new Mecze(rs.getString(1), rs.getDate(2), rs.getString(3),
                                rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getString(7));
                        tableMecze.getItems().add(mecz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void openMoreMecz(ActionEvent event) throws IOException {
        if (tableMecze.getSelectionModel().getSelectedItem() == null) {return;}
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/moreMecz.fxml"));

        Stage stage = new Stage();
        Mecze mecz = (Mecze) tableMecze.getSelectionModel().getSelectedItem();
        stage.setTitle("Dodatkowe informacje");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.show();

        MoreMeczController moreMeczController = loader.<MoreMeczController>getController();

        String SQL = "SELECT * from GOLE where MECZ_ID = " + mecz.getMeczId();
        moreMeczController.tableGole.getItems().clear();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Gole gol = new Gole(rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getInt(4), rs.getString(5), rs.getString(6));
                        moreMeczController.tableGole.getItems().add(gol);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();

    }

    public void fillLigi() throws SQLException {

        if (ligiJuzWczytane) return;
        ligiJuzWczytane = true;
        String SQL = "SELECT * from LIGI";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        //Iterate Row
                        Ligi liga = new Ligi(rs.getString(1), rs.getString(2));
                        tableLigi.getItems().add(liga);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void openEditLiga(ActionEvent event) throws IOException {

        if(tableLigi.getSelectionModel().getSelectedItem() != null) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editLiga.fxml"));

            Stage stage = new Stage();
            stage.setTitle("Edytuj");
            stage.setScene(new Scene((AnchorPane) loader.load()));
            EditLigaController editLigaController = loader.<EditLigaController>getController();

            editLigaController.connection = mainConnection;
            editLigaController.controller = this;

            Ligi liga = (Ligi) tableLigi.getSelectionModel().getSelectedItem();
            editLigaController.liga = liga;

            editLigaController.textFieldName.setText(liga.getNazwaLigi());
            editLigaController.comboBoxKraj.setPromptText(liga.getKraj());

            stage.show();
        }
    }

    public void openInsertLiga(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertLiga.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertLigaController insertLigaController = loader.<InsertLigaController>getController();

        insertLigaController.connection = mainConnection;
        insertLigaController.controller = this;

        stage.show();

    }

    public void openMoreLiga(ActionEvent event) throws IOException {

        if (tableLigi.getSelectionModel().getSelectedItem() == null) {return;}

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/moreLiga.fxml"));

        Stage stage = new Stage();
        Ligi liga = (Ligi) tableLigi.getSelectionModel().getSelectedItem();
        stage.setTitle(liga.getNazwaLigi() + " - dodatkowe informacje");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.show();

        MoreLigaController moreLigaController = loader.<MoreLigaController>getController();

        String SQL = "SELECT * from KLUBY where NAZWA_LIGI = '" + liga.getNazwaLigi() + "'";
        moreLigaController.tableKluby.getItems().clear();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Kluby klub = new Kluby(rs.getString("nazwa_klubu"), rs.getInt("rok_zalozenia"), rs.getString("nazwa_ligi"));
                        moreLigaController.tableKluby.getItems().add(klub);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void fillSedziowie() throws SQLException {

        if (sedziowieJuzWczytani) return;
        sedziowieJuzWczytani = true;
        String SQL = "SELECT * from SEDZIOWIE";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        //Iterate Row
                        Sedziowie sedzia = new Sedziowie(rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getInt(4), rs.getString(5));
                        tableSedziowie.getItems().add(sedzia);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void openEditSedzia(ActionEvent event) throws IOException {

        if (tableSedziowie.getSelectionModel().getSelectedItem() == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editSedzia.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Edytuj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        EditSedziaController editSedziaController = loader.<EditSedziaController>getController();

        editSedziaController.connection = mainConnection;
        editSedziaController.controller = this;

        Sedziowie sedzia = (Sedziowie) tableSedziowie.getSelectionModel().getSelectedItem();
        editSedziaController.sedzia = sedzia;

        editSedziaController.textFieldImie.setText(sedzia.getImie());
        editSedziaController.textFieldNazwisko.setText(sedzia.getNazwisko());
        editSedziaController.textFieldWiek.setText(String.valueOf(sedzia.getWiek()));
        editSedziaController.comboBoxKraj.setPromptText(sedzia.getPochodzenie());

        stage.show();
    }

    public void openInsertSedzia(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertSedzia.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertSedziaController insertSedziaController = loader.<InsertSedziaController>getController();

        insertSedziaController.connection = mainConnection;
        insertSedziaController.controller = this;

        stage.show();
    }

    public void fillGole() throws SQLException {

        if (goleJuzWczytane) return;
        goleJuzWczytane = true;
        String SQL = "SELECT * from GOLE";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        //Iterate Row
                        Gole gol = new Gole(rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getInt(4), rs.getString(5), rs.getString(6));
                        tableGole.getItems().add(gol);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void fillStadiony() {

        if (stadionyJuzWczytane) return;
        stadionyJuzWczytane = true;
        String SQL = "SELECT * from STADIONY";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Stadiony stadion = new Stadiony(rs.getString(1), rs.getInt(2), rs.getInt(3),
                                rs.getString(4), rs.getString(5));
                        tableStadiony.getItems().add(stadion);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void openEditStadion(ActionEvent event) throws IOException {

        if (tableStadiony.getSelectionModel().getSelectedItem() == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editStadion.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Edytuj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        EditStadionController editStadionController = loader.<EditStadionController>getController();

        editStadionController.connection = mainConnection;
        editStadionController.controller = this;

        Stadiony stadion = (Stadiony) tableStadiony.getSelectionModel().getSelectedItem();
        editStadionController.stadion = stadion;

        editStadionController.initializeOptions();
        editStadionController.firstTF.setText(stadion.getNazwa());
        editStadionController.secondTF.setText(String.valueOf(stadion.getRokZbudowania()));
        editStadionController.thirdTF.setText(String.valueOf(stadion.getPojemnosc()));
        editStadionController.fourthTF.setText(stadion.getMiasto());
        editStadionController.comboBoxClub.setPromptText(stadion.getNazwaKlubu());

        stage.show();
    }

    public void openInsertStadion(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertStadion.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertStadionController insertStadionController = loader.<InsertStadionController>getController();

        insertStadionController.connection = mainConnection;
        insertStadionController.controller = this;
        insertStadionController.initializeOptions();

        stage.show();
    }

    public void fillTrenerzy() throws SQLException {

        if (trenerzyJuzWczytani) return;
        trenerzyJuzWczytani = true;
        String SQL = "SELECT * from TRENERZY";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Trenerzy trener = new Trenerzy(rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getString(5));
                        tableTrenerzy.getItems().add(trener);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void openEditTrener(ActionEvent event) throws IOException {

        if (tableTrenerzy.getSelectionModel().getSelectedItem() == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editTrener.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Edytuj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        EditTrenerController editTrenerController = loader.<EditTrenerController>getController();

        editTrenerController.connection = mainConnection;
        editTrenerController.controller = this;

        Trenerzy trener = (Trenerzy) tableTrenerzy.getSelectionModel().getSelectedItem();
        editTrenerController.trener = trener;

        editTrenerController.initializeOptions();
        editTrenerController.secondTF.setText(trener.getImie());
        editTrenerController.thirdTF.setText(trener.getNazwisko());
        editTrenerController.comboBoxKraj.setPromptText(trener.getPochodzenie());
        editTrenerController.comboBoxClub.setPromptText(trener.getNazwaKlubu());

        stage.show();
    }

    public void openInsertTrener(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertTrener.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertTrenerController insertTrenerController = loader.<InsertTrenerController>getController();

        insertTrenerController.connection = mainConnection;
        insertTrenerController.controller = this;
        insertTrenerController.initializeOptions();

        stage.show();
    }

    public void fillWlasciciele() throws SQLException {

        if (wlascicieleJuzWczytani) return;
        wlascicieleJuzWczytani = true;
        String SQL = "SELECT * from WLASCICIELE";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Wlasciciele wlasciciel = new Wlasciciele(rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getDouble(4), rs.getString(5));
                        tableWlasciciele.getItems().add(wlasciciel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void openEditWlasciciel(ActionEvent event) throws IOException {

        if (tableWlasciciele.getSelectionModel().getSelectedItem() == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editWlasciciel.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Edytuj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        EditWlascicielController editWlascicielController = loader.<EditWlascicielController>getController();

        editWlascicielController.connection = mainConnection;
        editWlascicielController.controller = this;

        Wlasciciele wlasciciel = (Wlasciciele) tableWlasciciele.getSelectionModel().getSelectedItem();
        editWlascicielController.wlasciciel = wlasciciel;

        editWlascicielController.initializeOptions();
        editWlascicielController.secondTF.setText(wlasciciel.getImie());
        editWlascicielController.thirdTF.setText(wlasciciel.getNazwisko());
        editWlascicielController.fourthTF.setText(String.valueOf(wlasciciel.getMajatek()));
        editWlascicielController.comboBoxClub.setPromptText(wlasciciel.getNazwaKlubu());

        stage.show();
    }

    public void openInsertWlasciciel(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertWlasciciel.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertWlascicielController insertWlascicielController = loader.<InsertWlascicielController>getController();

        insertWlascicielController.connection = mainConnection;
        insertWlascicielController.controller = this;
        insertWlascicielController.initializeOptions();

        stage.show();
    }

    public void fillTransfery() throws SQLException {

        if (transferyJuzWczytane) return;
        transferyJuzWczytane = true;

        String SQL = "SELECT KWOTA_TRANSFERU, KLUB_SPRZEDAJACY, TRANSFERY.ID_PILKARZA, DATA_TRANSFERU, KLUB_KUPUJACY, " +
                " P.IMIE || ' ' || P.NAZWISKO from TRANSFERY JOIN PILKARZE P on TRANSFERY.ID_PILKARZA = P.ID_PILKARZA";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Transfery transfer = new Transfery(rs.getDouble(1), rs.getString(2), rs.getString(3),
                                rs.getDate(4), rs.getString(5), rs.getString(6));
                        tableTransfery.getItems().add(transfer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void addToTable(TableView tabela, Object byt) {
        tabela.getItems().add(byt);
    }

    public void removeFromTable(TableView tabela, Object byt) {
        tabela.getItems().remove(byt);
    }

    public void fillSearchEngine (ActionEvent event) {

        String table = (String) comboBoxTable.getSelectionModel().getSelectedItem();
        if (table == null) {return;}

        for (Node node : anchorPaneSearch.getChildren().filtered(n -> n instanceof Label)) {
            node.setVisible(false);
        }
        for (Node node : anchorPaneSearch.getChildren().filtered(n -> n instanceof TextField)) {
            node.setVisible(false);
        }

        textFieldA.setPromptText("");
        textFieldC.setPromptText("");
        textFieldA.clear();
        textFieldB.clear();
        textFieldC.clear();
        textFieldD.clear();

        if(table.equals("Klub")){

            labelA.setText("Nazwa klubu");
            labelB.setText("Liga");

            labelA.setVisible(true);
            labelB.setVisible(true);
            textFieldA.setVisible(true);
            textFieldB.setVisible(true);

        }

        if (table.equals("Liga")){

            labelA.setText("Nazwa ligi");
            labelB.setText("Kraj");

            labelA.setVisible(true);
            labelB.setVisible(true);
            textFieldA.setVisible(true);
            textFieldB.setVisible(true);

        }

        if (table.equals("Mecz")) {

            labelA.setText("Data");
            labelB.setText("Nazwa klubu");

            labelA.setVisible(true);
            labelB.setVisible(true);
            textFieldA.setVisible(true);
            textFieldA.setPromptText("YYYY-MM-DD");
            textFieldB.setVisible(true);
        }

        if (table.equals("Piłkarz")) {

            labelA.setText("Nazwisko");
            labelB.setText("Nazwa klubu");
            labelC.setText("Data urodzenia");
            labelD.setText("Pozycja");

            labelA.setVisible(true);
            labelB.setVisible(true);
            labelC.setVisible(true);
            labelD.setVisible(true);
            textFieldA.setVisible(true);
            textFieldB.setVisible(true);
            textFieldC.setVisible(true);
            textFieldC.setPromptText("YYYY-MM-DD");
            textFieldD.setVisible(true);

        }

        if (table.equals("Sędzia")) {

            labelA.setText("Nazwisko");
            labelB.setText("Kraj");

            labelA.setVisible(true);
            labelB.setVisible(true);
            textFieldA.setVisible(true);
            textFieldB.setVisible(true);

        }

        if (table.equals("Stadion")) {

            labelA.setText("Nazwa");
            labelB.setText("Miasto");
            labelC.setText("Nazwa klubu");

            labelA.setVisible(true);
            labelB.setVisible(true);
            labelC.setVisible(true);
            textFieldA.setVisible(true);
            textFieldB.setVisible(true);
            textFieldC.setVisible(true);
        }

        if (table.equals("Trener")) {

            labelA.setText("Nazwisko");
            labelB.setText("Nazwa klubu");

            labelA.setVisible(true);
            labelB.setVisible(true);
            textFieldA.setVisible(true);
            textFieldB.setVisible(true);
        }

        if (table.equals("Właściciel")) {
            labelA.setText("Nazwisko");
            labelB.setText("Nazwa klubu");

            labelA.setVisible(true);
            labelB.setVisible(true);
            textFieldA.setVisible(true);
            textFieldB.setVisible(true);
        }

    }

    public void fillSearchEnginePrototype (ActionEvent event) {
        String table = (String) comboBoxTable.getSelectionModel().getSelectedItem();

        if (table == null) {return;}

        if(table.equals("Klub")){
            Label labelNazwa = new Label("Nazwa klubu");
            labelNazwa.setLayoutX(30);
            labelNazwa.setLayoutY(60);
            Label labelLiga = new Label("Liga");
            labelLiga.setLayoutX(30);
            labelLiga.setLayoutY(100);

            TextField textFieldNazwa = new TextField();
            textFieldNazwa.setLayoutX(140);
            textFieldNazwa.setLayoutY(56);
            TextField textFieldLiga = new TextField();
            textFieldLiga.setLayoutX(140);
            textFieldLiga.setLayoutY(96);

            anchorPaneSearch.getChildren().removeIf(n -> n instanceof TextField);
            anchorPaneSearch.getChildren().removeIf(n -> n instanceof Label);
            anchorPaneSearch.getChildren().addAll(labelLiga, labelNazwa, textFieldLiga, textFieldNazwa);
        }

        if (table.equals("Liga")){
            Label labelNazwa = new Label("Nazwa ligi");
            labelNazwa.setLayoutX(30);
            labelNazwa.setLayoutY(60);
            Label labelKraj = new Label("Kraj");
            labelKraj.setLayoutX(30);
            labelKraj.setLayoutY(100);

            TextField textFieldNazwa = new TextField();
            textFieldNazwa.setLayoutX(140);
            textFieldNazwa.setLayoutY(56);
            TextField textFieldKraj = new TextField();
            textFieldKraj.setLayoutX(140);
            textFieldKraj.setLayoutY(96);

            anchorPaneSearch.getChildren().removeIf(n -> n instanceof TextField);
            anchorPaneSearch.getChildren().removeIf(n -> n instanceof Label);
            anchorPaneSearch.getChildren().addAll(labelNazwa, labelKraj, textFieldNazwa, textFieldKraj);
        }

        if (table.equals("Mecz")) {
            Label labelData = new Label("Data");
            labelData.setLayoutX(30);
            labelData.setLayoutY(60);
            Label labelKlub = new Label("Nazwa klubu");
            labelKlub.setLayoutX(30);
            labelKlub.setLayoutY(100);

            TextField textFieldData = new TextField();
            textFieldData.setPromptText("YYYY-MM-DD");
            textFieldData.setLayoutX(140);
            textFieldData.setLayoutY(56);
            TextField textFieldKlub = new TextField();
            textFieldKlub.setLayoutX(140);
            textFieldKlub.setLayoutY(96);

            anchorPaneSearch.getChildren().removeIf(n -> n instanceof TextField);
            anchorPaneSearch.getChildren().removeIf(n -> n instanceof Label);
            anchorPaneSearch.getChildren().addAll(labelData, labelKlub, textFieldData, textFieldKlub);
        }

        if (table.equals("Piłkarz")) {
            Label labelNazwisko = new Label("Nazwisko");
            labelNazwisko.setLayoutX(30);
            labelNazwisko.setLayoutY(60);
            Label labelKlub = new Label("Nazwa klubu");
            labelKlub.setLayoutX(30);
            labelKlub.setLayoutY(100);
            Label labelData = new Label("Data urodzenia");
            labelData.setLayoutX(30);
            labelData.setLayoutY(140);
            Label labelPos = new Label("Pozycja");
            labelPos.setLayoutX(30);
            labelPos.setLayoutY(180);

            TextField textFieldNazwisko = new TextField();
            textFieldNazwisko.setLayoutX(140);
            textFieldNazwisko.setLayoutY(56);
            TextField textFieldKlub = new TextField();
            textFieldKlub.setLayoutX(140);
            textFieldKlub.setLayoutY(96);
            TextField textFieldData = new TextField();
            textFieldData.setPromptText("YYYY-MM-DD");
            textFieldData.setLayoutX(140);
            textFieldData.setLayoutY(136);
            TextField textFieldPos = new TextField();
            textFieldPos.setLayoutX(140);
            textFieldPos.setLayoutY(176);

            anchorPaneSearch.getChildren().removeIf(n -> n instanceof TextField);
            anchorPaneSearch.getChildren().removeIf(n -> n instanceof Label);
            anchorPaneSearch.getChildren().addAll(labelNazwisko, labelData, labelPos,labelKlub);
            anchorPaneSearch.getChildren().addAll(textFieldNazwisko, textFieldKlub, textFieldPos, textFieldData);
        }

        if (table.equals("Sędzia")) {
            Label labelNazwisko = new Label("Nazwisko");
            labelNazwisko.setLayoutX(30);
            labelNazwisko.setLayoutY(60);
            Label labelKraj = new Label("Kraj");
            labelKraj.setLayoutX(30);
            labelKraj.setLayoutY(100);

            TextField textFieldNazwisko = new TextField();
            textFieldNazwisko.setLayoutX(140);
            textFieldNazwisko.setLayoutY(56);
            TextField textFieldKraj = new TextField();
            textFieldKraj.setLayoutX(140);
            textFieldKraj.setLayoutY(96);

            anchorPaneSearch.getChildren().removeIf(n -> n instanceof TextField);
            anchorPaneSearch.getChildren().removeIf(n -> n instanceof Label);
            anchorPaneSearch.getChildren().addAll(textFieldNazwisko, textFieldKraj, labelKraj, labelNazwisko);

        }

        if (table.equals("Stadion")) {
            Label labelNazwa = new Label("Nazwa");
            labelNazwa.setLayoutX(30);
            labelNazwa.setLayoutY(60);
            Label labelMiasto = new Label("Miasto");
            labelMiasto.setLayoutX(30);
            labelMiasto.setLayoutY(100);
            Label labelKlub = new Label("Nazwa klubu");
            labelKlub.setLayoutX(30);
            labelKlub.setLayoutY(140);

            TextField textFieldNazwa = new TextField();
            textFieldNazwa.setLayoutX(140);
            textFieldNazwa.setLayoutY(56);
            TextField textFieldMiasto = new TextField();
            textFieldMiasto.setLayoutX(140);
            textFieldMiasto.setLayoutY(96);
            TextField textFieldKlub = new TextField();
            textFieldKlub.setLayoutX(140);
            textFieldKlub.setLayoutY(136);

            anchorPaneSearch.getChildren().removeIf(n -> n instanceof TextField);
            anchorPaneSearch.getChildren().removeIf(n -> n instanceof Label);
            anchorPaneSearch.getChildren().addAll(textFieldKlub, textFieldMiasto, textFieldNazwa, labelKlub, labelMiasto, labelNazwa);
        }

        if (table.equals("Trener")) {
            Label labelNazwisko = new Label("Nazwisko");
            labelNazwisko.setLayoutX(30);
            labelNazwisko.setLayoutY(60);
            Label labelKlub = new Label("Nazwa klubu");
            labelKlub.setLayoutX(30);
            labelKlub.setLayoutY(100);

            TextField textFieldNazwisko = new TextField();
            textFieldNazwisko.setLayoutX(140);
            textFieldNazwisko.setLayoutY(56);
            TextField textFieldKlub = new TextField();
            textFieldKlub.setLayoutX(140);
            textFieldKlub.setLayoutY(96);

            anchorPaneSearch.getChildren().removeIf(n -> n instanceof TextField);
            anchorPaneSearch.getChildren().removeIf(n -> n instanceof Label);
            anchorPaneSearch.getChildren().addAll(textFieldNazwisko, textFieldKlub, labelKlub, labelNazwisko);
        }

        if (table.equals("Właściciel")) {
            Label labelNazwisko = new Label("Nazwisko");
            labelNazwisko.setLayoutX(30);
            labelNazwisko.setLayoutY(60);
            Label labelKlub = new Label("Nazwa klubu");
            labelKlub.setLayoutX(30);
            labelKlub.setLayoutY(100);

            TextField textFieldNazwisko = new TextField();
            textFieldNazwisko.setLayoutX(140);
            textFieldNazwisko.setLayoutY(56);
            TextField textFieldKlub = new TextField();
            textFieldKlub.setLayoutX(140);
            textFieldKlub.setLayoutY(96);

            anchorPaneSearch.getChildren().removeIf(n -> n instanceof TextField);
            anchorPaneSearch.getChildren().removeIf(n -> n instanceof Label);
            anchorPaneSearch.getChildren().addAll(textFieldNazwisko, textFieldKlub, labelKlub, labelNazwisko);
        }

    }

    public void showResults (ActionEvent event) {
        String table = (String) comboBoxTable.getSelectionModel().getSelectedItem();
        if (table == null) {return;}

        tableSearch.getItems().clear();
        tableSearch.getColumns().clear();

        if(table.equals("Klub")){
            String nazwa = textFieldA.getText();
            String liga = textFieldB.getText();
            if (nazwa == null || nazwa.equals("")) {nazwa = "#123456789";}
            if (liga == null || liga.equals("")) {liga = "#123456789";}

            tableSearch.getColumns().addAll(tableKluby.getColumns());

            String SQL = "SELECT * from KLUBY where NAZWA_KLUBU LIKE '%" + nazwa + "%' OR NAZWA_LIGI like '%" + liga + "%'";
            try {
                ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                while (rs.next()) {
                    Kluby klub = new Kluby(rs.getString(1), rs.getInt(2), rs.getString(3));
                    tableSearch.getItems().add(klub);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error on Building Data");
            }

        }

        if (table.equals("Liga")){
            String nazwa = textFieldA.getText();
            String kraj = textFieldB.getText();

            if (nazwa == null || nazwa.equals("")) {nazwa = "#123456789";}
            if (kraj == null || kraj.equals("")) {kraj = "#123456789";}

            tableSearch.getColumns().addAll(tableLigi.getColumns());

            String SQL = "SELECT * from LIGI where NAZWA_LIGI LIKE '%" + nazwa + "%' OR KRAJ like '%" + kraj + "%'";
            try {
                ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                while (rs.next()) {
                    Ligi liga = new Ligi(rs.getString(1), rs.getString(2));
                    tableSearch.getItems().add(liga);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error on Building Data");
            }
        }

        if (table.equals("Mecz")) {
            String data = textFieldA.getText();
            String nazwaKlubu = textFieldB.getText();
            if (data == null || data.equals("")) {data = "1800-01-01";}
            if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}

            tableSearch.getColumns().addAll(tableMecze.getColumns());

            String SQL = "SELECT * from MECZE where data = DATE '" + data + "' OR GOSCIE like '%" + nazwaKlubu +
                    "%' OR GOSPODARZE like '%" + nazwaKlubu + "%'";
            try {
                ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                while (rs.next()) {
                    Mecze mecz = new Mecze(rs.getString(1), rs.getDate(2), rs.getString(3), rs.getString(4),
                            rs.getInt(5), rs.getInt(6), rs.getString(7));
                    tableSearch.getItems().add(mecz);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error on Building Data");
            }
        }

        if (table.equals("Piłkarz")) {
            String nazwisko = textFieldA.getText();
            String nazwaKlubu = textFieldB.getText();
            String dataUrodzenia = textFieldC.getText();
            String pozycja = textFieldD.getText();
            if (nazwisko == null || nazwisko.equals("")) {nazwisko = "#123456789";}
            if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}
            if (dataUrodzenia == null || dataUrodzenia.equals("")) {dataUrodzenia = "1800-01-01";}
            if (pozycja == null || pozycja.equals("")) {pozycja = "#123456789";}

            tableSearch.getColumns().addAll(tablePilkarze.getColumns());

            String SQL = "SELECT * from PILKARZE where DATA_URODZENIA = DATE '" + dataUrodzenia + "' OR NAZWA_KLUBU like '%" + nazwaKlubu +
                    "%' OR nazwisko like '%" + nazwisko + "%' OR POZYCJA like '%" + pozycja + "%'";
            try {
                ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                while (rs.next()) {
                    Pilkarze pilkarz = new Pilkarze(rs.getString("id_pilkarza"), rs.getString("imie"),
                            rs.getString("nazwisko"), rs.getDate("data_urodzenia"), rs.getString("pozycja"),
                            rs.getDouble("wartosc_rynkowa"), rs.getDouble("pensja"), rs.getString("nazwa_klubu"));
                    tableSearch.getItems().add(pilkarz);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error on Building Data");
            }
        }

        if (table.equals("Sędzia")) {
            String nazwisko = textFieldA.getText();
            String kraj = textFieldB.getText();

            if (nazwisko == null || nazwisko.equals("")) {nazwisko = "#123456789";}
            if (kraj == null || kraj.equals("")) {kraj = "#123456789";}

            tableSearch.getColumns().addAll(tableSedziowie.getColumns());

            String SQL = "SELECT * from SEDZIOWIE where NAZWISKO LIKE '%" + nazwisko + "%' OR POCHODZENIE like '%" + kraj + "%'";
            try {
                ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                while (rs.next()) {
                    Sedziowie sedzia = new Sedziowie(rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getInt(4), rs.getString(5));
                    tableSearch.getItems().add(sedzia);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error on Building Data");
            }
        }

        if (table.equals("Stadion")) {
            String nazwa = textFieldA.getText();
            String miasto = textFieldB.getText();
            String nazwaKlubu = textFieldC.getText();

            if (nazwa == null || nazwa.equals("")) {nazwa = "#123456789";}
            if (miasto == null || miasto.equals("")) {miasto = "#123456789";}
            if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}

            tableSearch.getColumns().addAll(tableStadiony.getColumns());

            String SQL = "SELECT * from STADIONY where NAZWA like '%" + nazwa + "%' OR MIASTO like '%" + miasto +
                    "%' OR NAZWA_KLUBU like '%" + nazwaKlubu + "%'";
            try {
                ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                while (rs.next()) {
                    Stadiony stadion = new Stadiony(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5));
                    tableSearch.getItems().add(stadion);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error on Building Data");
            }
        }

        if (table.equals("Trener")) {
            String nazwisko = textFieldA.getText();
            String nazwaKlubu = textFieldB.getText();

            if (nazwisko == null || nazwisko.equals("")) {nazwisko = "#123456789";}
            if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}

            tableSearch.getColumns().addAll(tableTrenerzy.getColumns());

            String SQL = "SELECT * from TRENERZY where NAZWISKO LIKE '%" + nazwisko + "%' OR NAZWA_KLUBU like '%" + nazwaKlubu + "%'";
            try {
                ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                while (rs.next()) {
                    Trenerzy trener = new Trenerzy(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                    tableSearch.getItems().add(trener);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error on Building Data");
            }

        }

        if (table.equals("Właściciel")) {
            String nazwisko = textFieldA.getText();
            String nazwaKlubu = textFieldB.getText();

            if (nazwisko == null || nazwisko.equals("")) {nazwisko = "#123456789";}
            if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}

            tableSearch.getColumns().addAll(tableWlasciciele.getColumns());

            String SQL = "SELECT * from WLASCICIELE where NAZWISKO LIKE '%" + nazwisko + "%' OR NAZWA_KLUBU like '%" + nazwaKlubu + "%'";
            try {
                ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                while (rs.next()) {
                    Wlasciciele wlasciciel = new Wlasciciele(rs.getString(1), rs.getString(2),
                            rs.getString(3), rs.getDouble(4), rs.getString(5));
                    tableSearch.getItems().add(wlasciciel);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error on Building Data");
            }
        }
    }


    public void hideAll() {
        tablePilkarze.setVisible(false);
        tableKluby.setVisible(false);
    }
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
}
