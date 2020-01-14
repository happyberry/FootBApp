package sample;

public class RekordStrzelcow {
    public String imie;
    public String nazwisko;
    public String nazwaKlubu;
    public Integer liczbaPunktow;

    public RekordStrzelcow(String imie, String nazwisko, String nazwa, Integer liczba) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        nazwaKlubu = nazwa;
        liczbaPunktow = liczba;
    }

    public String getImie() { return this.imie; }

    public void setImie(String imie) { this.imie = imie; }

    public String getNazwisko() { return this.nazwisko; }

    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }

    public String getNazwaKlubu() { return nazwaKlubu; }

    public void setNazwaKlubu(String nazwaKlubu) { this.nazwaKlubu = nazwaKlubu; }

    public Integer getLiczbaPunktow() { return liczbaPunktow; }

    public void setLiczbaPunktow(Integer liczba) {this.liczbaPunktow = liczba; }

}

