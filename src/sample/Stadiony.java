package sample;


public class Stadiony {

  private String nazwa;
  private Integer rokZbudowania;
  private Integer pojemnosc;
  private String miasto;
  private String nazwaKlubu;

    public Stadiony(String nazwa, Integer rok, int capacity, String miasto, String nazwaKlubu) {
      this.nazwa = nazwa;
      rokZbudowania = rok;
      pojemnosc = capacity;
      this.miasto = miasto;
      this.nazwaKlubu = nazwaKlubu;
    }


    public String getNazwa() {
    return nazwa;
  }

  public void setNazwa(String nazwa) {
    this.nazwa = nazwa;
  }


  public Integer getRokZbudowania() {
    return rokZbudowania;
  }

  public void setRokZbudowania(Integer rokZbudowania) {
    this.rokZbudowania = rokZbudowania;
  }


  public Integer getPojemnosc() {
    return pojemnosc;
  }

  public void setPojemnosc(Integer pojemnosc) {
    this.pojemnosc = pojemnosc;
  }


  public String getMiasto() {
    return miasto;
  }

  public void setMiasto(String miasto) {
    this.miasto = miasto;
  }


  public String getNazwaKlubu() {
    return nazwaKlubu;
  }

  public void setNazwaKlubu(String nazwaKlubu) {
    this.nazwaKlubu = nazwaKlubu;
  }

}
