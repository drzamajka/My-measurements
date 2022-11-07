package pl.kalisz.ak.rafal.peczek.mojepomiary.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.WpisPomiarPosiadaPomiar;

@Dao
public interface LocalWpisPomiarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertWpisy(WpisPomiar... Wpisy);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(WpisPomiar WpisPomiar);

    @Update
    public void update(WpisPomiar WpisPomiar);

    @Update
    public int updateWpisy(WpisPomiar... Wpisy);

    @Delete
    public void delete(WpisPomiar WpisPomiar);

    @Query("delete from wpis_pomiar")
    void removeAllWpisy();


    @Query("SELECT * FROM wpis_pomiar")
    List<WpisPomiar> getAll();

    @Transaction
    @Query("SELECT * FROM wpis_pomiar WHERE  dataWykonania BETWEEN :dataOd AND :dataDo")
    List<WpisPomiarPosiadaPomiar> getAllbetwenData(Long  dataOd, Long  dataDo);

    @Query("SELECT * FROM wpis_pomiar WHERE id IN (:ids)")
    List<WpisPomiar> findAllByIds(int[] ids);

    @Query("SELECT * FROM wpis_pomiar WHERE id LIKE :id")
    WpisPomiar findById(int id);

    @Query("SELECT COUNT(id) FROM wpis_pomiar")
    int countAll();

    @Query("SELECT MAX(id) FROM wpis_pomiar")
    int getMaxId();


    //Relacia
    @Transaction
    @Query("SELECT * FROM wpis_pomiar")
    List<WpisPomiarPosiadaPomiar> getAllwithPomiar();

    @Transaction
    @Query("SELECT * FROM wpis_pomiar WHERE id LIKE :id")
    WpisPomiarPosiadaPomiar findByIdwithPomiar(int id);
}
