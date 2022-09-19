package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.DateConverter;

@Entity(tableName = "etap_terapa")
@TypeConverters(DateConverter.class)
public class EtapTerapa {

    @PrimaryKey
    private int id;
    private Date dataZaplanowania;
    private String wynikPomiary;
    private int idTerapi;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public EtapTerapa() {
    }

    public EtapTerapa(int id, Date dataZaplanowania, String wynikPomiary, int idTerapi, Date dataUtwozenia, Date dataAktualizacji) {
        this.id = id;
        this.dataZaplanowania = dataZaplanowania;
        this.wynikPomiary = wynikPomiary;
        this.idTerapi = idTerapi;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDataZaplanowania() {
        return dataZaplanowania;
    }

    public void setDataZaplanowania(Date dataZaplanowania) {
        this.dataZaplanowania = dataZaplanowania;
    }

    public String getWynikPomiary() {
        return wynikPomiary;
    }

    public void setWynikPomiary(String wynikPomiary) {
        this.wynikPomiary = wynikPomiary;
    }

    public int getIdTerapi() {
        return idTerapi;
    }

    public void setIdTerapi(int idTerapi) {
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
                "id=" + id +
                ", dataZaplanowania=" + dataZaplanowania +
                ", wynikPomiary='" + wynikPomiary + '\'' +
                ", idTerapi=" + idTerapi +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
