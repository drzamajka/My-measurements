package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;

public class PomiaryDopisz extends AppCompatActivity {

    private EditText nazwa, notatka;
    private AutoCompleteTextView jednostki;
    private List<Jednostka> listaJednostek;
    private int idWybranejJednostki;

    private PomiarRepository pomiarRepository;
    private JednostkiRepository jednostkiRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiary_dopisz);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

        idWybranejJednostki = 0;
        nazwa = (EditText) findViewById(R.id.editTextNazwa);
        notatka = (EditText) findViewById(R.id.editTextJednostka);
        jednostki = (AutoCompleteTextView) findViewById(R.id.spinner);


        listaJednostek = new ArrayList<>();
        jednostkiRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot postSnapshot: task.getResult().getChildren()) {
                        Jednostka jednostka = postSnapshot.getValue(Jednostka.class);
                        listaJednostek.add(jednostka);
                    }
                    ArrayList<String> data = new ArrayList<>();
                    for (Jednostka jednostka: listaJednostek){
                        data.add(jednostka.getNazwa()+" "+jednostka.getWartosc());
                    }

                    ArrayAdapter adapter = new ArrayAdapter ( PomiaryDopisz.this, android.R.layout.simple_spinner_dropdown_item, data);
                    jednostki.setAdapter(adapter);
                }
                else {
                    Log.i("Tag", "błąd odczytu jednostek" );
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

    public void zapiszNowaPozycia(View view){
        String nazwa = this.nazwa.getText().toString();
        String notatka = this.notatka.getText().toString();

        if( jednostki.getText().length()>0 && nazwa.length()>=2 && notatka.length()>=2) {

            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            notatka = notatka.substring(0, 1).toUpperCase() + notatka.substring(1);
            if (notatka.charAt(notatka.length() - 1) != '.')
                notatka += '.';
            String jednostkaId = listaJednostek.get(idWybranejJednostki).getId();


            pomiarRepository.insert(new Pomiar( nazwa, notatka, FirebaseAuth.getInstance().getCurrentUser().getUid(), jednostkaId, new Date(), new Date()));
            finish();
        }else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }

}
