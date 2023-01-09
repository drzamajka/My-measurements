package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.FullScreanDialog;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyPomiary.WpisPomiarAdapter;

public class PomiarEdytuj extends AppCompatActivity {

    public static final String EXTRA_Pomiar_ID = "pomiarId";
    private String pomiarId;

    Pomiar pomiar;
    private TextInputLayout nazwa, notatka;
    private AutoCompleteTextView jednostki;
    private TextInputLayout jednostkiL;
    private List<Jednostka> listaJednostek;
    private int idWybranejJednostki;
    public String textWybranejJednostki;

    private PomiarRepository pomiarRepository;
    private JednostkiRepository jednostkiRepository;
    private WpisPomiarRepository wpisPomiarRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiary_edytuj);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        wpisPomiarRepository = new WpisPomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

        idWybranejJednostki = 0;
        textWybranejJednostki = "Opis Tekstowy";
        pomiarId = (String) getIntent().getExtras().get(EXTRA_Pomiar_ID);

        nazwa = (TextInputLayout) findViewById(R.id.editTextNazwaLayout);
        notatka = (TextInputLayout) findViewById(R.id.editTextJednostkaLayout);
        jednostki = (AutoCompleteTextView) findViewById(R.id.spinner);
        jednostkiL = (TextInputLayout) findViewById(R.id.spinnerLayout);

        pomiar = pomiarRepository.findById(pomiarId);
        if(pomiar == null){
            finish();
        }

        listaJednostek = new ArrayList<>();
        jednostkiRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> data = new ArrayList<>();
                    data.add("Opis Tekstowy");
                    for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                        Jednostka jednostka = queryDocumentSnapshot.toObject(Jednostka.class);
                        jednostka.setId(queryDocumentSnapshot.getId());
                        listaJednostek.add(jednostka);
                        data.add(jednostka.getNazwa()+" "+jednostka.getWartosc());
                        if(jednostka.getId().equals(pomiar.getIdJednostki())) {
                            idWybranejJednostki = data.size();
                        }
                    }
                    ArrayAdapter adapter = new ArrayAdapter ( PomiarEdytuj.this, android.R.layout.simple_spinner_dropdown_item, data);
                    jednostki.setAdapter(adapter);
                    if(idWybranejJednostki!=0)
                        textWybranejJednostki = data.get(idWybranejJednostki-1);
                    jednostki.setText(textWybranejJednostki, false);
                    jednostkiL.setEnabled(false);
                }
                else {
                    Log.i("Tag", "błąd odczytu jednostek" );
                }
            }
        });



        nazwa.getEditText().setText(pomiar.getNazwa());
        notatka.getEditText().setText(pomiar.getNotatka());

        if(pomiar.getIdJednostki()!=null) {
            setupChart();
        }
        else{
            setupTextData();
        }


        jednostki.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idWybranejJednostki = position;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object_change, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId() ) {

            case R.id.edit:
                Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
                Button anuluj = (Button) findViewById(R.id.button_disable_edit);
                aktualizuj.setEnabled(true);
                anuluj.setEnabled(true);
                nazwa.setEnabled(true);
                notatka.setEnabled(true);
                jednostkiL.setEnabled(true);
                return true;
            case R.id.drop:{
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PomiarEdytuj.this);
                builder.setMessage("Czy na pewno usunąć");
                builder.setCancelable(false);
                builder.setPositiveButton("Tak", (DialogInterface.OnClickListener) (dialog, which) -> {
                    if (pomiar != null) {
                        pomiarRepository.delete(pomiar);
                    }
                    finish();
                });

                builder.setNegativeButton("Nie", (DialogInterface.OnClickListener) (dialog, which) -> {
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

    private void setupTextData() {
        LinearLayout wpisyLayout = (LinearLayout) findViewById(R.id.wpisyLayout);
        wpisyLayout.removeAllViews();


        if (pomiar != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            wpisPomiarRepository.getQueryByPomiarId(pomiar.getId(), 10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.w("TAG-grap", "task: " + task.isSuccessful());
                    if (task.isSuccessful()) {

                        List<WpisPomiar> wpisPomiarList = task.getResult().toObjects(WpisPomiar.class);
                        List<BarEntry> entries = new ArrayList<>();
                        if(wpisPomiarList.size()>0)
                            ((Button) findViewById(R.id.button_all)).setEnabled(true);

                        int i = 0;
                        for (WpisPomiar wpisPomiar : wpisPomiarList) {
                            View elementView = getLayoutInflater().inflate(R.layout.wpis_pomiar_text_view, null, false);
                            TextView wpisTextView = elementView.findViewById(R.id.wpisTextView);
                            TextView dataTextView = elementView.findViewById(R.id.dataTextView);
                            wpisTextView.setText(wpisPomiar.getWynikPomiary());
                            dataTextView.setText(sdf.format(wpisPomiar.getDataWykonania()));
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
                            if(i != 0)
                                layoutParams.setMargins(0, 24, 0, 0);
                            wpisyLayout.addView(elementView, layoutParams);
                            i++;
                        }


                    }
                }
            });

        }
    }

    private void setupChart() {
        View elementView = getLayoutInflater().inflate(R.layout.chart_view, null, false);
        LinearLayout wpisyLayout = (LinearLayout) findViewById(R.id.wpisyLayout);
        wpisyLayout.removeAllViews();
        wpisyLayout.addView(elementView);

        BarChart chart = (BarChart) elementView.findViewById(R.id.chart);

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorTextLight = ContextCompat.getColor(getApplicationContext(), com.firebase.ui.firestore.R.color.common_google_signin_btn_text_light);
        int colorTextDark = ContextCompat.getColor(getApplicationContext(), com.firebase.ui.firestore.R.color.common_google_signin_btn_text_dark);


        switch (getResources().getConfiguration().uiMode-1) {
            case Configuration.UI_MODE_NIGHT_YES:
                chart.getAxisLeft().setTextColor(colorTextDark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                chart.getAxisLeft().setTextColor(colorTextLight);
                break;
        }

        chart.setDrawValueAboveBar(false);
        chart.getDescription().setEnabled(false);

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
//        chart.getXAxis().setTextColor(Color.WHITE);


        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(true);



//wpisywanie danych
        if(pomiar   != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            wpisPomiarRepository.getQueryByPomiarId(pomiar.getId(), 10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.w("TAG-grap", "task: "+task.isSuccessful());
                    if(task.isSuccessful()){
                        List<WpisPomiar> wpisPomiarList = task.getResult().toObjects(WpisPomiar.class);
                        List<BarEntry> entries = new ArrayList<>();

                        if(wpisPomiarList.size()>0)
                            ((Button) findViewById(R.id.button_all)).setEnabled(true);

                        int i=0;
                        for(WpisPomiar wpisPomiar : wpisPomiarList){
                            entries.add(new BarEntry(Float.parseFloat(""+i), Float.parseFloat(wpisPomiar.getWynikPomiary())));
                            i++;
                        }

                        BarDataSet set = new BarDataSet(entries, "DataSet");
                        set.setColor(colorPrimary);
                        BarData data = new BarData(set);
                        data.setValueTextSize(14f);
                        data.setValueTextColor(colorTextLight);
                        data.setBarWidth(0.9f); // set custom bar width
                        chart.setData(data);
                        chart.setFitBars(true); // make the x-axis fit exactly all bars

                        Log.w("TAG-grap", "wpisPomiarList: "+wpisPomiarList.size());
                        chart.invalidate(); // refresh
                        TextView jednostka = elementView.findViewById(R.id.textView3);
                        jednostka.setText(listaJednostek.get(idWybranejJednostki-2).getNazwa());

                        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                            @Override
                            public void onValueSelected(Entry e, Highlight h) {
                                View elementView = getLayoutInflater().inflate(R.layout.wpis_pomiar_text_view, null, false);
                                TextView wpisTextView = elementView.findViewById(R.id.wpisTextView);
                                wpisTextView.setText("pomiar wykonany: "+sdf.format(wpisPomiarList.get((int) e.getX()).getDataWykonania()));
                                wpisyLayout.addView(elementView,1);

                                if(wpisyLayout.getChildCount()>2)
                                    wpisyLayout.removeViewAt(2);
                            }

                            @Override
                            public void onNothingSelected() {
                                wpisyLayout.removeViewAt(1);
                            }
                        });
                    }
                }
            });

        }
    }

    public void wyswietlWszystkie(View view){
        FirestoreRecyclerOptions<WpisPomiar> options
                = new FirestoreRecyclerOptions.Builder<WpisPomiar>()
                .setQuery(wpisPomiarRepository.getQuery().whereEqualTo("idPomiar", pomiar.getId()).orderBy("dataWykonania", Query.Direction.DESCENDING), WpisPomiar.class)
                .build();

        WpisPomiarAdapter wpisPomiarAdapter = new WpisPomiarAdapter(options);

        FullScreanDialog fullScreanDialog = new FullScreanDialog();
        fullScreanDialog.display(getSupportFragmentManager(), wpisPomiarAdapter, "Wszytkie pomiary");
    }

    public void stopEdit(View view) {
        Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
        Button anuluj = (Button) findViewById(R.id.button_disable_edit);
        aktualizuj.setEnabled(false);
        anuluj.setEnabled(false);
        nazwa.setEnabled(false);
        notatka.setEnabled(false);
        jednostkiL.setEnabled(false);
        nazwa.getEditText().setText(pomiar.getNazwa());
        notatka.getEditText().setText(pomiar.getNotatka());
        jednostki.setText(textWybranejJednostki, false);
    }

    public void aktualizuj(View view) {
        String nazwa = this.nazwa.getEditText().getText().toString();
        String notatka = this.notatka.getEditText().getText().toString();

        if( jednostki.getText().length()>0 && nazwa.length()>=2 && notatka.length()>=2) {
            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            notatka = notatka.substring(0, 1).toUpperCase() + notatka.substring(1);
            if (notatka.charAt(notatka.length() - 1) != '.')
                notatka += '.';

            String jednostkaId = null;
            if(idWybranejJednostki!=0)
                jednostkaId = listaJednostek.get(idWybranejJednostki-1).getId();

            pomiar.setNazwa(nazwa);
            pomiar.setNotatka(notatka);
            pomiar.setIdJednostki(jednostkaId);
            pomiar.setDataAktualizacji(new Date());

            pomiarRepository.update(pomiar);
            finish();
        }else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}
