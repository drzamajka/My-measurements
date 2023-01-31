package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;

public class JednostkiEdytuj extends AppCompatActivity {

    public static final String EXTRA_JEDNOSTKA_ID = "jednostkaId";
    private String jednostkaId;

    private TextInputLayout nazwa, wartosc;
    private AutoCompleteTextView typZmiennej;
    private TextInputLayout typZmiennejL;
    private Button aktualizuj, anuluj;
    private JednostkiRepository jednostkiRepository;
    private Jednostka jednostka;
    private int typZmiennejSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jednostki_edytuj);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        jednostkaId = (String) getIntent().getExtras().get(EXTRA_JEDNOSTKA_ID);

        nazwa = findViewById(R.id.editTextNazwaLayout);
        wartosc = findViewById(R.id.editTextJednostkaLayout);
        aktualizuj = findViewById(R.id.button_save_edit);
        anuluj = findViewById(R.id.button_disable_edit);
        typZmiennej = findViewById(R.id.spinner);
        typZmiennejL = findViewById(R.id.spinnerLayout);

        jednostka = jednostkiRepository.findById(jednostkaId);
        nazwa.getEditText().setText(jednostka.getNazwa());
        wartosc.getEditText().setText(jednostka.getWartosc());
        typZmiennejSelectedId = jednostka.getTypZmiennej();
        typZmiennej.setText(typZmiennej.getAdapter().getItem(typZmiennejSelectedId).toString(), false);

        typZmiennej.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                typZmiennejSelectedId = position;
            }
        });

        aktualizuj.setEnabled(false);
        anuluj.setEnabled(false);
        nazwa.setEnabled(false);
        wartosc.setEnabled(false);
        typZmiennejL.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object_change_and_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.edit:
                if (!jednostka.getCzyDomyslna()) {
                    Button aktualizuj = findViewById(R.id.button_save_edit);
                    Button anuluj = findViewById(R.id.button_disable_edit);
                    aktualizuj.setEnabled(true);
                    anuluj.setEnabled(true);
                    nazwa.setEnabled(true);
                    wartosc.setEnabled(true);
                    typZmiennejL.setEnabled(true);
                } else {
                    Toast.makeText(this, R.string.nie_mozna_edytowa_, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.drop: {

                if (!jednostka.getCzyDomyslna()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(JednostkiEdytuj.this);
                    builder.setMessage(R.string.czy_na_pewno_usun__);
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.tak), (dialog, which) -> {
                        if (jednostka != null) {
                            jednostkiRepository.delete(jednostka);
                        }
                        finish();
                    });

                    builder.setNegativeButton(getString(R.string.nie), (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else {
                    Toast.makeText(this, R.string.nie_mozna_usun__, Toast.LENGTH_LONG).show();
                }
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


    public void stopEdit(View view) {
        Button aktualizuj = findViewById(R.id.button_save_edit);
        Button anuluj = findViewById(R.id.button_disable_edit);
        aktualizuj.setEnabled(false);
        anuluj.setEnabled(false);
        nazwa.setEnabled(false);
        wartosc.setEnabled(false);
        typZmiennejL.setEnabled(false);
    }

    public void aktualizujJednostke(View view) {
        String nazwa = this.nazwa.getEditText().getText().toString().trim();
        String wartosc = this.wartosc.getEditText().getText().toString().trim();

        if (validateData(nazwa, wartosc, typZmiennejSelectedId)) {
            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();

            jednostka.setNazwa(nazwa);
            jednostka.setWartosc(wartosc);
            jednostka.setTypZmiennej(typZmiennejSelectedId);
            jednostka.setDataAktualizacji(new Date());
            jednostkiRepository.update(jednostka);
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