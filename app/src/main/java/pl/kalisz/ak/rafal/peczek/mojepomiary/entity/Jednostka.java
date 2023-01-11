package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;


import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;


@IgnoreExtraProperties
public class Jednostka {

    @DocumentId
    private String id;
    private String nazwa;
    private String wartosc;
    private int typZmiennej;
    private boolean czyDomyslna;
    private String idUzytkownika;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public Jednostka() {
    }

    public Jednostka(String nazwa, String wartosc, int typZmiennej, boolean czyDomyslna, String idUzytkownika, Date dataUtwozenia, Date dataAktualizacji) {
        this.id = null;
        this.nazwa = nazwa;
        this.wartosc = wartosc;
        this.typZmiennej = typZmiennej;
        this.czyDomyslna = czyDomyslna;
        this.idUzytkownika = idUzytkownika;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUzytkownika() {
        return idUzytkownika;
    }

    public void setIdUzytkownika(String idUzytkownika) {
        this.idUzytkownika = idUzytkownika;
    }

    public int getTypZmiennej() {
        return typZmiennej;
    }

    public void setTypZmiennej(int typZmiennej) {
        this.typZmiennej = typZmiennej;
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
                "id='" + id + '\'' +
                ", nazwa='" + nazwa + '\'' +
                ", wartosc='" + wartosc + '\'' +
                ", typZmiennej=" + typZmiennej +
                ", czyDomyslna=" + czyDomyslna +
                ", idUzytkownika='" + idUzytkownika + '\'' +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
