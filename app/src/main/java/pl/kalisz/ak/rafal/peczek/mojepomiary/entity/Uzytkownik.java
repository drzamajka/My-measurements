package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;


@IgnoreExtraProperties
public class Uzytkownik {

    @DocumentId
    private String id;
    private String imie;
    private String nazwisko;
    private Date dataUrodzenia;
    private String eMail;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public Uzytkownik() {
    }

    public Uzytkownik(String imie, String nazwisko, Date dataUrodzenia, String eMail, Date dataUtwozenia, Date dataAktualizacji) {
        id = null;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.dataUrodzenia = dataUrodzenia;
        this.eMail = eMail;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return "Uzytkownik{" +
                "id='" + id + '\'' +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", dataUrodzenia=" + dataUrodzenia +
                ", eMail='" + eMail + '\'' +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
