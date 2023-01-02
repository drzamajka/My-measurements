package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.auth.LoginActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;
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
    private LekRepository lekRepository;
    private EtapTerapiaRepository etapTerapiaRepository;
    private JednostkiRepository jednostkiRepository;
    private WpisPomiarRepository wpisPomiarRepository;
    private WpisLekRepository wpisLekRepository;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etap_terapi);
        userUid = FirebaseAuth.getInstance().getUid();
        aktywnosc = (Integer) getIntent().getExtras().get(EXTRA_Aktywnosc);
        etapId = (String) getIntent().getExtras().get(EXTRA_Etap_ID);
        etapTerapiaRepository = new EtapTerapiaRepository(userUid);
        etapTerapa = etapTerapiaRepository.findById(etapId);

        View elementView = getLayoutInflater().inflate(R.layout.progres_bar, null, false);
        MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(EtapTerapiActivity.this)
                .setCancelable(false)
                .setTitle("Ładowanie danych")
                .setView(elementView);
        AlertDialog progers = progresbilder.create();
        //progers.getWindow().getAttributes().windowAnimations = R.style.FaidDialogAnimation;
        progers.show();

        godzinaWykonania = (TextInputLayout) findViewById(R.id.godzinaWykonaniaLayout);
        notatka = (TextInputLayout) findViewById(R.id.editTextNotatkaLayout);
        przycisk = (Button) findViewById(R.id.button_save_edit);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        if(aktywnosc != 1 || etapTerapa.getDataWykonania() == null) {
            przycisk.setText("Wykonaj etap");
            Date date = new Date();
            godzinaWykonania.getEditText().setText(simpleDateFormat.format(date));
            dodajTimePicker(godzinaWykonania.getEditText());
        }
        else {
            przycisk.setText("Aktualizuj");
            notatka.getEditText().setText(etapTerapa.getNotatka());
            godzinaWykonania.getEditText().setText(simpleDateFormat.format(etapTerapa.getDataWykonania()));
            dodajTimePicker(godzinaWykonania.getEditText());
        }

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {

                terapiaRepository = new TerapiaRepository(userUid);
                pomiarRepository = new PomiarRepository(userUid);
                lekRepository = new LekRepository(userUid);
                jednostkiRepository = new JednostkiRepository(userUid);
                wpisPomiarRepository = new WpisPomiarRepository(userUid);
                wpisLekRepository = new WpisLekRepository(userUid);


                listaElementowL = new ArrayList<>();
                viewListyElementow = (LinearLayout) findViewById(R.id.listaElementow);


                terapia = terapiaRepository.findById(etapTerapa.getIdTerapi());
                ArrayList listaCzynnosci = new ArrayList();

                for(String uidCzynnosci : terapia.getIdsCzynnosci()){
                    View elementView = null;
                    try {
                        JSONObject czynnosc = new JSONObject(uidCzynnosci);
                        String szukaneId = (String) czynnosc.get("id");
                        if(czynnosc.get("typ").equals(Pomiar.class.getName())) {
                            Pomiar pomiar = pomiarRepository.findById(szukaneId);
                            listaCzynnosci.add(pomiar);

                            elementView = getLayoutInflater().inflate(R.layout.activity_etap_terapi_element_pomiar, null, false);
                            TextView textView = (TextView) elementView.findViewById(R.id.textView3);
                            TextView textView1 = (TextView) elementView.findViewById(R.id.notayka);

                            textView.setText(pomiar.getNazwa());
                            textView1.setText(pomiar.getNotatka());

                            if (pomiar.getIdJednostki() != null) {
                                TextView textView2 = (TextView) elementView.findViewById(R.id.jednostka);
                                textView2.setText(jednostkiRepository.findById(pomiar.getIdJednostki()).getWartosc());
                            } else {
                                EditText editText = (EditText) elementView.findViewById(R.id.editTextWynik);
                                editText.setMinLines(3);
                                editText.setGravity(Gravity.START);
                            }

                        }
                        else if(czynnosc.get("typ").equals(Lek.class.getName())) {
                            Lek lek = lekRepository.findById(szukaneId);
                            listaCzynnosci.add(lek);

                            elementView = getLayoutInflater().inflate(R.layout.activity_etap_terapi_element_lek, null, false);
                            TextView textView = (TextView) elementView.findViewById(R.id.textView3);
                            TextView textView1 = (TextView) elementView.findViewById(R.id.notayka);

                            String nazwa = lek.getNazwa()+": ";
                            Jednostka jednostka = jednostkiRepository.findById(lek.getIdJednostki());
                            if(jednostka.getTypZmiennej() == 0)
                                nazwa += (int)czynnosc.getInt("dawka");
                            else
                                nazwa += czynnosc.getDouble("dawka");
                            nazwa += " "+jednostka.getWartosc();
                            textView.setText(nazwa);
                            textView1.setText(lek.getNotatka());
                        }

                    }catch (
                            JSONException e) {
                        e.printStackTrace();
                    }
                    if(elementView != null) {
                        listaElementowL.add(elementView);
                        viewListyElementow.addView(elementView);
                    }
                }


                if(aktywnosc != 1 || etapTerapa.getDataWykonania() == null) {
                    przycisk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ArrayList<String> listaWynikow = validate(listaCzynnosci);
                            if(listaWynikow == null)
                                return;

                            Calendar c = Calendar.getInstance();
                            try {
                                c.setTime(simpleDateFormat.parse(godzinaWykonania.getEditText().getText().toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar cData = Calendar.getInstance();
                            cData.set(Calendar.HOUR, c.get(Calendar.HOUR));
                            cData.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
                            cData.set(Calendar.SECOND, c.get(Calendar.SECOND));

                            int index = 0;
                            for( Object czynnosc : listaCzynnosci){
                                if(czynnosc.getClass().equals(Pomiar.class)) {
                                    Pomiar pomiar = (Pomiar) czynnosc;

                                    wpisPomiarRepository.insert(new WpisPomiar(listaWynikow.get(index).toString(), pomiar.getId(), userUid, etapTerapa.getId(), cData.getTime(), new Date(), new Date()));

                                }
                                else if(czynnosc.getClass().equals(Lek.class)) {
                                    Lek lek = (Lek) czynnosc;
                                    int finalIndex = index;
                                    wpisLekRepository.getByLekId(lek.getId()).whereLessThan("dataWykonania", cData.getTime()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                try {
                                                    List<WpisLek> lista = task.getResult().toObjects(WpisLek.class);
                                                    JSONObject czynnosc = new JSONObject(terapia.getIdsCzynnosci().get(finalIndex));
                                                    Double obrut = czynnosc.getDouble("dawka");
                                                    Double zapasLeku = 0.0;
                                                    if (!lista.isEmpty()) {
                                                        WpisLek wpisLek = lista.get(0);
                                                        zapasLeku = Double.parseDouble(wpisLek.getPozostalyZapas());
                                                    }
                                                    if (listaWynikow.get(finalIndex).equals(String.valueOf(true))) {
                                                        wpisLekRepository.insert(new WpisLek(((Double) (obrut * -1)).toString(), ((Double) (zapasLeku - obrut)).toString(), ((Lek) listaCzynnosci.get(finalIndex)).getId(), userUid, etapTerapa.getId(), cData.getTime(), new Date(), new Date()));
                                                    }
                                                }catch (Exception e){
                                                }
                                            }

                                        }
                                    });
                                }
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
                    List listaWpisow = new ArrayList<>();
                    int i = 0;
                    for (Object czynnosc: listaCzynnosci) {
                        View elementView = listaElementowL.get(i);
                        if(czynnosc.getClass().equals(Pomiar.class)){
                            Pomiar pomiar = (Pomiar) czynnosc;
                            WpisPomiar wpisPomiar = wpisPomiarRepository.findByEtapIdPomiarId(etapTerapa.getId(), pomiar.getId());
                            listaWpisow.add(wpisPomiar);
                            if(wpisPomiar!=null) {
                                EditText editText = (EditText) elementView.findViewById(R.id.editTextWynik);
                                editText.setText(wpisPomiar.getWynikPomiary());
                            }
                        }
                        if(czynnosc.getClass().equals(Lek.class)) {
                            Lek lek = (Lek) czynnosc;
                            WpisLek wpisLek = wpisLekRepository.findByEtapIdLekId(etapTerapa.getId(), lek.getId());
                            listaWpisow.add(wpisLek);
                            if(wpisLek!=null) {
                                MaterialSwitch materialSwitch = (MaterialSwitch) elementView.findViewById(R.id.toggleSwitch);
                                materialSwitch.setChecked(true);
                            }
                        }
                        i++;
                    }

                    przycisk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<String> listaWynikow = validate(listaCzynnosci);
                            Log.w("TAG-etap", "listaWynikow: "+listaWynikow.toString());
                            if(listaWynikow == null)
                                return;



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

                            etapTerapa.setNotatka(notatka.getEditText().toString());
                            etapTerapa.setDataWykonania(cData.getTime());
                            etapTerapa.setDataAktualizacji(new Date());
                            etapTerapiaRepository.update(etapTerapa);

                            int index = 0;
                            for( Object wpis : listaWpisow){
                                if(wpis != null && wpis.getClass().equals(WpisPomiar.class)) {
                                    WpisPomiar wpisPomiar = (WpisPomiar)wpis;
                                    wpisPomiar.setWynikPomiary(listaWynikow.get(index).toString());
                                    wpisPomiar.setDataAktualizacji(new Date());
                                    wpisPomiarRepository.update(wpisPomiar);
                                }
                                else {

                                    WpisLek wpisLek = (WpisLek)wpis;
                                    if(listaWynikow.get(index).equals(String.valueOf(true))){

                                        if(wpis != null) {
                                            wpisLek.setDataWykonania(cData.getTime());
                                            wpisLek.setDataAktualizacji(new Date());
                                            wpisLekRepository.update(wpisLek);
                                        }
                                        else {
                                            final int finalIndex = index;
                                            wpisLekRepository.getByLekId(((Lek)listaCzynnosci.get(index)).getId()).whereLessThan("dataWykonania", cData.getTime()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        try {
                                                            List<WpisLek> lista = task.getResult().toObjects(WpisLek.class);
                                                            JSONObject czynnosc = new JSONObject(terapia.getIdsCzynnosci().get(finalIndex));
                                                            Double obrut = czynnosc.getDouble("dawka");
                                                            Double zapasLeku = 0.0;
                                                            if (!lista.isEmpty()) {
                                                                WpisLek wpisLek = lista.get(0);
                                                                zapasLeku = Double.parseDouble(wpisLek.getPozostalyZapas());
                                                            }
                                                            if (listaWynikow.get(finalIndex).equals(String.valueOf(true))) {
                                                                wpisLekRepository.insert(new WpisLek(((Double) (obrut * -1)).toString(), ((Double) (zapasLeku - obrut)).toString(), ((Lek) listaCzynnosci.get(finalIndex)).getId(), userUid, etapTerapa.getId(), cData.getTime(), new Date(), new Date()));
                                                            }
                                                        }catch (Exception e){
                                                        }
                                                    }
                                                }
                                            });
                                        }

                                    }
                                    else if(wpis != null){
                                        wpisLekRepository.delete(wpisLek);
                                    }


                                }
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
                progers.cancel();
            }
        };

        handler.postDelayed(r, 500);



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

    private ArrayList validate(ArrayList listaCzynnosci){
        ArrayList<String> listaWynikow = new ArrayList<>();
        for(int i=0; i<listaElementowL.size(); i++) {
            View element = listaElementowL.get(i);
            Object czynnosc = listaCzynnosci.get(i);
            if(czynnosc.getClass().equals(Pomiar.class)) {
                String wynikOdczyt = ((EditText) element.findViewById(R.id.editTextWynik)).getText().toString();
                Pomiar pomiar = (Pomiar) czynnosc;
                if (pomiar.getIdJednostki() != null) {
                    wynikOdczyt = wynikOdczyt.replaceAll("\\,", ".");
                    if (wynikOdczyt.length() > 0 && Double.parseDouble(wynikOdczyt) > 0) {
                        wynikOdczyt = ((Double) (Double.parseDouble(wynikOdczyt))).toString();
                    } else {
                        Toast.makeText(getApplicationContext(), "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }
                listaWynikow.add(wynikOdczyt);
            }
            else if(czynnosc.getClass().equals(Lek.class)) {
                MaterialSwitch materialSwitch = element.findViewById(R.id.toggleSwitch);
                if(materialSwitch.isChecked())
                    listaWynikow.add(String.valueOf(true));
                else
                    listaWynikow.add(String.valueOf(false));
            }
        }
        return listaWynikow;
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