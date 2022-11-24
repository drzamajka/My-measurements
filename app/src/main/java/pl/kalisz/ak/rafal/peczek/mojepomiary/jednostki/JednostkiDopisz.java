package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import androidx.appcompat.app.AppCompatActivity;

import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class JednostkiDopisz extends AppCompatActivity {

    private EditText nazwa, wartosc;
    private AutoCompleteTextView dokladnosc, przeznaczenie;
    private UsersRoomDatabase database;
    private int dokladnoscSelectedId, przeznaczenieSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jednostki_dopisz);

        nazwa = (EditText) findViewById(R.id.editTextNazwa);
        wartosc = (EditText) findViewById(R.id.editTextJednostka);
        dokladnosc = (AutoCompleteTextView) findViewById(R.id.spinner);
        przeznaczenie = (AutoCompleteTextView) findViewById(R.id.spinner2);
        dokladnoscSelectedId = 0;
        przeznaczenieSelectedId = 0;

        dokladnosc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dokladnoscSelectedId = position;
            }
        });

        przeznaczenie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                przeznaczenieSelectedId = position;
            }
        });

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

        if(nazwa.length() >= 2 && wartosc.length() >= 1)
        {
            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            int userid = 0;

            int id = database.localJednostkaDao().getMaxId();
            database.localJednostkaDao().insert(new Jednostka((id+1), nazwa, wartosc, dokladnoscSelectedId, przeznaczenieSelectedId, false, userid, new Date(), new Date() ));
            finish();
        }else
                Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}