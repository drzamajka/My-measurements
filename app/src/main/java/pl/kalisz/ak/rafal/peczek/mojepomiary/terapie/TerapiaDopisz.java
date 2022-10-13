package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class TerapiaDopisz extends AppCompatActivity {

    private int czestotliwoscView;
    private ArrayList<View> listaElementowL, listaGodzin;
    private Spinner czestotliwosc;
    private EditText dataRozpoczecia, dataZakonczenia, notatka;
    private LinearLayout viewListyElementow, viewListyCzestotliwosci;
    private UsersRoomDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terapia_dopisz);
        listaElementowL = new ArrayList<>();
        listaGodzin = new ArrayList<>();
        notatka = (EditText) findViewById(R.id.editTextTextMultiLine);
        dataRozpoczecia = (EditText) findViewById(R.id.dataRozpoczecia);
        dataZakonczenia = (EditText) findViewById(R.id.dataZakonczenia);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        dataRozpoczecia.setText(sdf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, 1);
        dataZakonczenia.setText(sdf.format(c.getTime()));
        viewListyElementow = (LinearLayout) findViewById(R.id.listaElementow);
        viewListyCzestotliwosci = (LinearLayout) findViewById(R.id.listaCzestotliwosci);
        czestotliwosc = (Spinner) findViewById(R.id.czestotliwosc);
        database = UsersRoomDatabase.getInstance(getApplicationContext());

        this.dodajElement(new View(getApplicationContext()));
        this.dodajDatePicker(dataRozpoczecia);
        this.dodajDatePicker(dataZakonczenia);

        czestotliwosc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "wybrano id:"+i+l, Toast.LENGTH_LONG).show();
                viewListyCzestotliwosci.removeAllViews();
                listaGodzin.clear();
                View elementView = getczestotliwoscView((int) czestotliwosc.getSelectedItemId(), true);
                listaGodzin.add(elementView);
                viewListyCzestotliwosci.addView(elementView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "nic nnie wybrano", Toast.LENGTH_LONG).show();
            }
        });

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
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                    try {
                        Date data = sdf.parse(editText.getText().toString());
                        c.setTime(data);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        TerapiaDopisz.this,
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if((int) czestotliwosc.getSelectedItemId() != 1 && editText.getText().length() <= 0) {
                                    View elementView = getczestotliwoscView((int) czestotliwosc.getSelectedItemId(), false);
                                    listaGodzin.get(listaGodzin.size()-1).findViewById(R.id.usunGodzine).setEnabled(true);
                                    listaGodzin.add(elementView);
                                    viewListyCzestotliwosci.addView(elementView);
                                }
                                if(minute>9)
                                    editText.setText(hourOfDay + ":" + minute);
                                else
                                    editText.setText(hourOfDay + ":0" + minute);

                            }
                        },
                        hour, minute, DateFormat.is24HourFormat(TerapiaDopisz.super.getApplicationContext()));
                timePickerDialog.show();
            }
        });
    }

    private void dodajDatePicker(EditText editText){
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                if(editText.getText().length() > 0){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date data = sdf.parse(editText.getText().toString());
                        c.setTime(data);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TerapiaDopisz.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void dodajElement(View view) {
        View elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_element, null, false);
        //elementView.setId(liczbaElementow++);
        listaElementowL.add(elementView);
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
                if(listaElementowL.size()>1) {
                    listaElementow.removeView(elementView);
                    listaElementowL.remove(elementView);
                }else{
                    Toast.makeText(getApplicationContext(), "Terapia musi posiadać conajmniej jedną składową", Toast.LENGTH_LONG).show();
                }

            }
        });

        listaElementow.addView(elementView);
    }

    public void zapiszNowaPozycia(View view) throws ParseException {
        Date dataRozpoczecia, dataZakonczenia;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dataRozpoczecia = sdf.parse(this.dataRozpoczecia.getText().toString());
        dataZakonczenia = sdf.parse(this.dataZakonczenia.getText().toString());

        ArrayList listaWybranych = new ArrayList();
        ArrayList<Integer> idsCzynnosci = new ArrayList<>();
        for(View v: listaElementowL){
            Spinner spinner = (Spinner) v.findViewById(R.id.spinnerPomiary);
            Pomiar pomiar = (database.localPomiarDao().findByName((String)spinner.getSelectedItem()));
            listaWybranych.add(pomiar);
            idsCzynnosci.add(pomiar.getId());
        }

        ArrayList<Date> listaDatZaplanowanychTerapi = new ArrayList();
        ArrayList<Time> listaGodzin = new ArrayList<>();
        switch ((int) czestotliwosc.getSelectedItemId()) {
            case 1: { // co x godzin
                Calendar c =Calendar.getInstance();
                SimpleDateFormat stf = new SimpleDateFormat("hh:mm");
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
                        SimpleDateFormat stf = new SimpleDateFormat("hh:mm");
                        Date date = stf.parse(editText.getText().toString());
                        listaGodzin.add(new Time(date.getTime()));
                    }
                }
            }
            break;
        }
        Log.v("TerapieDopisz-godziny", "lista godzin:"+listaGodzin);                                            /// log

        Calendar c = Calendar.getInstance();
        c.setTime(dataRozpoczecia);

        Log.v("TerapieDopisz", "start pentli");                                            /// log

        while (dataZakonczenia.after(c.getTime())) {
            Log.v("TerapieDopisz-kalkulacia", "data:"+c.getTime());                 /// log
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR, 0);

            for (Time godzina: listaGodzin) {
                c.set(Calendar.MINUTE, godzina.getMinutes());
                c.set(Calendar.HOUR, godzina.getHours());
                listaDatZaplanowanychTerapi.add(c.getTime());
            }

            if(czestotliwosc.getSelectedItemId() == 2) {
                String coDni = ((Spinner)viewListyCzestotliwosci.findViewById(R.id.spinnerPomiary)).getSelectedItem().toString();
                c.add(Calendar.DATE, Integer.parseInt(coDni));
            }else
                c.add(Calendar.DATE, 1);
        }
        Log.v("TerapieDopisz-daty", "lista dat:"+listaDatZaplanowanychTerapi);                                            /// log


        for(View v: listaElementowL){
            Spinner spinner = (Spinner) v.findViewById(R.id.spinnerPomiary);
            listaWybranych.add(database.localPomiarDao().findByName((String)spinner.getSelectedItem()));
        }
        //Toast.makeText(this, "liczba pomiarów="+listaElementowl.size(), Toast.LENGTH_LONG).show();
        Toast.makeText(this, "liczba wybranych="+listaWybranych, Toast.LENGTH_LONG).show();


        int id = database.localTerapiaDao().countAll();
        int idUzytkownika = database.localUzytkownikDao().getAll().get(0).getId();
        Terapia terapia = new Terapia((id + 1), idUzytkownika, 1, idsCzynnosci, dataRozpoczecia, dataZakonczenia, new Date(), new Date());
        Log.v("TerapieDopisz-terapia", "terapia:"+terapia);                                            /// log
        database.localTerapiaDao().insert( terapia);
        ArrayList<EtapTerapa> listaEtapowTerapi = new ArrayList<>();
        for (Date data: listaDatZaplanowanychTerapi ) {
            int idE = database.localEtapTerapaDao().countAll();
            EtapTerapa etapTerapa = new EtapTerapa( idE, data, null, "", terapia.getId(), new Date(), new Date());
            listaEtapowTerapi.add(etapTerapa);
            database.localEtapTerapaDao().insert(etapTerapa);
        }

        Log.v("TerapieDopisz-etapy", "lista etapów:"+listaEtapowTerapi);                                            /// log
        finish();


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