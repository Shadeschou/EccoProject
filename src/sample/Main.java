package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("ECCO Corp.");
        primaryStage.setScene(new Scene(root, 1020, 678));
        try {
            IDCard idCard1 = new IDCard("frankID.fxml", 2222);
            IDCard idCard2 = new IDCard("karstenID.fxml", 1111);
            Controller.idCardArrayList.add(idCard1);
            Controller.idCardArrayList.add(idCard2);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }
}
