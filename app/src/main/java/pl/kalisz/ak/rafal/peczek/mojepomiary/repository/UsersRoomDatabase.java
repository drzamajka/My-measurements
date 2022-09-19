package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.LocalEtapTerapaDao;
import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.LocalJednostkaDao;
import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.LocalPomiarDao;
import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.LocalTerapiaDao;
import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.LocalUzytkownikDao;
import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.LocalWpisPomiarDao;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;

@Database(entities = {Uzytkownik.class, Jednostka.class, Pomiar.class, Terapia.class, EtapTerapa.class, WpisPomiar.class}, version = 16, exportSchema = false)
public abstract class UsersRoomDatabase extends RoomDatabase {

    private static UsersRoomDatabase instance;

    public abstract LocalUzytkownikDao localUzytkownikDao();
    public abstract LocalJednostkaDao localJednostkaDao();
    public abstract LocalPomiarDao localPomiarDao();
    public abstract LocalTerapiaDao localTerapiaDao();
    public abstract LocalEtapTerapaDao localEtapTerapaDao();
    public abstract LocalWpisPomiarDao localWpisPomiarDao();



    public static synchronized UsersRoomDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context,
                            UsersRoomDatabase.class, "user_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(seedDatabase(context))
                            .build();
        }
        return instance;
    }

    private static  RoomDatabase.Callback  seedDatabase(Context context){
        return new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                new Thread() {
                    @Override
                    public void run() {
                        LocalJednostkaDao jednostkiDao = getInstance(context).localJednostkaDao();
                        jednostkiDao.insertJednostki(
                                new Jednostka(1, "Centymetry", "cm", 5, 0, true, 0,  new Date(0), new Date(0) ),
                                new Jednostka(2, "Minimetry", "mm", 3, 0, true, 0,  new Date(0), new Date(0) ),
                                new Jednostka(3, "Kilogramy", "kg", 5, 0, true, 0,  new Date(0), new Date(0) ),
                                new Jednostka(4, "Gramy", "g", 4, 0, true, 0,  new Date(0), new Date(0) ),
                                new Jednostka(5, "Stopnie Celsjusza", "°C", 4, 0, true, 0,  new Date(0), new Date(0) ),
                                new Jednostka(6, "Stopnie Fahrenheita", "°F", 4, 0, true, 0,  new Date(0), new Date(0) )
                        );
                    }
                }.start();
            }
        };
    }


    public static void destroyInstance() {
        instance = null;
    }

}
