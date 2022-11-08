package pl.kalisz.ak.rafal.peczek.mojepomiary.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.EtapTerapiPosiaRelacie;

@Dao
public interface LocalEtapTerapaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertEtapy(EtapTerapa... etapy);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(EtapTerapa etapTerapa);

    @Update
    public void update(EtapTerapa etapTerapa);

    @Update
    public int updateEtapy(EtapTerapa... etapy);

    @Delete
    public void delete(EtapTerapa etapTerapa);

    @Query("DELETE FROM etap_terapa WHERE idTerapi LIKE :idTerapi")
    public void deleteByIdTerapi(int idTerapi);

    @Query("delete from etap_terapa")
    void removeAllEtapy();



    @Query("SELECT * FROM etap_terapa")
    List<EtapTerapa> getAll();

    @Query("SELECT * FROM etap_terapa WHERE dataZaplanowania BETWEEN :dataOd AND :dataDo ORDER BY dataZaplanowania ASC")
    List<EtapTerapiPosiaRelacie> getAllWithRelationsBetwenData(Long  dataOd, Long  dataDo);

    @Query("SELECT * FROM etap_terapa WHERE dataZaplanowania > :dataOd ORDER BY dataZaplanowania ASC")
    List<EtapTerapa> getAllAfterData(Long  dataOd);

    @Query("SELECT * FROM etap_terapa WHERE id IN (:ids)")
    List<EtapTerapa> findAllByIds(int[] ids);

    @Query("SELECT * FROM etap_terapa WHERE id LIKE :id")
    EtapTerapa findById(int id);

    @Query("SELECT * FROM etap_terapa WHERE id LIKE :id")
    EtapTerapiPosiaRelacie findByIdWithRelations(int id);

    @Query("SELECT COUNT(id) FROM etap_terapa")
    int countAll();

    @Query("SELECT MAX(id) FROM etap_terapa")
    int getMaxId();
}