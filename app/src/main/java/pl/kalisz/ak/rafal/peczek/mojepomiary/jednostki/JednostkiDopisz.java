package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;

public class JednostkiDopisz extends AppCompatActivity {

    private EditText nazwa, wartosc;
    private AutoCompleteTextView typZmiennej;
    private JednostkiRepository jednostkiRepository;
    private int typZmiennejSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jednostki_dopisz);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nazwa = (EditText) findViewById(R.id.editTextNazwa);
        wartosc = (EditText) findViewById(R.id.editTextJednostka);
        typZmiennej = (AutoCompleteTextView) findViewById(R.id.spinner);
        typZmiennejSelectedId = 0;

        typZmiennej.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                typZmiennejSelectedId = position;
            }
        });

    }

    public void zapiszNowaPozycia(View view){
        String nazwa = this.nazwa.getText().toString();
        String wartosc = this.wartosc.getText().toString();

        if(nazwa.length() >= 2 && wartosc.length() >= 1)
        {
            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Jednostka jednostka = new Jednostka(nazwa, wartosc, typZmiennejSelectedId, false, userid, new Date(), new Date() );

            jednostkiRepository.insert(jednostka);
            finish();
        }else
                Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}