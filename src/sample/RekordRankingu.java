package sample;

public class RekordRankingu {

    public String nazwaKlubu;
    public Integer liczbaPunktow;

    public RekordRankingu(String nazwa, Integer liczba) {
        nazwaKlubu = nazwa;
        liczbaPunktow = liczba;
    }

    public String getNazwaKlubu() { return nazwaKlubu; }

    public void setNazwaKlubu(String nazwaKlubu) { this.nazwaKlubu = nazwaKlubu; }

    public Integer getLiczbaPunktow() { return liczbaPunktow; }

    public void setLiczbaPunktow(Integer liczba) {this.liczbaPunktow = liczba; }

}
