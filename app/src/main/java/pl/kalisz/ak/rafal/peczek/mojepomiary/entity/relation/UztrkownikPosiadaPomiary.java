package pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;

public class UztrkownikPosiadaPomiary {

    @Embedded
    public Uzytkownik uzytkownik;
    @Relation(
            parentColumn = "id",
            entityColumn = "idUzytkownika"
    )
    public List<Pomiar> pomiary;

    @Override
    public String toString() {
        String string = "UztrkownikPosiadaPomiary{" +
                "uzytkownik=" + uzytkownik +",\n pomiary=\n";
        for(Pomiar pomiar : pomiary)
            string += pomiar.toString()+"\n";

        return string;
    }
}
