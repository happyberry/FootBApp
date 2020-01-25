package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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
    private TableView tableRanking;
    @FXML
    private TableView tableStrzelcy;
    @FXML
    private TableColumn tableColumnWartosc;
    @FXML
    private TableColumn tableColumnPensja;
    @FXML
    private TableColumn tableColumnKwotaTransferu;
    @FXML
    private TableColumn tableColumnMajatek;
    @FXML
    private ComboBox comboBoxTable;
    @FXML
    private ComboBox comboBoxLeague, comboBoxYear, comboBoxBYear, comboBoxCYear, comboBoxBMonth, comboBoxCMonth;
    @FXML
    private AnchorPane anchorPaneSearch;
    @FXML
    private Label labelA, labelB, labelC, labelD, labelWarning;
    @FXML
    private TextField textFieldA, textFieldB, textFieldC, textFieldD;
    public Stage primaryStage;

    public boolean pilkarzeJuzWczytani = false;
    public boolean klubyJuzWczytane = false;
    public boolean ligiJuzWczytane = false;
    public boolean goleJuzWczytane = false;
    public boolean meczeJuzWczytane = false;
    public boolean sedziowieJuzWczytani = false;
    public boolean trenerzyJuzWczytani = false;
    public boolean stadionyJuzWczytane = false;
    public boolean transferyJuzWczytane = false;
    public boolean wlascicieleJuzWczytani = false;

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

    public <E> void reformatDoubleCell(TableColumn column) {
        column.setCellFactory(new Callback<TableColumn, TableCell>() {
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell<E, Double>() {
                    @Override
                    public void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                        setGraphic(null);
                    }

                    private String getString() {
                        String ret = "";
                        if (getItem() != null) {
                            String gi = getItem().toString();
                            NumberFormat df = DecimalFormat.getInstance();
                            df.setMinimumFractionDigits(2);
                            df.setRoundingMode(RoundingMode.DOWN);

                            ret = df.format(Double.parseDouble(gi));
                        } else {
                            ret = "0.00";
                        }
                        return ret;
                    }
                };
                return cell;
            }
        });
    }

    public void initialize() {
        comboBoxLeague.getItems().add("Wszystkie");
        comboBoxLeague.getSelectionModel().select(0);
        comboBoxYear.getSelectionModel().select(9);
        this.<Pilkarze>reformatDoubleCell(tableColumnWartosc);
        this.<Pilkarze>reformatDoubleCell(tableColumnPensja);
        this.<Transfery>reformatDoubleCell(tableColumnKwotaTransferu);
        this.<Wlasciciele>reformatDoubleCell(tableColumnMajatek);
    }

    public void fetchInitialData() {
        comboBoxLeague.getItems().clear();
        comboBoxLeague.getItems().add("Wszystkie");
        String SQL = "SELECT NAZWA_LIGI from LIGI ORDER BY NAZWA_LIGI";

        try {
            ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
            while (rs.next()) {
                comboBoxLeague.getItems().add(rs.getString("nazwa_ligi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error on Picking Ligue Names");
        }
        comboBoxLeague.hide();
        comboBoxLeague.show();
    }

    public void fillRanking() {
        fillRankingStrzelcow();
        fillRankingKlubow();
    }

    public void fillRankingKlubow() {

        if (comboBoxLeague.getSelectionModel().getSelectedItem() == null) return;
        String poczatek = null;
        String koniec = null;
        if (comboBoxYear.getSelectionModel().getSelectedItem() == null) {
            poczatek = "DATE '2019-07-01'";
            koniec = "DATE '2020-06-30' ";
        }
        else {
            Integer rokPoczatku = Integer.parseInt(comboBoxYear.getSelectionModel().getSelectedItem().toString().substring(0,4));
            System.out.println(rokPoczatku);
            poczatek = "DATE '" + String.valueOf(rokPoczatku) +"-07-01'";
            koniec = "DATE '" + String.valueOf(rokPoczatku+1) + "-06-30' ";
        }
        tableRanking.getItems().clear();
        String liga = comboBoxLeague.getSelectionModel().getSelectedItem().toString();
        if (liga.equals("Wszystkie")) {
            tableRanking.setPlaceholder(new Label("Wybierz ligę by zobaczyć jej tabelę"));
            return;
        } else {
            tableRanking.setPlaceholder(new Label("Brak danych dotyczących tej ligii"));
        }
        String SQL = "SELECT NAZWA_KLUBU, liczpunktywsezonie(NAZWA_KLUBU, " + poczatek + ", " + koniec + ") as punkty from KLUBY WHERE NAZWA_LIGI = '" + liga + "' ORDER BY punkty DESC";
        try {
            ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
            while (rs.next()) {
                RekordRankingu rekord = new RekordRankingu(rs.getString(1), rs.getInt(2));
                //System.out.println(rekord.nazwaKlubu);
                tableRanking.getItems().add(rekord);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Error on Picking Ligue Names");
        }


    }

    public void fillRankingStrzelcow() {

        if (comboBoxLeague.getSelectionModel().getSelectedItem() == null) return;
        tableStrzelcy.setPlaceholder(new Label("Brak danych dotyczących tego sezonu"));
        String poczatek = null;
        String koniec = null;
        if (comboBoxYear.getSelectionModel().getSelectedItem() == null) {
            poczatek = "DATE '2019-07-01'";
            koniec = "DATE '2020-06-30' ";
        }
        else {
            Integer rokPoczatku = Integer.parseInt(comboBoxYear.getSelectionModel().getSelectedItem().toString().substring(0,4));
            System.out.println(rokPoczatku);
            poczatek = "DATE '" + String.valueOf(rokPoczatku) +"-07-01'";
            koniec = "DATE '" + String.valueOf(rokPoczatku+1) + "-06-30' ";
        }
        String text = "AND m.data BETWEEN " + poczatek + " AND " + koniec;
        tableStrzelcy.getItems().clear();
        String liga = comboBoxLeague.getSelectionModel().getSelectedItem().toString();
        if (!liga.equals("Wszystkie")) { liga = "k2.nazwa_ligi = '" + liga + "' AND "; }
        else { liga = ""; }
        String SQL = "SELECT p.imie, p.nazwisko, p.nazwa_klubu, COUNT(*) AS bramki\n" +
                "FROM pilkarze p INNER JOIN gole g ON p.id_pilkarza = g.id_pilkarza INNER JOIN mecze m ON m.mecz_ID = g.mecz_ID INNER JOIN kluby k1 ON k1.nazwa_klubu = m.gospodarze\n" +
                "INNER JOIN kluby k2 ON k2.nazwa_klubu = m.goscie\n" +
                "WHERE " + liga + "k2.nazwa_ligi = k1.nazwa_ligi AND czy_samobojczy = 0" + text +
                "GROUP BY p.imie, p.nazwisko, p.id_pilkarza, p.nazwa_klubu " +
                "ORDER BY bramki DESC";
        try {
            ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
            while (rs.next()) {
                RekordStrzelcow rekord = new RekordStrzelcow(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4));
                tableStrzelcy.getItems().add(rekord);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Error on Picking Ligue Names");
        }

    }

    public void fillKluby() throws SQLException {

        if (klubyJuzWczytane) return;
        tableKluby.getItems().clear();
        klubyJuzWczytane = true;
        String SQL = "SELECT * from KLUBY ORDER BY NAZWA_LIGI, NAZWA_KLUBU";
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
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
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
        String nazwaLigi = klub.getNazwaLigi();
        if (nazwaLigi == null) nazwaLigi = "";
        editKlubController.comboBoxLeague.setPromptText(nazwaLigi);

        stage.showAndWait();
        //stage.show();

    }

    public void openInsertKlub(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertKlub.fxml"));

        Stage stage = new Stage();
        //stage.setAlwaysOnTop(true);
        //stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertKlubController insertKlubController = loader.<InsertKlubController>getController();

        insertKlubController.connection = mainConnection;
        insertKlubController.controller = this;
        insertKlubController.initializeOptions();

        //stage.showAndWait();
        stage.show();

    }

    public void openMoreKlub(ActionEvent event) throws IOException {
        if (tableKluby.getSelectionModel().getSelectedItem() == null) {return;}

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/moreKlub.fxml"));

        Stage stage = new Stage();
        //stage.setAlwaysOnTop(true);
        //stage.initModality(Modality.APPLICATION_MODAL);
        Kluby klub = (Kluby) tableKluby.getSelectionModel().getSelectedItem();
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.setTitle(klub.getNazwaKlubu() + " - dodatkowe informacje");

        stage.show();

        MoreKlubController moreKlubController = loader.<MoreKlubController>getController();

        String SQL = "SELECT * from PILKARZE where NAZWA_KLUBU = '" + klub.getNazwaKlubu() + "' ORDER BY POZYCJA, NAZWISKO";
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

        moreKlubController.labelNazwa.setText(klub.getNazwaKlubu());
        moreKlubController.labelLiga.setText(klub.getNazwaLigi());
        moreKlubController.labelRok.setText(klub.getRokZalozenia().toString());
        try {
            Statement statement = mainConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("select NAZWA, MIASTO from STADIONY where NAZWA_KLUBU = '" + klub.getNazwaKlubu() + "'");
            while(resultSet.next()) {
                moreKlubController.labelStadion.setText(resultSet.getString(1));
                moreKlubController.labelMiasto.setText(resultSet.getString(2));
            }
            resultSet = statement.executeQuery("select imie || ' ' || nazwisko from WLASCICIELE where NAZWA_KLUBU = '" + klub.getNazwaKlubu() + "'");
            while(resultSet.next()) {
                moreKlubController.labelWlasciciel.setText(resultSet.getString(1));
            }
            resultSet = statement.executeQuery("select imie || ' ' || nazwisko from TRENERZY where NAZWA_KLUBU = '" + klub.getNazwaKlubu() + "'");
            while(resultSet.next()) {
                moreKlubController.labelTrener.setText(resultSet.getString(1));
            }

        }
        catch (Exception e) {
            System.out.println("Error on building data");
        }

    }

    public void fillPilkarze() throws SQLException {

        if (pilkarzeJuzWczytani) return;
        tablePilkarze.getItems().clear();
        pilkarzeJuzWczytani = true;

        String SQL = "SELECT * from PILKARZE ORDER BY NAZWA_KLUBU, POZYCJA, NAZWISKO";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        System.out.println(rs.getString("imie"));
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
        tableMecze.getItems().clear();
        meczeJuzWczytane = true;
        String SQL = "SELECT MECZ_ID, DATA, GOSPODARZE, GOSCIE, WYNIK_GOSPODARZY, WYNIK_GOSCI, NVL(ID_SEDZIEGO, -1), IMIE || ' ' || NAZWISKO from MECZE left outer join sedziowie using(id_sedziego) ORDER BY DATA";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        //Iterate Row
                        System.out.println(rs.getString(8));
                        Mecze mecz = new Mecze(rs.getString(1), rs.getDate(2), rs.getString(3),
                                rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getString(8));
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

    public void openEditMecz(ActionEvent event) throws IOException {

        if(tableMecze.getSelectionModel().getSelectedItem() == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editMecz.fxml"));

        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Edytuj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        EditMeczController editMeczController = loader.<EditMeczController>getController();

        editMeczController.connection = mainConnection;
        editMeczController.controller = this;
        editMeczController.initializeOptions();

        Mecze mecz = (Mecze) tableMecze.getSelectionModel().getSelectedItem();
        editMeczController.mecz = mecz;
        editMeczController.sedziaId = mecz.getIdSedziego();

        editMeczController.comboBoxDay.setPromptText(String.valueOf(mecz.getData().getDate()));
        editMeczController.comboBoxMonth.setPromptText(String.valueOf(mecz.getData().getMonth()+1));
        editMeczController.comboBoxYear.setPromptText(String.valueOf(mecz.getData().getYear()+1900));
        editMeczController.comboBoxGosc.setPromptText(mecz.getGoscie());
        editMeczController.comboBoxGosp.setPromptText(mecz.getGospodarze());
        editMeczController.textFieldSedzia.setText(mecz.getDaneSedziego());
        editMeczController.textFieldWynikGosc.setText(String.valueOf(mecz.getWynikGosci()));
        editMeczController.textFieldWynikGosp.setText(String.valueOf(mecz.getWynikGospodarzy()));

        //stage.show();
        stage.showAndWait();
    }

    public void openInsertMecz(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertMecz.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertMeczController insertMeczController = loader.<InsertMeczController>getController();

        insertMeczController.connection = mainConnection;
        insertMeczController.controller = this;
        insertMeczController.initializeOptions();

        stage.show();

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
        moreMeczController.box.getChildren().clear();
        moreMeczController.labelGospodarze.setText(mecz.getGospodarze());
        moreMeczController.labelGoscie.setText(mecz.getGoscie());
        moreMeczController.labelWynik.setText(mecz.getWynik());
        moreMeczController.labelMiasto.setVisible(false);

        if (mecz.getWynikGosci() > mecz.getWynikGospodarzy()) {
            moreMeczController.labelGoscie.setTextFill(Color.web("green"));
            moreMeczController.labelGospodarze.setTextFill(Color.web("red"));
        } else if (mecz.getWynikGospodarzy() > mecz.getWynikGosci()) {
            moreMeczController.labelGoscie.setTextFill(Color.web("red"));
            moreMeczController.labelGospodarze.setTextFill(Color.web("green"));
        }

        try {
            Statement statement = mainConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("select NAZWA_LIGI from kluby where NAZWA_KLUBU = '" + mecz.getGospodarze() + "'");
            resultSet.next();
            String ligaGospodarzy = resultSet.getString(1);
            resultSet = statement.executeQuery("select NAZWA_LIGI from kluby where NAZWA_KLUBU = '" + mecz.getGoscie() + "'");
            resultSet.next();
            String ligaGosci = resultSet.getString(1);
            if (ligaGosci.equals(ligaGospodarzy)) {
                moreMeczController.labelData.setText(ligaGosci + ", " + mecz.getData().toString());
            } else {
                moreMeczController.labelData.setText("Mecz towarzyski, " + mecz.getData().toString());
            }

        }
        catch (Exception e) {
            System.out.println("Error on building data");
        }

        try {
            Statement statement = mainConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("select MIASTO,NAZWA from STADIONY where NAZWA_KLUBU = '" + mecz.getGospodarze() + "'");
            while (resultSet.next()) {
                String miejsce = resultSet.getString(1) + ", " + resultSet.getString(2);
                moreMeczController.labelMiasto.setText(miejsce);
                moreMeczController.labelMiasto.setVisible(true);
            }
        }
        catch (Exception e) {
            System.out.println("Error on building data");
        }

        String SQL = "SELECT CZY_SAMOBOJCZY, CZY_DLA_GOSPODARZY, GOL_ID, MECZ_ID, ID_PILKARZA, MINUTA, IMIE || ' ' || NAZWISKO, " +
                "GOSPODARZE, GOSCIE, DATA from GOLE join PILKARZE using(id_pilkarza) join MECZE using(mecz_id) WHERE MECZ_ID = " + mecz.getMeczId() + " ORDER BY MINUTA";

        Integer translateGospodarze = 0;
        Integer translateGoscie = 0;
        try {
            ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
            while (rs.next()) {
                Integer samobojczy = rs.getInt(1);
                Integer czyGospodarze = rs.getInt(2);
                String okolicznosci = null;
                if (samobojczy == 0) {
                    okolicznosci = "Nie";
                } else {
                    okolicznosci = "Tak";
                }
                Gole gol = new Gole(rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getInt(6), samobojczy, czyGospodarze, rs.getString(7), okolicznosci,
                        rs.getString(8), rs.getString(9), rs.getDate(10));

                Label labelNowyGol = new Label();
                labelNowyGol.setPrefWidth(200);
                String text = gol.getDaneStrzelca() + " " + gol.getMinuta().toString() + "'";
                System.out.println(text);
                if (samobojczy == 1) {
                    text += "(S)";
                }
                labelNowyGol.setText(text);
                if (czyGospodarze == 1) {
                    System.out.println("Gospodarze");
                    labelNowyGol.setTranslateX(0);
                    labelNowyGol.setTranslateY(translateGospodarze += 10);
                } else {
                    labelNowyGol.setTranslateX(356);
                    labelNowyGol.setAlignment(Pos.CENTER_RIGHT);
                    labelNowyGol.setTranslateY(translateGoscie += 10);
                }
                System.out.println(translateGospodarze + " " + translateGoscie);
                moreMeczController.box.getChildren().add(labelNowyGol);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }


    }

    public void fillLigi() throws SQLException {

        if (ligiJuzWczytane) return;
        tableLigi.getItems().clear();
        ligiJuzWczytane = true;
        String SQL = "SELECT * from LIGI ORDER BY NAZWA_LIGI";
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
        tableSedziowie.getItems().clear();
        sedziowieJuzWczytani = true;
        String SQL = "SELECT * from SEDZIOWIE ORDER BY POCHODZENIE";
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

    public void openMoreSedzia(ActionEvent event) throws IOException {

        if (tableSedziowie.getSelectionModel().getSelectedItem() == null) {return;}

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/moreSedzia.fxml"));

        Stage stage = new Stage();
        Sedziowie sedzia = (Sedziowie) tableSedziowie.getSelectionModel().getSelectedItem();
        stage.setTitle(sedzia.getNazwisko() + " - dodatkowe informacje");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.show();

        MoreSedziaController moreSedziaController = loader.<MoreSedziaController>getController();

        String SQL = "SELECT MECZ_ID, DATA, GOSPODARZE, GOSCIE, WYNIK_GOSPODARZY, WYNIK_GOSCI, ID_SEDZIEGO, IMIE || ' ' || NAZWISKO from MECZE left outer join sedziowie using(id_sedziego) where ID_SEDZIEGO = " + sedzia.getIdSedziego();
        moreSedziaController.tableMecze.getItems().clear();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Mecze mecz = new Mecze(rs.getString(1), rs.getDate(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getString(8));
                        moreSedziaController.tableMecze.getItems().add(mecz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on Building Data");
                }
            }
        };
        new Thread(r).start();
    }

    public void fillGole() throws SQLException {

        if (goleJuzWczytane) return;
        tableGole.getItems().clear();
        goleJuzWczytane = true;
        String SQL = "SELECT CZY_SAMOBOJCZY, CZY_DLA_GOSPODARZY, GOL_ID, MECZ_ID, ID_PILKARZA, MINUTA, IMIE || ' ' || NAZWISKO, " +
                "GOSPODARZE, GOSCIE, DATA from GOLE join PILKARZE using(id_pilkarza) join MECZE using(mecz_id)";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
                    while (rs.next()) {
                        Integer samobojczy = rs.getInt(1);
                        Integer czyGospodarze = rs.getInt(2);
                        String okolicznosci = null;
                        if (samobojczy == 0) {
                            okolicznosci = "Nie";
                        } else {
                            okolicznosci = "Tak";
                        }

                        Gole gol = new Gole(rs.getString(3), rs.getString(4), rs.getString(5),
                                rs.getInt(6), samobojczy, czyGospodarze, rs.getString(7), okolicznosci,
                                rs.getString(8), rs.getString(9), rs.getDate(10));
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

    public void openEditGol(ActionEvent event) throws IOException, SQLException {

        if (tableGole.getSelectionModel().getSelectedItem() == null) return;
        Gole gol = (Gole) tableGole.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editGol.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Edytuj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        EditGolController editGolController = loader.<EditGolController>getController();

        editGolController.connection = mainConnection;
        editGolController.controller = this;
        editGolController.gol = gol;

        Statement statement = mainConnection.createStatement();
        ResultSet rs = statement.executeQuery("select imie || ' ' || nazwisko from PILKARZE where ID_PILKARZA = " + gol.getIdPilkarza());
        rs.next();
        editGolController.textFieldPilkarz.setText(rs.getString(1));
        editGolController.idPilkarza = gol.getIdPilkarza();
        rs = statement.executeQuery("select gospodarze || '-' || goscie from MECZE where MECZ_ID = " + gol.getMeczId());
        rs.next();
        rs = statement.executeQuery("SELECT MECZ_ID, DATA, GOSPODARZE, GOSCIE, WYNIK_GOSPODARZY, WYNIK_GOSCI, ID_SEDZIEGO, IMIE || ' ' || NAZWISKO" +
                " from MECZE left outer join sedziowie using(id_sedziego) WHERE MECZ_ID = " + gol.getMeczId());
        rs.next();
        Mecze mecz = new Mecze(rs.getString(1), rs.getDate(2), rs.getString(3), rs.getString(4),
                rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getString(8));
        editGolController.textFieldMecz.setText(mecz.getGospodarze() + "-" + mecz.getGoscie());
        editGolController.mecz = mecz;
        editGolController.textFieldMinuta.setText(gol.getMinuta().toString());
        if (gol.getCzySamobojczy() == 1) {
            editGolController.checkBoxSamobojczy.setSelected(true);
        } else {
            editGolController.checkBoxSamobojczy.setSelected(false);
        }
        if (gol.getCzyDlaGospodarzy() == 1) {
            editGolController.radioButtonGoscie.setSelected(false);
            editGolController.radioButtonGospodarze.setSelected(true);
        } else {
            editGolController.radioButtonGospodarze.setSelected(false);
            editGolController.radioButtonGoscie.setSelected(true);
        }

        stage.show();
    }

    public void openInsertGol(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertGol.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertGolController insertGolController = loader.<InsertGolController>getController();

        insertGolController.connection = mainConnection;
        insertGolController.controller = this;

        stage.show();
    }

    public void fillStadiony() {

        if (stadionyJuzWczytane) return;
        tableStadiony.getItems().clear();
        stadionyJuzWczytane = true;
        String SQL = "SELECT * from STADIONY ORDER BY NAZWA_KLUBU";
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
        tableTrenerzy.getItems().clear();
        trenerzyJuzWczytani = true;
        String SQL = "SELECT * from TRENERZY ORDER BY NAZWA_KLUBU";
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
        String nazwaKlubu = trener.getNazwaKlubu();
        if (nazwaKlubu == null) nazwaKlubu = "";
        editTrenerController.comboBoxClub.setPromptText(nazwaKlubu);

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
        tableWlasciciele.getItems().clear();
        wlascicieleJuzWczytani = true;
        String SQL = "SELECT * from WLASCICIELE ORDER BY NAZWA_KLUBU";
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
        tableTransfery.getItems().clear();
        transferyJuzWczytane = true;

        String SQL = "SELECT KWOTA_TRANSFERU, KLUB_SPRZEDAJACY, TRANSFERY.ID_PILKARZA, DATA_TRANSFERU, KLUB_KUPUJACY, " +
                " P.IMIE || ' ' || P.NAZWISKO from TRANSFERY JOIN PILKARZE P on TRANSFERY.ID_PILKARZA = P.ID_PILKARZA ORDER BY DATA_TRANSFERU";
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

    public void openEditTransfer(ActionEvent event) throws IOException {

        if (tableTransfery.getSelectionModel().getSelectedItem() == null) return;

        Transfery transfer = (Transfery) tableTransfery.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/editTransfer.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Edytuj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        EditTransferController editTransferController = loader.<EditTransferController>getController();

        editTransferController.connection = mainConnection;
        editTransferController.controller = this;
        editTransferController.transfer = transfer;

        editTransferController.initializeOptions();
        editTransferController.comboBoxBuy.setPromptText(transfer.getKlubKupujacy());
        editTransferController.comboBoxDay.setPromptText(String.valueOf(transfer.getDataTransferu().getDate()));
        editTransferController.comboBoxMonth.setPromptText(String.valueOf(transfer.getDataTransferu().getMonth()+1));
        editTransferController.comboBoxYear.setPromptText(String.valueOf(transfer.getDataTransferu().getYear()+1900));
        editTransferController.comboBoxSell.setPromptText(transfer.getKlubSprzedajacy());
        editTransferController.textFieldID.setText(transfer.getDanePilkarza());
        editTransferController.textFieldKwota.setText(String.valueOf(transfer.getKwotaTransferu()));

        stage.show();
    }

    public void openInsertTransfer(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/insertTransfer.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Dodaj");
        stage.setScene(new Scene((AnchorPane) loader.load()));
        InsertTransferController insertTransferController = loader.<InsertTransferController>getController();

        insertTransferController.connection = mainConnection;
        insertTransferController.controller = this;
        insertTransferController.initializeOptions();

        stage.show();
    }

    public void addToTable(TableView tabela, Object byt) {
        tabela.getItems().add(byt);
    }

    public void removeFromTable(TableView tabela, Object byt) {
        tabela.getItems().remove(byt);
    }

    public void fillSearchEngine (Event event) {

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
        comboBoxBMonth.setVisible(false);
        comboBoxBYear.setVisible(false);
        comboBoxCMonth.setVisible(false);
        comboBoxCYear.setVisible(false);
        comboBoxBYear.getItems().clear();
        comboBoxCYear.getItems().clear();

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
            //textFieldA.setVisible(true);
            //textFieldA.setPromptText("YYYY-MM-DD");
            textFieldB.setVisible(true);

            comboBoxBYear.getItems().addAll(2000, 2001, 2002,2003,2004,2005,2006,2007, 2008, 2009,
                    2010, 2011, 2012,2013,2014,2015,2016,2017, 2018, 2019,
                    2020, 2021, 2022,2023,2024,2025,2026,2027, 2028, 2029, 2030);
            comboBoxCYear.getItems().addAll(2000, 2001, 2002,2003,2004,2005,2006,2007, 2008, 2009,
                    2010, 2011, 2012,2013,2014,2015,2016,2017, 2018, 2019,
                    2020, 2021, 2022,2023,2024,2025,2026,2027, 2028, 2029, 2030);
            comboBoxBMonth.setLayoutY(56);
            comboBoxBYear.setLayoutY(56);
            comboBoxCMonth.setLayoutY(56);
            comboBoxCYear.setLayoutY(56);

            comboBoxBMonth.setVisible(true);
            comboBoxBYear.setVisible(true);
            comboBoxCMonth.setVisible(true);
            comboBoxCYear.setVisible(true);
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
            //textFieldC.setVisible(true);
            //textFieldC.setPromptText("YYYY-MM-DD");
            textFieldD.setVisible(true);

            comboBoxBYear.getItems().addAll(1970, 1971, 1972,1973,1974,1975,1976,1977, 1978, 1979,
                    1980, 1981, 1982,1983,1984,1985,1986,1987, 1988, 1989,
                    1990, 1991, 1992,1993,1994,1995,1996,1997, 1998, 1999,
                    2000, 2001, 2002,2003,2004,2005,2006,2007, 2008, 2009,2010);
            comboBoxCYear.getItems().addAll(1970, 1971, 1972,1973,1974,1975,1976,1977, 1978, 1979,
                    1980, 1981, 1982,1983,1984,1985,1986,1987, 1988, 1989,
                    1990, 1991, 1992,1993,1994,1995,1996,1997, 1998, 1999,
                    2000, 2001, 2002,2003,2004,2005,2006,2007, 2008, 2009,2010);
            comboBoxBMonth.setLayoutY(136);
            comboBoxBYear.setLayoutY(136);
            comboBoxCMonth.setLayoutY(136);
            comboBoxCYear.setLayoutY(136);

            comboBoxBMonth.setVisible(true);
            comboBoxBYear.setVisible(true);
            comboBoxCMonth.setVisible(true);
            comboBoxCYear.setVisible(true);
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

    public void showResults (ActionEvent event) {
        String table = (String) comboBoxTable.getSelectionModel().getSelectedItem();
        if (table == null) {return;}

        tableSearch.getItems().clear();
        tableSearch.getColumns().clear();

        if(table.equals("Klub")){
            String nazwa = textFieldA.getText();
            String liga = textFieldB.getText();
            String wyszukajLige;
            if (nazwa == null || nazwa.equals("")) {nazwa = "#123456789";}
            if (liga == null || liga.equals("")) {
                wyszukajLige = "nazwa_ligi IS NULL";
            } else {
                wyszukajLige = "nazwa_ligi LIKE '%" + liga + "%'";
            }
            //if (liga == null || liga.equals("")) {liga = "#123456789";}

            initializeTableColumns(tableKluby);

            String SQL = "SELECT * from KLUBY where NAZWA_KLUBU LIKE '%" + nazwa + "%' OR " + wyszukajLige;
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

            initializeTableColumns(tableLigi);

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
            //String data = textFieldA.getText();
            String nazwaKlubu = textFieldB.getText();
            //if (data == null || data.equals("")) {data = "1800-01-01";}
            if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}

            String wyszukajDate;
            Integer yearB = (Integer) comboBoxBYear.getSelectionModel().getSelectedItem();
            String monthB = (String) comboBoxBMonth.getSelectionModel().getSelectedItem();
            Integer yearC = (Integer) comboBoxCYear.getSelectionModel().getSelectedItem();
            String monthC = (String) comboBoxCMonth.getSelectionModel().getSelectedItem();
            if (yearB == null || yearC == null || monthB == null || monthC == null) {
                wyszukajDate = "";
                labelWarning.setVisible(true);
            } else {
                labelWarning.setVisible(false);
                Integer dayC = 28;
                if (monthC.equals("02")) {
                    if (yearC % 4 == 0) dayC = 29;
                    else dayC = 28;
                } else if (monthC.equals("04") || monthC.equals("06") || monthC.equals("09") || monthC.equals("11")) {
                    dayC = 30;
                } else { dayC = 31;}
                wyszukajDate = "OR DATA BETWEEN DATE '" + yearB + "-" + monthB + "-01'" + "AND DATE '" +
                        yearC + "-" + monthC + "-" + dayC + "'";
            }

            initializeTableColumns(tableMecze);

            String SQL = "SELECT MECZ_ID, DATA, GOSPODARZE, GOSCIE, WYNIK_GOSPODARZY, WYNIK_GOSCI, ID_SEDZIEGO, IMIE || ' ' || NAZWISKO from MECZE left outer join sedziowie using(id_sedziego) where GOSCIE like '%" + nazwaKlubu +
                    "%' OR GOSPODARZE like '%" + nazwaKlubu + "%'" + wyszukajDate;
            try {
                ResultSet rs = mainConnection.createStatement().executeQuery(SQL);
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

        if (table.equals("Piłkarz")) {
            String nazwisko = textFieldA.getText();
            String nazwaKlubu = textFieldB.getText();
            //String dataUrodzenia = textFieldC.getText();
            String pozycja = textFieldD.getText();
            String wyszukajKlub;
            if (nazwisko == null || nazwisko.equals("")) {nazwisko = "#123456789";}
            //if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}
            //if (dataUrodzenia == null || dataUrodzenia.equals("")) {dataUrodzenia = "1800-01-01";}
            if (pozycja == null || pozycja.equals("")) {pozycja = "#123456789";}
            if (nazwaKlubu == null || nazwaKlubu.equals("")) {
                wyszukajKlub = "nazwa_klubu IS NULL";
            } else {
                wyszukajKlub = "nazwa_klubu LIKE '%" + nazwaKlubu + "%'";
            }

            String wyszukajDate;
            Integer yearB = (Integer) comboBoxBYear.getSelectionModel().getSelectedItem();
            String monthB = (String) comboBoxBMonth.getSelectionModel().getSelectedItem();
            Integer yearC = (Integer) comboBoxCYear.getSelectionModel().getSelectedItem();
            String monthC = (String) comboBoxCMonth.getSelectionModel().getSelectedItem();
            if (yearB == null || yearC == null || monthB == null || monthC == null) {
                wyszukajDate = "";
                labelWarning.setVisible(true);
            } else {
                labelWarning.setVisible(false);
                Integer dayC = 28;
                if (monthC.equals("02")) {
                    if (yearC % 4 == 0) dayC = 29;
                    else dayC = 28;
                } else if (monthC.equals("04") || monthC.equals("06") || monthC.equals("09") || monthC.equals("11")) {
                    dayC = 30;
                } else { dayC = 31;}
                wyszukajDate = " OR DATA_urodzenia BETWEEN DATE '" + yearB + "-" + monthB + "-01'" + "AND DATE '" +
                        yearC + "-" + monthC + "-" + dayC + "'";
            }

            initializeTableColumns(tablePilkarze);

            String SQL = "SELECT * from PILKARZE where " + wyszukajKlub + " OR nazwisko like '%" + nazwisko + "%' OR POZYCJA like '%"
                    + pozycja + "%'" + wyszukajDate;
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

            initializeTableColumns(tableSedziowie);

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

            initializeTableColumns(tableStadiony);

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
            String wyszukajKlub;

            if (nazwisko == null || nazwisko.equals("")) {nazwisko = "#123456789";}
           //if (nazwaKlubu == null || nazwaKlubu.equals("")) {nazwaKlubu = "#123456789";}
            if (nazwaKlubu == null || nazwaKlubu.equals("")) {
                wyszukajKlub = "nazwa_klubu IS NULL";
            } else {
                wyszukajKlub = "nazwa_klubu LIKE '%" + nazwaKlubu + "%'";
            }

            initializeTableColumns(tableTrenerzy);

            String SQL = "SELECT * from TRENERZY where NAZWISKO LIKE '%" + nazwisko + "%' OR " + wyszukajKlub;
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

            initializeTableColumns(tableWlasciciele);

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

    public void initializeTableColumns(TableView source) {
        for (Object column: source.getColumns()) {
            TableColumn x = (TableColumn) column;
            TableColumn newCol = new TableColumn(x.getText());
            newCol.setPrefWidth(x.getPrefWidth());
            newCol.setVisible(x.isVisible());
            newCol.setCellValueFactory(x.getCellValueFactory());
            if (newCol.getText().contains("€")) {
                this.reformatDoubleCell(newCol);
            };
            tableSearch.getColumns().add(newCol);
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
