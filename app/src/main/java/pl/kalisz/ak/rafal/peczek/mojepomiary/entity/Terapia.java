package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.DateConverter;

@Entity(tableName = "terapie")
@TypeConverters(DateConverter.class)
public class Terapia {

    @PrimaryKey
    private int id;
    private int idUzytkownika;
    private int typ;
    private ArrayList<Integer> idsCzynnosci;
    private Date dataRozpoczecia;
    private Date dataZakonczenia;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public Terapia() {
    }

    public Terapia(int id, int idUzytkownika, int typ, ArrayList<Integer> idsCzynnosci, Date dataRozpoczecia, Date dataZakonczenia, Date dataUtwozenia, Date dataAktualizacji) {
        this.id = id;
        this.idUzytkownika = idUzytkownika;
        this.typ = typ;
        this.idsCzynnosci = idsCzynnosci;
        this.dataRozpoczecia = dataRozpoczecia;
        this.dataZakonczenia = dataZakonczenia;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUzytkownika() {
        return idUzytkownika;
    }

    public void setIdUzytkownika(int idUzytkownika) {
        this.idUzytkownika = idUzytkownika;
    }

    public int getTyp() {
        return typ;
    }

    public void setTyp(int typ) {
        this.typ = typ;
    }

    public ArrayList<Integer> getIdsCzynnosci() {
        return idsCzynnosci;
    }

    public void setIdsCzynnosci(ArrayList<Integer> idsCzynnosci) { this.idsCzynnosci = idsCzynnosci; }

    public Date getDataRozpoczecia() {
        return dataRozpoczecia;
    }

    public void setDataRozpoczecia(Date dataRozpoczecia) {
        this.dataRozpoczecia = dataRozpoczecia;
    }

    public Date getDataZakonczenia() {
        return dataZakonczenia;
    }

    public void setDataZakonczenia(Date dataZakonczenia) {
        this.dataZakonczenia = dataZakonczenia;
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
        return "Terapia{" +
                "id=" + id +
                ", idUzytkownika=" + idUzytkownika +
                ", typ=" + typ +
                ", idsCzynnosci=" + idsCzynnosci.toString() +
                ", dataRozpoczecia=" + dataRozpoczecia +
                ", dataZakonczenia=" + dataZakonczenia +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
