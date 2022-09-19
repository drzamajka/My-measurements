package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.DateConverter;


@Entity(tableName = "jednostki", indices = {@Index(value = {"nazwa", "wartosc"}, unique = true)})
@TypeConverters(DateConverter.class)
public class Jednostka {

    @PrimaryKey
    private int id;
    private String nazwa;
    private String wartosc;
    private int dokladnosc;
    private int przeznaczenie;
    private boolean czyDomyslna;
    private int idUzytkownika;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public Jednostka() {
    }

    public Jednostka(int id, String nazwa, String wartosc, int dokladnosc, int przeznaczenie, boolean czyDomyslna,  int idUzytkownika, Date dataUtwozenia, Date dataAktualizacji) {
        this.id = id;
        this.nazwa = nazwa;
        this.wartosc = wartosc;
        this.dokladnosc = dokladnosc;
        this.przeznaczenie = przeznaczenie;
        this.czyDomyslna = czyDomyslna;
        this.idUzytkownika = idUzytkownika;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }


    public int getIdUzytkownika() {  return idUzytkownika; }

    public void setIdUzytkownika(int idUzytkownika) { this.idUzytkownika = idUzytkownika; }

    public int getDokladnosc() { return dokladnosc; }

    public void setDokladnosc(int dokladnosc) { this.dokladnosc = dokladnosc; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getWartosc() {
        return wartosc;
    }

    public void setWartosc(String wartosc) {
        this.wartosc = wartosc;
    }

    public int getPrzeznaczenie() {
        return przeznaczenie;
    }

    public void setPrzeznaczenie(int przeznaczenie) {
        this.przeznaczenie = przeznaczenie;
    }

    public boolean getCzyDomyslna() {
        return czyDomyslna;
    }

    public void setCzyDomyslna(boolean czyDomyslna) {
        this.czyDomyslna = czyDomyslna;
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
        return "Jednostka{" +
                "id=" + id +
                ", nazwa='" + nazwa + '\'' +
                ", wartosc='" + wartosc + '\'' +
                ", dokladnosc=" + dokladnosc +
                ", przeznaczenie=" + przeznaczenie +
                ", czyDomyslna=" + czyDomyslna +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
