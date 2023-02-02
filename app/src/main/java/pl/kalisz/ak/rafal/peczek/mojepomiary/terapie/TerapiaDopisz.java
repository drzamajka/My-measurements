package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.recivers.OdbiornikPowiadomien;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;

public class TerapiaDopisz extends AppCompatActivity {

    private ArrayList<View> listaElementowL, listaGodzin;
    private AutoCompleteTextView czestotliwosc;
    private int wybranaCzestotliwosc;
    private TextInputLayout dataRozpoczecia, dataZakonczenia, notatka;
    private LinearLayout viewListyElementow, viewListyCzestotliwosci;

    private TerapiaRepository terapiaRepository;
    private PomiarRepository pomiarRepository;
    private LekRepository lekRepository;
    private JednostkiRepository jednostkiRepository;
    private WpisLekRepository wpisLekRepository;
    private String userUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terapia_dopisz);
        listaElementowL = new ArrayList<>();
        listaGodzin = new ArrayList<>();
        wybranaCzestotliwosc = 0;
        dataRozpoczecia = findViewById(R.id.dataRozpoczeciaLayout);
        dataZakonczenia = findViewById(R.id.dataZakonczeniaLayout);
        notatka = findViewById(R.id.NotatkaLayout);
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_daty));
        Calendar c = Calendar.getInstance();
        dataRozpoczecia.getEditText().setText(sdf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, 1);
        dataZakonczenia.getEditText().setText(sdf.format(c.getTime()));
        viewListyElementow = findViewById(R.id.listaElementow);
        viewListyCzestotliwosci = findViewById(R.id.listaCzestotliwosci);
        czestotliwosc = findViewById(R.id.czestotliwosc);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        terapiaRepository = new TerapiaRepository(userUid);
        pomiarRepository = new PomiarRepository(userUid);
        lekRepository = new LekRepository(userUid);
        jednostkiRepository = new JednostkiRepository(userUid);
        wpisLekRepository = new WpisLekRepository(userUid);

        this.dodajDateRangePicker(dataRozpoczecia.getEditText(), dataZakonczenia.getEditText());

        czestotliwosc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wybranaCzestotliwosc = position;
                viewListyCzestotliwosci.removeAllViews();
                listaGodzin.clear();
                View elementView = getczestotliwoscView(wybranaCzestotliwosc, true);
                listaGodzin.add(elementView);
                viewListyCzestotliwosci.addView(elementView);
            }
        });
        czestotliwosc.setText(czestotliwosc.getAdapter().getItem(0).toString(), false);
        View elementView = getczestotliwoscView(wybranaCzestotliwosc, true);
        listaGodzin.add(elementView);
        viewListyCzestotliwosci.addView(elementView);

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

    //    zpais stanu
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private View getczestotliwoscView(int wybranaCzestotliwosc, Boolean isOnItemChange) {
        View elementView;
        switch (wybranaCzestotliwosc) {
            case 1:
                elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_x_godzin, null, false);
                TimePicker timePicker = elementView.findViewById(R.id.czestotliwoscWyk);
                timePicker.setMinute(0);
                timePicker.setHour(4);
                timePicker.setIs24HourView(true);
                EditText editTextTime1 = elementView.findViewById(R.id.editTextTime1);
                EditText editTextTime2 = elementView.findViewById(R.id.editTextTime2);
                editTextTime1.setText("6:00");
                editTextTime2.setText("22:00");
                dodajTimePicker(editTextTime1);
                dodajTimePicker(editTextTime2);
                break;
            case 2: {
                if (isOnItemChange) {
                    View daysSelecterView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_x_dni, null, false);
                    viewListyCzestotliwosci.addView(daysSelecterView);
                }
                elementView = getczestotliwoscView(0, false);
            }
            break;
            default: {
                elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_x_razy, null, false);
                EditText editText = elementView.findViewById(R.id.editTextTime);
                Button button = elementView.findViewById(R.id.usunGodzine);
                button.setEnabled(false);
                dodajTimePicker(editText);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listaGodzin.size() > 2) {
                            viewListyCzestotliwosci.removeView(elementView);
                            listaGodzin.remove(elementView);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.terapia_musi_posiada_godzine, Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
            break;
        }
        return elementView;
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
                        .setTitleText(R.string.okre_l_godzin__etapu)
                        .setMinute(minute)
                        .build();

                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (wybranaCzestotliwosc != 1 && editText.getText().length() <= 0) {
                            View elementView = getczestotliwoscView(wybranaCzestotliwosc, false);
                            listaGodzin.get(listaGodzin.size() - 1).findViewById(R.id.usunGodzine).setEnabled(true);
                            listaGodzin.add(elementView);
                            viewListyCzestotliwosci.addView(elementView);
                        }
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

    private void dodajDateRangePicker(TextView textViewOd, TextView textViewDo) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_daty));
                Calendar ct = Calendar.getInstance();
                try {
                    ct.setTime(simpleDateFormat.parse(textViewOd.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                c.set(ct.get(Calendar.YEAR), ct.get(Calendar.MONTH), ct.get(Calendar.DAY_OF_MONTH), 0, 0);
                Date dataWybranaOd = new Date(c.getTimeInMillis());

                try {
                    ct.setTime(simpleDateFormat.parse(textViewDo.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.set(ct.get(Calendar.YEAR), ct.get(Calendar.MONTH), ct.get(Calendar.DAY_OF_MONTH), 0, 0);
                Date dataWybranaDo = new Date(c.getTimeInMillis());


                MaterialDatePicker dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setSelection(new Pair<>(
                                        dataWybranaOd.getTime(),
                                        dataWybranaDo.getTime()
                                )
                        )
                        .setCalendarConstraints(
                                new CalendarConstraints.Builder()
                                        .setValidator(
                                                new CalendarConstraints.DateValidator() {
                                                    @Override
                                                    public boolean isValid(long date) {
                                                        return MaterialDatePicker.todayInUtcMilliseconds() <= date;
                                                    }

                                                    @Override
                                                    public int describeContents() {
                                                        return 0;
                                                    }

                                                    @Override
                                                    public void writeToParcel(@NonNull Parcel dest, int flags) {

                                                    }
                                                }
                                        )
                                        .build()
                        )
                        .build();

                dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(((Pair<Long, Long>) selection).first);
                        textViewOd.setText(calendar.get(Calendar.DAY_OF_MONTH) + getString(R.string.lacznik_daty) + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.lacznik_daty) + calendar.get(Calendar.YEAR));
                        calendar.setTimeInMillis(((Pair<Long, Long>) selection).second);
                        textViewDo.setText(calendar.get(Calendar.DAY_OF_MONTH) + getString(R.string.lacznik_daty) + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.lacznik_daty) + calendar.get(Calendar.YEAR));
                    }
                });

                dateRangePicker.show(getSupportFragmentManager(), "tag");

            }
        };

        textViewOd.setOnClickListener(onClickListener);
        textViewDo.setOnClickListener(onClickListener);
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
                        .setCalendarConstraints(
                                new CalendarConstraints.Builder()
                                        .setValidator(
                                                new CalendarConstraints.DateValidator() {
                                                    @Override
                                                    public boolean isValid(long date) {
                                                        return MaterialDatePicker.todayInUtcMilliseconds() <= date;
                                                    }

                                                    @Override
                                                    public int describeContents() {
                                                        return 0;
                                                    }

                                                    @Override
                                                    public void writeToParcel(@NonNull Parcel dest, int flags) {

                                                    }
                                                }
                                        )
                                        .build()
                        )
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

    public void dodajElement(View view) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
        builder.setTitle(R.string.wybierz_element);
        builder.setNegativeButton(getString(R.string.anuluj), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setItems(new String[]{getString(R.string.dodaj_pomiar), getString(R.string.dodaj_lek)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                View elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_element, null, false);
                LinearLayout listaElementow = findViewById(R.id.listaElementow);
                AutoCompleteTextView spinner = elementView.findViewById(R.id.spinner);
                TextInputLayout spinnerL = elementView.findViewById(R.id.spinnerLayout);
                Button button = elementView.findViewById(R.id.usun);

                switch (which) {
                    case 0: {

                        spinnerL.setHint(R.string.pomiar);
                        List<Pomiar> listaPomiarow = new ArrayList<>();
                        pomiarRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<String> data = new ArrayList<>();
                                    for (Pomiar pomiar : task.getResult().toObjects(Pomiar.class)) {
                                        listaPomiarow.add(pomiar);
                                        data.add(pomiar.getNazwa());
                                    }
                                    ArrayAdapter adapter = new ArrayAdapter(TerapiaDopisz.this, android.R.layout.simple_spinner_dropdown_item, data);
                                    spinner.setAdapter(adapter);
                                }
                            }
                        });
                        break;
                    }
                    case 1: {

                        spinnerL.setHint(getText(R.string.lek));
                        List<Lek> listaLekow = new ArrayList<>();
                        lekRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<String> data = new ArrayList<>();
                                    for (Lek lek : task.getResult().toObjects(Lek.class)) {
                                        listaLekow.add(lek);
                                        data.add(lek.getNazwa());
                                    }
                                    ArrayAdapter adapter = new ArrayAdapter(TerapiaDopisz.this, android.R.layout.simple_spinner_dropdown_item, data);
                                    spinner.setAdapter(adapter);
                                }
                            }
                        });
                        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                View extendelementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_lek, null, false);
                                TextView notatka = extendelementView.findViewById(R.id.notatka);
                                TextView zapas = extendelementView.findViewById(R.id.zapas);
                                TextView editTextZasob = extendelementView.findViewById(R.id.editTextZasob);
                                TextView jednostka = extendelementView.findViewById(R.id.jednostka);
                                LinearLayout extendContentLayout = elementView.findViewById(R.id.extendContent);
                                extendContentLayout.removeAllViews();
                                Lek lek = listaLekow.get(position);

                                notatka.setText(getString(R.string.Notatka) + lek.getNotatka());
                                wpisLekRepository.getQueryByLekId(lek.getId(), 1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<WpisLek> lista = queryDocumentSnapshots.toObjects(WpisLek.class);
                                        if (!lista.isEmpty()) {
                                            zapas.setText(getString(R.string.w_sk_adzie_pozosta_o) + lista.get(0).getPozostalyZapas());
                                        }
                                        else{
                                            zapas.setText(R.string.lek_jescze_nie_posiada_zapasu);
                                        }

                                        jednostkiRepository.queryById(lek.getIdJednostki()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Jednostka jednostka1 = documentSnapshot.toObject(Jednostka.class);
                                                jednostka.setText(jednostka1.getWartosc());
                                                zapas.setText(zapas.getText() + getString(R.string.spacia) + jednostka1.getWartosc());
                                                if (jednostka1.getTypZmiennej() == 0)
                                                    editTextZasob.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            }
                                        });
                                    }
                                });

                                extendContentLayout.addView(extendelementView);
                            }
                        });

                        break;
                    }
                }

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listaElementowL.size() > 1) {
                            listaElementow.removeView(elementView);
                            listaElementowL.remove(elementView);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.terapia_musi_posiada_, Toast.LENGTH_LONG).show();
                        }

                    }
                });

                listaElementowL.add(elementView);
                listaElementow.addView(elementView);

            }
        });
        builder.show();

    }

    public void zapiszNowaTerapie(View view) throws ParseException, JSONException {
        Date dataRozpoczecia, dataZakonczenia;
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_daty));
        dataRozpoczecia = sdf.parse(this.dataRozpoczecia.getEditText().getText().toString());
        dataZakonczenia = sdf.parse(this.dataZakonczenia.getEditText().getText().toString());


        ArrayList listaWybranych = new ArrayList();
        ArrayList<String> idsCzynnosci = new ArrayList<>();
        for (View v : listaElementowL) {
            AutoCompleteTextView spinner = v.findViewById(R.id.spinner);
            TextInputLayout spinnerL = v.findViewById(R.id.spinnerLayout);
            String wybranaCzynnosc = spinner.getText().toString();
            if (wybranaCzynnosc.length() > 0) {
                spinnerL.setErrorEnabled(false);
                if (spinnerL.getHint().equals(getString(R.string.lek))) {
                    TextInputLayout editTextZasob = v.findViewById(R.id.editTextZasobLayout);
                    Lek lek = (lekRepository.findByName(wybranaCzynnosc));
                    if (editTextZasob.getEditText().getText().toString().length() == 0) {
                        editTextZasob.setError(getString(R.string.Wprowad_warto__));
                        return;
                    }
                    Double dawkaLeku = Double.parseDouble(editTextZasob.getEditText().getText().toString());
                    if (dawkaLeku == 0) {
                        editTextZasob.setError(getString(R.string.Wprowad_warto__));
                        return;
                    } else
                        editTextZasob.setErrorEnabled(false);
                    listaWybranych.add(lek);
                    JSONObject czynnosc = new JSONObject();
                    czynnosc.put("typ", Lek.class.getName());
                    czynnosc.put("dawka", dawkaLeku);
                    czynnosc.put("id", lek.getId());
                    if (!idsCzynnosci.contains(czynnosc.toString()))
                        idsCzynnosci.add(czynnosc.toString());
                    else
                        listaElementowL.remove(v);
                } else {
                    Pomiar pomiar = (pomiarRepository.findByName(wybranaCzynnosc));
                    listaWybranych.add(pomiar);
                    JSONObject czynnosc = new JSONObject();
                    czynnosc.put("typ", Pomiar.class.getName());
                    czynnosc.put("id", pomiar.getId());
                    if (!idsCzynnosci.contains(czynnosc.toString()))
                        idsCzynnosci.add(czynnosc.toString());
                    else
                        listaElementowL.remove(v);
                }
            } else {
                spinnerL.setError(getString(R.string.wybierz_element_terapii));
            }
        }

        ArrayList<Date> listaDatZaplanowanychTerapi = new ArrayList();
        ArrayList<Time> listaGodzin = new ArrayList<>();
        switch (wybranaCzestotliwosc) {
            case 1: { // co x godzin
                Calendar c = Calendar.getInstance();
                SimpleDateFormat stf = new SimpleDateFormat(getString(R.string.format_czasu));
                Date dateOd = stf.parse(((EditText) viewListyCzestotliwosci.findViewById(R.id.editTextTime1)).getText().toString());
                Date dateDo = stf.parse(((EditText) viewListyCzestotliwosci.findViewById(R.id.editTextTime2)).getText().toString());
                TimePicker timePicker = viewListyCzestotliwosci.findViewById(R.id.czestotliwoscWyk);
                c.setTime(dateOd);
                while (dateDo.after(c.getTime())) {
                    listaGodzin.add(new Time(c.getTime().getTime()));
                    c.add(Calendar.HOUR, timePicker.getHour());
                    c.add(Calendar.MINUTE, timePicker.getMinute());
                }
                if (dateDo.equals(c.getTime())) {
                    listaGodzin.add(new Time(c.getTime().getTime()));
                }
            }
            break;
            //case 2: co x dni
            default: {
                for (View godzina : this.listaGodzin) {
                    EditText editText = godzina.findViewById(R.id.editTextTime);
                    if (editText.getText().length() > 0) {
                        SimpleDateFormat stf = new SimpleDateFormat(getString(R.string.format_czasu));
                        Date date = stf.parse(editText.getText().toString());
                        listaGodzin.add(new Time(date.getTime()));
                    }
                }
            }
            break;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(dataRozpoczecia);

        while (!dataZakonczenia.before(c.getTime())) {
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR, 0);


            for (Time godzina : listaGodzin) {
                Calendar tmpC = (Calendar) c.clone();
                tmpC.set(Calendar.MINUTE, godzina.getMinutes());
                tmpC.set(Calendar.HOUR, godzina.getHours());
                listaDatZaplanowanychTerapi.add(tmpC.getTime());
            }

            if (wybranaCzestotliwosc == 2) {
                String coDni = ((AutoCompleteTextView) viewListyCzestotliwosci.findViewById(R.id.spinnerDni)).getText().toString();
                c.add(Calendar.DATE, Integer.parseInt(coDni));
            } else
                c.add(Calendar.DATE, 1);
        }

        if (listaWybranych.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.terapia_musi_zawiera_, Toast.LENGTH_LONG).show();
            return;
        }
        if (listaDatZaplanowanychTerapi.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.terapia_musi_posiadac, Toast.LENGTH_LONG).show();
            return;
        }


        EtapTerapiaRepository etapTerapiaRepository = new EtapTerapiaRepository(userUid);
        Terapia terapia = new Terapia(userUid, 1, notatka.getEditText().getText().toString(), idsCzynnosci, dataRozpoczecia, dataZakonczenia, new Date(), new Date());
        Task<DocumentReference> task = terapiaRepository.insert(terapia).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    terapia.setId(task.getResult().getId());
                    ArrayList<EtapTerapa> listaEtapowTerapi = new ArrayList<>();
                    for (Date data : listaDatZaplanowanychTerapi) {
                        EtapTerapa etapTerapa = new EtapTerapa(data, null, "", terapia.getId(), userUid, new Date(), new Date());
                        listaEtapowTerapi.add(etapTerapa);
                        Task<DocumentReference> taskEtap = etapTerapiaRepository.insert(etapTerapa);
                        taskEtap.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Log.w("TAG-terapia", "dodawanie etapu");
                                if (task.isSuccessful()) {
                                    Log.w("TAG-terapia", "dodano etap");
                                    etapTerapa.setId(task.getResult().getId());
                                    setAlarm(etapTerapa);
                                }
                            }
                        });
                    }

                    finish();
                }
            }
        });
    }

    private void setAlarm(EtapTerapa etapTerapa) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.w("TAG-powiadomienie", "zgłaszam etap: " + etapTerapa.getId());
        Intent intent = new Intent(this, OdbiornikPowiadomien.class);
        intent.putExtra("EXTRA_Etap_ID", etapTerapa.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) etapTerapa.getDataUtwozenia().getTime(), intent, PendingIntent.FLAG_MUTABLE);

        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, etapTerapa.getDataZaplanowania().getTime(), pendingIntent);
    }
}