package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.recivers.OdbiornikPowiadomien;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;

public class TerapiaDopisz extends AppCompatActivity {

    private int czestotliwoscView;
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
        dataRozpoczecia = (TextInputLayout) findViewById(R.id.dataRozpoczeciaLayout);
        dataZakonczenia = (TextInputLayout) findViewById(R.id.dataZakonczeniaLayout);
        notatka = (TextInputLayout) findViewById(R.id.NotatkaLayout);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        dataRozpoczecia.getEditText().setText(sdf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, 1);
        dataZakonczenia.getEditText().setText(sdf.format(c.getTime()));
        viewListyElementow = (LinearLayout) findViewById(R.id.listaElementow);
        viewListyCzestotliwosci = (LinearLayout) findViewById(R.id.listaCzestotliwosci);
        czestotliwosc = (AutoCompleteTextView) findViewById(R.id.czestotliwosc);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        terapiaRepository = new TerapiaRepository(userUid);
        pomiarRepository = new PomiarRepository(userUid);
        lekRepository = new LekRepository(userUid);
        jednostkiRepository = new JednostkiRepository(userUid);
        wpisLekRepository = new WpisLekRepository(userUid);

        //this.dodajElement(new View(getApplicationContext()));
        this.dodajDateRangePicker(dataRozpoczecia.getEditText(), dataZakonczenia.getEditText());


        czestotliwosc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wybranaCzestotliwosc = position;
                Toast.makeText(getApplicationContext(), "wybrano id:"+wybranaCzestotliwosc, Toast.LENGTH_LONG).show();
                viewListyCzestotliwosci.removeAllViews();
                listaGodzin.clear();
                View elementView = getczestotliwoscView((int) wybranaCzestotliwosc, true);
                listaGodzin.add(elementView);
                viewListyCzestotliwosci.addView(elementView);
            }
        });
        czestotliwosc.setText(czestotliwosc.getAdapter().getItem(0).toString(), false);
        View elementView = getczestotliwoscView((int) wybranaCzestotliwosc, true);
        listaGodzin.add(elementView);
        viewListyCzestotliwosci.addView(elementView);
        createNotificationChannel();

    }

    private void createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence nazwa = "mojePomiaryChanell";
            String opis = "Źródło powiadomień";
            int znaczenie = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel chanel = new NotificationChannel("mojepomiary", nazwa, znaczenie);
            chanel.setDescription(opis);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(chanel);
        }

    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId() ) {
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

    private View getczestotliwoscView(int wybranaCzestotliwosc, Boolean isOnItemChange){
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
                    if(isOnItemChange){
                        View daysSelecterView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_x_dni, null, false);
                        viewListyCzestotliwosci.addView(daysSelecterView);
                    }
                    elementView = getczestotliwoscView(0, false);
                }
                break;
            default: {
                    elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_x_razy, null, false);
                    EditText editText = elementView.findViewById(R.id.editTextTime);
                    Button button = (Button) elementView.findViewById(R.id.usunGodzine);
                    button.setEnabled(false);
                    dodajTimePicker(editText);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listaGodzin.size() > 2) {
                                viewListyCzestotliwosci.removeView(elementView);
                                listaGodzin.remove(elementView);
                            } else {
                                Toast.makeText(getApplicationContext(), "Terapia musi posiadać conajmniej jedną godzine", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
                break;
        }
        return elementView;
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
                        if(wybranaCzestotliwosc != 1 && editText.getText().length() <= 0) {
                            View elementView = getczestotliwoscView(wybranaCzestotliwosc, false);
                            listaGodzin.get(listaGodzin.size()-1).findViewById(R.id.usunGodzine).setEnabled(true);
                            listaGodzin.add(elementView);
                            viewListyCzestotliwosci.addView(elementView);
                        }
                        if(timePicker.getMinute() > 9)
                            editText.setText(timePicker.getHour() + ":" + timePicker.getMinute());
                        else
                            editText.setText(timePicker.getHour() + ":0" + timePicker.getMinute());
                    }
                });

                timePicker.show(getSupportFragmentManager(), "fragment_tag");


            }
        });
    }

    private void dodajDateRangePicker(TextView textViewOd, TextView textViewDo){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
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



                MaterialDatePicker dateRangePicker  = MaterialDatePicker.Builder.dateRangePicker ()
                        .setSelection( new Pair<>(
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
                                                        return MaterialDatePicker.todayInUtcMilliseconds() <= date ;
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
                        calendar.setTimeInMillis((Long) ((Pair<Long, Long>)selection).first);
                        textViewOd.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
                        calendar.setTimeInMillis((Long) ((Pair<Long, Long>)selection).second);
                        textViewDo.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
                    }
                });

                dateRangePicker.show(getSupportFragmentManager(), "tag");

            }
        };

        textViewOd.setOnClickListener( onClickListener);
        textViewDo.setOnClickListener( onClickListener);
    }

    private void dodajDatePicker(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Calendar ct = Calendar.getInstance();
                try {
                    ct.setTime(simpleDateFormat.parse(textView.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                c.set(ct.get(Calendar.YEAR), ct.get(Calendar.MONTH), ct.get(Calendar.DAY_OF_MONTH), 0, 0);
                Date dataWybrana = new Date(c.getTimeInMillis());
                Log.i("Tag-main", "data:" + dataWybrana.getTime());



                MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(dataWybrana.getTime())
                        .setCalendarConstraints(
                                new CalendarConstraints.Builder()
                                        .setValidator(
                                                new CalendarConstraints.DateValidator() {
                                                    @Override
                                                    public boolean isValid(long date) {
                                                        return MaterialDatePicker.todayInUtcMilliseconds() <= date ;
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
                        textView.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");

            }
        });
    }

    public void dodajElement(View view) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
        builder.setTitle("Dodaj element");
        builder.setNegativeButton("anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setItems(new String[]{"dodaj pomiar", "dodaj lek"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                View elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_element, null, false);
                LinearLayout listaElementow = (LinearLayout) findViewById(R.id.listaElementow);
                AutoCompleteTextView spinner = (AutoCompleteTextView) elementView.findViewById(R.id.spinner);
                TextInputLayout spinnerL = (TextInputLayout) elementView.findViewById(R.id.spinnerLayout);
                Button button = (Button) elementView.findViewById(R.id.usun);

                switch (which) {
                    case 0: {

                        spinnerL.setHint("Pomiar");
                        List<Pomiar> listaPomiarow = new ArrayList<>();
                        pomiarRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    ArrayList<String> data = new ArrayList<>();
                                    for (Pomiar pomiar : task.getResult().toObjects(Pomiar.class)){
                                        listaPomiarow.add(pomiar);
                                        data.add(pomiar.getNazwa());
                                    }
                                    ArrayAdapter adapter = new ArrayAdapter ( TerapiaDopisz.this, android.R.layout.simple_spinner_dropdown_item, data);
                                    spinner.setAdapter(adapter);
                                }
                            }
                        });
                        break;
                    }
                    case 1: {

                        spinnerL.setHint("Lek");
                        List<Lek> listaLekow = new ArrayList<>();
                        lekRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    ArrayList<String> data = new ArrayList<>();
                                    for (Lek lek : task.getResult().toObjects(Lek.class)){
                                        listaLekow.add(lek);
                                        data.add(lek.getNazwa());
                                    }
                                    ArrayAdapter adapter = new ArrayAdapter ( TerapiaDopisz.this, android.R.layout.simple_spinner_dropdown_item, data);
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
                                Log.w("TAG-terapia", "wbrano: "+listaLekow.get(position));
                                Lek lek = listaLekow.get(position);

                                notatka.setText("Notatka: "+lek.getNotatka());
                                wpisLekRepository.getByLekId(lek.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<WpisLek> lista = queryDocumentSnapshots.toObjects(WpisLek.class);
                                        if(!lista.isEmpty())
                                            zapas.setText("W składzie pozostało: "+lista.get(0).getPozostalyZapas());

                                        jednostkiRepository.queryById(lek.getIdJednostki()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Jednostka jednostka1 = documentSnapshot.toObject(Jednostka.class);
                                                jednostka.setText(jednostka1.getWartosc());
                                                zapas.setText(zapas.getText()+" "+jednostka1.getWartosc());
                                                if(jednostka1.getTypZmiennej() == 0)
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

                //usuwanie
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listaElementowL.size()>1) {
                            listaElementow.removeView(elementView);
                            listaElementowL.remove(elementView);
                        }else{
                            Toast.makeText(getApplicationContext(), "Terapia musi posiadać conajmniej jedną składową", Toast.LENGTH_LONG).show();
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dataRozpoczecia = sdf.parse(this.dataRozpoczecia.getEditText().getText().toString());
        dataZakonczenia = sdf.parse(this.dataZakonczenia.getEditText().getText().toString());


        ArrayList listaWybranych = new ArrayList();
        ArrayList<String> idsCzynnosci = new ArrayList<>();
        for(View v: listaElementowL){
            AutoCompleteTextView spinner = (AutoCompleteTextView) v.findViewById(R.id.spinner);
            TextInputLayout spinnerL = (TextInputLayout) v.findViewById(R.id.spinnerLayout);
            String wybranaCzynnosc = (String) spinner.getText().toString();
            if(wybranaCzynnosc.length()>0) {
                spinnerL.setErrorEnabled(false);
                if (spinnerL.getHint().equals("Lek")) {
                    TextInputLayout editTextZasob = v.findViewById(R.id.editTextZasobLayout);
                    Lek lek = (lekRepository.findByName(wybranaCzynnosc));
                    if(editTextZasob.getEditText().getText().toString().length()==0) {
                        editTextZasob.setError("Wprowadź poprawne dane");
                        return;
                    }
                    Double dawkaLeku = Double.parseDouble(editTextZasob.getEditText().getText().toString());
                    if(dawkaLeku==0) {
                        editTextZasob.setError("Wprowadź poprawne dane");
                        return;
                    }
                    else
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
            }
            else{
                spinnerL.setError("Wybierz element terapi");
            }
        }

        ArrayList<Date> listaDatZaplanowanychTerapi = new ArrayList();
        ArrayList<Time> listaGodzin = new ArrayList<>();
        switch (wybranaCzestotliwosc) {
            case 1: { // co x godzin
                Calendar c =Calendar.getInstance();
                SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
                Date dateOd = stf.parse(((EditText)viewListyCzestotliwosci.findViewById(R.id.editTextTime1)).getText().toString());
                Date dateDo = stf.parse(((EditText)viewListyCzestotliwosci.findViewById(R.id.editTextTime2)).getText().toString());
                TimePicker timePicker = (TimePicker) viewListyCzestotliwosci.findViewById(R.id.czestotliwoscWyk);
                c.setTime(dateOd);
                while (dateDo.after(c.getTime())){
                    listaGodzin.add(new Time(c.getTime().getTime()));
                    c.add(Calendar.HOUR, timePicker.getHour());
                    c.add(Calendar.MINUTE, timePicker.getMinute());
                }
                if(dateDo.equals(c.getTime())){
                    listaGodzin.add(new Time(c.getTime().getTime()));
                }
            }
            break;
            //case 2: co x dni
            default: {
                for (View godzina : this.listaGodzin) {
                    EditText editText = godzina.findViewById(R.id.editTextTime);
                    if(editText.getText().length()>0) {
                        SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
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


            for (Time godzina: listaGodzin) {
                Calendar tmpC = (Calendar) c.clone();
                tmpC.set(Calendar.MINUTE, godzina.getMinutes());
                tmpC.set(Calendar.HOUR, godzina.getHours());
                Log.v("TerapieDopisz-data", "lista dat:"+tmpC.getTime());
                listaDatZaplanowanychTerapi.add(tmpC.getTime());
            }

            if(wybranaCzestotliwosc == 2) {
                String coDni = ((AutoCompleteTextView)viewListyCzestotliwosci.findViewById(R.id.spinnerDni)).getText().toString();
                c.add(Calendar.DATE, Integer.parseInt(coDni));
            }else
                c.add(Calendar.DATE, 1);
        }

        Log.w("TAG", "listaWybranych : "+listaWybranych.toString());

        if(listaWybranych.isEmpty()){
            Toast.makeText(getApplicationContext(), "Terapia musi zawoerać conajmniej jedną czynność", Toast.LENGTH_SHORT).show();
            return;
        }
        if(listaDatZaplanowanychTerapi.isEmpty()){
            Toast.makeText(getApplicationContext(), "Terapia musi posiadać conajmniej jeden zaplanowany etap", Toast.LENGTH_SHORT).show();
            return;
        }


        EtapTerapiaRepository etapTerapiaRepository = new EtapTerapiaRepository(userUid);
        Terapia terapia = new Terapia(userUid, 1, notatka.getEditText().getText().toString(), idsCzynnosci, dataRozpoczecia, dataZakonczenia, new Date(), new Date());
        Task<DocumentReference> task = terapiaRepository.insert(terapia).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.w("TAG-terapia", "dodano terapie");
                    terapia.setId(task.getResult().getId());
                    ArrayList<EtapTerapa> listaEtapowTerapi = new ArrayList<>();
                    for (Date data: listaDatZaplanowanychTerapi ) {
                        EtapTerapa etapTerapa = new EtapTerapa(data, null, "", terapia.getId(), userUid, new Date(), new Date());
                        listaEtapowTerapi.add(etapTerapa);
                        Task<DocumentReference> taskEtap = etapTerapiaRepository.insert(etapTerapa);
                        taskEtap.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Log.w("TAG-terapia", "dodawanie etapu");
                                if(task.isSuccessful()){
                                    Log.w("TAG-terapia", "dodano etap");
                                    etapTerapa.setId(task.getResult().getId());
                                    setAlarm(etapTerapa);
                                }
                            }
                        });
                    }

                    finish();
                }
                else Log.w("TAG-terapia", "nie dodano terapi");
            }
        });
//        while (!task.isComplete()){
//            Log.w("TAG-terapia", "dodawanie terapi");
//        }
//        if (task.isSuccessful()) {
//            Log.w("TAG-terapia", "dodano terapie");
//            terapia.setId(task.getResult().getId());
//            ArrayList<EtapTerapa> listaEtapowTerapi = new ArrayList<>();
//            for (Date data: listaDatZaplanowanychTerapi ) {
//                EtapTerapa etapTerapa = new EtapTerapa(data, null, "", terapia.getId(), userUid, new Date(), new Date());
//                listaEtapowTerapi.add(etapTerapa);
//                Task<DocumentReference> taskEtap = etapTerapiaRepository.insert(etapTerapa);
//                taskEtap.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        Log.w("TAG-terapia", "dodawanie etapu");
//                        if(task.isSuccessful()){
//                            Log.w("TAG-terapia", "dodano etap");
//                            etapTerapa.setId(task.getResult().getId());
//                            setAlarm(etapTerapa);
//                        }
//                    }
//                });
//            }
//
//            finish();
//        }
    }

    private void setAlarm(EtapTerapa etapTerapa) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, OdbiornikPowiadomien.class);
        intent.putExtra("EXTRA_Etap_ID", etapTerapa.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)etapTerapa.getDataZaplanowania().getTime(), intent,PendingIntent.FLAG_MUTABLE);

        alarmManager.setAndAllowWhileIdle (AlarmManager.RTC_WAKEUP,etapTerapa.getDataZaplanowania().getTime(), pendingIntent);

        Log.v("Tag-powiadomienie", "lista dat:"+intent.toString());
    }
}