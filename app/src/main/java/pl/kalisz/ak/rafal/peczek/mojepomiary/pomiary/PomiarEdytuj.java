package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiary_edytuj);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

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
                            idWybranejJednostki = data.size()+1;
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
            default:
                return super.onOptionsItemSelected(item);
        }
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
