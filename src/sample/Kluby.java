package sample;


public class Kluby {

  private String nazwaKlubu;
  private Integer rokZalozenia;
  private String nazwaLigi;

  public Kluby(String nazwaKlubu, Integer rokZalozenia, String nazwaLigi) {
    this.nazwaKlubu = nazwaKlubu;
    this.rokZalozenia = rokZalozenia;
    this.nazwaLigi = nazwaLigi;
  }

  public String getNazwaKlubu() {
    return nazwaKlubu;
  }

  public void setNazwaKlubu(String nazwaKlubu) {
    this.nazwaKlubu = nazwaKlubu;
  }


  public Integer getRokZalozenia() {
    return rokZalozenia;
  }

  public void setRokZalozenia(Integer rokZalozenia) {
    this.rokZalozenia = rokZalozenia;
  }


  public String getNazwaLigi() {
    return nazwaLigi;
  }

  public void setNazwaLigi(String nazwaLigi) {
    this.nazwaLigi = nazwaLigi;
  }

}
