package application.body;


public class account {
    private String username = null;
    private String password = null;
    public int circle_match = 0;
    public int circle_win = 0;
    public int line_match = 0;
    public int line_win = 0;

    public account(String username, String password, int circle_match, int circle_win, int line_match, int line_win) {
        this.username = username;
        this.password = password;
        this.circle_match = circle_match;
        this.circle_win = circle_win;
        this.line_match = line_match;
        this.line_win = line_win;
    }

    public account() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getCircle_match() {
        return circle_match;
    }

    public int getCircle_win() {
        return circle_win;
    }

    public int getLine_match() {
        return line_match;
    }

    public int getLine_win() {
        return line_win;
    }
    public String getAccount(){
        return ""+username+","+password+","+circle_match+","+circle_win+","+line_match+","+line_win;
    }
}
