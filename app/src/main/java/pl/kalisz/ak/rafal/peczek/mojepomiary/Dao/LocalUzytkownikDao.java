package pl.kalisz.ak.rafal.peczek.mojepomiary.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.UztrkownikPosiadaPomiary;

@Dao
public interface LocalUzytkownikDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(Uzytkownik... uzytkownicy);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Uzytkownik uzytkownik);

    @Update
    public void update(Uzytkownik uzytkownik);

    @Update
    public int updateUsers(Uzytkownik... uzytkownicy);

    @Delete
    public void delete(Uzytkownik uzytkownik);

    @Delete
    public int deleteUsers(Uzytkownik... uzytkownicy);

    @Query("delete from urzytkownicy")
    void removeAllUsers();



    @Query("SELECT * FROM urzytkownicy")
    List<Uzytkownik> getAll();

    @Query("SELECT * FROM urzytkownicy WHERE id IN (:userIds)")
    List<Uzytkownik> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM urzytkownicy WHERE login LIKE :login ")
    Uzytkownik findByName(String login);

//    relacia
    @Transaction
    @Query("SELECT * FROM urzytkownicy")
    List<UztrkownikPosiadaPomiary>  getAllwithPomiary();

    @Query("SELECT MAX(id) FROM urzytkownicy")
    int getMaxId();

}
