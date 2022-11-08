package application.controller;

import application.action.Action;
import application.action.log;
import application.action.move;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.util.Pair;

public class Controller implements Initializable {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = 2;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;
    private static int logCheck = 0;

    public ArrayList<Action> actions = new ArrayList<>();


    @FXML
    private Pane base_square;

    @FXML
    private Rectangle game_panel;

    private static boolean TURN = false;

    private ClientHandler clientHandler;


    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clientHandler = new ClientHandler(false);
        chooseLog();

//        boolean login = false;
//
//        while (!login) {
//
//            Action re = clientHandler.getActions();
//            if (re != null && re.getName().equals("login")) {
//                if (re.getStatus().equals("success")) {
//                    login = true;
//                }
//            }
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

        new Thread(() -> {
            try {
                while (true) {
                    Action re = clientHandler.getActions();
                    if (re != null) {
//                        System.out.println("change");
                        if (re.getName().equals("move")) {
                            moveAction(re);
                            break;
                        }

                    }
                    Thread.sleep(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        game_panel.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / BOUND);
            int y = (int) (event.getY() / BOUND);
            if (refreshBoard(x, y) && TURN == clientHandler.getMyTurn()) {
//                TURN = !TURN;
                Action action = new move("move", chessBoard, TURN, flag);
//                System.out.println("send : "+action);
                clientHandler.send(action);
            }
        });
    }

    private boolean refreshBoard(int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
//            drawChess();
            return true;
        }
        return false;
    }

    private void drawChess() {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j);
                        break;
                    case PLAY_2:
                        drawLine(i, j);
                        break;
                    case EMPTY:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle(int i, int j) {
        Platform.runLater(() -> {
            Circle circle = new Circle();
            base_square.getChildren().add(circle);
            circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
            circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
            circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
            circle.setStroke(Color.RED);
            circle.setFill(Color.TRANSPARENT);
            flag[i][j] = true;
        });

    }

    private void drawLine(int i, int j) {
        Platform.runLater(() -> {
            Line line_a = new Line();
            Line line_b = new Line();
            base_square.getChildren().add(line_a);
            base_square.getChildren().add(line_b);
            line_a.setStartX(i * BOUND + OFFSET * 1.5);
            line_a.setStartY(j * BOUND + OFFSET * 1.5);
            line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
            line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
            line_a.setStroke(Color.BLUE);

            line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
            line_b.setStartY(j * BOUND + OFFSET * 1.5);
            line_b.setEndX(i * BOUND + OFFSET * 1.5);
            line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
            line_b.setStroke(Color.BLUE);
            flag[i][j] = true;
        });

    }

    private void moveAction(Action re) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                chessBoard[i][j] = re.getBoard()[i][j];
                flag[i][j] = re.getFlag()[i][j];
            }
        }
        TURN = re.getTurn();
        drawChess();
        if (!re.getStatus().equals("going")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Game Over");
//                            alert.setContentText();
                if (re.getStatus().equals("1"))
                    alert.setContentText("Circle win");
//                                System.out.println("Circle win");
                else if (re.getStatus().equals("2"))
                    alert.setContentText("Line win");
//                                System.out.println("Line win");
                else alert.setContentText("Draw");
                alert.showAndWait();
                this.clientHandler.closeClient();
            });

        }
    }

    private void chooseLog() {
        Platform.runLater(() -> {
            logCheck = 0;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Log");
            alert.setHeaderText("sign in or sign up");
            alert.setContentText("Choose your option");

            ButtonType buttonTypeOne = new ButtonType("Sign in");
            ButtonType buttonTypeTwo = new ButtonType("Sign up");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                // ... user chose "One"
                alert.close();
                signIn();


            } else if (result.get() == buttonTypeTwo) {
                // ... user chose "Two"
                alert.close();
                signUp();
            } else {
                // ... user chose CANCEL or closed the dialog
                alert.close();
            }

//            clientHandler.getActions();
        });
    }

    private void signIn() {
        Platform.runLater(() -> {
            // Create the custom dialog.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Sign in Dialog");
            dialog.setHeaderText("Look, a Custom Sign in Dialog");

// Set the icon (must be included in the project).
//            dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

// Set the button types.
            ButtonType loginButtonType = new ButtonType("Sign in", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("Username");
            PasswordField password = new PasswordField();
            password.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);

// Enable/Disable login button depending on whether a username was entered.
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
            username.textProperty().addListener((observable, oldValue, newValue) -> {
                loginButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
            Platform.runLater(() -> username.requestFocus());
// Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(username.getText(), password.getText());
                } else if (dialogButton == ButtonType.CANCEL) {
                    if (logCheck == 0) {
                        chooseLog();
                        logCheck++;
                    }
                    dialog.close();
//                    System.out.println(test++);
                    return null;
                }
                return null;
            });
            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(usernamePassword -> {
//                System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
                Action log = new log("log", "signin",usernamePassword.getKey(),usernamePassword.getValue(),"","Line");
                clientHandler.send(log);
            });
        });
    }

    private void signUp() {
        Platform.runLater(() -> {
            // Create the custom dialog.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Sign up Dialog");
            dialog.setHeaderText("Look, a Custom Sign up Dialog");

// Set the icon (must be included in the project).
//            dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

// Set the button types.
            ButtonType loginButtonType = new ButtonType("Sign up", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("Username");
            PasswordField password = new PasswordField();
            password.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);

// Enable/Disable login button depending on whether a username was entered.
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
            username.textProperty().addListener((observable, oldValue, newValue) -> {
                loginButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
            Platform.runLater(() -> username.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(username.getText(), password.getText());
                } else if (dialogButton == ButtonType.CANCEL) {
                    if (logCheck == 0) {
                        chooseLog();
                        logCheck++;
                    }
                    dialog.close();
//                    System.out.println(test++);
                    return null;
                }
                return null;
            });
            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(usernamePassword -> {
                System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
                Action log = new log("log", "signup",usernamePassword.getKey(),usernamePassword.getValue(),"","Line");
                clientHandler.send(log);
            });
        });
    }
}
