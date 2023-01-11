package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

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

        nazwa = (TextInputLayout) findViewById(R.id.editTextNazwaLayout);
        wartosc = (TextInputLayout) findViewById(R.id.editTextJednostkaLayout);
        aktualizuj = (Button) findViewById(R.id.button_save_edit);
        anuluj = (Button) findViewById(R.id.button_disable_edit);
        typZmiennej = (AutoCompleteTextView) findViewById(R.id.spinner);
        typZmiennejL = (TextInputLayout) findViewById(R.id.spinnerLayout);


        jednostka = jednostkiRepository.findById(jednostkaId);
        nazwa.getEditText().setText(jednostka.getNazwa());
        wartosc.getEditText().setText(jednostka.getWartosc());
        typZmiennejSelectedId = jednostka.getTypZmiennej();
        String[] listaDokladnosci = getResources().getStringArray(R.array.typZmiennej);
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
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId() ) {

            case R.id.edit:
                if(!jednostka.getCzyDomyslna()) {
                    Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
                    Button anuluj = (Button) findViewById(R.id.button_disable_edit);
                    aktualizuj.setEnabled(true);
                    anuluj.setEnabled(true);
                    nazwa.setEnabled(true);
                    wartosc.setEnabled(true);
                    typZmiennejL.setEnabled(true);
                }
                else {
                    Toast.makeText(this, "Nie mozna edytować domyślnej jednostki", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.drop: {

                if (!jednostka.getCzyDomyslna()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(JednostkiEdytuj.this);
                    builder.setMessage("Czy na pewno usunąć");
//                builder.setTitle("Alert !");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Tak", (DialogInterface.OnClickListener) (dialog, which) -> {
                        if (jednostka != null) {
                            jednostkiRepository.delete(jednostka);
                        }
                        finish();
                    });

                    builder.setNegativeButton("Nie", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                } else {
                    Toast.makeText(this, "Nie mozna usunąć domyślnej jednostki", Toast.LENGTH_LONG).show();
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
        Button aktualizuj = (Button) findViewById(R.id.button_save_edit);
        Button anuluj = (Button) findViewById(R.id.button_disable_edit);
        aktualizuj.setEnabled(false);
        anuluj.setEnabled(false);
        nazwa.setEnabled(false);
        wartosc.setEnabled(false);
        typZmiennejL.setEnabled(false);
    }

    public void aktualizujJednostke(View view) {
        String nazwa = this.nazwa.getEditText().getText().toString();
        String wartosc = this.wartosc.getEditText().getText().toString();

        if(nazwa.length() >= 2 && wartosc.length() >= 1) {
            nazwa = nazwa.substring(0, 1).toUpperCase() + nazwa.substring(1).toLowerCase();


            jednostka.setNazwa(nazwa);
            jednostka.setWartosc(wartosc);
            jednostka.setTypZmiennej(typZmiennejSelectedId);
            jednostka.setDataAktualizacji(new Date());
            jednostkiRepository.update(jednostka);
            finish();
        }else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}