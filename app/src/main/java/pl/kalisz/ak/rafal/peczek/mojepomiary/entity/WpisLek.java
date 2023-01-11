package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;


@IgnoreExtraProperties
public class WpisLek {

    @DocumentId
    private String id;
    private String sumaObrotu;
    private String pozostalyZapas;
    private String idUzytkownika;
    private String idLeku;
    private String idEtapTerapi;
    private Date dataWykonania;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public WpisLek() {
    }

    public WpisLek(String sumaObrotu, String pozostalyZapas, String idLeku, String idUzytkownika, String idEtapTerapi, Date dataWykonania, Date dataUtwozenia, Date dataAktualizacji) {
        id = null;
        this.sumaObrotu = sumaObrotu;
        this.pozostalyZapas = pozostalyZapas;
        this.idLeku = idLeku;
        this.idUzytkownika = idUzytkownika;
        this.idEtapTerapi = idEtapTerapi;
        this.dataWykonania = dataWykonania;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public WpisLek(String sumaObrotu, String pozostalyZapas, String idLeku, String idUzytkownika, Date dataWykonania, Date dataUtwozenia, Date dataAktualizacji) {
        id = null;
        this.sumaObrotu = sumaObrotu;
        this.pozostalyZapas = pozostalyZapas;
        this.idLeku = idLeku;
        this.idUzytkownika = idUzytkownika;
        this.dataWykonania = dataWykonania;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSumaObrotu() {
        return sumaObrotu;
    }

    public void setSumaObrotu(String sumaObrotu) {
        this.sumaObrotu = sumaObrotu;
    }

    public String getPozostalyZapas() {
        return pozostalyZapas;
    }

    public void setPozostalyZapas(String pozostalyZapas) {
        this.pozostalyZapas = pozostalyZapas;
    }

    public String getIdUzytkownika() {
        return idUzytkownika;
    }

    public void setIdUzytkownika(String idUzytkownika) {
        this.idUzytkownika = idUzytkownika;
    }

    public String getIdLeku() {
        return idLeku;
    }

    public void setIdLeku(String idLeku) {
        this.idLeku = idLeku;
    }

    public String getIdEtapTerapi() {
        return idEtapTerapi;
    }

    public void setIdEtapTerapi(String idEtapTerapi) {
        this.idEtapTerapi = idEtapTerapi;
    }

    public Date getDataWykonania() {
        return dataWykonania;
    }

    public void setDataWykonania(Date dataWykonania) {
        this.dataWykonania = dataWykonania;
    }

    public Date getDataUtwozenia() {
        return dataUtwozenia;
    }

    public void setDataUtwozenia(Date dataUtwozenia) {
        this.dataUtwozenia = dataUtwozenia;
    }

    public Date getDataAktualizacji() {
        return dataAktualizacji;
    }

    public void setDataAktualizacji(Date dataAktualizacji) {
        this.dataAktualizacji = dataAktualizacji;
    }

    @Override
    public String toString() {
        return "WpisLek{" +
                "id='" + id + '\'' +
                ", sumaObrotu='" + sumaObrotu + '\'' +
                ", pozostalyZapas='" + pozostalyZapas + '\'' +
                ", idUzytkownika='" + idUzytkownika + '\'' +
                ", idLeku='" + idLeku + '\'' +
                ", idEtapTerapi='" + idEtapTerapi + '\'' +
                ", dataWykonania=" + dataWykonania +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
