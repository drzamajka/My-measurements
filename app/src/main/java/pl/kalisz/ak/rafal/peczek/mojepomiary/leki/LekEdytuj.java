package pl.kalisz.ak.rafal.peczek.mojepomiary.leki;

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
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyLeki.WpisLekAdapter;

public class LekEdytuj extends AppCompatActivity {

    public static final String EXTRA_Lek_ID = "lekId";
    private String lekId;

    private Lek lek;
    private TextInputLayout nazwa, notatka;
    private AutoCompleteTextView jednostki;
    private TextInputLayout jednostkiL;
    private List<Jednostka> listaJednostek;
    private int idWybranejJednostki;
    public String textWybranejJednostki;

    private LekRepository lekRepository;
    private JednostkiRepository jednostkiRepository;
    private WpisLekRepository wpisLekRepository;
    private WpisLekAdapter wpisLekAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lek_edytuj);


        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        lekRepository = new LekRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        wpisLekRepository = new WpisLekRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

        idWybranejJednostki = 0;
        textWybranejJednostki = "";
        lekId = (String) getIntent().getExtras().get(EXTRA_Lek_ID);

        nazwa = (TextInputLayout) findViewById(R.id.editTextNazwaLayout);
        notatka = (TextInputLayout) findViewById(R.id.editTextJednostkaLayout);
        jednostki = (AutoCompleteTextView) findViewById(R.id.spinner);
        jednostkiL = (TextInputLayout) findViewById(R.id.spinnerLayout);

        lek = lekRepository.findById(lekId);
        if(lek == null){
            finish();
        }

        listaJednostek = new ArrayList<>();
        jednostkiRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> data = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                        Jednostka jednostka = queryDocumentSnapshot.toObject(Jednostka.class);
                        jednostka.setId(queryDocumentSnapshot.getId());
                        listaJednostek.add(jednostka);
                        data.add(jednostka.getNazwa()+" "+jednostka.getWartosc());
                        if(lek.getIdJednostki().equals(jednostka.getId())) {
                            idWybranejJednostki = data.size()-1;
                        }
                    }
                    ArrayAdapter adapter = new ArrayAdapter ( LekEdytuj.this, android.R.layout.simple_spinner_dropdown_item, data);
                    jednostki.setAdapter(adapter);

                    textWybranejJednostki = data.get(idWybranejJednostki);
                    jednostki.setText(textWybranejJednostki, false);
                    jednostkiL.setEnabled(false);
                }
                else {
                    Log.i("Tag", "błąd odczytu jednostek" );
                }
            }
        });



        nazwa.getEditText().setText(lek.getNazwa());
        notatka.getEditText().setText(lek.getNotatka());



        jednostki.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idWybranejJednostki = position;
            }
        });

        setupChart();
    }


    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object_change_and_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LekEdytuj.this);
                builder.setMessage("Czy na pewno usunąć");
                builder.setCancelable(false);
                builder.setPositiveButton("Tak", (DialogInterface.OnClickListener) (dialog, which) -> {
                    if (lek != null) {
                        lekRepository.delete(lek);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        if(lek   != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            wpisLekRepository.getQueryByLekId(lek.getId(), 10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.w("TAG-grap", "task: "+task.isSuccessful());
                    if(task.isSuccessful()){
                        List<WpisLek> wpisLekList = task.getResult().toObjects(WpisLek.class);
                        List<BarEntry> entries = new ArrayList<>();


                        if(wpisLekList.size()>0)
                            ((Button) findViewById(R.id.button_all)).setEnabled(true);



                        for(int i = 0; i<wpisLekList.size(); i++){
                        //for(WpisLek wpisLek : wpisLekList){
                            WpisLek wpisLek = wpisLekList.get(wpisLekList.size()-(i+1));
                            entries.add(new BarEntry(Float.parseFloat(""+i), Float.parseFloat(wpisLek.getPozostalyZapas())));
                        }

                        BarDataSet set = new BarDataSet(entries, "DataSet");
                        set.setColor(colorPrimary);
                        BarData data = new BarData(set);
                        data.setValueTextSize(14f);
                        data.setValueTextColor(colorTextLight);
                        data.setBarWidth(0.9f); // set custom bar width
                        chart.setData(data);
                        chart.setFitBars(true); // make the x-axis fit exactly all bars

                        Log.w("TAG-grap", "wpisLekList: "+wpisLekList.size());
                        chart.invalidate(); // refresh
                        TextView jednostka = elementView.findViewById(R.id.textView3);
                        jednostka.setText(listaJednostek.get(idWybranejJednostki).getNazwa());

                        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                            @Override
                            public void onValueSelected(Entry e, Highlight h) {
                                View elementView = getLayoutInflater().inflate(R.layout.wpis_pomiar_text_view, null, false);
                                TextView wpisTextView = elementView.findViewById(R.id.wpisTextView);
                                TextView dataTextView = elementView.findViewById(R.id.dataTextView);
                                WpisLek wpisLek = wpisLekList.get(wpisLekList.size() - (((int) e.getX() + 1)));
                                if(Double.parseDouble(wpisLek.getSumaObrotu())>=0) {
                                    wpisTextView.setText("uzupełniono o ");
                                    if(listaJednostek.get(idWybranejJednostki).getTypZmiennej() == 0){
                                        wpisTextView.setText(wpisTextView.getText()+((int)Double.parseDouble(wpisLek.getSumaObrotu())+" ")+listaJednostek.get(idWybranejJednostki).getWartosc()+" leku");
                                    }
                                    else{
                                        wpisTextView.setText(wpisTextView.getText()+(Double.parseDouble(wpisLek.getSumaObrotu())+" ")+listaJednostek.get(idWybranejJednostki).getWartosc()+" leku");
                                    }
                                }
                                else{
                                    wpisTextView.setText("pobrano ");
                                    if(listaJednostek.get(idWybranejJednostki).getTypZmiennej() == 0){
                                        wpisTextView.setText(wpisTextView.getText()+((int)(Double.parseDouble(wpisLek.getSumaObrotu())*-1)+" ")+listaJednostek.get(idWybranejJednostki).getWartosc()+" leku");
                                    }
                                    else{
                                        wpisTextView.setText(wpisTextView.getText()+((Double.parseDouble(wpisLek.getSumaObrotu())*-1)+" ")+listaJednostek.get(idWybranejJednostki).getWartosc()+" leku");
                                    }
                                }


                                            //+Double.parseDouble(wpisLek.getSumaObrotu())+" "+listaJednostek.get(idWybranejJednostki).getWartosc());
                                dataTextView.setText(sdf.format(wpisLek.getDataWykonania()));
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
        FirestoreRecyclerOptions<WpisLek> options
                = new FirestoreRecyclerOptions.Builder<WpisLek>()
                .setQuery(wpisLekRepository.getQuery().whereEqualTo("idLeku", lek.getId()).orderBy("dataWykonania", Query.Direction.DESCENDING), WpisLek.class)
                .build();

        WpisLekAdapter wpisLekAdapter = new WpisLekAdapter(options);

        FullScreanDialog fullScreanDialog = new FullScreanDialog();
        fullScreanDialog.display(getSupportFragmentManager(), wpisLekAdapter, "Wszytkie wpisy");
    }

    public void stopEdit(View view) {
        Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
        Button anuluj = (Button) findViewById(R.id.button_disable_edit);
        aktualizuj.setEnabled(false);
        anuluj.setEnabled(false);
        nazwa.setEnabled(false);
        notatka.setEnabled(false);
        jednostkiL.setEnabled(false);
        nazwa.getEditText().setText(lek.getNazwa());
        notatka.getEditText().setText(lek.getNotatka());
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
            String jednostkaId = listaJednostek.get(idWybranejJednostki).getId();

            lek.setNazwa(nazwa);
            lek.setNotatka(notatka);
            lek.setIdJednostki(jednostkaId);
            lek.setDataAktualizacji(new Date());

            lekRepository.update(lek);
            finish();
        }else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }

}
