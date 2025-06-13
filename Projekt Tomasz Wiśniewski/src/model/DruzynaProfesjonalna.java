package model;

// Konkretna implementacja drużyny w tym przypadku profesjonalnej. Rozszerza klasę Druzyna i dodaje swoje unikalne pole - sponsora.
public class DruzynaProfesjonalna extends Druzyna {

    private String sponsor;

    public DruzynaProfesjonalna(int id, String nazwa, String sponsor) {
        super(id, nazwa);
        this.sponsor = sponsor;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String s) {
        this.sponsor = s;
    }

    @Override
    public String pobierzTyp() {
        return "Profesjonalna";
    }

    @Override
    public String toString() {
        // 5. Nadpisuję metodę toString() żeby w liście w GUI było widać że to drużyna 'PRO'.
        return String.format("%s [PRO]", super.getNazwa());
    }
}