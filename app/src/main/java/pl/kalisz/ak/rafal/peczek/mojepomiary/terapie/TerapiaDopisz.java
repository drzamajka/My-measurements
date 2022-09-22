package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class TerapiaDopisz extends AppCompatActivity {

    private int liczbaElementow;
    private ArrayList<View> listaElementowl;
    private Spinner pomiary;
    private DatePicker dataRozpoczecia, dataZakonczenia;
    private LinearLayout listaElementow;
    private UsersRoomDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terapia_dopisz);
        liczbaElementow = 0;
        listaElementowl = new ArrayList<>();
        dataRozpoczecia = (DatePicker) findViewById(R.id.dataRozpoczecia);
        dataZakonczenia = (DatePicker) findViewById(R.id.dataZakonczenia);
        listaElementow = (LinearLayout) findViewById(R.id.listaElementow);
        //pomiary = (Spinner) findViewById(R.id.spinnerPomiary);

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

        //pomiary.setAdapter(adapter);

    }

    public void dodajElement(View view) {
        View elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_element, null, false);
        //elementView.setId(liczbaElementow++);
        listaElementowl.add(elementView);
        LinearLayout listaElementow = (LinearLayout) findViewById(R.id.listaElementow);

        Spinner spinner = (Spinner) elementView.findViewById(R.id.spinnerPomiary);
        Button button = (Button) elementView.findViewById(R.id.usunPomiar);
        List<Pomiar> listaPomiarow = database.localPomiarDao().getAll();

        //jednostka.
        ArrayList<String> data = new ArrayList<>();
        data.add("Wybierz pomiar");
        for (Pomiar pomiar: listaPomiarow) {
            data.add(pomiar.getNazwa());
        }
        ArrayAdapter adapter = new ArrayAdapter ( this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);

        //usuwanie
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaElementow.removeView(elementView);
                listaElementowl.remove(elementView);
            }
        });

        listaElementow.addView(elementView);
    }

    public void zapiszNowaPozycia(View view){

        //LinearLayout listaElementow = (LinearLayout) findViewById(R.id.listaElementow);
        ArrayList listaWybranych = new ArrayList();
        for(View v: listaElementowl){
            Spinner spinner = (Spinner) v.findViewById(R.id.spinnerPomiary);
            listaWybranych.add(spinner.getSelectedItem());
        }
        //Toast.makeText(this, "liczba pomiarów="+listaElementowl.size(), Toast.LENGTH_LONG).show();
        Toast.makeText(this, "liczba wybranych="+listaWybranych, Toast.LENGTH_LONG).show();


//        Calendar calendar = Calendar.getInstance();
//
//        calendar.set(this.dataRozpoczecia.getYear(), this.dataRozpoczecia.getMonth(), this.dataRozpoczecia.getDayOfMonth());
//        Date dataRozpoczecia = calendar.getTime();
//
//        calendar.set(this.dataZakonczenia.getYear(), this.dataZakonczenia.getMonth(), this.dataZakonczenia.getDayOfMonth());
//        Date dataZakonczenia = calendar.getTime();
//
//        int pomiary = this.pomiary.getSelectedItemPosition();
//
//
//        int id = database.localPomiarDao().countAll();
//        int userid = database.localUzytkownikDao().getAll().get(0).getId();
//        database.localTerapiaDao().insert(new Terapia((id+1), userid, 0, pomiary, dataRozpoczecia, dataZakonczenia, new Date(), new Date() ));
//        finish();
    }
}