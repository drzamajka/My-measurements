package pl.kalisz.ak.rafal.peczek.mojepomiary.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;


@Dao
public interface LocalJednostkaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertJednostki(Jednostka... jednostki);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Jednostka jednostka);

    @Update
    public void update(Jednostka jednostka);

    @Update
    public int updateJednostki(Jednostka... jednostki);

    @Delete
    public void delete(Jednostka jednostka);

    @Query("delete from jednostki")
    void removeAllJednostki();



    @Query("SELECT * FROM jednostki")
    List<Jednostka> getAll();

    @Query("SELECT * FROM jednostki WHERE id IN (:ids)")
    List<Jednostka> findAllByIds(int[] ids);

    @Query("SELECT * FROM jednostki WHERE id LIKE :id")
    Jednostka findById(int id);

    @Query("SELECT * FROM jednostki WHERE nazwa LIKE :nazwa ")
    Jednostka findByName(String nazwa);

    @Query("SELECT COUNT(id) FROM jednostki")
    int countAll();
}
