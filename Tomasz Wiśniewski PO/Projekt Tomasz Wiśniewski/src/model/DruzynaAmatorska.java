package model;

public class DruzynaAmatorska extends Druzyna {
    private String miasto;

    public DruzynaAmatorska(int id, String nazwa, String miasto) {
        super(id, nazwa);
        this.miasto = miasto;
    }

    public String getMiasto() { return miasto; }
    public void setMiasto(String miasto) { this.miasto = miasto; }

    @Override
    public String pobierzTyp() {
        return "Amatorska";
    }

    @Override
    public String toString() { return String.format("%s [AM]", super.getNazwa()); }
}