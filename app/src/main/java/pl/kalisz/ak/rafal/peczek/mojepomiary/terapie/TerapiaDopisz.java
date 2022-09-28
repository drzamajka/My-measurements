package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class TerapiaDopisz extends AppCompatActivity {

    private int czestotliwoscView;
    private ArrayList<View> listaElementowL, listaCzestotliwosciL;
    private Spinner czestotliwosc;
    private EditText dataRozpoczecia, dataZakonczenia;
    private LinearLayout listaElementow, listaCzestotliwosci;
    private UsersRoomDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terapia_dopisz);
        listaElementowL = new ArrayList<>();
        listaCzestotliwosciL = new ArrayList<>();
        dataRozpoczecia = (EditText) findViewById(R.id.dataRozpoczecia);
        dataZakonczenia = (EditText) findViewById(R.id.dataZakonczenia);
        listaElementow = (LinearLayout) findViewById(R.id.listaElementow);
        listaCzestotliwosci = (LinearLayout) findViewById(R.id.listaCzestotliwosci);
        czestotliwosc = (Spinner) findViewById(R.id.czestotliwosc);
        database = UsersRoomDatabase.getInstance(getApplicationContext());

        this.dodajElement(new View(getApplicationContext()));
        this.dodajDatePicker(dataRozpoczecia);
        this.dodajDatePicker(dataZakonczenia);

        czestotliwosc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "wybrano id:"+i+l, Toast.LENGTH_LONG).show();
                View elementView = getczestotliwoscView(true);
                listaCzestotliwosci.removeAllViews();
                listaCzestotliwosciL.add(elementView);
                listaCzestotliwosci.addView(elementView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "nic nnie wybrano", Toast.LENGTH_LONG).show();
            }
        });

    }

    private View getczestotliwoscView(Boolean isOnItemChange){
        View elementView;
        switch ((int) czestotliwosc.getSelectedItemId()) {
            case 1:
                elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_x_godzin, null, false);
                break;
            case 2:
                elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_x_dni, null, false);
                break;
            default:
                elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_x_razy, null, false);
                EditText editText = elementView.findViewById(R.id.editTextTime);
                Button button = (Button) elementView.findViewById(R.id.usunGodzine);
                button.setEnabled(false);
                dodajTimePicker(editText);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listaCzestotliwosciL.size()>2) {
                            listaCzestotliwosci.removeView(elementView);
                            listaCzestotliwosciL.remove(elementView);
                        }else{
                            Toast.makeText(getApplicationContext(), "Terapia musi posiadać conajmniej jedną godzine", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                break;
        }
        return elementView;
    }

    private void dodajTimePicker(EditText editText){
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        TerapiaDopisz.this,
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(editText.getText().toString().length() <= 0) {
                                    View elementView = getczestotliwoscView(false);
                                    listaCzestotliwosciL.get(listaCzestotliwosciL.size()-1).findViewById(R.id.usunGodzine).setEnabled(true);
                                    listaCzestotliwosciL.add(elementView);
                                    listaCzestotliwosci.addView(elementView);
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

    public void zapiszNowaPozycia(View view){

        //LinearLayout listaElementow = (LinearLayout) findViewById(R.id.listaElementow);
        ArrayList listaWybranych = new ArrayList();
        for(View v: listaElementowL){
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