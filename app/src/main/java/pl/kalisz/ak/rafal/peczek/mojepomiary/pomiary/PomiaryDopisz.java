package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class PomiaryDopisz extends AppCompatActivity {

    private EditText nazwa, notatka;
    private AutoCompleteTextView jednostki;
    private UsersRoomDatabase database;
    private List<Jednostka> listaJednostek;
    private int idWybranejJednostki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiary_dopisz);

        idWybranejJednostki = 0;
        nazwa = (EditText) findViewById(R.id.editTextNazwa);
        notatka = (EditText) findViewById(R.id.editTextJednostka);
        jednostki = (AutoCompleteTextView) findViewById(R.id.spinner);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        listaJednostek = database.localJednostkaDao().getAll();

        //jednostki
        ArrayList<String> data = new ArrayList<>();
        for (Jednostka jednostka: listaJednostek){
            data.add(jednostka.getNazwa()+" "+jednostka.getWartosc());
        }

        ArrayAdapter adapter = new ArrayAdapter ( this, android.R.layout.simple_spinner_dropdown_item, data);
        jednostki.setAdapter(adapter);

        jednostki.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idWybranejJednostki = position;
            }
        });


    }

    public void zapiszNowaPozycia(View view){
        String nazwa = this.nazwa.getText().toString();
        String notatka = this.notatka.getText().toString();

        if( jednostki.getText().length()>0 && nazwa.length()>=2 && notatka.length()>=2) {

            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            notatka = notatka.substring(0, 1).toUpperCase() + notatka.substring(1);
            if (notatka.charAt(notatka.length() - 1) != '.')
                notatka += '.';
            int jednostkaId = listaJednostek.get(idWybranejJednostki).getId();

            int id = database.localPomiarDao().getMaxId();
            int userid = 0;
            database.localPomiarDao().insert(new Pomiar((id + 1), nazwa, notatka, userid, jednostkaId, new Date(), new Date()));
            finish();
        }else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }

}
