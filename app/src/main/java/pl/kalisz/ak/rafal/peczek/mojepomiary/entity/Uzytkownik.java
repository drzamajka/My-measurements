package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.DateConverter;


@IgnoreExtraProperties
public class Uzytkownik {


    private String imie;
    private String nazwisko;
    private Date dataUrodzenia;
    private String eMail;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public Uzytkownik() {
    }

    public Uzytkownik(String imie, String nazwisko, Date dataUrodzenia, String eMail, Date dataUtwozenia, Date dataAktualizacji) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.dataUrodzenia = dataUrodzenia;
        this.eMail = eMail;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public Date getDataUrodzenia() {
        return dataUrodzenia;
    }

    public void setDataUrodzenia(Date dataUrodzenia) {
        this.dataUrodzenia = dataUrodzenia;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        String tekst = "Użytkownik{" +
                "imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'';
        if(dataUrodzenia!=null)
            tekst += ", dataUrodzenia=" + simpleDateFormat.format(dataUrodzenia) ;
        tekst += ", eMail='" + eMail + '\'' +
                ", dataUtwozenia=" + simpleDateFormat.format(dataUtwozenia) +
                ", dataAktualizacji=" + simpleDateFormat.format(dataAktualizacji) +
                '}';

        return tekst;
    }
}
