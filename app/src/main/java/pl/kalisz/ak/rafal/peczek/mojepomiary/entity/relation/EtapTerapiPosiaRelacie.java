package pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;

public class EtapTerapiPosiaRelacie {
    @Embedded
    public EtapTerapa etapTerapa;
    @Relation(
            parentColumn = "id",
            entityColumn = "idEtapTerapi"
    )
    public List<WpisPomiar> wpist;

    @Relation(
            parentColumn = "idTerapi",
            entityColumn = "id"
    )
    public Terapia terapia;
}
