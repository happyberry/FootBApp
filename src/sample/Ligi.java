package sample;


public class Ligi {

  private String nazwaLigi;
  private String kraj;

  public Ligi(String nazwa, String country){
    nazwaLigi = nazwa;
    kraj = country;
  }

  public String getNazwaLigi() {
    return nazwaLigi;
  }

  public void setNazwaLigi(String nazwaLigi) {
    this.nazwaLigi = nazwaLigi;
  }


  public String getKraj() {
    return kraj;
  }

  public void setKraj(String kraj) {
    this.kraj = kraj;
  }

}
