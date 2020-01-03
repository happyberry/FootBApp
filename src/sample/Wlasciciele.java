package sample;


public class Wlasciciele {

  private String idWlasciciela;
  private String imie;
  private String nazwisko;
  private Double majatek;
  private String nazwaKlubu;

  public Wlasciciele(String id, String imie, String nazwisko, Double majatek, String klub) {
    this.idWlasciciela = id;
    this.imie = imie;
    this.nazwisko = nazwisko;
    this.majatek = majatek;
    this.nazwaKlubu = klub;
  }


  public String getIdWlasciciela() {
    return idWlasciciela;
  }

  public void setIdWlasciciela(String idWlasciciela) {
    this.idWlasciciela = idWlasciciela;
  }


  public String getImie() {
    return imie;
  }

  public void setImie(String imie) {
    this.imie = imie;
  }


  public String getNazwisko() {
    return nazwisko;
  }

  public void setNazwisko(String nazwisko) {
    this.nazwisko = nazwisko;
  }


  public Double getMajatek() {
    return majatek;
  }

  public void setMajatek(Double majatek) {
    this.majatek = majatek;
  }


  public String getNazwaKlubu() {
    return nazwaKlubu;
  }

  public void setNazwaKlubu(String nazwaKlubu) {
    this.nazwaKlubu = nazwaKlubu;
  }

}
