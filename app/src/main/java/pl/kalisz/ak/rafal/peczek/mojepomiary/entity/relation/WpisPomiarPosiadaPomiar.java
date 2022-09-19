package pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;

public class WpisPomiarPosiadaPomiar {
    @Embedded
    public WpisPomiar wpis;
    @Relation(
            parentColumn = "idPomiar",
            entityColumn = "id"
    )
    public Pomiar pomiary;

    @Override
    public String toString() {
        return "WpisPomiarPosiadaPomiar{" +
                "wpis=" + wpis +
                ", pomiary=" + pomiary +
                '}';
    }
}
