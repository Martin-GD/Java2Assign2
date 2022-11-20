package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Client2 extends Application {
    private static int logCheck = 0;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();

            fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI_player2_new.fxml"));
            Pane root = fxmlLoader.load();
            primaryStage.setTitle("Tic Tac Toe Client2 Circle");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.setOnCloseRequest(e -> {
                Platform.exit();
//                System.out.println("Circle Abnormal exit");
                System.exit(0);
            });
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
