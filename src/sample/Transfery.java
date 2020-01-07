package sample;


import java.sql.Date;

public class Transfery {

  private Double kwotaTransferu;
  private String klubSprzedajacy;
  private String idPilkarza;
  private java.sql.Date dataTransferu;
  private String klubKupujacy;
  private String danePilkarza;

  public Transfery(double kwota, String sprzedajacy, String id, Date data, String kupujacy, String dane) {
    kwotaTransferu = kwota;
    klubSprzedajacy = sprzedajacy;
    idPilkarza = id;
    dataTransferu = data;
    klubKupujacy = kupujacy;
    danePilkarza = dane;
  }


  public Double getKwotaTransferu() {
    return kwotaTransferu;
  }

  public void setKwotaTransferu(Double kwotaTransferu) {
    this.kwotaTransferu = kwotaTransferu;
  }


  public String getKlubSprzedajacy() {
    return klubSprzedajacy;
  }

  public void setKlubSprzedajacy(String klubSprzedajacy) {
    this.klubSprzedajacy = klubSprzedajacy;
  }


  public String getIdPilkarza() {
    return idPilkarza;
  }

  public void setIdPilkarza(String idPilkarza) {
    this.idPilkarza = idPilkarza;
  }


  public java.sql.Date getDataTransferu() {
    return dataTransferu;
  }

  public void setDataTransferu(java.sql.Date dataTransferu) {
    this.dataTransferu = dataTransferu;
  }


  public String getKlubKupujacy() {
    return klubKupujacy;
  }

  public void setKlubKupujacy(String klubKupujacy) {
    this.klubKupujacy = klubKupujacy;
  }

  public String getDanePilkarza() {
    return danePilkarza;
  }

  public void setDanePilkarza(String danePilkarza) {
    this.danePilkarza = danePilkarza;
  }
}
