package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.FullScreanDialog;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
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

public class TerapiaEdytuj extends AppCompatActivity {

    public static final String EXTRA_Terapia_ID = "terapiaId";
    private String terapiaId;

    private TextInputLayout elementyTerapi;
    private TextInputLayout dataRozpoczecia, dataZakonczenia, notatka;
    private Terapia terapia;
    private List<Object> listaElementowTerapi;
    private List<Jednostka> listaJednostek;

    private TerapiaRepository terapiaRepository;
    private PomiarRepository pomiarRepository;
    private LekRepository lekRepositoryl;
    private JednostkiRepository jednostkiRepository;
    private WpisPomiarRepository wpisPomiarRepository;
    private WpisLekRepository wpisLekRepository;
    private String userUid;

    private EtapTerapiaRepository etapTerapiaRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terapia_edytuj);
        terapiaId = (String) getIntent().getExtras().get(EXTRA_Terapia_ID);

        elementyTerapi = findViewById(R.id.elementyLayout);
        dataRozpoczecia = findViewById(R.id.dataRozpoczeciaLayout);
        dataZakonczenia = findViewById(R.id.dataZakonczeniaLayout);
        notatka = findViewById(R.id.NotatkaLayout);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        terapiaRepository = new TerapiaRepository(userUid);
        pomiarRepository = new PomiarRepository(userUid);
        lekRepositoryl = new LekRepository(userUid);
        jednostkiRepository = new JednostkiRepository(userUid);
        etapTerapiaRepository = new EtapTerapiaRepository(userUid);
        wpisPomiarRepository = new WpisPomiarRepository(userUid);
        wpisLekRepository = new WpisLekRepository(userUid);

        listaElementowTerapi = new ArrayList<>();
        listaJednostek = jednostkiRepository.getAll();


        terapia = terapiaRepository.findById(terapiaId);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        dataRozpoczecia.getEditText().setText(sdf.format(terapia.getDataRozpoczecia()));
        dataZakonczenia.getEditText().setText(sdf.format(terapia.getDataZakonczenia()));
        notatka.getEditText().setText(terapia.getNotatka());

        try {
            String tytul = "";
            ArrayList<String> listaElementow = terapia.getIdsCzynnosci();
            for (int i = 0; i < listaElementow.size(); i++) {
                if (i != 0) {
                    tytul += "\n";
                }
                JSONObject czynnosc = new JSONObject(listaElementow.get(i));
                String szukaneId = (String) czynnosc.get("id");
                if (czynnosc.get("typ").equals(Pomiar.class.getName())) {
                    Pomiar pomiar = pomiarRepository.findById(szukaneId);
                    if (pomiar != null) {
                        listaElementowTerapi.add(pomiar);
                        tytul += pomiar.getNazwa();
                    }
                } else if (czynnosc.get("typ").equals(Lek.class.getName())) {
                    Lek lek = lekRepositoryl.findById(szukaneId);
                    if (lek != null) {
                        listaElementowTerapi.add(lek);
                        tytul += lek.getNazwa() + ": ";

                        Jednostka jednostka = null;
                        for (Jednostka jednostkaTMP : listaJednostek) {
                            if (jednostkaTMP.getId().equals(lek.getIdJednostki()))
                                jednostka = jednostkaTMP;
                        }

                        if (jednostka != null) {
                            if (jednostka.getTypZmiennej() == 0)
                                tytul += (int) czynnosc.get("dawka");
                            else
                                tytul += czynnosc.get("dawka");
                            tytul += getString(R.string.spacia) + jednostka.getWartosc();
                        }
                    }
                }
            }
            elementyTerapi.getEditText().setText(tytul);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }


        etapTerapiaRepository.getQueryByIdTerapi(terapia.getId()).orderBy("dataZaplanowania", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<EtapTerapa> listaEtapTerapi = task.getResult().toObjects(EtapTerapa.class);
                    if (listaEtapTerapi.size() > 0) {
                        Button button = findViewById(R.id.button_all);
                        button.setEnabled(true);
                        setupChart(task.getResult().toObjects(EtapTerapa.class));
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object_delete, menu);
        return true;
    }

    private void setupChart(List<EtapTerapa> listaEtapTerapi) {
        View elementView = null;
        LinearLayout wpisyLayout = findViewById(R.id.wpisyLayout);
        wpisyLayout.removeAllViews();

        for (Object czynnosc : listaElementowTerapi) {
            if (czynnosc.getClass().equals(Lek.class)) {
                elementView = getLekChart((Lek) czynnosc, listaEtapTerapi);
            } else if (czynnosc.getClass().equals(Pomiar.class)) {
                elementView = getPomiarChart((Pomiar) czynnosc, listaEtapTerapi);
            }
            if (elementView != null)
                wpisyLayout.addView(elementView);
        }

    }

    private View getLekChart(Lek lek, List<EtapTerapa> listaEtapTerapi) {
        View elementView = getLayoutInflater().inflate(R.layout.chart_view, null, false);
        BarChart chart = elementView.findViewById(R.id.chart);

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorTextLight = ContextCompat.getColor(getApplicationContext(), com.firebase.ui.firestore.R.color.common_google_signin_btn_text_light);
        int colorTextDark = ContextCompat.getColor(getApplicationContext(), com.firebase.ui.firestore.R.color.common_google_signin_btn_text_dark);

        switch (getResources().getConfiguration().uiMode - 1) {
            case Configuration.UI_MODE_NIGHT_YES:
                chart.getAxisLeft().setTextColor(colorTextDark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                chart.getAxisLeft().setTextColor(colorTextLight);
                break;
        }

        chart.getAxisRight().setEnabled(false); //legenda z prawej
        chart.getAxisLeft().setEnabled(true); //legenda z lewej
        chart.getAxisLeft().setDrawAxisLine(true);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setTextSize(14f);
        chart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value);

                if (index == 1 && Float.parseFloat(index + "") == value)
                    return getString(R.string.tak);
                if (index == 0 && Float.parseFloat(index + "") == value)
                    return getString(R.string.nie);

                return "";
            }
        });

        chart.setDrawGridBackground(false); // tło
        chart.setDrawBorders(false); // ramka
        chart.setDrawValueAboveBar(false);  // wartość słópków nad
        chart.getLegend().setEnabled(false); // legenda
        chart.getDescription().setEnabled(false); // opis

        // os x
        chart.getXAxis().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setTextSize(14f);

        chart.setScaleEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(true);

        if (lek != null) {
            TextView tytulView = elementView.findViewById(R.id.textView5);
            tytulView.setText(lek.getNazwa());

            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_czasu) + getString(R.string.format_daty));
            wpisLekRepository.getQueryByLekId(lek.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.w("TAG-grap", "task: " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        List<WpisLek> wpisLekList = task.getResult().toObjects(WpisLek.class);
                        List<BarEntry> entries = new ArrayList<>();

                        for (int i = 0; i < listaEtapTerapi.size(); i++) {
                            EtapTerapa etapTerapa = listaEtapTerapi.get(listaEtapTerapi.size() - 1 - i);
                            WpisLek wpisLekTMP = null;
                            for (WpisLek wpisLek : wpisLekList) {
                                if (etapTerapa.getId().equals(wpisLek.getIdEtapTerapi())) {
                                    wpisLekTMP = wpisLek;
                                }
                            }
                            if (wpisLekTMP != null) {
                                entries.add(new BarEntry(Float.parseFloat("" + (i)), Float.parseFloat("1")));
                            } else if (i + 1 == listaEtapTerapi.size()) {
                                entries.add(new BarEntry(Float.parseFloat("" + (i)), Float.parseFloat("0")));
                            }

                        }
                        if (entries.get(0).getX() != 0f) {
                            entries.add(new BarEntry(Float.parseFloat("0"), Float.parseFloat("0")));
                        }

                        BarDataSet set = new BarDataSet(entries, "DataSet");
                        set.setColor(colorPrimary);
                        BarData data = new BarData(set);
                        data.setDrawValues(false);
                        data.setHighlightEnabled(false);
                        data.setBarWidth(0.9f); // set custom bar width
                        chart.setData(data);
                        chart.setFitBars(true); // make the x-axis fit exactly all bars

                        Log.w("TAG-grap", "wpisLekList: " + wpisLekList.size());
                        chart.invalidate(); // refresh
                        TextView jednostkaView = elementView.findViewById(R.id.textView3);
                        jednostkaView.setText("Czy pobrano lek");

                    }
                }
            });

        }

        return elementView;
    }

    private View getPomiarChart(Pomiar pomiar, List<EtapTerapa> listaEtapTerapi) {
        View elementView = getLayoutInflater().inflate(R.layout.chart_view, null, false);

        BarChart chart = elementView.findViewById(R.id.chart);

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorTextLight = ContextCompat.getColor(getApplicationContext(), com.firebase.ui.firestore.R.color.common_google_signin_btn_text_light);
        int colorTextDark = ContextCompat.getColor(getApplicationContext(), com.firebase.ui.firestore.R.color.common_google_signin_btn_text_dark);


        switch (getResources().getConfiguration().uiMode - 1) {
            case Configuration.UI_MODE_NIGHT_YES:
                chart.getAxisLeft().setTextColor(colorTextDark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                chart.getAxisLeft().setTextColor(colorTextLight);
                break;
        }


        chart.getAxisRight().setEnabled(false); //legenda z prawej
        chart.getAxisLeft().setEnabled(true); //legenda z lewej
        chart.getAxisLeft().setDrawAxisLine(true);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setTextSize(14f);

        chart.setDrawGridBackground(false); // tło
        chart.setDrawBorders(false); // ramka
        chart.setDrawValueAboveBar(false);  // wartość słópków nad
        chart.getLegend().setEnabled(false); // legenda
        chart.getDescription().setEnabled(false); // opis

        // os x
        chart.getXAxis().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setTextSize(14f);

        chart.setScaleEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(true);

        if (pomiar != null) {
            TextView tytulView = elementView.findViewById(R.id.textView5);
            tytulView.setText(pomiar.getNazwa());

            if (pomiar.getIdJednostki() == null) {
                chart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = Math.round(value);

                        if (index == 1 && Float.parseFloat(index + "") == value)
                            return getString(R.string.tak);
                        if (index == 0 && Float.parseFloat(index + "") == value)
                            return getString(R.string.nie);

                        return "";
                    }
                });
            }

            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_czasu) + getString(R.string.format_daty));
            wpisPomiarRepository.getQueryByPomiarId(pomiar.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.w("TAG-grap", "task: " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        List<WpisPomiar> wpisPomiarList = task.getResult().toObjects(WpisPomiar.class);
                        List<BarEntry> entries = new ArrayList<>();


                        for (int i = 0; i < listaEtapTerapi.size(); i++) {
                            EtapTerapa etapTerapa = listaEtapTerapi.get(listaEtapTerapi.size() - 1 - i);
                            WpisPomiar wpisPomiarTMP = null;
                            for (WpisPomiar wpisPomiar : wpisPomiarList) {
                                if (etapTerapa.getId().equals(wpisPomiar.getIdEtapTerapi())) {
                                    wpisPomiarTMP = wpisPomiar;
                                }
                            }
                            if (wpisPomiarTMP != null) {
                                if (pomiar.getIdJednostki() != null) {
                                    entries.add(new BarEntry(Float.parseFloat("" + (i)), Float.parseFloat(wpisPomiarTMP.getWynikPomiary())));
                                } else {
                                    if (wpisPomiarTMP.getWynikPomiary().length() > 1) {
                                        entries.add(new BarEntry(Float.parseFloat("" + (i)), Float.parseFloat("1")));
                                    } else {
                                        entries.add(new BarEntry(Float.parseFloat("" + (i)), Float.parseFloat("0")));
                                    }
                                }
                            } else if (i + 1 == listaEtapTerapi.size()) {
                                entries.add(new BarEntry(Float.parseFloat("" + (i)), 0));
                            }

                        }
                        if (entries.get(0).getX() != 0f) {
                            entries.add(new BarEntry(Float.parseFloat("" + 0), 0));
                        }


                        BarDataSet set = new BarDataSet(entries, "DataSet");
                        set.setColor(colorPrimary);
                        BarData data = new BarData(set);
                        data.setDrawValues(false);
                        data.setHighlightEnabled(false);
                        data.setBarWidth(0.9f); // set custom bar width
                        chart.setData(data);
                        //chart.setFitBars(true); // make the x-axis fit exactly all bars

                        chart.invalidate(); // refresh

                        Jednostka jednostka = null;
                        for (Jednostka jednostkaTMP : listaJednostek) {
                            if (jednostkaTMP.getId().equals(pomiar.getIdJednostki()))
                                jednostka = jednostkaTMP;
                        }
                        TextView jednostkaView = elementView.findViewById(R.id.textView3);
                        if (jednostka != null) {
                            jednostkaView.setText(jednostka.getNazwa());
                        } else {
                            jednostkaView.setText("Czy wykonano pomiar");
                        }
                    } else {
                        Log.w("TAG-grap", "task error: " + task.getException());
                    }
                }
            });

        }

        return elementView;
    }

    public void wyswietlWszystkie(View view) {
        FirestoreRecyclerOptions<EtapTerapa> options
                = new FirestoreRecyclerOptions.Builder<EtapTerapa>()
                .setQuery(etapTerapiaRepository.getQuery().whereEqualTo("idTerapi", terapia.getId()).orderBy("dataZaplanowania", Query.Direction.ASCENDING), EtapTerapa.class)
                .build();

        EtapAdapter etapAdapter = new EtapAdapter(options);

        FullScreanDialog fullScreanDialog = new FullScreanDialog();
        fullScreanDialog.display(getSupportFragmentManager(), etapAdapter, "Sczegóły wpisów");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drop: {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(TerapiaEdytuj.this);
                builder.setMessage("Czy na pewno usunąć");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton(getString(R.string.tak), (dialog, which) -> {
                    if (terapia != null) {
                        terapiaRepository.delete(terapia);
                    }
                    finish();
                });

                builder.setNegativeButton(getString(R.string.nie), (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
                return true;
            }
            case android.R.id.home: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}