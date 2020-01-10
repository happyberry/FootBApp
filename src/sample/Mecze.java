package sample;


public class Mecze {

  private String meczId;
  private java.sql.Date data;
  private String gospodarze;
  private String goscie;
  private Integer wynikGospodarzy;
  private Integer wynikGosci;
  private String idSedziego;
  private String daneSedziego;

  public Mecze (String mId, java.sql.Date date, String gosp, String gosc,
                Integer wgosp, Integer wgosci, String idS, String dane){
    meczId = mId;
    data = date;
    gospodarze = gosp;
    goscie = gosc;
    wynikGospodarzy = wgosp;
    wynikGosci = wgosci;
    idSedziego = idS;
    daneSedziego = dane;
  }

  public String getMeczId() {
    return meczId;
  }

  public void setMeczId(String meczId) {
    this.meczId = meczId;
  }


  public java.sql.Date getData() {
    return data;
  }

  public void setData(java.sql.Date data) {
    this.data = data;
  }


  public String getGospodarze() {
    return gospodarze;
  }

  public void setGospodarze(String gospodarze) {
    this.gospodarze = gospodarze;
  }


  public String getGoscie() {
    return goscie;
  }

  public void setGoscie(String goscie) {
    this.goscie = goscie;
  }


  public Integer getWynikGospodarzy() {
    return wynikGospodarzy;
  }

  public void setWynikGospodarzy(Integer wynikGospodarzy) {
    this.wynikGospodarzy = wynikGospodarzy;
  }


  public Integer getWynikGosci() {
    return wynikGosci;
  }

  public void setWynikGosci(Integer wynikGosci) {
    this.wynikGosci = wynikGosci;
  }


  public String getIdSedziego() {
    return idSedziego;
  }

  public void setIdSedziego(String idSedziego) {
    this.idSedziego = idSedziego;
  }

  public String getDaneSedziego() {
    return daneSedziego;
  }

  public void setDaneSedziego(String daneSedziego) {
    this.daneSedziego = daneSedziego;
  }
}
