package model;

public class DruzynaFirmowa extends Druzyna {

    private String firma;

    public DruzynaFirmowa(int id, String nazwa, String firma) {
        super(id, nazwa);
        this.firma = firma;
    }

    public String getFirma() { return firma; }
    public void setFirma(String firma) { this.firma = firma; }

    @Override
    public String pobierzTyp() {
        return "Firmowa";
    }

    @Override
    public String toString() {
        return String.format("%s (Firma: %s)", super.getNazwa(), this.firma);
    }
}