package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class JednostkiEdytuj extends AppCompatActivity {

    public static final String EXTRA_JEDNOSTKA_ID = "jednostkaId";
    private int jednostkaId;

    private TextInputLayout nazwa, wartosc;
    private AutoCompleteTextView dokladnosc, przeznaczenie;
    private TextInputLayout dokladnoscL, przeznaczenieL;
    private UsersRoomDatabase database;
    private Jednostka jednostka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jednostki_edytuj);


        jednostkaId = (Integer) getIntent().getExtras().get(EXTRA_JEDNOSTKA_ID);

        nazwa = (TextInputLayout) findViewById(R.id.editTextNazwaLayout);
        wartosc = (TextInputLayout) findViewById(R.id.editTextJednostkaLayout);
        dokladnosc = (AutoCompleteTextView) findViewById(R.id.spinner);
        przeznaczenie = (AutoCompleteTextView) findViewById(R.id.spinner2);
        dokladnoscL = (TextInputLayout) findViewById(R.id.spinnerLayout);
        przeznaczenieL = (TextInputLayout) findViewById(R.id.spinner2Layout);


        database = UsersRoomDatabase.getInstance(getApplicationContext());
        jednostka = database.localJednostkaDao().findById(jednostkaId);
        nazwa.getEditText().setText(jednostka.getNazwa());
        wartosc.getEditText().setText(jednostka.getWartosc());
        String[] listaDokladnosci = getResources().getStringArray(R.array.dokladnosc);
        dokladnosc.setText(dokladnosc.getAdapter().getItem(jednostka.getDokladnosc()).toString(), true);
        przeznaczenie.setText(przeznaczenie.getAdapter().getItem(jednostka.getPrzeznaczenie()).toString(), true);


        Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
        Button anuluj = (Button) findViewById(R.id.button_disable_edit);
        aktualizuj.setEnabled(false);
        anuluj.setEnabled(false);
        nazwa.setEnabled(false);
        wartosc.setEnabled(false);
//        dokladnosc.setEnabled(false);
        dokladnoscL.setEnabled(false);
//        przeznaczenie.setEnabled(false);
        przeznaczenieL.setEnabled(false);


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
                    dokladnoscL.setEnabled(true);
                    przeznaczenieL.setEnabled(true);
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
        dokladnoscL.setEnabled(false);
        przeznaczenieL.setEnabled(false);
    }

    public void aktualizujJednostke(View view) {
        String nazwa = this.nazwa.getEditText().getText().toString();
        String wartosc = this.wartosc.getEditText().getText().toString();
        int dokladnosc = (int) this.dokladnosc.getText().length();
        int przeznaczenie = (int) this.przeznaczenie.getText().length();

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