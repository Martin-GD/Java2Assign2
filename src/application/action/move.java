package application.action;

import java.util.Arrays;

public class move implements Action {
  private String Name;
  private int[][] Board;
  private boolean Turn;
  private boolean[][] Flag;
  private String Status;

  public move(String name, int[][] board, boolean turn, boolean[][] flag) {
    Name = name;
    Board = board;
    Turn = turn;
    Flag = flag;
    Status = checkStatus();
  }

  public move(String str) {
    String[] s = str.split(",");
    int cnt = 0;
    Name = s[cnt++];
    Board = new int[3][3];
    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 3; j++)
        Board[i][j] = Integer.parseInt(s[cnt++]);

    Turn = Boolean.parseBoolean(s[cnt++]);
    Flag = new boolean[3][3];
    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 3; j++)
        Flag[i][j] = Boolean.parseBoolean(s[cnt++]);
    Status = checkStatus();

  }

  private String checkStatus() {


    for (int i = 0; i < 3; i++) {
      if (Board[i][0] == Board[i][1] && Board[i][0] == Board[i][2] && Board[i][2] != 0) {
        if (Board[i][0] == 1)
          return "1";
        else return "2";
      }
    }
    for (int i = 0; i < 3; i++) {
      if (Board[0][i] == Board[1][i] && Board[0][i] == Board[2][i] && Board[0][i] != 0) {
        if (Board[0][i] == 1)
          return "1";
        else return "2";
      }
    }
    if (Board[0][0] == Board[1][1] && Board[0][0] == Board[2][2] && Board[2][2] != 0) {
      if (Board[0][0] == 1)
        return "1";
      else return "2";
    }
    if (Board[2][0] == Board[1][1] && Board[2][0] == Board[0][2] && Board[0][2] != 0) {
      if (Board[2][0] == 1)
        return "1";
      else return "2";
    }
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (Board[i][j] == 0)
          return "going";
      }
    }
    return "tie";
  }

  @Override
  public String getName() {
    return Name;
  }

  @Override
  public int[][] getBoard() {
    return Board;
  }

  @Override
  public boolean getTurn() {
    return Turn;
  }

  @Override
  public void setTurn() {
    Turn = !Turn;
  }

  @Override
  public boolean[][] getFlag() {
    return Flag;
  }

  @Override
  public String toString() {
    return "" + Name + "," + Board[0][0] + "," + Board[0][1] + "," + Board[0][2] + ","
            + Board[1][0] + "," + Board[1][1] + "," + Board[1][2] + ","
            + Board[2][0] + "," + Board[2][1] + "," + Board[2][2] + ","
            + Turn + "," + Flag[0][0] + "," + Flag[0][1] + "," + Flag[0][2] + ","
            + Flag[1][0] + "," + Flag[1][1] + "," + Flag[1][2] + ","
            + Flag[2][0] + "," + Flag[2][1] + "," + Flag[2][2];
  }

  @Override
  public String getStatus() {
    return Status;
  }

  @Override
  public String getType() {
    return null;
  }

  @Override
  public String getUserName() {
    return null;
  }

  @Override
  public String getPassword() {
    return null;
  }
}
