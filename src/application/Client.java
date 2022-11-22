package application;

import application.action.Action;
import application.action.log;
import application.body.account;
import application.controller.ServerAccount;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class Client extends Application {
    private static int logCheck = 0;

    private Stage primaryStage;
    private String myName;

    @Override
    public void start(Stage primaryStage) {
        try {

            this.primaryStage = primaryStage;


            FXMLLoader fxmlLoader = new FXMLLoader();

            fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
            Pane root = fxmlLoader.load();
            primaryStage.setTitle("Tic Tac Toe Client ");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
//            primaryStage.setOnCloseRequest(e -> Platform.exit());
            primaryStage.setOnCloseRequest(e -> {
                Platform.exit();
//                System.out.println("Line Abnormal exit");
                System.exit(0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void match() {
        launch();
    }

}
