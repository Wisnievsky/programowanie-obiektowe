package model;

public class Gracz {
    private int id;
    private String nick;
    private int druzynaId;

    public Gracz(int id, String nick, int druzynaId) {
        this.id = id;
        this.nick = nick;
        this.druzynaId = druzynaId;
    }

    public int getId() { return id; }
    public String getNick() { return nick; }
    public int getDruzynaId() { return druzynaId; }

    public void setId(int id) { this.id = id; }
    public void setNick(String nick) { this.nick = nick; }
    public void setDruzynaId(int druzynaId) { this.druzynaId = druzynaId; }
}