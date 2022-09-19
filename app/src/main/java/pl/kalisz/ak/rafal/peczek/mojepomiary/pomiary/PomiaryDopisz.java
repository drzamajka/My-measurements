package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class PomiaryDopisz extends AppCompatActivity {

    private EditText nazwa, notatka;
    private Spinner jednostki;
    private UsersRoomDatabase database;
    private List<Jednostka> ListaJednostek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiary_dopisz);

        nazwa = (EditText) findViewById(R.id.editTextNazwa);
        notatka = (EditText) findViewById(R.id.editTextJednostka);
        jednostki = (Spinner) findViewById(R.id.spinner);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        ListaJednostek = database.localJednostkaDao().getAll();

        //jednostka.
        int i = 1;
        String[] strJednostki = new String[ListaJednostek.size()+1];
        strJednostki[0] = "Wybierz jednostkę";
        for (Jednostka jednostka: ListaJednostek) {
            strJednostki[i] = jednostka.getNazwa()+" "+jednostka.getWartosc();
            i++;
        }

        ArrayAdapter<String > adapter = new ArrayAdapter<String> ( getApplicationContext(),
                android.R.layout.simple_spinner_item, strJednostki);



        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jednostki.setAdapter(adapter);
        jednostki.setEnabled(true);

    }

    public void zapiszNowaPozycia(View view){
        String nazwa = this.nazwa.getText().toString();
        String notatka = this.notatka.getText().toString();
        int jednostka = (int) this.jednostki.getSelectedItemId();

        if(jednostka != 0 && nazwa.length()>=2 && notatka.length()>=2) {

            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            notatka = notatka.substring(0, 1).toUpperCase() + notatka.substring(1);
            if (notatka.charAt(notatka.length() - 1) != '.')
                notatka += '.';
            jednostka = ListaJednostek.get(jednostka - 1).getId();

            int id = database.localPomiarDao().countAll();
            int userid = database.localUzytkownikDao().getAll().get(0).getId();
            database.localPomiarDao().insert(new Pomiar((id + 1), nazwa, notatka, userid, jednostka, new Date(), new Date()));
            finish();
        }else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}
