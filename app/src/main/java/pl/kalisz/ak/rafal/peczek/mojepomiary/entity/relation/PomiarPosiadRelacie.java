package pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;

public class PomiarPosiadRelacie {
    @Embedded
    public Pomiar pomiar;
    @Relation(
            parentColumn = "id",
            entityColumn = "idEtapTerapi"
    )
    public List<WpisPomiar> pomiary;

    @Relation(
            parentColumn = "id",
            entityColumn = "idJednostki"
    )
    public Jednostka jednostka;
}
