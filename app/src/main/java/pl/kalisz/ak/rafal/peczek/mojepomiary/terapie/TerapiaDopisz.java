package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class TerapiaDopisz extends AppCompatActivity {

    private Spinner pomiary;
    private DatePicker dataRozpoczecia, dataZakonczenia;
    private UsersRoomDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terapia_dopisz);

        dataRozpoczecia = (DatePicker) findViewById(R.id.dataRozpoczecia);
        dataZakonczenia = (DatePicker) findViewById(R.id.dataZakonczenia);
        pomiary = (Spinner) findViewById(R.id.spinnerPomiary);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        List<Pomiar> listaPomiarow = database.localPomiarDao().getAll();

        //jednostka.
        int i = 0;
        String[] strPomiarow = new String[listaPomiarow.size()];
        for (Pomiar pomiar: listaPomiarow) {
            strPomiarow[i] = pomiar.getNazwa();
            i++;
        }

        ArrayAdapter<String > adapter = new ArrayAdapter<String> (getApplicationContext(),
                android.R.layout.simple_list_item_1, strPomiarow);

        pomiary.setAdapter(adapter);

    }

    public void zapiszNowaPozycia(View view){
        Calendar calendar = Calendar.getInstance();

        calendar.set(this.dataRozpoczecia.getYear(), this.dataRozpoczecia.getMonth(), this.dataRozpoczecia.getDayOfMonth());
        Date dataRozpoczecia = calendar.getTime();

        calendar.set(this.dataZakonczenia.getYear(), this.dataZakonczenia.getMonth(), this.dataZakonczenia.getDayOfMonth());
        Date dataZakonczenia = calendar.getTime();

        int pomiary = this.pomiary.getSelectedItemPosition();


        int id = database.localPomiarDao().countAll();
        int userid = database.localUzytkownikDao().getAll().get(0).getId();
        database.localTerapiaDao().insert(new Terapia((id+1), userid, 0, pomiary, dataRozpoczecia, dataZakonczenia, new Date(), new Date() ));
        finish();
    }
}