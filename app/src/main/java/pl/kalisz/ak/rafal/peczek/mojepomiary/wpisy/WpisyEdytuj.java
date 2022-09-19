package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class WpisyEdytuj extends AppCompatActivity {

    public static final String EXTRA_Wpisu_ID = "wpisId";
    private int wpisId;

    private EditText wynik;
    private TextView jednostka;
    private Spinner pomiary;
    private TimePicker dataWykonaniaGodzina;
    private DatePicker dataWykonaniaData;
    private UsersRoomDatabase database;
    private List<Pomiar> listaPomiarow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wpisy_edytuj);

        wpisId = (Integer) getIntent().getExtras().get(EXTRA_Wpisu_ID);

        wynik = (EditText) findViewById(R.id.editTextWynik);
        pomiary = (Spinner) findViewById(R.id.spinnerPomiary);
        dataWykonaniaGodzina = (TimePicker) findViewById(R.id.dataWykonaniaGodzina);
        dataWykonaniaData = (DatePicker) findViewById(R.id.dataWykonaniaData);
        jednostka = (TextView) findViewById(R.id.jednostka);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        listaPomiarow = database.localPomiarDao().getAll();
        WpisPomiar wpis = database.localWpisPomiarDao().findById(wpisId);
        wynik.setText(wpis.getWynikPomiary().toString());

        Pomiar pomiarlo = database.localWpisPomiarDao().findByIdwithPomiar(wpisId).pomiary;
        Jednostka jednostka = database.localJednostkaDao().findById(pomiarlo.getIdJednostki());
        this.jednostka.setText(jednostka.getWartosc());

        //jednostka.
        int i = 1, idWybranegoPomiaru = 0;
        String[] strPomiarow = new String[listaPomiarow.size()+1];
        strPomiarow[0] = "Wybierz pomiar";
        for (Pomiar pomiar: listaPomiarow) {
            strPomiarow[i] = pomiar.getNazwa();
            if( wpis.getIdPomiar() == pomiar.getId())
                idWybranegoPomiaru = i;
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<> (this,
                android.R.layout.simple_list_item_1, strPomiarow);

        pomiary.setAdapter(adapter);
        pomiary.setSelection(idWybranegoPomiaru);

        dataWykonaniaGodzina.setIs24HourView(true);

    }

    public void usun(View view) {
        if (database != null) {
            WpisPomiar wpis = database.localWpisPomiarDao().findById(wpisId);
            database.localWpisPomiarDao().delete(wpis);
        }
        finish();
    }

    public void aktualizuj(View view) {
        String wynik = this.wynik.getText().toString();
        int pomiary = (int) this.pomiary.getSelectedItemId();
        if(pomiary != 0 && wynik != null) {
            pomiary = listaPomiarow.get(pomiary-1).getId();
            Calendar calendar = new GregorianCalendar(dataWykonaniaData.getYear(),
                    dataWykonaniaData.getMonth(),
                    dataWykonaniaData.getDayOfMonth(),
                    dataWykonaniaGodzina.getCurrentHour(),
                    dataWykonaniaGodzina.getCurrentMinute());

            Date dataWykonania = calendar.getTime();

            WpisPomiar wpis = database.localWpisPomiarDao().findById(wpisId);
            wpis.setWynikPomiary(wynik);
            wpis.setIdPomiar(pomiary);
            wpis.setDataWykonania(dataWykonania);
            wpis.setDataAktualizacji(new Date());

            database.localWpisPomiarDao().insert(wpis);
            finish();
        }
        else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();

    }
}