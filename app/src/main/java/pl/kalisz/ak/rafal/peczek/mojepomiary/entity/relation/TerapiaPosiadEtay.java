package pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;

public class TerapiaPosiadEtay {
    @Embedded
    public Terapia terapia;
    @Relation(
            parentColumn = "id",
            entityColumn = "idTerapi"
    )
    public List<EtapTerapa> etapy;
}
