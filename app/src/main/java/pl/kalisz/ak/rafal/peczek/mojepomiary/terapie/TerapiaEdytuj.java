package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.TerapiaPosiadEtay;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class TerapiaEdytuj extends AppCompatActivity {

    public static final String EXTRA_Terapia_ID = "terapiaId";
    private int terapiaId;
    private TextView elementyTerapi;
    private EditText dataRozpoczecia, dataZakonczenia, notatka;
    private UsersRoomDatabase database;
    private Terapia terapia;
    private List<EtapTerapa> etapyTerapi;

    private RecyclerView rvPomiary;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terapia_edytuj);

        terapiaId = (Integer) getIntent().getExtras().get(EXTRA_Terapia_ID);
        elementyTerapi = (TextView) findViewById(R.id.textViewElementy);
        dataRozpoczecia = (EditText) findViewById(R.id.dataRozpoczecia);
        dataZakonczenia = (EditText) findViewById(R.id.dataZakonczenia);
        notatka = (EditText) findViewById(R.id.editTextNotatka);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        TerapiaPosiadEtay terapiaPosiadEtay = (TerapiaPosiadEtay) database.localTerapiaDao().findTerapieAndWpisyById(terapiaId);
        terapia = terapiaPosiadEtay.terapia;
        etapyTerapi = terapiaPosiadEtay.etapy;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        ArrayList<Integer> listaElementow = terapia.getIdsCzynnosci();
        for (int id: listaElementow) {
            Pomiar pomiar = database.localPomiarDao().findById(id);
            if(id != listaElementow.get(0))
                elementyTerapi.setText(elementyTerapi.getText()+"\n"+pomiar.getNazwa());
            else
                elementyTerapi.setText(pomiar.getNazwa());
        }
        dataRozpoczecia.setText(sdf.format(terapia.getDataRozpoczecia()));
        dataZakonczenia.setText(sdf.format(terapia.getDataZakonczenia()));
        notatka.setText(terapia.getNotatka());

        rvPomiary = (RecyclerView) findViewById(R.id.recycleView);
        //rvPomiary.setHasFixedSize(true);

        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
        } else
            layoutManager = new LinearLayoutManager(this);

        rvPomiary.setLayoutManager(layoutManager);
        //rvPomiary.setItemAnimator(new DefaultItemAnimator());
        rvPomiary = (RecyclerView) findViewById(R.id.recycleView);
    }

    @Override
    protected void onResume(){
        super.onResume();
        adapter = new EtapAdapter(etapyTerapi, database);
        rvPomiary.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId() ) {

            case R.id.drop:

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(TerapiaEdytuj.this);
                builder.setMessage("Czy na pewno usunąć");
//                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Tak", (DialogInterface.OnClickListener) (dialog, which) -> {
                    if (database != null) {
                        database.localEtapTerapaDao().deleteByIdTerapi(terapia.getId());
                        database.localTerapiaDao().delete(terapia);

                    }
                    finish();
                });

                builder.setNegativeButton("Nie", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}