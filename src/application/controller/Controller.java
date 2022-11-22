package application.controller;

import application.action.Action;
import application.action.log;
import application.action.move;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;
import javafx.util.Pair;

public class Controller implements Initializable {
  private static final int PLAY_1 = 1;
  private static final int PLAY_2 = 2;
  private static final int EMPTY = 0;
  private static final int BOUND = 90;
  private static final int OFFSET = 15;
  private static int logCheck = 0;
  private int connected = 0;


  public ArrayList<Action> actions = new ArrayList<>();


  @FXML
  private Pane base_square;

  @FXML
  private Rectangle game_panel;


  private static boolean TURN = false;

  private ClientHandler clientHandler;
  private ServerAccount accounts;
  private String myName;
  private int isAddMatch = 0;

  private static final int[][] chessBoard = new int[3][3];
  private static final boolean[][] flag = new boolean[3][3];

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    chooseLog();

    try {
      accounts = new ServerAccount();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }


    new Thread(() -> {
      try {
        while (true) {
          if (clientHandler != null) {
            Action re = clientHandler.getActions();
            if (re != null) {
              if (re.getName().equals("move")) {
                if (isAddMatch == 0) {
                  accounts.addMatch(clientHandler.getUsername(), clientHandler.myTurn);
                  String t = clientHandler.myTurn ? "Circle" : "Line";
                  user.setText("" + clientHandler.getUsername() + "&" + t);

                  isAddMatch++;
                }
                moveAction(re);
                if (!re.getStatus().equals("going"))
                  break;
              } else if (re.getName().equals("error")) {
                if (re.getStatus().equals("another player close")) {
                  accounts.changeData(clientHandler.getUsername(), clientHandler.myTurn, true);
                }
                alertMsg(re.getStatus());
                System.out.println("error");
              }

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
        Action action = new move("move", chessBoard, TURN, flag);
        clientHandler.send(action);
      }
    });

  }

  private boolean refreshBoard(int x, int y) {
    if (chessBoard[x][y] == EMPTY) {
      chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
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
        if (re.getStatus().equals("1")) {
          // TODO: change
          alert.setContentText("Circle win");
          if (clientHandler.myTurn) {
            try {
              Thread.sleep(500);
            } catch (Exception e) {

            }
          }
          accounts.changeData(clientHandler.getUsername(),
                  clientHandler.myTurn, clientHandler.myTurn);
        } else if (re.getStatus().equals("2")) {
          alert.setContentText("Line win");
          accounts.changeData(clientHandler.getUsername(),
                  clientHandler.myTurn, !clientHandler.getMyTurn());
        } else {
          alert.setContentText("Draw");
          accounts.changeData(clientHandler.getUsername(),
                  clientHandler.myTurn, false);
        }
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
        alert.close();
        System.exit(0);
      }

    });
  }

  private void signIn() {
    Platform.runLater(() -> {
      // Create the custom dialog.
      Dialog<Pair<String, String>> dialog = new Dialog<>();
      dialog.setTitle("Sign in Dialog");
      dialog.setHeaderText("Look, a Custom Sign in Dialog");


      ButtonType loginButtonType = new ButtonType("Sign in", ButtonBar.ButtonData.OK_DONE);
      dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

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

      Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
      loginButton.setDisable(true);

      username.textProperty().addListener((observable, oldValue, newValue) -> {
        loginButton.setDisable(newValue.trim().isEmpty());
      });

      dialog.getDialogPane().setContent(grid);

      Platform.runLater(() -> username.requestFocus());
      dialog.setResultConverter(dialogButton -> {
        if (dialogButton == loginButtonType) {
          return new Pair<>(username.getText(), password.getText());
        } else if (dialogButton == ButtonType.CANCEL) {
          if (logCheck == 0) {
            chooseLog();
            logCheck++;
          }
          dialog.close();
          return null;
        }
        return null;
      });
      Optional<Pair<String, String>> result = dialog.showAndWait();

      result.ifPresent(usernamePassword -> {
        if (accounts.checkUser(usernamePassword.getKey(), usernamePassword.getValue())) {
          System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
          Action log = new log("log", "signup", usernamePassword.getKey(), usernamePassword.getValue(), "", "Line");
          myName = usernamePassword.getKey();
          alertMsg("sign in successful!");
        } else {
          chooseLog();
          alertMsg("Wrong username or password!");
        }
      });
    });
  }

  private void signUp() {
    Platform.runLater(() -> {
      // Create the custom dialog.
      Dialog<Pair<String, String>> dialog = new Dialog<>();
      dialog.setTitle("Sign up Dialog");
      dialog.setHeaderText("Look, a Custom Sign up Dialog");

      ButtonType loginButtonType = new ButtonType("Sign up", ButtonBar.ButtonData.OK_DONE);
      dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
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

      Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
      loginButton.setDisable(true);

      username.textProperty().addListener((observable, oldValue, newValue) -> {
        loginButton.setDisable(newValue.trim().isEmpty());
      });

      dialog.getDialogPane().setContent(grid);

      Platform.runLater(() -> username.requestFocus());

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
        Action log = new log("log", "signup", usernamePassword.getKey(), usernamePassword.getValue(), "", "Line");
        myName = usernamePassword.getKey();
        accounts.insertUser(usernamePassword.getKey(), usernamePassword.getValue());
        alertMsg("sign up successful!");
      });
    });
  }

  private void alertMsg(String s) {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("message");
      alert.setHeaderText("notice:");
      alert.setContentText(s);
      alert.showAndWait();
    });
  }

  @FXML
  protected void connectButton() {
    if (connected == 0) {
      clientHandler = new ClientHandler(myName);
      connected++;
    }
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("connect");
      alert.setHeaderText("detail:");
      alert.setContentText("connected");
      alert.showAndWait();
    });
  }

  @FXML
  protected void accountButton() {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("account");
      alert.setHeaderText("detail:");
      if (clientHandler != null)
        alert.setContentText(accounts.getOne(clientHandler.getUsername()));
      else
        alert.setContentText("you need connect server");
      alert.showAndWait();
    });
  }

  @FXML
  protected void exitButton() {
    if (clientHandler != null)
      clientHandler.closeClient();
    System.exit(0);
  }

  @FXML
  TextArea user;
}
