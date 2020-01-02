package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    public Connection connection = null;


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader firstLoader = new FXMLLoader(getClass().getResource("scenes/mainView.fxml"));
        Parent root = firstLoader.load();
        Controller controller = firstLoader.getController();
        primaryStage.setTitle("FootballApp");
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.show();

        try {
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@//admlab2.cs.put.poznan.pl:1521/dblab02_students.cs.put.poznan.pl",
                    "inf136820", "inf136820");
            controller.mainConnection = connection;
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                    "nie udało się połączyć z bazą danych", ex);
            System.exit(-1);
        }
        System.out.println("Polaczono z baza danych");


        Connection finalCon = connection;
        EventHandler quitHandler = new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                try {
                    finalCon.close();
                } catch (
                        SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Odłączono od bazy danych");
                System.exit(0);
            }

        };

        primaryStage.setOnCloseRequest(quitHandler);
    }




    public static void main(String[] args) {
        launch(args);
    }
}
