package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;

public class UserRepository {

    public static final String SP_NAME = "userDetails";
    private static Uzytkownik uzytkownik;

    SharedPreferences userLocalDatabase;
    private UsersRoomDatabase database;
    Boolean remoteDatabase;


    //        int rand = (int) ((Math.random() * (1000 - 0)) + 0);
//        database = UsersRoomDatabase.getInstance(getApplicationContext());
//        database.localUserDao().insert(new User(rand,"test"+rand,"test","","",new Date(),"aa"+rand,new Date(),new Date()));

//    List<User> lista = database.localUserDao().getAll();

    public UserRepository(Context context) {
        //userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
        database = UsersRoomDatabase.getInstance(context);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context );
        remoteDatabase = sharedPreferences.getBoolean("database", true);
        Log.i("Tag-UserLocalStore", "remoteDatabase: "+ remoteDatabase);
    }


    public void storeUserData(Uzytkownik uzytkownik) {
        Log.i("Tag-UserLocalStore", "zapis: "+ uzytkownik.toString());
        database.localUzytkownikDao().insert(uzytkownik);

//        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
//        userLocalDatabaseEditor.putInt("id", user.getId());
//        userLocalDatabaseEditor.putString("login", user.getLogin());
//        userLocalDatabaseEditor.putString("imie", user.getImie());
//        userLocalDatabaseEditor.putString("nazwisko", user.getNazwisko());
//        userLocalDatabaseEditor.putString("haslo", user.getHaslo());
//        if(user.getDataUrodzenia()!= null)
//            userLocalDatabaseEditor.putString("dataUrodzenia", user.getDataUrodzenia().toString());
//        else
//            userLocalDatabaseEditor.putString("dataUrodzenia", null);
//        userLocalDatabaseEditor.putString("eMail", user.getEMail());
//        if(user.getDataUrodzenia()!= null)
//            userLocalDatabaseEditor.putString("dataUtwozenia", user.getDataUtwozenia().toString());
//        else
//            userLocalDatabaseEditor.putString("dataUtwozenia", null);
//        if(user.getDataAktualizacji()!= null)
//            userLocalDatabaseEditor.putString("dataAktualizacji", user.getDataAktualizacji().toString());
//        else
//            userLocalDatabaseEditor.putString("dataUrodzenia", null);
//        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
//        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
//        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
//        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
//        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
//        userLocalDatabaseEditor.clear();
//        userLocalDatabaseEditor.commit();
        database.localUzytkownikDao().removeAllUsers();
    }

    public Uzytkownik getLoggedInUser() {
//        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
//            return null;
//        }
//
//        int id = userLocalDatabase.getInt("id", -1);
//        String login = userLocalDatabase.getString("login", "");
//        String imie = userLocalDatabase.getString("imie", "");
//        String nazwisko = userLocalDatabase.getString("nazwisko", "");
//        String haslo = userLocalDatabase.getString("haslo", "");
//        //Date dataUrodzenia = userLocalDatabase.getString("dataUrodzenia", "");
//        Date dataUrodzenia = new Date();
//        String eMail = userLocalDatabase.getString("eMail", "");
//        //Date dataUtwozenia = userLocalDatabase.getString("dataUtwozenia", "");
//        Date dataUtwozenia = new Date();
//        //Date dataAktualizacji = userLocalDatabase.getString("dataAktualizacji", "");
//        Date dataAktualizacji = new Date();
//        User user = new User(id, login, imie, nazwisko, haslo, dataUrodzenia, eMail, dataUtwozenia, dataAktualizacji);

        Uzytkownik uzytkownik = null;

        if(remoteDatabase) {
            List<Uzytkownik> lista = database.localUzytkownikDao().getAll();
            if (lista.size() != 0) {
                uzytkownik = lista.get(0);
                if(uzytkownik.getId() == 0) {
                    clearUserData();
                    uzytkownik = null;
                }

            }
        }else{
            uzytkownik = new Uzytkownik(0,"brak","brak","brak","brak",new Date(0),"brak",new Date(0),new Date(0));
            database.localUzytkownikDao().insert(uzytkownik);
        }
        //Log.i("Tag-UserLocalStore", "odczyt: "+user.toString());
        return uzytkownik;
    }
}