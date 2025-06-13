package model;

public class Mecz {
    private int druzyna1Id;
    private int druzyna2Id;
    private String mapa;
    private int wynik1;
    private int wynik2;
    private int zwyciezcaId;

    public Mecz(int d1, int d2, String mapa, int wynik1, int wynik2, int zwyciezcaId) {
        this.druzyna1Id = d1;
        this.druzyna2Id = d2;
        this.mapa = mapa;
        this.wynik1 = wynik1;
        this.wynik2 = wynik2;
        this.zwyciezcaId = zwyciezcaId;
    }

    public int getDruzyna1Id() { return druzyna1Id; }
    public int getDruzyna2Id() { return druzyna2Id; }
    public String getMapa() { return mapa; }
    public int getWynik1() { return wynik1; }
    public int getWynik2() { return wynik2; }
    public int getZwyciezcaId() { return zwyciezcaId; }

    public void setWynik1(int wynik) { this.wynik1 = wynik; }
    public void setWynik2(int wynik) { this.wynik2 = wynik; }
    public void setZwyciezcaId(int id) { this.zwyciezcaId = id; }
}