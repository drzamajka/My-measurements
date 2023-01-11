package pl.kalisz.ak.rafal.peczek.mojepomiary.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zeoflow.password.strength.PasswordChecker;
import com.zeoflow.password.strength.enums.PasswordType;
import com.zeoflow.password.strength.resources.Configuration;
import com.zeoflow.password.strength.resources.ConfigurationBuilder;
import com.zeoflow.password.strength.resources.Dictionary;
import com.zeoflow.password.strength.resources.DictionaryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


import pl.kalisz.ak.rafal.peczek.mojepomiary.MainActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout eMail, imie, nazwisko, haslo, hasloPowtuz, dataUrodzenia;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        eMail = findViewById(R.id.eMailLayout);
        imie = findViewById(R.id.imieLayout);
        nazwisko = findViewById(R.id.nazwiskoLayout);
        haslo = findViewById(R.id.hasloLayout);
        hasloPowtuz = findViewById(R.id.hasloPowtuzLayout);
        dataUrodzenia = findViewById(R.id.dataUrodzeniaLayout);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        dataUrodzenia.getEditText().setText(sdf.format(c.getTime()));
        dodajDatePicker(dataUrodzenia.getEditText());

    }


    public void register(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }

        View elementView = getLayoutInflater().inflate(R.layout.progres_bar, null, false);
        MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(RegisterActivity.this)
                .setCancelable(false)
                .setTitle("Rejsreacia")
                .setView(elementView);
        AlertDialog progers = progresbilder.show();

        String eMail = this.eMail.getEditText().getText().toString().trim();
        String imie = this.imie.getEditText().getText().toString().trim();
        String nazwisko = this.nazwisko.getEditText().getText().toString().trim();
        String haslo = this.haslo.getEditText().getText().toString().trim();
        String hasloPowtuz = this.hasloPowtuz.getEditText().getText().toString().trim();
        //Data urodzenia
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            calendar.setTime(sdf.parse(dataUrodzenia.getEditText().getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dataUrodzenia = calendar.getTime();





        if( validateData(eMail, imie, nazwisko, haslo, hasloPowtuz, dataUrodzenia) ){
            Uzytkownik uzytkownik = new Uzytkownik();
            uzytkownik.setImie(imie.substring(0, 1).toUpperCase() + imie.substring(1));
            uzytkownik.setNazwisko(nazwisko.substring(0, 1).toUpperCase() + nazwisko.substring(1));
            uzytkownik.setDataUrodzenia(dataUrodzenia);
            uzytkownik.setEMail(eMail);
            uzytkownik.setDataUtwozenia(new Date());
            uzytkownik.setDataAktualizacji(new Date());

            TextInputLayout eMailStatic = this.eMail;
            TextInputLayout hasloStatic = this.haslo;
            mAuth.createUserWithEmailAndPassword(eMail, haslo)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                mDatabase.collection("users").document(user.getUid()).set(uzytkownik);
                                mAuth.getCurrentUser().sendEmailVerification();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                try {
                                    throw task.getException();
                                } catch(FirebaseNetworkException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(RegisterActivity.this)
                                            .setTitle("Rejsreacia")
                                            .setMessage("Brak dostępu do internetu")
                                            .setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    hasloStatic.setError("Za słabe hasło!");
                                } catch(FirebaseAuthUserCollisionException e) {
                                    eMailStatic.setError("Adres Email juz zajęty!");
                                } catch(Exception e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(RegisterActivity.this)
                                            .setTitle("Rejsreacia")
                                            .setMessage(e.getLocalizedMessage())
                                            .setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                }

                                progers.cancel();
                            }
                        }
                    });
        }else{
            progers.cancel();
        }




    }

    boolean validateData(String eMail, String imie, String nazwisko, String haslo, String hasloPowtuz, Date dataUrodzenia){
        boolean status = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()){
            this.eMail.setError("Niepoprawny Email!");
            status = false;
        }else
            this.eMail.setErrorEnabled(false);

        if(imie.length()<3){
            this.imie.setError("Nieprawidłowe imie!");
            status = false;
        }else
            this.imie.setErrorEnabled(false);

        if(nazwisko.length()<3){
            this.nazwisko.setError("Nieprawidłowe nazwisko!");
            status = false;
        }else
            this.nazwisko.setErrorEnabled(false);



        // Create our configuration object and set our custom minimum
        // entropy, and custom dictionary list
        Configuration configuration = new ConfigurationBuilder()
                .setMinimumEntropy(35d)
                .createConfiguration(this);

        // Create our PasswordChecker object with the configuration we built
        PasswordChecker passwordChecker = new PasswordChecker(configuration);

        // passwordStrength is of PasswordType type
        // PasswordType can be (VERY_WEAK, WEAK, MEDIUM, STRONG, VERY_STRONG)
        PasswordType passwordStrength = passwordChecker.estimate(haslo).getStrength();

        Toast.makeText(this, "złozoność: "+passwordStrength.ordinal(), Toast.LENGTH_SHORT).show();

        if( passwordStrength.equals(PasswordType.VERY_WEAK) || haslo.length()<6){
            if(haslo.length()<6){
                this.haslo.setError("Minimum 6 znaków!");
            }
            else {
                this.haslo.setError("słabe hasło!");
            }
            status = false;
        }else
            this.haslo.setErrorEnabled(false);

        if(!haslo.equals(hasloPowtuz)){
            this.hasloPowtuz.setError("Hasła nie są zgodne!");
            status = false;
        }else
            this.hasloPowtuz.setErrorEnabled(false);

        return status;
    }

    private void dodajDatePicker(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Calendar ct = Calendar.getInstance();
                try {
                    ct.setTime(simpleDateFormat.parse(textView.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                c.set(ct.get(Calendar.YEAR), ct.get(Calendar.MONTH), ct.get(Calendar.DAY_OF_MONTH), 0, 0);
                Date dataWybrana = new Date(c.getTimeInMillis());
                Log.i("Tag-main", "data:" + dataWybrana.getTime());



                MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(dataWybrana.getTime())
                        .setCalendarConstraints(
                                new CalendarConstraints.Builder()
                                        .setValidator(
                                                new CalendarConstraints.DateValidator() {
                                                    @Override
                                                    public boolean isValid(long date) {
                                                        return MaterialDatePicker.todayInUtcMilliseconds() >= date ;
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
                        textView.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");

            }
        });
    }
}
