package application.action;

public class log implements Action{
    private String name;
    private String type;
    private String userName;
    private String password;
    public String status;
    public String identity;
    public log() {
    }
    public log(String name, String type, String userName, String password, String status, String identity){
        this.name = name;
        this.type = type;
        this.userName = userName;
        this.password = password;
        this.status = status;
        this.identity = identity;
    }
    public log(String s){
        String[] srr = s.split(",");
        this.name = srr[0];
        this.type = srr[1];
        this.userName = srr[2];
        this.password = srr[3];
        this.status = srr[4];
        this.identity = srr[5];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int[][] getBoard() {
        return null;
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
        return null;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getType() {
        return type;
    }
    @Override
    public String getUserName(){
        return userName;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String toString(){
        return ""+name+","+type+","+userName+","+password+","+status+","+identity;
    }
}
