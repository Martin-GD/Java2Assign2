package application.action;

public class error implements Action {
  private String name;
  private String status;

  public error() {
  }

  public error(String name, String status) {
    this.name = name;
    this.status = status;
  }

  public error(String s) {
    String[] srr = s.split(",");
    this.name = srr[0];
    this.status = srr[1];
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "" + name + "," + status;
  }

  @Override
  public int[][] getBoard() {
    return new int[0][];
  }

  @Override
  public boolean getTurn() {
    return false;
  }

  @Override
  public void setTurn() {

  }

  @Override
  public boolean[][] getFlag() {
    return new boolean[0][];
  }

  @Override
  public String getStatus() {
    return status;
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
