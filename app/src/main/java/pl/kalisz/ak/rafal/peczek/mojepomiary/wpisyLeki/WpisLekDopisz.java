package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyLeki;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.auth.LoginActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;

public class WpisLekDopisz extends AppCompatActivity {


    private TextInputLayout wartoscOperacji, godzinaWykonania;
    private AutoCompleteTextView lek;
    private ChipGroup chipGroup;
    private Chip chipAdd, chipRemove;
    private TextInputLayout lekL;
    private List<Lek> listaLekow;
    private int idWybranegoLeku;

    private LekRepository lekRepository;
    private JednostkiRepository jednostkiRepository;
    private WpisLekRepository wpisLekRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wpis_lek_dopisz);

        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        lekRepository = new LekRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        wpisLekRepository = new WpisLekRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        idWybranegoLeku = -1;

        wartoscOperacji = findViewById(R.id.editTextZasobLayout);
        godzinaWykonania = findViewById(R.id.godzinaWykonaniaLayout);
        lek = findViewById(R.id.spinnerLeki);
        lekL = findViewById(R.id.spinnerLekiLayout);
        chipAdd = findViewById(R.id.chip);
        chipRemove = findViewById(R.id.chip2);
        chipGroup = findViewById(R.id.chipGroup);



        listaLekow = new ArrayList<>();
        lekRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> data = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Lek lek = queryDocumentSnapshot.toObject(Lek.class);
                        lek.setId(queryDocumentSnapshot.getId());
                        listaLekow.add(lek);
                        data.add(lek.getNazwa());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(WpisLekDopisz.this, android.R.layout.simple_spinner_dropdown_item, data);
                    lek.setAdapter(adapter);
                } else {
                    finish();
                }
            }
        });


        TextView textView5 = findViewById(R.id.jednostka);
        lek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lekL.setErrorEnabled(false);
                idWybranegoLeku = position;
                Lek lek = listaLekow.get(position);
                Jednostka jednostka = jednostkiRepository.findById(lek.getIdJednostki());
                textView5.setText(jednostka.getWartosc());
                wartoscOperacji.getEditText().setText("");
                wartoscOperacji.setEnabled(true);
                if (jednostka.getTypZmiennej() == 0) {
                    wartoscOperacji.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    wartoscOperacji.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }

                LinearLayout extendContentLayout = findViewById(R.id.cardViewLayout);
                View extendelementView = getLayoutInflater().inflate(R.layout.lek_cardview, null, false);
                TextView notatka = extendelementView.findViewById(R.id.notatka);
                notatka.setText(getString(R.string.Notatka)  + lek.getNotatka());
                TextView zapas = extendelementView.findViewById(R.id.zapas);
                wpisLekRepository.getQueryByLekId(lek.getId(), 1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<WpisLek> lista = queryDocumentSnapshots.toObjects(WpisLek.class);
                        if (!lista.isEmpty()) {
                            zapas.setText(getString(R.string.w_sk_adzie_pozosta_o) + lista.get(0).getPozostalyZapas()+ getString(R.string.spacia) + jednostka.getWartosc());
                        }
                        else {
                            zapas.setText(R.string.lek_jescze_nie_posiada_zapasu);
                        }
                    }
                });

                extendContentLayout.removeAllViews();
                extendContentLayout.addView(extendelementView);
            }
        });

        chipAdd.setChecked(true);
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if(checkedIds.get(0).equals(chipAdd.getId()))
                    wartoscOperacji.setHint(getText(R.string.dodawany_zas_b));
                if(checkedIds.get(0).equals(chipRemove.getId()))
                    wartoscOperacji.setHint(R.string.pobierany_zas_b);
            }
        });

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_czasu));
        godzinaWykonania.getEditText().setText(simpleDateFormat.format(date));
        dodajTimePicker(godzinaWykonania.getEditText());
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

    public void zapiszNowaPozycia(View view) throws ParseException {
        String wynik = wartoscOperacji.getEditText().getText().toString();
        if (validateData(wynik, idWybranegoLeku)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_czasu));
            Calendar c = Calendar.getInstance();
            c.setTime(simpleDateFormat.parse(godzinaWykonania.getEditText().getText().toString()));
            Calendar cData = Calendar.getInstance();
            cData.set(Calendar.HOUR, c.get(Calendar.HOUR));
            cData.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
            cData.set(Calendar.SECOND, c.get(Calendar.SECOND));

            String lekId = listaLekow.get(idWybranegoLeku).getId();
            wpisLekRepository.getQueryByLekId(lekId, 1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Double doubleWynik = Double.parseDouble(wynik);
                        if(chipGroup.getCheckedChipIds().size() > 0 && chipGroup.getCheckedChipIds().get(0).equals(chipRemove.getId())){
                            doubleWynik = doubleWynik * -1;
                        }

                        List<WpisLek> lista = task.getResult().toObjects(WpisLek.class);
                        Double zapasLeku = doubleWynik;
                        if (!lista.isEmpty()) {
                            WpisLek wpisLek = lista.get(0);
                            zapasLeku = Double.parseDouble(wpisLek.getPozostalyZapas()) + doubleWynik;
                        }
                        wpisLekRepository.insert(new WpisLek(doubleWynik.toString(), zapasLeku.toString(), lekId, FirebaseAuth.getInstance().getCurrentUser().getUid(), cData.getTime(), new Date(), new Date()));
                    } else {
                        MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(WpisLekDopisz.this)
                                .setTitle(R.string.b__d_zapisu)
                                .setMessage(task.getException().getLocalizedMessage())
                                .setPositiveButton(R.string.submit, (dialog, which) -> dialog.cancel());
                        progresbilder.show();
                    }
                }
            });
            finish();
        }
    }

    boolean validateData(String wynik, int idWybranegoLeku) {
        boolean status = true;

        if (wynik.length() == 0 || Double.parseDouble(wynik)<=0 ) {
            wartoscOperacji.setError(getString(R.string.wprowad__warto___obrotu));
            status = false;
        } else
            wartoscOperacji.setErrorEnabled(false);


        if (idWybranegoLeku == -1) {
            lekL.setError(getString(R.string.wybiez_lek));
            status = false;
        } else
            lekL.setErrorEnabled(false);

        return status;
    }

    private void dodajTimePicker(EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MINUTE, 0);
                if (editText.getText().length() > 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_czasu));
                    try {
                        Date data = sdf.parse(editText.getText().toString());
                        c.setTime(data);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(hour)
                        .setTitleText(R.string.okresl_godzine_wykonania)
                        .setMinute(minute)
                        .build();

                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timePicker.getMinute() > 9)
                            editText.setText(timePicker.getHour() + ":" + timePicker.getMinute());
                        else
                            editText.setText(timePicker.getHour() + ":0" + timePicker.getMinute());
                    }
                });

                timePicker.show(getSupportFragmentManager(), "fragment_tag");
            }
        });
    }
}