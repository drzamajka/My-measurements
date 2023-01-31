package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;

public class JednostkiDopisz extends AppCompatActivity {

    private TextInputLayout nazwa, wartosc;
    private JednostkiRepository jednostkiRepository;
    private int typZmiennejSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jednostki_dopisz);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nazwa = findViewById(R.id.editTextNazwaLayout);
        wartosc = findViewById(R.id.editTextJednostkaLayout);
        AutoCompleteTextView typZmiennej = findViewById(R.id.spinner);
        typZmiennejSelectedId = -1;

        typZmiennej.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                typZmiennejSelectedId = position;
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
        String wartosc = this.wartosc.getEditText().getText().toString().trim();
        if (validateData(nazwa, wartosc, typZmiennejSelectedId)) {

            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Jednostka jednostka = new Jednostka(nazwa, wartosc, typZmiennejSelectedId, false, userid, new Date(), new Date());

            jednostkiRepository.insert(jednostka);
            finish();
        }
    }

    boolean validateData(String nazwa, String wartosc, int typZmiennej) {
        boolean status = true;

        if (nazwa.length() < 3) {
            this.nazwa.setError(getString(R.string.minimum_3_znak_w));
            status = false;
        } else
            this.nazwa.setErrorEnabled(false);

        if (wartosc.length() < 1) {
            this.wartosc.setError(getString(R.string.Wprowad_warto__));
            status = false;
        } else
            this.wartosc.setErrorEnabled(false);

        TextInputLayout typZmiennejLayout = findViewById(R.id.spinnerLayout);

        if (typZmiennej == -1) {
            typZmiennejLayout.setError(getString(R.string.wybierz_typ));
            status = false;
        } else
            typZmiennejLayout.setErrorEnabled(false);

        return status;
    }
}