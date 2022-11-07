package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import androidx.appcompat.app.AppCompatActivity;

import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class JednostkiDopisz extends AppCompatActivity {

    private EditText nazwa, wartosc;
    private Spinner dokladnosc, przeznaczenie;
    private UsersRoomDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jednostki_dopisz);

        nazwa = (EditText) findViewById(R.id.editTextNazwa);
        wartosc = (EditText) findViewById(R.id.editTextJednostka);
        dokladnosc = (Spinner) findViewById(R.id.spinner);
        przeznaczenie = (Spinner) findViewById(R.id.spinner2);

        try{
            database = UsersRoomDatabase.getInstance(getApplicationContext());
        }catch (SQLException e){
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    public void zapiszNowaPozycia(View view){
        String nazwa = this.nazwa.getText().toString();
        String wartosc = this.wartosc.getText().toString();
        int dokladnosc = (int) this.dokladnosc.getSelectedItemId();
        int przeznaczenie = (int) this.przeznaczenie.getSelectedItemId();

        if(dokladnosc != 0 && przeznaczenie != 0 && nazwa.length() >= 2 && wartosc.length() >= 1)
        {
            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            int userid = database.localUzytkownikDao().getAll().get(0).getId();

            int id = database.localJednostkaDao().getMaxId();
            database.localJednostkaDao().insert(new Jednostka((id+1), nazwa, wartosc, dokladnosc, 1, false, userid, new Date(), new Date() ));
            finish();
        }else
                Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}