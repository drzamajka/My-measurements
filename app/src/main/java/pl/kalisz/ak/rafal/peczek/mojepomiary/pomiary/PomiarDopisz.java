package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;

public class PomiarDopisz extends AppCompatActivity {

    private TextInputLayout nazwa, notatka;
    private AutoCompleteTextView jednostki;
    private List<Jednostka> listaJednostek;
    private int idWybranejJednostki;

    private PomiarRepository pomiarRepository;
    private JednostkiRepository jednostkiRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiar_dopisz);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

        idWybranejJednostki = -1;
        nazwa = findViewById(R.id.editTextNazwaLayout);
        notatka = findViewById(R.id.editTextJednostkaLayout);
        jednostki = findViewById(R.id.spinner);


        listaJednostek = new ArrayList<>();
        jednostkiRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> data = new ArrayList<>();
                    data.add(getString(R.string.opis_tekstowy));
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Jednostka jednostka = queryDocumentSnapshot.toObject(Jednostka.class);
                        jednostka.setId(queryDocumentSnapshot.getId());
                        listaJednostek.add(jednostka);
                        data.add(jednostka.getNazwa() + getString(R.string.spacia) + jednostka.getWartosc());
                    }
                    ArrayAdapter adapter = new ArrayAdapter(PomiarDopisz.this, android.R.layout.simple_spinner_dropdown_item, data);
                    jednostki.setAdapter(adapter);
                } else {
                    finish();
                }
            }
        });


        jednostki.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idWybranejJednostki = position;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void zapiszNowaPozycia(View view) {
        String nazwa = this.nazwa.getEditText().getText().toString().trim();
        String notatka = this.notatka.getEditText().getText().toString().trim();

        if (validateData(nazwa, idWybranejJednostki)) {

            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            if(notatka.length() == 0){
                notatka = getString(R.string.brak_notatki);
            }
            notatka = notatka.substring(0, 1).toUpperCase() + notatka.substring(1);
            if (notatka.charAt(notatka.length() - 1) != '.')
                notatka += '.';
            String jednostkaId = null;
            if (idWybranejJednostki != 0)
                jednostkaId = listaJednostek.get(idWybranejJednostki - 1).getId();

            pomiarRepository.insert(new Pomiar(nazwa, notatka, FirebaseAuth.getInstance().getCurrentUser().getUid(), jednostkaId, new Date(), new Date()));
            finish();
        }
    }

    boolean validateData(String nazwa, int idWybranejJednostki) {
        boolean status = true;

        if (nazwa.length() < 3) {
            this.nazwa.setError(getString(R.string.minimum_3_znak_w));
            status = false;
        } else
            this.nazwa.setErrorEnabled(false);

        TextInputLayout JednostkaLayout = findViewById(R.id.spinnerLayout);

        if (idWybranejJednostki == -1) {
            JednostkaLayout.setError(getString(R.string.wybie__jednostke));
            status = false;
        } else
            JednostkaLayout.setErrorEnabled(false);

        return status;
    }

}
