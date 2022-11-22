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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

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

    private TextInputLayout elementyTerapi;
    private TextInputLayout dataRozpoczecia, dataZakonczenia, notatka;
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

        elementyTerapi = (TextInputLayout) findViewById(R.id.elementyLayout);
        dataRozpoczecia = (TextInputLayout) findViewById(R.id.dataRozpoczeciaLayout);
        dataZakonczenia = (TextInputLayout) findViewById(R.id.dataZakonczeniaLayout);
        notatka = (TextInputLayout) findViewById(R.id.NotatkaLayout);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        TerapiaPosiadEtay terapiaPosiadEtay = (TerapiaPosiadEtay) database.localTerapiaDao().findTerapieAndWpisyById(terapiaId);
        terapia = terapiaPosiadEtay.terapia;
        etapyTerapi = terapiaPosiadEtay.etapy;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        ArrayList<Integer> listaElementow = terapia.getIdsCzynnosci();
        for (int id: listaElementow) {
            Pomiar pomiar = database.localPomiarDao().findById(id);
            if(id != listaElementow.get(0))
                elementyTerapi.getEditText().setText(elementyTerapi.getEditText().getText()+"\n"+pomiar.getNazwa());
            else
                elementyTerapi.getEditText().setText(pomiar.getNazwa());
        }

        dataRozpoczecia.getEditText().setText(sdf.format(terapia.getDataRozpoczecia()));
        dataZakonczenia.getEditText().setText(sdf.format(terapia.getDataZakonczenia()));
        notatka.getEditText().setText(terapia.getNotatka());

        rvPomiary = (RecyclerView) findViewById(R.id.recycleView);

        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
        } else
            layoutManager = new LinearLayoutManager(this);

        rvPomiary.setLayoutManager(layoutManager);
        //rvPomiary.setItemAnimator(new DefaultItemAnimator());
        rvPomiary = (RecyclerView) findViewById(R.id.recycleView);
        rvPomiary.setHasFixedSize(true);
        rvPomiary.setNestedScrollingEnabled(false);
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
                builder.setTitle("Alert !");
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