package model;

public class DruzynaUniwersytecka extends Druzyna {

    String uczelnia;

    public DruzynaUniwersytecka(int id, String nazwa, String uczelnia) {
        super(id, nazwa);
        this.uczelnia = uczelnia;
    }

    public String getUczelnia() { return uczelnia; }

    public void setUczelnia(String uczelnia) { this.uczelnia = uczelnia; }

    @Override
    public String pobierzTyp() { return "Uniwersytecka"; }

    @Override
    public String toString() {
        return String.format("%s (%s)", super.getNazwa(), this.uczelnia);
    }
}