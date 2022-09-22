package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class PomiaryEdytuj extends AppCompatActivity {

    public static final String EXTRA_Pomiar_ID = "pomiarId";
    private int pomiarId;

    Pomiar pomiar;
    private EditText nazwa, notatka;
    private Spinner jednostki;
    private UsersRoomDatabase database;
    private List<Jednostka> listaJednostek;
    private int idWybranejJednostki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiary_edytuj);

        pomiarId = (Integer) getIntent().getExtras().get(EXTRA_Pomiar_ID);

        nazwa = (EditText) findViewById(R.id.editTextNazwa);
        notatka = (EditText) findViewById(R.id.editTextJednostka);
        jednostki = (Spinner) findViewById(R.id.spinner);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        listaJednostek = database.localJednostkaDao().getAll();
        pomiar = database.localPomiarDao().findById(pomiarId);
        nazwa.setText(pomiar.getNazwa());
        notatka.setText(pomiar.getNotatka());

        //jednostka.
        ArrayList<String> data = new ArrayList<>();
        data.add("Wybierz jednostkę");
        for (Jednostka jednostka: listaJednostek){
            if(pomiar.getIdJednostki() == jednostka.getId())
                idWybranejJednostki = data.size();
            data.add(jednostka.getNazwa()+" "+jednostka.getWartosc());
        }

        ArrayAdapter adapter = new ArrayAdapter ( this, android.R.layout.simple_spinner_dropdown_item, data);

        jednostki.setAdapter(adapter);
        jednostki.setSelection(idWybranejJednostki);
        jednostki.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object_change, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId() ) {

            case R.id.edit:
                Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
                Button anuluj = (Button) findViewById(R.id.button_disable_edit);
                aktualizuj.setEnabled(true);
                anuluj.setEnabled(true);
                nazwa.setEnabled(true);
                notatka.setEnabled(true);
                jednostki.setEnabled(true);
                return true;
            case R.id.drop:
                if (database != null) {
                    Pomiar pomiar = database.localPomiarDao().findById(pomiarId);
                    database.localPomiarDao().delete(pomiar);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void stopEdit(View view) {
        Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
        Button anuluj = (Button) findViewById(R.id.button_disable_edit);
        aktualizuj.setEnabled(false);
        anuluj.setEnabled(false);
        nazwa.setEnabled(false);
        notatka.setEnabled(false);
        jednostki.setEnabled(false);
        nazwa.setText(pomiar.getNazwa());
        notatka.setText(pomiar.getNotatka());
        jednostki.setSelection(idWybranejJednostki);
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
            jednostka = listaJednostek.get(jednostka - 1).getId();

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
