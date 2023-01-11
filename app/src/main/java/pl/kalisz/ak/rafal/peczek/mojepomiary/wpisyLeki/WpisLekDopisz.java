package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyLeki;

import android.os.Bundle;
import android.util.Log;
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
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;

public class WpisLekDopisz extends AppCompatActivity {


    private TextInputLayout wartoscOperacji;
    private AutoCompleteTextView lek;
    private TextInputLayout lekL;
    private List<Lek> listaLekow;
    private int idWybranegoLeku;

    private LekRepository lekRepository;
    private JednostkiRepository jednostkiRepository;
    private WpisLekRepository wpisLekRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wpis_lek_dopisz);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        lekRepository = new LekRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        wpisLekRepository = new WpisLekRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        idWybranegoLeku = 0;

        wartoscOperacji = findViewById(R.id.editTextZasobLayout);
        lek = findViewById(R.id.spinnerLeki);
        lekL = findViewById(R.id.spinnerLekiLayout);


        listaLekow = new ArrayList<>();
        lekRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> data = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Lek lek = queryDocumentSnapshot.toObject(Lek.class);
                        lek.setId(queryDocumentSnapshot.getId());
                        listaLekow.add(lek);
                        data.add(lek.getNazwa());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(WpisLekDopisz.this, android.R.layout.simple_spinner_dropdown_item, data);
                    lek.setAdapter(adapter);
                } else {
                    Log.i("Tag-1", "błąd odczytu leków" + task.getResult());
                }
            }
        });


        TextView textView5 = findViewById(R.id.jednostka);
        lek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idWybranegoLeku = position;
                Jednostka jednostka = jednostkiRepository.findById(listaLekow.get(position).getIdJednostki());
                textView5.setText(jednostka.getWartosc());
            }
        });

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_czasu));

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
        String wynik = this.wartoscOperacji.getEditText().getText().toString();
        if (wynik.length() > 0) {
            String lekId = listaLekow.get(idWybranegoLeku).getId();
            wpisLekRepository.getQueryByLekId(lekId, 1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<WpisLek> lista = task.getResult().toObjects(WpisLek.class);
                        Double zapasLeku = Double.parseDouble(wynik);
                        if (!lista.isEmpty()) {
                            WpisLek wpisLek = lista.get(0);
                            zapasLeku = Double.parseDouble(wpisLek.getPozostalyZapas()) + zapasLeku;
                        }
                        wpisLekRepository.insert(new WpisLek(wynik, zapasLeku.toString(), lekId, FirebaseAuth.getInstance().getCurrentUser().getUid(), new Date(), new Date(), new Date()));
                    } else
                        Log.v("Tag-", "dupa:" + task.getException());
                }
            });
            finish();
        } else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
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
                        .setTitleText("Okresl godzine etapu")
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