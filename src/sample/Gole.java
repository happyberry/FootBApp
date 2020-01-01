package sample;


public class Gole {

  private String golId;
  private String meczId;
  private String idPilkarza;
  private String minuta;
  private String czySamobojczy;
  private String czyDlaGospodarzy;

  public Gole(String id, String mecz, String idpilkarza, String minuta, String czysam, String czygosp){
    golId = id;
    meczId = mecz;
    idPilkarza = idpilkarza;
    this.minuta = minuta;
    czySamobojczy = czysam;
    czyDlaGospodarzy = czygosp;
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


  public String getMinuta() {
    return minuta;
  }

  public void setMinuta(String minuta) {
    this.minuta = minuta;
  }


  public String getCzySamobojczy() {
    return czySamobojczy;
  }

  public void setCzySamobojczy(String czySamobojczy) {
    this.czySamobojczy = czySamobojczy;
  }


  public String getCzyDlaGospodarzy() {
    return czyDlaGospodarzy;
  }

  public void setCzyDlaGospodarzy(String czyDlaGospodarzy) {
    this.czyDlaGospodarzy = czyDlaGospodarzy;
  }

}
