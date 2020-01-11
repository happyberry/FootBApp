package sample;


import java.sql.Date;

public class Gole {

  private String golId;
  private String meczId;
  private String idPilkarza;
  private Integer minuta;
  private Integer czySamobojczy;
  private Integer czyDlaGospodarzy;
  private String daneStrzelca;
  private String okolicznosci;
  private String gospodarze;
  private String goscie;
  private Date data;

  public Gole(String id, String mecz, String idpilkarza, Integer minuta, Integer czysam, Integer czygosp, String dane, String oko, String gospodarze, String goscie, Date data){
    this.data = data;
    golId = id;
    meczId = mecz;
    idPilkarza = idpilkarza;
    this.minuta = minuta;
    czySamobojczy = czysam;
    czyDlaGospodarzy = czygosp;
    daneStrzelca = dane;
    okolicznosci = oko;
    this.gospodarze = gospodarze;
    this.goscie = goscie;
  }

  public String getGolId() {
    return golId;
  }

  public void setGolId(String golId) {
    this.golId = golId;
  }


  public String getMeczId() {
    return meczId;
  }

  public void setMeczId(String meczId) {
    this.meczId = meczId;
  }


  public String getIdPilkarza() {
    return idPilkarza;
  }

  public void setIdPilkarza(String idPilkarza) {
    this.idPilkarza = idPilkarza;
  }


  public Integer getMinuta() {
    return minuta;
  }

  public void setMinuta(Integer minuta) {
    this.minuta = minuta;
  }


  public Integer getCzySamobojczy() {
    return czySamobojczy;
  }

  public void setCzySamobojczy(Integer czySamobojczy) {
    this.czySamobojczy = czySamobojczy;
  }


  public Integer getCzyDlaGospodarzy() {
    return czyDlaGospodarzy;
  }

  public void setCzyDlaGospodarzy(Integer czyDlaGospodarzy) {
    this.czyDlaGospodarzy = czyDlaGospodarzy;
  }

  public String getDaneStrzelca() {
    return daneStrzelca;
  }

  public void setDaneStrzelca(String daneStrzelca) {
    this.daneStrzelca = daneStrzelca;
  }

  public String getOkolicznosci() {
    return okolicznosci;
  }

  public void setOkolicznosci(String okolicznosci) {
    this.okolicznosci = okolicznosci;
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

  public Date getData() {
    return data;
  }

  public void setData(Date data) {
    this.data = data;
  }
}
