package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.DateConverter;

@Entity(tableName = "wpis_pomiar")
@TypeConverters(DateConverter.class)
public class WpisPomiar {

    @PrimaryKey
    private int id;
    private String wynikPomiary;
    private int idPomiar;
    private int idEtapTerapi;
    private Date dataWykonania;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public WpisPomiar() {
    }

    public WpisPomiar(int id, String wynikPomiary, int idPomiar, int idEtapTerapi, Date dataWykonania, Date dataUtwozenia, Date dataAktualizacji) {
        this.id = id;
        this.wynikPomiary = wynikPomiary;
        this.idPomiar = idPomiar;
        this.idEtapTerapi = idEtapTerapi;
        this.dataWykonania = dataWykonania;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public WpisPomiar(int id, String wynikPomiary, int idPomiar, Date dataWykonania, Date dataUtwozenia, Date dataAktualizacji) {
        this.id = id;
        this.wynikPomiary = wynikPomiary;
        this.idPomiar = idPomiar;
        this.dataWykonania = dataWykonania;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWynikPomiary() {
        return wynikPomiary;
    }

    public void setWynikPomiary(String wynikPomiary) {
        this.wynikPomiary = wynikPomiary;
    }

    public int getIdPomiar() {
        return idPomiar;
    }

    public void setIdPomiar(int idPomiar) {
        this.idPomiar = idPomiar;
    }

    public int getIdEtapTerapi() {
        return idEtapTerapi;
    }

    public void setIdEtapTerapi(int idEtapTerapi) {
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
                "id=" + id +
                ", wynikPomiary='" + wynikPomiary + '\'' +
                ", idPomiar=" + idPomiar +
                ", idEtapTerapi=" + idEtapTerapi +
                ", dataWykonania=" + dataWykonania +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
