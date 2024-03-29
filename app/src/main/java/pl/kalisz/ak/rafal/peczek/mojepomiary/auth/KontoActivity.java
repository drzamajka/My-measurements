package pl.kalisz.ak.rafal.peczek.mojepomiary.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zeoflow.password.strength.PasswordChecker;
import com.zeoflow.password.strength.enums.PasswordType;
import com.zeoflow.password.strength.resources.Configuration;
import com.zeoflow.password.strength.resources.ConfigurationBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;

public class KontoActivity extends AppCompatActivity {

    private TextInputLayout eMail, imie, nazwisko, dataUrodzenia;
    private CheckBox eMailCheckBox;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private Uzytkownik uzytkownik;
    private Boolean edytowane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konto);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        eMail = findViewById(R.id.eMailLayout);
        imie = findViewById(R.id.imieLayout);
        nazwisko = findViewById(R.id.nazwiskoLayout);
        dataUrodzenia = findViewById(R.id.dataUrodzeniaLayout);
        eMailCheckBox = findViewById(R.id.eMailCheckBox);

        edytowane = false;

        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_daty));
        Calendar c = Calendar.getInstance();
        dataUrodzenia.getEditText().setText(sdf.format(c.getTime()));
        dodajDatePicker(dataUrodzenia.getEditText());

        mDatabase.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    uzytkownik = task.getResult().toObject(Uzytkownik.class);
                    if(uzytkownik != null) {
                        if (!mAuth.getCurrentUser().getEmail().equals(uzytkownik.getEMail())) {
                            uzytkownik.setEMail(mAuth.getCurrentUser().getEmail());
                            mDatabase.collection("users").document(uzytkownik.getId()).set(uzytkownik);
                        }


                        eMail.getEditText().setText(mAuth.getCurrentUser().getEmail());
                        imie.getEditText().setText(uzytkownik.getImie());
                        nazwisko.getEditText().setText(uzytkownik.getNazwisko());
                        dataUrodzenia.getEditText().setText(sdf.format(uzytkownik.getDataUrodzenia()));
                    }
                }
            }
        });

        eMailCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    eMail.setEnabled(true);
                } else {
                    eMail.setEnabled(false);
                    if (uzytkownik != null) {
                        eMail.getEditText().setText(uzytkownik.getEMail());
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Button eMailButton = findViewById(R.id.eMailButton);
        TextView eMailTextView = findViewById(R.id.eMailTextView);
        if (mAuth.getCurrentUser().isEmailVerified()) {
            eMailButton.setVisibility(View.GONE);
            eMailTextView.setVisibility(View.VISIBLE);
        } else {
            eMailButton.setVisibility(View.VISIBLE);
            eMailTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object_change_and_delete_accaunt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.edit: {
                if (!edytowane) {
                    edytowane = true;
                    Button aktualizuj = findViewById(R.id.button_save_edit);
                    aktualizuj.setEnabled(true);
                    Button anuluj = findViewById(R.id.button_disable_edit);
                    anuluj.setEnabled(true);
                    switchEditing(imie, true);
                    switchEditing(nazwisko, true);
                    switchEditing(eMail, true);
                    eMail.setEnabled(false);
                    eMailCheckBox.setVisibility(View.VISIBLE);
                }
                return true;
            }
            case R.id.drop: {
                MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(KontoActivity.this);
                MaterialAlertDialogBuilder builder2 = new MaterialAlertDialogBuilder(KontoActivity.this);
                MaterialAlertDialogBuilder builder3 = new MaterialAlertDialogBuilder(KontoActivity.this);
                View passwodView = getLayoutInflater().inflate(R.layout.password_view, null, false);
                TextInputLayout staticHaslo = passwodView.findViewById(R.id.hasloLayout);

                builder1.setMessage(R.string.po_usunięciu_konta_wszytkie);
                builder1.setTitle(R.string.us_wanie_konta);
                builder1.setCancelable(false);
                builder1.setPositiveButton(R.string.usu, (dialog, which) -> {
                    dialog.cancel();
                    builder2.show();
                });

                builder1.setNegativeButton(R.string.anuluj, (dialog, which) -> {
                    dialog.cancel();
                });

                builder2.setMessage(R.string.To_b_czie_nieodwracalne);
                builder2.setTitle(R.string.czy_na_pewno_usun__);
                builder2.setCancelable(false);
                builder2.setPositiveButton(getString(R.string.tak), (dialog, which) -> {
                    dialog.cancel();
                    builder3.show();
                });

                builder2.setNegativeButton(getString(R.string.nie), (dialog, which) -> {
                    dialog.cancel();
                });


                builder3.setView(passwodView);
                builder3.setTitle(R.string.wpisz_haslo);
                builder3.setPositiveButton(getString(R.string.tak), (dialog, which) -> {
                    mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), staticHaslo.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.cancel();
                                deleteAccaunt();
                            }
                            else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                            .setTitle(R.string.aktualizacia)
                                            .setMessage(R.string.brak_dostępu_do_internetu)
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch (FirebaseAuthInvalidUserException | FirebaseAuthInvalidCredentialsException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                            .setTitle(R.string.aktualizacia)
                                            .setMessage(R.string.nieprawidłowe_dane_logowania)
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch (Exception e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                            .setTitle(R.string.aktualizacia)
                                            .setMessage(task.getException().getLocalizedMessage())
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                }
                            }
                        }
                    });

                });
                builder3.setNegativeButton(R.string.anuluj, (dialog, which) -> {
                    dialog.cancel();
                });
                builder3.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();
                    }
                });

                builder1.show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteAccaunt() {
        if (isNetworkAvailable()) {
            View elementView = getLayoutInflater().inflate(R.layout.progres_bar, null, false);
            MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                    .setCancelable(false)
                    .setTitle(R.string.usuwanie)
                    .setView(elementView);
            AlertDialog progers = progresbilder.show();
            Boolean[] status = new Boolean[4];

            String userUid = FirebaseAuth.getInstance().getUid();
            PomiarRepository pomiarRepository = new PomiarRepository(userUid);
            LekRepository lekRepository = new LekRepository(userUid);
            TerapiaRepository terapiaRepository = new TerapiaRepository(userUid);
            JednostkiRepository jednostkiRepository = new JednostkiRepository(userUid);

            terapiaRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<Terapia> list = task.getResult().toObjects(Terapia.class);
                        for (Terapia obiekt : list) {
                            terapiaRepository.delete(obiekt);
                        }
                        status[0] = true;
                    }
                }
            });
            pomiarRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<Pomiar> list = task.getResult().toObjects(Pomiar.class);
                        for (Pomiar obiekt : list) {
                            pomiarRepository.delete(obiekt);
                        }
                        status[1] = true;
                    }
                }
            });
            lekRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<Lek> list = task.getResult().toObjects(Lek.class);
                        for (Lek obiekt : list) {
                            lekRepository.delete(obiekt);
                        }
                        status[2] = true;
                    }
                }
            });
            jednostkiRepository.getQuery().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<Jednostka> list = task.getResult().toObjects(Jednostka.class);
                        for (Jednostka obiekt : list) {
                            jednostkiRepository.delete(obiekt);
                        }
                        status[3] = true;
                    }
                }
            });


            Observer<Boolean[]> observer = new Observer<Boolean[]>() {

                @Override
                public void onChanged(Boolean[] booleans) {
                    Boolean ukonczono = true;
                    for (Boolean tmp : booleans) {
                        if (tmp != null && !tmp) {
                            ukonczono = false;
                        }
                    }
                    if (ukonczono) {
                        mDatabase.collection("users").document(mAuth.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            finish();
                                                            Toast.makeText(getApplicationContext(), (R.string.dane_usuniete), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        finish();
                                        Toast.makeText(getApplicationContext(), (R.string.b__d_usuwania), Toast.LENGTH_LONG).show();
                                        progers.cancel();
                                    }
                                }
                            }
                        });
                    } else{
                        this.onChanged(status);
                    }
                }
            };
            observer.onChanged(status);

        } else {
            MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                    .setTitle(R.string.usuwanie)
                    .setMessage(R.string.brak_dostępu_do_internetu)
                    .setPositiveButton(R.string.submit, (dialog, which) -> {
                        dialog.cancel();
                    });
            progresbilder.show();
        }
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void switchEditing(TextInputLayout pole, Boolean stan) {

        pole.getEditText().setClickable(stan);
        pole.getEditText().setFocusable(stan);
        pole.getEditText().setFocusableInTouchMode(stan);
        pole.getEditText().setCursorVisible(stan);
    }

    public void upadte(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }

        View elementView = getLayoutInflater().inflate(R.layout.progres_bar, null, false);
        MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                .setCancelable(false)
                .setTitle(R.string.aktualizacia)
                .setView(elementView);
        AlertDialog progers = progresbilder.show();

        String eMail = this.eMail.getEditText().getText().toString().trim();
        String imie = this.imie.getEditText().getText().toString().trim();
        String nazwisko = this.nazwisko.getEditText().getText().toString().trim();
        //Data urodzenia
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_daty));
        try {
            calendar.setTime(sdf.parse(dataUrodzenia.getEditText().getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dataUrodzenia = calendar.getTime();

        if (validateData(eMail, imie, nazwisko, dataUrodzenia)) {
            uzytkownik.setImie(imie.substring(0, 1).toUpperCase() + imie.substring(1));
            uzytkownik.setNazwisko(nazwisko.substring(0, 1).toUpperCase() + nazwisko.substring(1));
            uzytkownik.setDataUrodzenia(dataUrodzenia);
            uzytkownik.setDataAktualizacji(new Date());

            if (eMailCheckBox.isChecked()) {
                View passwodView = getLayoutInflater().inflate(R.layout.password_view, null, false);
                TextInputLayout staticEMail = this.eMail;
                TextInputLayout staticHaslo = passwodView.findViewById(R.id.hasloLayout);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(KontoActivity.this);
                builder.setView(passwodView);
                builder.setTitle(R.string.wpisz_haslo);
                builder.setPositiveButton(getString(R.string.tak), (dialog, which) -> {
                    mAuth.signInWithEmailAndPassword(uzytkownik.getEMail(), staticHaslo.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updateEmail(eMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            staticEMail.setErrorEnabled(false);
                                            uzytkownik.setEMail(eMail);
                                            mDatabase.collection("users").document(uzytkownik.getId()).set(uzytkownik).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        stopEdit(null);

                                                    } else {
                                                        try {
                                                            throw task.getException();
                                                        } catch (Exception e) {
                                                            MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                                                    .setTitle(R.string.aktualizacia)
                                                                    .setMessage(e.getLocalizedMessage())
                                                                    .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                                        dialog.cancel();
                                                                    });
                                                            progresbilder.show();
                                                        }
                                                    }
                                                    progers.cancel();

                                                }
                                            });
                                        } else {
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthUserCollisionException e) {
                                                staticEMail.setError(getString(R.string.adres_email_juz_zajety));
                                            } catch (Exception e) {
                                                MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                                        .setTitle(R.string.aktualizacia)
                                                        .setMessage(e.getLocalizedMessage())
                                                        .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                            dialog.cancel();
                                                        });
                                                progresbilder.show();
                                            }
                                            progers.cancel();
                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                            .setTitle(R.string.aktualizacia)
                                            .setMessage(R.string.brak_dostępu_do_internetu)
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch (FirebaseAuthInvalidUserException | FirebaseAuthInvalidCredentialsException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                            .setTitle(R.string.aktualizacia)
                                            .setMessage(R.string.nieprawidłowe_dane_logowania)
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch (Exception e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                            .setTitle(R.string.aktualizacia)
                                            .setMessage(task.getException().getLocalizedMessage())
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                }
                                progers.cancel();
                            }
                        }
                    });

                });
                builder.setNegativeButton(R.string.anuluj, (dialog, which) -> {
                    progers.cancel();
                    dialog.cancel();
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();
                        progers.cancel();
                    }
                });
                builder.show();


            } else {
                mDatabase.collection("users").document(uzytkownik.getId()).set(uzytkownik).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            stopEdit(null);

                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                        .setTitle(R.string.aktualizacia)
                                        .setMessage(e.getLocalizedMessage())
                                        .setPositiveButton(R.string.submit, (dialog, which) -> {
                                            dialog.cancel();
                                        });
                                progresbilder.show();
                            }
                        }
                        progers.cancel();

                    }
                });
            }

        }
    }

    public void upadtePassword(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }

        View elementView = getLayoutInflater().inflate(R.layout.progres_bar, null, false);
        MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                .setCancelable(false)
                .setTitle(R.string.zmiana_has_a)
                .setView(elementView);
        AlertDialog progers = progresbilder.show();


        View passwodView = getLayoutInflater().inflate(R.layout.password_change_view, null, false);
        TextInputLayout staticStareHaslo = passwodView.findViewById(R.id.stareHasloLayout);
        TextInputLayout staticHaslo = passwodView.findViewById(R.id.hasloLayout);
        TextInputLayout staticPowtuzHaslo = passwodView.findViewById(R.id.hasloPowtuzLayout);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(KontoActivity.this);
        builder.setView(passwodView);
        builder.setTitle(R.string.nmien_has_o);
        builder.setPositiveButton(R.string.aktualizuj, null);
        builder.setNegativeButton(R.string.anuluj, (dialog, which) -> {
            progers.cancel();
            dialog.cancel();
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                progers.cancel();
            }
        });
        AlertDialog hasloDialog = builder.show();
        hasloDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasloDialog.hide();
                if (validateData(staticStareHaslo, staticHaslo, staticPowtuzHaslo)) {
                    mAuth.signInWithEmailAndPassword(uzytkownik.getEMail(), staticStareHaslo.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                hasloDialog.cancel();
                                mAuth.getCurrentUser().updatePassword(staticHaslo.getEditText().getText().toString());
                                progers.cancel();
                            } else {
                                hasloDialog.show();
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                            .setTitle(R.string.logowanie)
                                            .setMessage(R.string.brak_dostępu_do_internetu)
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    staticStareHaslo.setError(getString(R.string.nieprawid_owe_has_o));
                                } catch (Exception e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(KontoActivity.this)
                                            .setTitle(R.string.logowanie)
                                            .setMessage(task.getException().getLocalizedMessage())
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                }
                            }
                        }
                    });
                } else {
                    hasloDialog.show();
                }
            }
        });
    }

    public void stopEdit(View view) {
        edytowane = false;
        Button aktualizuj = findViewById(R.id.button_save_edit);
        aktualizuj.setEnabled(false);
        Button anuluj = findViewById(R.id.button_disable_edit);
        anuluj.setEnabled(false);

        anuluj.setEnabled(false);
        switchEditing(imie, false);
        switchEditing(nazwisko, false);
        switchEditing(eMail, false);
        eMailCheckBox.setChecked(false);
        eMail.setEnabled(true);
        eMailCheckBox.setVisibility(View.GONE);

        if (uzytkownik != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_daty));
            eMail.getEditText().setText(uzytkownik.getEMail());
            imie.getEditText().setText(uzytkownik.getImie());
            nazwisko.getEditText().setText(uzytkownik.getNazwisko());
            dataUrodzenia.getEditText().setText(sdf.format(uzytkownik.getDataUrodzenia()));
        }
    }

    public void sendVerfyEmail(View view) {
        Button eMailButton = findViewById(R.id.eMailButton);
        eMailButton.setEnabled(false);
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), (R.string.wys_ano_wiadomo___email), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseNetworkException e) {
                        Toast.makeText(getApplicationContext(), R.string.brak_dostępu_do_internetu, Toast.LENGTH_SHORT).show();
                    } catch (FirebaseTooManyRequestsException e) {
                        Toast.makeText(getApplicationContext(), (R.string.za_du_o_zapyta_), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                eMailButton.setEnabled(true);
            }
        });
    }

    boolean validateData(String eMail, String imie, String nazwisko, Date dataUrodzenia) {
        boolean status = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
            this.eMail.setError(getString(R.string.niepoprawny_e_mail));
            status = false;
        } else
            this.eMail.setErrorEnabled(false);

        if (imie.length() < 3) {
            this.imie.setError(getString(R.string.nieprawid_owe_imie));
            status = false;
        } else
            this.imie.setErrorEnabled(false);

        if (nazwisko.length() < 3) {
            this.nazwisko.setError(getString(R.string.nieprawid_owe_nazwisko));
            status = false;
        } else
            this.nazwisko.setErrorEnabled(false);

        return status;
    }

    boolean validateData(TextInputLayout stareHaslo, TextInputLayout haslo, TextInputLayout hasloPowtuz) {
        boolean status = true;


        if (stareHaslo.getEditText().getText().length() < 6) {
            stareHaslo.setError(getString(R.string.minimum_6_znak_w));
            status = false;
        } else
            stareHaslo.setErrorEnabled(false);


        // Create our configuration object and set our custom minimum
        // entropy, and custom dictionary list
        Configuration configuration = new ConfigurationBuilder()
                .setMinimumEntropy(35d)
                .createConfiguration(this);

        // Create our PasswordChecker object with the configuration we built
        PasswordChecker passwordChecker = new PasswordChecker(configuration);

        // passwordStrength is of PasswordType type
        // PasswordType can be (VERY_WEAK, WEAK, MEDIUM, STRONG, VERY_STRONG)
        PasswordType passwordStrength = passwordChecker.estimate(haslo.getEditText().getText().toString()).getStrength();

        if (passwordStrength.equals(PasswordType.VERY_WEAK) || haslo.getEditText().getText().length() < 6) {
            if (haslo.getEditText().getText().length() < 6) {
                haslo.setError(getString(R.string.minimum_6_znak_w));
            } else {
                haslo.setError(getString(R.string.s_abe_has_o));
            }
            status = false;
        } else
            haslo.setErrorEnabled(false);

        if (!haslo.getEditText().getText().toString().equals(hasloPowtuz.getEditText().getText().toString())) {
            hasloPowtuz.setError(getString(R.string.has_a_nie_s_));
            status = false;
        } else
            hasloPowtuz.setErrorEnabled(false);

        return status;
    }

    private void dodajDatePicker(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edytowane) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_daty));
                    Calendar ct = Calendar.getInstance();
                    try {
                        ct.setTime(simpleDateFormat.parse(textView.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    c.set(ct.get(Calendar.YEAR), ct.get(Calendar.MONTH), ct.get(Calendar.DAY_OF_MONTH), 0, 0);
                    Date dataWybrana = new Date(c.getTimeInMillis());

                    MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker()
                            .setSelection(dataWybrana.getTime())
                            .setCalendarConstraints(
                                    new CalendarConstraints.Builder()
                                            .setValidator(
                                                    new CalendarConstraints.DateValidator() {
                                                        @Override
                                                        public boolean isValid(long date) {
                                                            return MaterialDatePicker.todayInUtcMilliseconds() >= date;
                                                        }

                                                        @Override
                                                        public int describeContents() {
                                                            return 0;
                                                        }

                                                        @Override
                                                        public void writeToParcel(@NonNull Parcel dest, int flags) {

                                                        }
                                                    }
                                            )
                                            .build()
                            )
                            .build();

                    materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick(Object selection) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis((Long) selection);
                            textView.setText(calendar.get(Calendar.DAY_OF_MONTH) + getString(R.string.lacznik_daty) + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.lacznik_daty) + calendar.get(Calendar.YEAR));
                        }
                    });
                    materialDatePicker.show(getSupportFragmentManager(), "tag");
                }
            }
        });
    }
}