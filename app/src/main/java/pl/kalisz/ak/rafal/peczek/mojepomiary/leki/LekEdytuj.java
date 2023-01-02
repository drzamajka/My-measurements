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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyLeki.WpisLekAdapter;

public class LekEdytuj extends AppCompatActivity {

    public static final String EXTRA_Lek_ID = "lekId";
    private String lekId;

    Lek lek;
    private TextInputLayout nazwa, notatka;
    private AutoCompleteTextView jednostki;
    private TextInputLayout jednostkiL;
    private RecyclerView recyclerView;
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
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);

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
                        if(lek.getIdJednostki() == jednostka.getId()) {
                            idWybranejJednostki = data.size();
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


        recyclerView.setLayoutManager(
                new LinearLayoutManager(getApplicationContext()));

        FirestoreRecyclerOptions<WpisLek> options
                = new FirestoreRecyclerOptions.Builder<WpisLek>()
                .setQuery(wpisLekRepository.getQuery().whereEqualTo("idLeku", lek.getId()).orderBy("dataWykonania", Query.Direction.DESCENDING), WpisLek.class)
                .build();

        wpisLekAdapter = new WpisLekAdapter(options);

        wpisLekAdapter.startListening();
        recyclerView.setAdapter(wpisLekAdapter);


        jednostki.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idWybranejJednostki = position;
            }
        });
    }


    @Override
    public void onStart()
    {
        super.onStart();
        wpisLekAdapter.startListening();
        recyclerView.setAdapter(wpisLekAdapter);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        wpisLekAdapter.stopListening();
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
