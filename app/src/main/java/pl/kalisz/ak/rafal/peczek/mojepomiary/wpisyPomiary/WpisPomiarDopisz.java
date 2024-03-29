package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyPomiary;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;

public class WpisPomiarDopisz extends AppCompatActivity {


    private TextInputLayout wynik, godzinaWykonania;
    private AutoCompleteTextView pomiary;
    private TextInputLayout pomiaryL;
    private List<Pomiar> listaPomiarow;
    private int idWybranegoPomiaru;

    private PomiarRepository pomiarRepository;
    private JednostkiRepository jednostkiRepository;
    private WpisPomiarRepository wpisPomiarRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wpis_pomiar_dopisz);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        wpisPomiarRepository = new WpisPomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        idWybranegoPomiaru = -1;

        wynik = findViewById(R.id.editTextWynikLayout);
        godzinaWykonania = findViewById(R.id.godzinaWykonaniaLayout);
        pomiary = findViewById(R.id.spinnerPomiary);
        pomiaryL = findViewById(R.id.spinnerPomiaryLayout);


        listaPomiarow = new ArrayList<>();
        pomiarRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> data = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Pomiar pomiar = queryDocumentSnapshot.toObject(Pomiar.class);
                        pomiar.setId(queryDocumentSnapshot.getId());
                        listaPomiarow.add(pomiar);
                        data.add(pomiar.getNazwa());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(WpisPomiarDopisz.this, android.R.layout.simple_spinner_dropdown_item, data);
                    pomiary.setAdapter(adapter);
                } else {
                    Log.i("Tag-1", "błąd odczytu jednostek" + task.getResult());
                }
            }
        });


        TextView textView5 = findViewById(R.id.jednostka);
        pomiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pomiaryL.setErrorEnabled(false);
                idWybranegoPomiaru = position;
                Pomiar pomiar = listaPomiarow.get(position);
                wynik.setEnabled(true);
                wynik.getEditText().setText("");
                if (pomiar.getIdJednostki() != null) {
                    Jednostka jednostka = jednostkiRepository.findById(pomiar.getIdJednostki());
                    textView5.setText(jednostka.getWartosc());
                    if (jednostka.getTypZmiennej() == 0) {
                        wynik.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        wynik.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    }
                    wynik.getEditText().setMinLines(1);
                    wynik.getEditText().setGravity(Gravity.CENTER_VERTICAL);
                } else {
                    textView5.setText("");
                    wynik.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                    wynik.getEditText().setMinLines(3);
                    wynik.getEditText().setGravity(Gravity.START);
                }
            }
        });

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_czasu));
        godzinaWykonania.getEditText().setText(simpleDateFormat.format(date));
        dodajTimePicker(godzinaWykonania.getEditText());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void zapiszNowaPozycia(View view) throws ParseException {
        String wynik = this.wynik.getEditText().getText().toString();
        if (wynik.length() > 0 && idWybranegoPomiaru >= 0) {
            String pomiarId = listaPomiarow.get(idWybranegoPomiaru).getId();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_czasu));
            Calendar c = Calendar.getInstance();
            c.setTime(simpleDateFormat.parse(godzinaWykonania.getEditText().getText().toString()));
            Calendar cData = Calendar.getInstance();
            cData.set(Calendar.HOUR, c.get(Calendar.HOUR));
            cData.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
            cData.set(Calendar.SECOND, c.get(Calendar.SECOND));

            wpisPomiarRepository.insert(new WpisPomiar(wynik, pomiarId, FirebaseAuth.getInstance().getCurrentUser().getUid(), cData.getTime(), new Date(), new Date()));
            finish();
        } else {
            if (idWybranegoPomiaru < 0) {
                pomiaryL.setError("Wybiez pomiar!");
            } else {
                this.wynik.setError("Wprowadż wartość pomiaru!");
            }
        }
    }

    private void dodajTimePicker(EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MINUTE, 0);
                if (editText.getText().length() > 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_czasu));
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
                        .setTitleText(getString(R.string.okresl_godzine_wykonania))
                        .setMinute(minute)
                        .build();

                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timePicker.getMinute() > 9)
                            editText.setText(timePicker.getHour() + ":" + timePicker.getMinute());
                        else
                            editText.setText(timePicker.getHour() + ":0" + timePicker.getMinute());
                    }
                });

                timePicker.show(getSupportFragmentManager(), "fragment_tag");
            }
        });
    }
}