package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;



import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;


@IgnoreExtraProperties
public class Lek {

    @DocumentId
    private String id;
    private String nazwa;
    private String notatka;
    private String idUzytkownika;
    private String idJednostki;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public Lek() {
    }

    public Lek(String nazwa, String notatka, String idUzytkownika, String idJednostki, Date dataUtwozenia, Date dataAktualizacji) {
        id = null;
        this.nazwa = nazwa;
        this.notatka = notatka;
        this.idUzytkownika = idUzytkownika;
        this.idJednostki = idJednostki;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

    public String getIdUzytkownika() {
        return idUzytkownika;
    }

    public void setIdUzytkownika(String idUzytkownika) {
        this.idUzytkownika = idUzytkownika;
    }

    public String getIdJednostki() {
        return idJednostki;
    }

    public void setIdJednostki(String idJednostki) {
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
                "id='" + id + '\'' +
                ", nazwa='" + nazwa + '\'' +
                ", notatka='" + notatka + '\'' +
                ", idUzytkownika='" + idUzytkownika + '\'' +
                ", idJednostki='" + idJednostki + '\'' +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
