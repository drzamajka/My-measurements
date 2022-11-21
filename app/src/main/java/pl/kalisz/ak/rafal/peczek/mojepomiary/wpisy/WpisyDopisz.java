package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class WpisyDopisz extends AppCompatActivity {


    private TextInputLayout wynik, godzinaWykonania;
    private AutoCompleteTextView pomiary;
    private TextInputLayout pomiaryL;
    private UsersRoomDatabase database;
    private List<Pomiar> listaPomiarow;
    private int idWybranegoPomiaru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wpisy_dopisz);

        idWybranegoPomiaru = 0;

        wynik = (TextInputLayout) findViewById(R.id.editTextWynikLayout);
        godzinaWykonania = (TextInputLayout) findViewById(R.id.godzinaWykonaniaLayout);
        pomiary = (AutoCompleteTextView) findViewById(R.id.spinnerPomiary);
        pomiaryL = (TextInputLayout) findViewById(R.id.spinnerPomiaryLayout);


        database = UsersRoomDatabase.getInstance(getApplicationContext());
        listaPomiarow = database.localPomiarDao().getAll();


        ArrayList<String> data = new ArrayList<>();
        for (Pomiar pomiar: listaPomiarow) {
            data.add(pomiar.getNazwa());
        }

        ArrayAdapter adapter = new ArrayAdapter ( this, android.R.layout.simple_spinner_dropdown_item, data);
        pomiary.setAdapter(adapter);

        TextView textView5 = findViewById(R.id.textView5);
        pomiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idWybranegoPomiaru = position;
                textView5.setText(database.localJednostkaDao().findById(listaPomiarow.get(position).getIdJednostki()).getWartosc());

            }
        });

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        godzinaWykonania.getEditText().setText(simpleDateFormat.format(date));
        dodajTimePicker(godzinaWykonania.getEditText());

    }


    public void zapiszNowaPozycia(View view) throws ParseException {
        String wynik = this.wynik.getEditText().getText().toString();
        if( wynik.length()>0) {
            int pomiarId = listaPomiarow.get(idWybranegoPomiaru).getId();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Calendar c = Calendar.getInstance();
            c.setTime(simpleDateFormat.parse(godzinaWykonania.getEditText().getText().toString()));
            Calendar cData = Calendar.getInstance();
            cData.set(Calendar.HOUR, c.get(Calendar.HOUR));
            cData.set(Calendar.MONTH, c.get(Calendar.MONTH));
            cData.set(Calendar.SECOND, c.get(Calendar.SECOND));

            int id = database.localWpisPomiarDao().getMaxId();
            database.localWpisPomiarDao().insert(new WpisPomiar((id+1),wynik, pomiarId, cData.getTime(), new Date(), new Date() ));
            finish();
        }
        else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }

    private void dodajTimePicker(EditText editText){
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MINUTE, 0);
                if(editText.getText().length() > 0){
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    try {
                        Date data = sdf.parse(editText.getText().toString());
                        c.setTime(data);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(hour)
                        .setTitleText("Okresl godzine etapu")
                        .setMinute(minute)
                        .build();

                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(timePicker.getHour() + ":" + timePicker.getMinute());
                    }
                });

                timePicker.show(getSupportFragmentManager(), "fragment_tag");
            }
        });
    }
}