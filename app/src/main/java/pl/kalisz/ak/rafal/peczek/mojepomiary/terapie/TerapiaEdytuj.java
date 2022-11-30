package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;

public class TerapiaEdytuj extends AppCompatActivity {

    public static final String EXTRA_Terapia_ID = "terapiaId";
    private String terapiaId;

    private TextInputLayout elementyTerapi;
    private TextInputLayout dataRozpoczecia, dataZakonczenia, notatka;
    private Terapia terapia;
    private List<EtapTerapa> etapyTerapi;

    private TerapiaRepository terapiaRepository;
    private PomiarRepository pomiarRepository;
    private String userUid;

    private RecyclerView rvEtapy;
    private EtapAdapter etapAdapter;
    private static EtapTerapiaRepository etapTerapiaRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terapia_edytuj);
        terapiaId = (String) getIntent().getExtras().get(EXTRA_Terapia_ID);

        elementyTerapi = (TextInputLayout) findViewById(R.id.elementyLayout);
        dataRozpoczecia = (TextInputLayout) findViewById(R.id.dataRozpoczeciaLayout);
        dataZakonczenia = (TextInputLayout) findViewById(R.id.dataZakonczeniaLayout);
        notatka = (TextInputLayout) findViewById(R.id.NotatkaLayout);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        terapiaRepository = new TerapiaRepository(userUid);
        pomiarRepository = new PomiarRepository(userUid);
        etapTerapiaRepository = new EtapTerapiaRepository(userUid);


        terapia = terapiaRepository.findById(terapiaId);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        ArrayList<String> listaElementow = terapia.getIdsCzynnosci();
        for (String id: listaElementow) {
            Pomiar pomiar = pomiarRepository.findById(id);
            if(id != listaElementow.get(0))
                elementyTerapi.getEditText().setText(elementyTerapi.getEditText().getText()+"\n"+pomiar.getNazwa());
            else
                elementyTerapi.getEditText().setText(pomiar.getNazwa());
        }

        dataRozpoczecia.getEditText().setText(sdf.format(terapia.getDataRozpoczecia()));
        dataZakonczenia.getEditText().setText(sdf.format(terapia.getDataZakonczenia()));
        notatka.getEditText().setText(terapia.getNotatka());


        rvEtapy = (RecyclerView) findViewById(R.id.recycleView);
        rvEtapy.setLayoutManager(
                new LinearLayoutManager(getApplicationContext()));

        FirestoreRecyclerOptions<EtapTerapa> options
                = new FirestoreRecyclerOptions.Builder<EtapTerapa>()
                .setQuery(etapTerapiaRepository.getQuery().whereEqualTo("idTerapi", terapia.getId()).orderBy("dataZaplanowania", Query.Direction.ASCENDING), EtapTerapa.class)
                .build();

        etapAdapter = new EtapAdapter(options);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        etapAdapter.startListening();
        rvEtapy.setAdapter(etapAdapter);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        etapAdapter.stopListening();
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
                    if (terapia != null) {
                        terapiaRepository.delete(terapia);
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