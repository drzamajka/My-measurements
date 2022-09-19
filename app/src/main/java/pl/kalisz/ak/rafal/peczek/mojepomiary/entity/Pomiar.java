package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.DateConverter;

@Entity(tableName = "pomiary", indices = {@Index(value = {"nazwa"}, unique = true)})
@TypeConverters(DateConverter.class)
public class Pomiar {
    @PrimaryKey
    private int id;
    private String nazwa;
    private String notatka;
    private int idUzytkownika;
    private int idJednostki;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public Pomiar() {
    }

    public Pomiar(int id, String nazwa, String notatka, int idUzytkownika, int idJednostki, Date dataUtwozenia, Date dataAktualizacji) {
        this.id = id;
        this.nazwa = nazwa;
        this.notatka = notatka;
        this.idUzytkownika = idUzytkownika;
        this.idJednostki = idJednostki;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

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

    public String getNotatka() {
        return notatka;
    }

    public void setNotatka(String notatka) {
        this.notatka = notatka;
    }

    public int getIdUzytkownika() {
        return idUzytkownika;
    }

    public void setIdUzytkownika(int idUzytkownika) {
        this.idUzytkownika = idUzytkownika;
    }

    public int getIdJednostki() {
        return idJednostki;
    }

    public void setIdJednostki(int idJednostki) {
        this.idJednostki = idJednostki;
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
        return "Pomiar{" +
                "id=" + id +
                ", nazwa='" + nazwa + '\'' +
                ", notatka='" + notatka + '\'' +
                ", idUzytkownika=" + idUzytkownika +
                ", idJednostki=" + idJednostki +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
