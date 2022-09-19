package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class PomiaryEdytuj extends AppCompatActivity {

    public static final String EXTRA_Pomiar_ID = "pomiarId";
    private int pomiarId;

    private EditText nazwa, notatka;
    private Spinner jednostki;
    private UsersRoomDatabase database;
    private List<Jednostka> ListaJednostek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiary_edytuj);

        pomiarId = (Integer) getIntent().getExtras().get(EXTRA_Pomiar_ID);

        nazwa = (EditText) findViewById(R.id.editTextNazwa);
        notatka = (EditText) findViewById(R.id.editTextJednostka);
        jednostki = (Spinner) findViewById(R.id.spinner);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        ListaJednostek = database.localJednostkaDao().getAll();
        Pomiar pomiar = database.localPomiarDao().findById(pomiarId);
        nazwa.setText(pomiar.getNazwa());
        notatka.setText(pomiar.getNotatka());

        //jednostka.
        int i = 1, idWybranejJednostki = 0;
        String[] strJednostki = new String[ListaJednostek.size()+1];
        strJednostki[0] = "Wybierz jednostkę";
        for (Jednostka jednostka: ListaJednostek) {
            strJednostki[i] = jednostka.getNazwa()+" "+jednostka.getWartosc();
            if(pomiar.getIdJednostki() == jednostka.getId())
                idWybranejJednostki = i;
            i++;
        }

        ArrayAdapter<String > adapter = new ArrayAdapter<String> (getApplicationContext(),
                android.R.layout.simple_list_item_1, strJednostki);

        jednostki.setAdapter(adapter);
        jednostki.setSelection(idWybranejJednostki);
    }


    public void usun(View view) {
        if (database != null) {
            Pomiar pomiar = database.localPomiarDao().findById(pomiarId);
            database.localPomiarDao().delete(pomiar);
        }
        finish();
    }

    public void aktualizuj(View view) {
        String nazwa = this.nazwa.getText().toString();
        String notatka = this.notatka.getText().toString();
        int jednostka = (int) this.jednostki.getSelectedItemId();

        if(jednostka != 0 && nazwa.length()>=2 && notatka.length()>=2) {
            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            notatka = notatka.substring(0, 1).toUpperCase() + notatka.substring(1);
            if (notatka.charAt(notatka.length() - 1) != '.')
                notatka += '.';
            jednostka = ListaJednostek.get(jednostka - 1).getId();

            Pomiar pomiar = database.localPomiarDao().findById(pomiarId);
            pomiar.setNazwa(nazwa);
            pomiar.setNotatka(notatka);
            pomiar.setIdJednostki(jednostka);
            pomiar.setDataAktualizacji(new Date());

            database.localPomiarDao().insert(pomiar);
            finish();
        }else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}
