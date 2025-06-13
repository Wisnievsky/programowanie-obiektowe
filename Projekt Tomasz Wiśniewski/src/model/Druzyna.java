package model;

// To jest moja klasa bazowa dla wszystkich drużyn. Zrobiłem ją abstrakcyjną żeby nie dało się przypadkiem stworzyć obiektu 'ogólnej' drużyny. Każda drużyna musi być konkretnego typu.
public abstract class Druzyna {
    protected int id;
    protected String nazwa;

    public Druzyna(int id, String name) {
        this.nazwa = name;
        this.id = id;
    }

    public abstract String pobierzTyp();

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getNazwa() { return nazwa; }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    @Override
    public String toString() {
        return this.nazwa;
    }
}