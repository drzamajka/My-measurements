package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;


@IgnoreExtraProperties
public class WpisPomiar {

    @DocumentId
    private String id;
    private String wynikPomiary;
    private String idUzytkownika;
    private String idPomiar;
    private String idEtapTerapi;
    private Date dataWykonania;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public WpisPomiar() {
    }

    public WpisPomiar(String wynikPomiary, String idPomiar, String idUzytkownika, String idEtapTerapi, Date dataWykonania, Date dataUtwozenia, Date dataAktualizacji) {
        id = null;
        this.wynikPomiary = wynikPomiary;
        this.idPomiar = idPomiar;
        this.idUzytkownika = idUzytkownika;
        this.idEtapTerapi = idEtapTerapi;
        this.dataWykonania = dataWykonania;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public WpisPomiar(String wynikPomiary, String idPomiar, String idUzytkownika, Date dataWykonania, Date dataUtwozenia, Date dataAktualizacji) {
        id = null;
        this.wynikPomiary = wynikPomiary;
        this.idPomiar = idPomiar;
        this.idUzytkownika = idUzytkownika;
        this.dataWykonania = dataWykonania;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getWynikPomiary() {
        return wynikPomiary;
    }

    public void setWynikPomiary(String wynikPomiary) {
        this.wynikPomiary = wynikPomiary;
    }

    public String getIdUzytkownika() { return idUzytkownika; }

    public void setIdUzytkownika(String idUzytkownika) { this.idUzytkownika = idUzytkownika; }

    public String getIdPomiar() {return idPomiar;}

    public void setIdPomiar(String idPomiar) {
        this.idPomiar = idPomiar;
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
        return "WpisPomiar{" +
                "id='" + id + '\'' +
                ", wynikPomiary='" + wynikPomiary + '\'' +
                ", idUzytkownika='" + idUzytkownika + '\'' +
                ", idPomiar='" + idPomiar + '\'' +
                ", idEtapTerapi='" + idEtapTerapi + '\'' +
                ", dataWykonania=" + dataWykonania +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
