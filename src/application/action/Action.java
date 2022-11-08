package application.action;

public interface Action {
    public String getName();
    public int[][] getBoard();
    public boolean getTurn();
    public void setTurn();
    public boolean[][] getFlag();
    public String toString();
    public String getStatus();
    public String getType();
    public String getUserName();
    public String getPassword();
}
