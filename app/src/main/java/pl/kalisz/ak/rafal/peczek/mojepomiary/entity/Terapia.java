package pl.kalisz.ak.rafal.peczek.mojepomiary.entity;


import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties
public class Terapia {

    @DocumentId
    private String id;
    private String idUzytkownika;
    private int typ;
    private String notatka;
    private ArrayList<String> idsCzynnosci;
    private Date dataRozpoczecia;
    private Date dataZakonczenia;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public Terapia() {
    }

    public Terapia(String idUzytkownika, int typ, String notatka, ArrayList<String> idsCzynnosci, Date dataRozpoczecia, Date dataZakonczenia, Date dataUtwozenia, Date dataAktualizacji) {
        id = null;
        this.idUzytkownika = idUzytkownika;
        this.typ = typ;
        this.notatka = notatka;
        this.idsCzynnosci = idsCzynnosci;
        this.dataRozpoczecia = dataRozpoczecia;
        this.dataZakonczenia = dataZakonczenia;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public String getId() {return id;}

    public void setId(String id) { this.id = id; }

    public String getIdUzytkownika() {
        return idUzytkownika;
    }

    public void setIdUzytkownika(String idUzytkownika) {
        this.idUzytkownika = idUzytkownika;
    }

    public int getTyp() {
        return typ;
    }

    public void setTyp(int typ) {
        this.typ = typ;
    }

    public String getNotatka() { return notatka; }

    public void setNotatka(String notatka) { this.notatka = notatka; }

    public ArrayList<String> getIdsCzynnosci() {
        return idsCzynnosci;
    }

    public void setIdsCzynnosci(ArrayList<String> idsCzynnosci) { this.idsCzynnosci = idsCzynnosci; }

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
                "id='" + id + '\'' +
                ", idUzytkownika='" + idUzytkownika + '\'' +
                ", typ=" + typ +
                ", notatka='" + notatka + '\'' +
                ", idsCzynnosci=" + idsCzynnosci +
                ", dataRozpoczecia=" + dataRozpoczecia +
                ", dataZakonczenia=" + dataZakonczenia +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
