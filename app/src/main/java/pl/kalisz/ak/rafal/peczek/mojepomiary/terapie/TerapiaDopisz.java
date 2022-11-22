package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.media.RatingCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import pl.kalisz.ak.rafal.peczek.mojepomiary.recivers.OdbiornikPowiadomien;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.recivers.SampleBootReceiver;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class TerapiaDopisz extends AppCompatActivity {

    private int czestotliwoscView;
    private ArrayList<View> listaElementowL, listaGodzin;
    private AutoCompleteTextView czestotliwosc;
    private int wybranaCzestotliwosc;
    private TextInputLayout dataRozpoczecia, dataZakonczenia, notatka;
    private LinearLayout viewListyElementow, viewListyCzestotliwosci;
    private UsersRoomDatabase database;



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
        database = UsersRoomDatabase.getInstance(getApplicationContext());

        this.dodajElement(new View(getApplicationContext()));
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


//        czestotliwosc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), "wybrano id:"+i+l, Toast.LENGTH_LONG).show();
//                viewListyCzestotliwosci.removeAllViews();
//                listaGodzin.clear();
//                View elementView = getczestotliwoscView((int) czestotliwosc.getSelectedItemId(), true);
//                listaGodzin.add(elementView);
//                viewListyCzestotliwosci.addView(elementView);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(getApplicationContext(), "nic nnie wybrano", Toast.LENGTH_LONG).show();
//            }
//        });

        //powiadomienia
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



//                TimePickerDialog timePickerDialog = new TimePickerDialog(
//                        TerapiaDopisz.this,
//                        new TimePickerDialog.OnTimeSetListener(){
//                            @Override
//                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                if((int) czestotliwosc.getSelectedItemId() != 1 && editText.getText().length() <= 0) {
//                                    View elementView = getczestotliwoscView((int) czestotliwosc.getSelectedItemId(), false);
//                                    listaGodzin.get(listaGodzin.size()-1).findViewById(R.id.usunGodzine).setEnabled(true);
//                                    listaGodzin.add(elementView);
//                                    viewListyCzestotliwosci.addView(elementView);
//                                }
//                                if(minute>9)
//                                    editText.setText(hourOfDay + ":" + minute);
//                                else
//                                    editText.setText(hourOfDay + ":0" + minute);
//
//                            }
//                        },
//                        hour, minute, DateFormat.is24HourFormat(TerapiaDopisz.super.getApplicationContext()));
//                timePickerDialog.show();
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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
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
        View elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_element, null, false);
        //elementView.setId(liczbaElementow++);
        listaElementowL.add(elementView);
        LinearLayout listaElementow = (LinearLayout) findViewById(R.id.listaElementow);

        AutoCompleteTextView spinner = (AutoCompleteTextView) elementView.findViewById(R.id.spinnerPomiary);
        Button button = (Button) elementView.findViewById(R.id.usunPomiar);
        List<Pomiar> listaPomiarow = database.localPomiarDao().getAll();

        //jednostka.
        ArrayList<String> data = new ArrayList<>();
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

    public void zapiszNowaTerapie(View view) throws ParseException {
        Date dataRozpoczecia, dataZakonczenia;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dataRozpoczecia = sdf.parse(this.dataRozpoczecia.getEditText().getText().toString());
        dataZakonczenia = sdf.parse(this.dataZakonczenia.getEditText().getText().toString());


        ArrayList listaWybranych = new ArrayList();
        ArrayList<Integer> idsCzynnosci = new ArrayList<>();
        for(View v: listaElementowL){
            AutoCompleteTextView spinner = (AutoCompleteTextView) v.findViewById(R.id.spinnerPomiary);
            Pomiar pomiar = (database.localPomiarDao().findByName((String)spinner.getText().toString()));
            listaWybranych.add(pomiar);
            idsCzynnosci.add(pomiar.getId());
        }


        ArrayList<Date> listaDatZaplanowanychTerapi = new ArrayList();
        ArrayList<Time> listaGodzin = new ArrayList<>();
        switch (wybranaCzestotliwosc) {
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


        for(View v: listaElementowL){
            AutoCompleteTextView spinner = (AutoCompleteTextView) v.findViewById(R.id.spinnerPomiary);
            listaWybranych.add(database.localPomiarDao().findByName((String)spinner.getText().toString()));
        }
        Toast.makeText(this, "liczba wybranych="+listaWybranych, Toast.LENGTH_LONG).show();


        int id = database.localTerapiaDao().getMaxId();
        int idUzytkownika = database.localUzytkownikDao().getAll().get(0).getId();
        Terapia terapia = new Terapia((id + 1), idUzytkownika, 1, notatka.getEditText().getText().toString(), idsCzynnosci, dataRozpoczecia, dataZakonczenia, new Date(), new Date());
        database.localTerapiaDao().insert( terapia);
        ArrayList<EtapTerapa> listaEtapowTerapi = new ArrayList<>();
        for (Date data: listaDatZaplanowanychTerapi ) {
            int idE = database.localEtapTerapaDao().getMaxId();
            EtapTerapa etapTerapa = new EtapTerapa( idE+1, data, null, "", terapia.getId(), new Date(), new Date());
            listaEtapowTerapi.add(etapTerapa);
            database.localEtapTerapaDao().insert(etapTerapa);
            setAlarm(etapTerapa);
        }

        ComponentName receiver = new ComponentName(this, SampleBootReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        finish();
    }

    private void setAlarm(EtapTerapa etapTerapa) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, OdbiornikPowiadomien.class);
        intent.putExtra("EXTRA_Etap_ID", etapTerapa.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), etapTerapa.getId(), intent,PendingIntent.FLAG_MUTABLE);

        alarmManager.setAndAllowWhileIdle (AlarmManager.RTC_WAKEUP,etapTerapa.getDataZaplanowania().getTime()-60*1000, pendingIntent);

        Log.v("Tag-powiadomienie", "lista dat:"+intent.toString());
    }
}