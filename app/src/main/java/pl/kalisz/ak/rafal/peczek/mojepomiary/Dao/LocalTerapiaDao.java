package pl.kalisz.ak.rafal.peczek.mojepomiary.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.TerapiaPosiadEtay;

@Dao
public interface LocalTerapiaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTerapie(Terapia... terapie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Terapia terapia);

    @Update
    public void update(Terapia terapia);

    @Update
    public int updateTerapie(Terapia... terapie);

    @Delete
    public void delete(Terapia terapia);

    @Query("delete from terapie")
    void removeAllTerapie();



    @Query("SELECT * FROM terapie")
    List<Terapia> getAll();

    @Query("SELECT * FROM terapie WHERE id IN (:ids)")
    List<Terapia> findAllByIds(int[] ids);

    @Query("SELECT * FROM terapie WHERE id LIKE :id")
    Terapia findById(int id);

    @Transaction
    @Query("SELECT * FROM terapie")
    public List<TerapiaPosiadEtay> getTerapieAndWpisy();

    @Transaction
    @Query("SELECT * FROM terapie WHERE id LIKE :id")
    TerapiaPosiadEtay findTerapieAndWpisyById(int id);


    @Query("SELECT COUNT(id) FROM terapie")
    int countAll();

    @Query("SELECT MAX(id) FROM terapie")
    int getMaxId();
}
