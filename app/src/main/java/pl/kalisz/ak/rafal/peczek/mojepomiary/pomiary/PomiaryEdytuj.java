package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki.JednostkiEdytuj;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class PomiaryEdytuj extends AppCompatActivity {

    public static final String EXTRA_Pomiar_ID = "pomiarId";
    private int pomiarId;

    Pomiar pomiar;
    private TextInputLayout nazwa, notatka;
    private AutoCompleteTextView jednostki;
    private TextInputLayout jednostkiL;
    private UsersRoomDatabase database;
    private List<Jednostka> listaJednostek;
    private int idWybranejJednostki;
    private String textWybranejJednostki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiary_edytuj);

        idWybranejJednostki = 0;
        textWybranejJednostki = "";
        pomiarId = (Integer) getIntent().getExtras().get(EXTRA_Pomiar_ID);

        nazwa = (TextInputLayout) findViewById(R.id.editTextNazwaLayout);
        notatka = (TextInputLayout) findViewById(R.id.editTextJednostkaLayout);
        jednostki = (AutoCompleteTextView) findViewById(R.id.spinner);
        jednostkiL = (TextInputLayout) findViewById(R.id.spinnerLayout);

        database = UsersRoomDatabase.getInstance(getApplicationContext());
        listaJednostek = database.localJednostkaDao().getAll();
        pomiar = database.localPomiarDao().findById(pomiarId);
        nazwa.getEditText().setText(pomiar.getNazwa());
        notatka.getEditText().setText(pomiar.getNotatka());

        //jednostka.
        ArrayList<String> data = new ArrayList<>();
        for (Jednostka jednostka: listaJednostek){
            String element = jednostka.getNazwa()+" "+jednostka.getWartosc();
            if(pomiar.getIdJednostki() == jednostka.getId()) {
                idWybranejJednostki = data.size();
                textWybranejJednostki = element;
            }
            data.add(element);
        }

        ArrayAdapter adapter = new ArrayAdapter ( this, android.R.layout.simple_spinner_dropdown_item, data);
        jednostki.setAdapter(adapter);

        jednostki.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idWybranejJednostki = position;
            }
        });

        jednostki.setText(textWybranejJednostki, false);
        jednostkiL.setEnabled(false);
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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PomiaryEdytuj.this);
                builder.setMessage("Czy na pewno usunąć");
                builder.setCancelable(false);
                builder.setPositiveButton("Tak", (DialogInterface.OnClickListener) (dialog, which) -> {
                    if (database != null) {
                        Pomiar pomiar = database.localPomiarDao().findById(pomiarId);
                        database.localPomiarDao().delete(pomiar);
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
            int jednostkaId = listaJednostek.get(idWybranejJednostki).getId();

            Pomiar pomiar = database.localPomiarDao().findById(pomiarId);
            pomiar.setNazwa(nazwa);
            pomiar.setNotatka(notatka);
            pomiar.setIdJednostki(jednostkaId);
            pomiar.setDataAktualizacji(new Date());

            database.localPomiarDao().insert(pomiar);
            finish();
        }else
            Toast.makeText(this, "Wprowadż poprawne dane", Toast.LENGTH_SHORT).show();
    }
}
