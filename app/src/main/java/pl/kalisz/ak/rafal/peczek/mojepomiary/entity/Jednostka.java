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
    private int dokladnosc;
    private int przeznaczenie;
    private boolean czyDomyslna;
    private String idUzytkownika;
    private Date dataUtwozenia;
    private Date dataAktualizacji;

    public Jednostka() {
    }

    public Jednostka(String nazwa, String wartosc, int dokladnosc, int przeznaczenie, boolean czyDomyslna,  String idUzytkownika, Date dataUtwozenia, Date dataAktualizacji) {
        this.id = null;
        this.nazwa = nazwa;
        this.wartosc = wartosc;
        this.dokladnosc = dokladnosc;
        this.przeznaczenie = przeznaczenie;
        this.czyDomyslna = czyDomyslna;
        this.idUzytkownika = idUzytkownika;
        this.dataUtwozenia = dataUtwozenia;
        this.dataAktualizacji = dataAktualizacji;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getIdUzytkownika() {  return idUzytkownika; }

    public void setIdUzytkownika(String idUzytkownika) { this.idUzytkownika = idUzytkownika; }

    public int getDokladnosc() { return dokladnosc; }

    public void setDokladnosc(int dokladnosc) { this.dokladnosc = dokladnosc; }

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
                "id='" + id + '\'' +
                ", nazwa='" + nazwa + '\'' +
                ", wartosc='" + wartosc + '\'' +
                ", dokladnosc=" + dokladnosc +
                ", przeznaczenie=" + przeznaczenie +
                ", czyDomyslna=" + czyDomyslna +
                ", idUzytkownika='" + idUzytkownika + '\'' +
                ", dataUtwozenia=" + dataUtwozenia +
                ", dataAktualizacji=" + dataAktualizacji +
                '}';
    }
}
