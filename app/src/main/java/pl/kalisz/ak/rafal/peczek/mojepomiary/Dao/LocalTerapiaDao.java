package pl.kalisz.ak.rafal.peczek.mojepomiary.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;

@Dao
public interface LocalTerapiaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTerapie(Terapia... terapie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Terapia jednostka);

    @Update
    public void update(Terapia jednostka);

    @Update
    public int updateTerapie(Terapia... terapie);

    @Delete
    public void delete(Terapia jednostka);

    @Query("delete from terapie")
    void removeAllTerapie();



    @Query("SELECT * FROM terapie")
    List<Terapia> getAll();

    @Query("SELECT * FROM terapie WHERE id IN (:ids)")
    List<Terapia> findAllByIds(int[] ids);

    @Query("SELECT * FROM terapie WHERE id LIKE :id")
    Terapia findById(int id);


    @Query("SELECT COUNT(id) FROM terapie")
    int countAll();
}
