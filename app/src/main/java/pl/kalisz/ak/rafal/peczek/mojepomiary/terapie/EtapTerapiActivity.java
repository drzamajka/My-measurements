package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;

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
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;

public class EtapTerapiActivity extends AppCompatActivity {

    public static String EXTRA_Etap_ID = "etapId";
    public static String EXTRA_Aktywnosc = "Aktywnosc";

    private String etapId;
    private int aktywnosc;

    private LinearLayout viewListyElementow;
    private ArrayList<View> listaElementowL;
    private Button przycisk;
    private TextInputLayout notatka, godzinaWykonania;

    private EtapTerapa etapTerapa;
    private Terapia terapia;

    private String userUid;
    private TerapiaRepository terapiaRepository;
    private PomiarRepository pomiarRepository;
    private EtapTerapiaRepository etapTerapiaRepository;
    private JednostkiRepository jednostkiRepository;
    private WpisPomiarRepository wpisPomiarRepository;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etap_terapi);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        terapiaRepository = new TerapiaRepository(userUid);
        pomiarRepository = new PomiarRepository(userUid);
        etapTerapiaRepository = new EtapTerapiaRepository(userUid);
        jednostkiRepository = new JednostkiRepository(userUid);
        wpisPomiarRepository = new WpisPomiarRepository(userUid);

        etapId = (String) getIntent().getExtras().get(EXTRA_Etap_ID);
        aktywnosc = (Integer) getIntent().getExtras().get(EXTRA_Aktywnosc);
        listaElementowL = new ArrayList<>();


        etapTerapa = etapTerapiaRepository.findById(etapId);
        terapia = terapiaRepository.findById(etapTerapa.getIdTerapi());
        ArrayList<Pomiar> listaCzynnosci = new ArrayList<>();
        for(String uidCzynnosci : terapia.getIdsCzynnosci()){
            listaCzynnosci.add(pomiarRepository.findById(uidCzynnosci));
        }


        godzinaWykonania = (TextInputLayout) findViewById(R.id.godzinaWykonaniaLayout);
        notatka = (TextInputLayout) findViewById(R.id.editTextNotatkaLayout);
        viewListyElementow = (LinearLayout) findViewById(R.id.listaElementow);
        przycisk = (Button) findViewById(R.id.button_save_edit);




        for (Pomiar pomiar: listaCzynnosci) {
            View elementView = getLayoutInflater().inflate(R.layout.activity_etap_terapi_element, null, false);
            listaElementowL.add(elementView);

            TextView textView = (TextView) elementView.findViewById(R.id.textView3);
            textView.setText(pomiar.getNazwa());
            TextView textView1 = (TextView) elementView.findViewById(R.id.notayka);
            textView1.setText(pomiar.getNotatka());
            TextView textView2 = (TextView) elementView.findViewById(R.id.jednostka);
            textView2.setText(jednostkiRepository.findById(pomiar.getIdJednostki()).getWartosc());

            viewListyElementow.addView(elementView);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        if(aktywnosc != 1) {
            przycisk.setText("Wykonaj etap");
            Date date = new Date();
            godzinaWykonania.getEditText().setText(simpleDateFormat.format(date));
            dodajTimePicker(godzinaWykonania.getEditText());

            przycisk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Double> listaWynikow = new ArrayList<>();
                    for (View element : listaElementowL) {
                        String wynikOdczyt = ((EditText) element.findViewById(R.id.editTextWynik)).getText().toString();
                        wynikOdczyt = wynikOdczyt.replaceAll("\\,", ".");
                        if (wynikOdczyt.length() > 0 && Double.parseDouble(wynikOdczyt) > 0) {
                            Double wynik = Double.parseDouble(wynikOdczyt);
                            listaWynikow.add(wynik);
                        } else {
                            Toast.makeText(getApplicationContext(), "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(simpleDateFormat.parse(godzinaWykonania.getEditText().getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar cData = Calendar.getInstance();
                    cData.set(Calendar.HOUR, c.get(Calendar.HOUR));
                    cData.set(Calendar.MONTH, c.get(Calendar.MONTH));
                    cData.set(Calendar.SECOND, c.get(Calendar.SECOND));

                    int index = 0;
                    for (Pomiar pomiar: listaCzynnosci) {
                        wpisPomiarRepository.insert(new WpisPomiar(listaWynikow.get(index).toString(), pomiar.getId(), userUid, etapTerapa.getId(), c.getTime(), new Date(), new Date() ));
                        index++;
                    }
                    etapTerapa.setDataWykonania(cData.getTime());
                    etapTerapa.setDataAktualizacji(new Date());
                    etapTerapa.setNotatka(notatka.getEditText().getText().toString());
                    etapTerapiaRepository.update(etapTerapa);
                    finish();

                }
            });
        }
        else {
            przycisk.setText("Aktualizuj");
            notatka.getEditText().setText(etapTerapa.getNotatka());
            godzinaWykonania.getEditText().setText(simpleDateFormat.format(etapTerapa.getDataWykonania()));
            dodajTimePicker(godzinaWykonania.getEditText());
            List<WpisPomiar> listaWpisow = wpisPomiarRepository.findByEtapId(etapTerapa.getId());

            int i = 0;
            for( WpisPomiar wpis : listaWpisow){
                EditText editText = (EditText) listaElementowL.get(i).findViewById(R.id.editTextWynik);
                editText.setText(wpis.getWynikPomiary());
                i++;
            }
            przycisk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Double> listaWynikow = new ArrayList<>();
                    for (View element : listaElementowL) {
                        String wynikOdczyt = ((EditText) element.findViewById(R.id.editTextWynik)).getText().toString();
                        wynikOdczyt = wynikOdczyt.replaceAll("\\,", ".");
                        if (wynikOdczyt.length() > 0 && Double.parseDouble(wynikOdczyt) > 0) {
                            Double wynik = Double.parseDouble(wynikOdczyt);
                            listaWynikow.add(wynik);
                        } else {
                            Toast.makeText(getApplicationContext(), "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(simpleDateFormat.parse(godzinaWykonania.getEditText().getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar cData = Calendar.getInstance();
                    cData.set(Calendar.HOUR, c.get(Calendar.HOUR));
                    cData.set(Calendar.MONTH, c.get(Calendar.MONTH));
                    cData.set(Calendar.SECOND, c.get(Calendar.SECOND));

                    int index = 0;
                    for( WpisPomiar wpis : listaWpisow){
                        wpis.setWynikPomiary(listaWynikow.get(index).toString());
                        wpis.setDataAktualizacji(new Date());
                        wpisPomiarRepository.update(wpis);
                        index++;
                    }
                    etapTerapa.setDataWykonania(c.getTime());
                    etapTerapa.setDataAktualizacji(new Date());
                    etapTerapa.setNotatka(notatka.getEditText().getText().toString());
                    etapTerapiaRepository.update(etapTerapa);
                    finish();

                }
            });
        }
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

}