package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class JednostkiEdytuj extends AppCompatActivity {

    public static final String EXTRA_JEDNOSTKA_ID = "jednostkaId";
    private int jednostkaId;

    private EditText nazwa, wartosc;
    private Spinner dokladnosc, przeznaczenie;
    private UsersRoomDatabase database;
    private Jednostka jednostka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jednostki_edytuj);


        jednostkaId = (Integer) getIntent().getExtras().get(EXTRA_JEDNOSTKA_ID);

        nazwa = (EditText) findViewById(R.id.editTextNazwa);
        wartosc = (EditText) findViewById(R.id.editTextJednostka);
        dokladnosc = (Spinner) findViewById(R.id.spinner);
        przeznaczenie = (Spinner) findViewById(R.id.spinner2);


        database = UsersRoomDatabase.getInstance(getApplicationContext());
        jednostka = database.localJednostkaDao().findById(jednostkaId);
        nazwa.setText(jednostka.getNazwa());
        wartosc.setText(jednostka.getWartosc());
        dokladnosc.setSelection(jednostka.getDokladnosc());
        przeznaczenie.setSelection(jednostka.getPrzeznaczenie());

        Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
        Button anuluj = (Button) findViewById(R.id.button_disable_edit);
        aktualizuj.setEnabled(false);
        anuluj.setEnabled(false);
        nazwa.setEnabled(false);
        wartosc.setEnabled(false);
        dokladnosc.setEnabled(false);
        przeznaczenie.setEnabled(false);


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
                if(!jednostka.getCzyDomyslna()) {
                    Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
                    Button anuluj = (Button) findViewById(R.id.button_disable_edit);
                    aktualizuj.setEnabled(true);
                    anuluj.setEnabled(true);
                    nazwa.setEnabled(true);
                    wartosc.setEnabled(true);
                    dokladnosc.setEnabled(true);
                    przeznaczenie.setEnabled(true);
                }
                else {
                    Toast.makeText(this, "Nie mozna edytować domyślnej jednostki", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.drop:
                if(!jednostka.getCzyDomyslna()) {
                    if (database != null) {
                        Jednostka jednostka = database.localJednostkaDao().findById(jednostkaId);
                        database.localJednostkaDao().delete(jednostka);
                    }
                    finish();
                }
                else {
                    Toast.makeText(this, "Nie mozna usunąć domyślnej jednostki", Toast.LENGTH_LONG).show();
                }
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
        wartosc.setEnabled(false);
        dokladnosc.setEnabled(false);
        przeznaczenie.setEnabled(false);
    }

    public void aktualizujJednostke(View view) {
        String nazwa = this.nazwa.getText().toString();
        String wartosc = this.wartosc.getText().toString();
        int dokladnosc = (int) this.dokladnosc.getSelectedItemId();
        int przeznaczenie = (int) this.przeznaczenie.getSelectedItemId();

        if(dokladnosc != 0 && przeznaczenie != 0 && nazwa.length() >= 2 && wartosc.length() >= 1) {
            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();

            Jednostka jednostka = database.localJednostkaDao().findById(jednostkaId);
            jednostka.setNazwa(nazwa);
            jednostka.setWartosc(wartosc);
            jednostka.setDokladnosc(dokladnosc);
            jednostka.setPrzeznaczenie(przeznaczenie);
            jednostka.setDataAktualizacji(new Date());
            database.localJednostkaDao().insert(jednostka);
            finish();
        }else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}