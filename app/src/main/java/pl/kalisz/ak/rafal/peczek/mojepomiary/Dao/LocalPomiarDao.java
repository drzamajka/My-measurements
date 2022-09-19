package pl.kalisz.ak.rafal.peczek.mojepomiary.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;


@Dao
public interface LocalPomiarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertPomiary(Pomiar... pomiary);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Pomiar pomiar);

    @Update
    public void update(Pomiar pomiar);

    @Update
    public int updatePomiary(Pomiar... pomiary);

    @Delete
    public void delete(Pomiar pomiar);

    @Query("delete from pomiary")
    void removeAllPomiary();



    @Query("SELECT * FROM pomiary")
    List<Pomiar> getAll();

    @Query("SELECT * FROM pomiary WHERE id IN (:ids)")
    List<Pomiar> loadAllByIds(int[] ids);

    @Query("SELECT * FROM pomiary WHERE nazwa LIKE :nazwa ")
    Pomiar findByName(String nazwa);

    @Query("SELECT COUNT(id) FROM pomiary")
    int countAll();

    @Query("SELECT * FROM pomiary WHERE id LIKE :id")
    Pomiar findById(int id);
}
