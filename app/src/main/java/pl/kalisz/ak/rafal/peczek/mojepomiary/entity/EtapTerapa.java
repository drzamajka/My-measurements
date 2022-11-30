package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;



@IgnoreExtraProperties
public class EtapTerapa {

    @Exclude
    private String id;
    private Date dataZaplanowania;
    private Date dataWykonania;
    private String notatka;
    private String idTerapi;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public EtapTerapa() {
    }

    public EtapTerapa(Date dataZaplanowania, Date dataWykonania, String notatka, String idTerapi, Date dataUtwozenia, Date dataAktualizacji) {
        this.id = null;
        this.dataZaplanowania = dataZaplanowania;
        this.dataWykonania = dataWykonania;
        this.notatka = notatka;
        this.idTerapi = idTerapi;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Date getDataZaplanowania() {
        return dataZaplanowania;
    }

    public void setDataZaplanowania(Date dataZaplanowania) { this.dataZaplanowania = dataZaplanowania; }

    public Date getDataWykonania() { return dataWykonania; }

    public void setDataWykonania(Date dataWykonania) { this.dataWykonania = dataWykonania; }

    public String getNotatka() {
        return notatka;
    }

    public void setNotatka(String notatka) {
        this.notatka = notatka;
    }

    public String getIdTerapi() {
        return idTerapi;
    }

    public void setIdTerapi(String idTerapi) {
        this.idTerapi = idTerapi;
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
        return "EtapTerapa{" +
                "dataZaplanowania=" + dataZaplanowania +
                ", dataWykonania=" + dataWykonania +
                ", notatka='" + notatka +
                ", idTerapi=" + idTerapi +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
