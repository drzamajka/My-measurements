package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyPomiary;

import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import java.util.TimeZone;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;

public class WpisPomiarEdytuj extends AppCompatActivity {

    public static final String EXTRA_Wpisu_ID = "wpisId";
    private String wpisId;

    private TextInputLayout wynik, godzinaWykonania, dataWykonania;
    private AutoCompleteTextView pomiary;
    private TextInputLayout pomiaryL;
    private WpisPomiar wpisPomiar;
    private List<Pomiar> listaPomiarow;
    private int idWybranegoPomiaru;
    private String textWybranegoPomiaru;

    private PomiarRepository pomiarRepository;
    private JednostkiRepository jednostkiRepository;
    private WpisPomiarRepository wpisPomiarRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wpis_pomiar_edytuj);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        wpisPomiarRepository = new WpisPomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

        wpisId = (String) getIntent().getExtras().get(EXTRA_Wpisu_ID);
        idWybranegoPomiaru = 0;
        textWybranegoPomiaru = "";

        wynik = findViewById(R.id.editTextWynikLayout);
        godzinaWykonania = findViewById(R.id.godzinaWykonaniaLayout);
        dataWykonania = findViewById(R.id.dataWykonaniaLayout);
        pomiary = findViewById(R.id.spinnerPomiary);
        pomiaryL = findViewById(R.id.spinnerPomiaryLayout);

        wpisPomiar = wpisPomiarRepository.findById(wpisId);
        if (wpisPomiar == null) {
            finish();
        }

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
                        if (wpisPomiar.getIdPomiar().equals(pomiar.getId())) {
                            idWybranegoPomiaru = data.size() - 1;
                        }
                    }
                    TextView textView5 = findViewById(R.id.jednostka);
                    if (listaPomiarow.get(idWybranegoPomiaru).getIdJednostki() != null) {
                        Jednostka jednostka = jednostkiRepository.findById(listaPomiarow.get(idWybranegoPomiaru).getIdJednostki());
                        textView5.setText(jednostka.getWartosc());
                    } else {
                        textView5.setText("");
                        wynik.getEditText().setMinLines(3);
                        wynik.getEditText().setGravity(Gravity.START);
                    }
                    textWybranegoPomiaru = data.get(idWybranegoPomiaru);
                    pomiary.setText(textWybranegoPomiaru, false);
                    ArrayAdapter adapter = new ArrayAdapter(WpisPomiarEdytuj.this, android.R.layout.simple_spinner_dropdown_item, data);
                    pomiary.setAdapter(adapter);
                } else {
                    Log.i("Tag-1", "błąd odczytu jednostek" + task.getResult());
                }
            }
        });


        wynik.getEditText().setText(wpisPomiar.getWynikPomiary());

        TextView textView5 = findViewById(R.id.jednostka);
        pomiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idWybranegoPomiaru = position;
                Pomiar pomiar = listaPomiarow.get(position);
                if (pomiar.getIdJednostki() != null) {
                    Jednostka jednostka = jednostkiRepository.findById(pomiar.getIdJednostki());
                    textView5.setText(jednostka.getWartosc());
                    wynik.getEditText().setMinLines(1);
                    wynik.getEditText().setGravity(Gravity.CENTER_VERTICAL);
                } else {
                    textView5.setText("");
                    wynik.getEditText().setMinLines(3);
                    wynik.getEditText().setGravity(Gravity.START);
                }
            }
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_czasu));
        godzinaWykonania.getEditText().setText(simpleDateFormat.format(wpisPomiar.getDataWykonania()));
        simpleDateFormat = new SimpleDateFormat(getString(R.string.format_daty));
        dataWykonania.getEditText().setText(simpleDateFormat.format(wpisPomiar.getDataWykonania()));

        dodajTimePicker(godzinaWykonania.getEditText());
        dodajDatePicker(dataWykonania.getEditText());


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

    public void usun(View view) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(WpisPomiarEdytuj.this);
        builder.setMessage("Czy na pewno usunąć");
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.tak), (dialog, which) -> {
            if (wpisPomiar != null) {
                wpisPomiarRepository.delete(wpisPomiar);
            }
            finish();
        });

        builder.setNegativeButton(getString(R.string.nie), (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }

    public void aktualizuj(View view) throws ParseException {
        String wynik = this.wynik.getEditText().getText().toString();
        if (wynik.length() > 0) {
            String pomiarId = listaPomiarow.get(idWybranegoPomiaru).getId();


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_czasu)+getString(R.string.format_daty));
            Date data = simpleDateFormat.parse(godzinaWykonania.getEditText().getText().toString() + getString(R.string.spacia) + dataWykonania.getEditText().getText().toString());

            wpisPomiar.setWynikPomiary(wynik);
            wpisPomiar.setIdPomiar(pomiarId);
            wpisPomiar.setDataWykonania(data);
            wpisPomiar.setDataAktualizacji(new Date());

            wpisPomiarRepository.update(wpisPomiar);
            finish();
        } else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();

    }

    private void dodajDatePicker(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_daty));
                Calendar ct = Calendar.getInstance();
                try {
                    ct.setTime(simpleDateFormat.parse(textView.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                c.set(ct.get(Calendar.YEAR), ct.get(Calendar.MONTH), ct.get(Calendar.DAY_OF_MONTH), 0, 0);
                Date dataWybrana = new Date(c.getTimeInMillis());


                MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(dataWybrana.getTime())
                        .build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis((Long) selection);
                        textView.setText(calendar.get(Calendar.DAY_OF_MONTH) + getString(R.string.lacznik_daty) + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.lacznik_daty) + calendar.get(Calendar.YEAR));
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");

            }
        });
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