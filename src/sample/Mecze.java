package sample;


public class Mecze {

  private String meczId;
  private java.sql.Date data;
  private String gospodarze;
  private String goscie;
  private String wynikGospodarzy;
  private String wynikGosci;
  private String idSedziego;

  public Mecze (String mId, java.sql.Date date, String gosp, String gosc,
                String wgosp, String wgosci, String idS){
    meczId = mId;
    data = date;
    gospodarze = gosp;
    goscie = gosc;
    wynikGospodarzy = wgosp;
    wynikGosci = wgosci;
    idSedziego = idS;
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


  public String getWynikGospodarzy() {
    return wynikGospodarzy;
  }

  public void setWynikGospodarzy(String wynikGospodarzy) {
    this.wynikGospodarzy = wynikGospodarzy;
  }


  public String getWynikGosci() {
    return wynikGosci;
  }

  public void setWynikGosci(String wynikGosci) {
    this.wynikGosci = wynikGosci;
  }


  public String getIdSedziego() {
    return idSedziego;
  }

  public void setIdSedziego(String idSedziego) {
    this.idSedziego = idSedziego;
  }

}
