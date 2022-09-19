package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class WpisyDopisz extends AppCompatActivity {


    private EditText wynik;
    private Spinner pomiary;
    private TimePicker dataWykonania;
    private UsersRoomDatabase database;
    private List<Pomiar> listaPomiarow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wpisy_dopisz);

        wynik = (EditText) findViewById(R.id.editTextWynik);
        pomiary = (Spinner) findViewById(R.id.spinnerPomiary);
        dataWykonania = (TimePicker) findViewById(R.id.dataWykonania);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        listaPomiarow = database.localPomiarDao().getAll();

        //jednostka.
        int i = 1;
        String[] strPomiarow = new String[listaPomiarow.size()+1];
        strPomiarow[0] = "Wybierz pomiar";
        for (Pomiar pomiar: listaPomiarow) {
            strPomiarow[i] = pomiar.getNazwa();
            i++;
        }

        ArrayAdapter<String > adapter = new ArrayAdapter<String> (getApplicationContext(),
                android.R.layout.simple_list_item_1, strPomiarow);

        pomiary.setAdapter(adapter);
        dataWykonania.setIs24HourView(true);
        dataWykonania.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

    }

    public void zapiszNowaPozycia(View view){
        String wynik = this.wynik.getText().toString();
        int pomiary = (int) this.pomiary.getSelectedItemId();
        if(pomiary != 0 && wynik != null) {
            pomiary = listaPomiarow.get(pomiary-1).getId();

            Date data = new Date();
            data.setHours(this.dataWykonania.getCurrentHour());
            data.setMinutes(this.dataWykonania.getCurrentMinute());

            int id = database.localWpisPomiarDao().countAll();
            database.localWpisPomiarDao().insert(new WpisPomiar((id+1),wynik, pomiary, 0, data, new Date(), new Date() ));
            finish();
        }
        else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}