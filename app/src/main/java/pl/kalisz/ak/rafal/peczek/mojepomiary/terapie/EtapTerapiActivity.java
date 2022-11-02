package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.EtapTerapiPosiaRelacie;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.PomiarPosiadRelacie;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.TerapiaPosiadEtay;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class EtapTerapiActivity extends AppCompatActivity {

    public static String EXTRA_Etap_ID = "etapId";
    public static String EXTRA_Aktywnosc = "Aktywnosc";

    private int etapId, aktywnosc;

    private TimePicker timePicker;
    private LinearLayout viewListyElementow;
    private ArrayList<View> listaElementowL;
    private Button przycisk;
    private EditText notatka;

    private UsersRoomDatabase database;
    private EtapTerapiPosiaRelacie etapTerapiPosiaRelacie;
    List<PomiarPosiadRelacie> listaCzynnosci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etap_terapi);

        etapId = (Integer) getIntent().getExtras().get(EXTRA_Etap_ID);
        aktywnosc = (Integer) getIntent().getExtras().get(EXTRA_Aktywnosc);
        listaElementowL = new ArrayList<>();

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        etapTerapiPosiaRelacie = database.localEtapTerapaDao().findByIdWithRelations(etapId);
        int[] listaIdsCzynnosci = etapTerapiPosiaRelacie.terapia.getIdsCzynnosci().stream().mapToInt(i -> i).toArray();
        listaCzynnosci = database.localPomiarDao().loadAllByIds(listaIdsCzynnosci);

        timePicker = (TimePicker) findViewById(R.id.dataWykonania);
        viewListyElementow = (LinearLayout) findViewById(R.id.listaElementow);
        notatka = (EditText) findViewById(R.id.editTextNotatka);
        przycisk = (Button) findViewById(R.id.button_save_edit);

        Date data = etapTerapiPosiaRelacie.etapTerapa.getDataZaplanowania();
        timePicker.setIs24HourView(true);
        timePicker.setHour(data.getHours());
        timePicker.setMinute(data.getMinutes());

        for (PomiarPosiadRelacie pomiar: listaCzynnosci) {
            View elementView = getLayoutInflater().inflate(R.layout.activity_etap_terapi_element, null, false);
            listaElementowL.add(elementView);

            TextView textView = (TextView) elementView.findViewById(R.id.textView3);
            textView.setText(pomiar.pomiar.getNazwa());
            TextView textView1 = (TextView) elementView.findViewById(R.id.notayka);
            textView1.setText(textView1.getText()+" "+pomiar.pomiar.getNotatka());
            TextView textView2 = (TextView) elementView.findViewById(R.id.jednostka);
            textView2.setText(pomiar.jednostka.getWartosc());
            EditText editText = (EditText) elementView.findViewById(R.id.editTextWynik);

            viewListyElementow.addView(elementView);
        }

        if(etapId != 0) {
            przycisk.setText("Wykonaj etap");
            przycisk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Double> listaWynikow = new ArrayList<>();
                    for (View element : listaElementowL) {
                        String wynikOdczyt = ((EditText) element.findViewById(R.id.editTextWynik)).getText().toString();
                        if (wynikOdczyt.length() > 0 && Double.parseDouble(wynikOdczyt) > 0) {
                            Double wynik = Double.parseDouble(wynikOdczyt);
                            listaWynikow.add(wynik);
                        } else {
                            Toast.makeText(getApplicationContext(), "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    int index = 0;
                    for (PomiarPosiadRelacie pomiar: listaCzynnosci) {

                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR, timePicker.getHour());
                        c.set(Calendar.MINUTE, timePicker.getMinute());

                        int id = database.localWpisPomiarDao().countAll();
                        database.localWpisPomiarDao().insert(new WpisPomiar((id+1),listaWynikow.get(index).toString(), pomiar.pomiar.getId(), etapTerapiPosiaRelacie.etapTerapa.getId(), c.getTime(), new Date(), new Date() ));
                        index++;
                    }
                    etapTerapiPosiaRelacie.etapTerapa.setDataWykonania(new Date());
                    etapTerapiPosiaRelacie.etapTerapa.setDataAktualizacji(new Date());
                    etapTerapiPosiaRelacie.etapTerapa.setNotatka(notatka.getText().toString());
                    database.localEtapTerapaDao().insert(etapTerapiPosiaRelacie.etapTerapa);
                    Toast.makeText(getApplicationContext(), "zapisano", Toast.LENGTH_SHORT).show();
                    finish();

                }
            });
        }
        else {
            przycisk.setText("Aktualizuj");
        }
    }

}