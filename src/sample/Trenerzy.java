package sample;


public class Trenerzy {

  private String idTrenera;
  private String imie;
  private String nazwisko;
  private String pochodzenie;
  private String nazwaKlubu;

  public Trenerzy(String idTrenera, String imie, String nazwisko, String kraj, String klub) {
    this.idTrenera = idTrenera;
    this.imie = imie;
    this.nazwisko = nazwisko;
    this.pochodzenie = kraj;
    this.nazwaKlubu = klub;
  }


  public String getIdTrenera() {
    return idTrenera;
  }

  public void setIdTrenera(String idTrenera) {
    this.idTrenera = idTrenera;
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


  public String getPochodzenie() {
    return pochodzenie;
  }

  public void setPochodzenie(String pochodzenie) {
    this.pochodzenie = pochodzenie;
  }


  public String getNazwaKlubu() {
    return nazwaKlubu;
  }

  public void setNazwaKlubu(String nazwaKlubu) {
    this.nazwaKlubu = nazwaKlubu;
  }

}
