package pl.kalisz.ak.rafal.peczek.mojepomiary.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import pl.kalisz.ak.rafal.peczek.mojepomiary.MainActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.recivers.SampleBootReceiver;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout eMail, haslo;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        eMail = findViewById(R.id.eMailLayout);
        haslo = findViewById(R.id.hasloLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
        }
    }

    public void reg(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    public void reset(View view) {
        startActivity(new Intent(getApplicationContext(), ZresetujHasloActivity.class));
    }

    public void login(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }


        View elementView = getLayoutInflater().inflate(R.layout.progres_bar, null, false);
        MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(LoginActivity.this)
                .setCancelable(false)
                .setTitle(R.string.logowanie)
                .setView(elementView);
        AlertDialog progers = progresbilder.show();

        String eMail = this.eMail.getEditText().getText().toString().trim();
        String haslo = this.haslo.getEditText().getText().toString().trim();


        if (validateData(eMail, haslo)) {
            mAuth.signInWithEmailAndPassword(eMail, haslo)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                new SampleBootReceiver().renewAlarmManager(getApplicationContext());
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(LoginActivity.this)
                                            .setTitle(R.string.logowanie)
                                            .setMessage(R.string.Brak_dostępu_do_internetu)
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch (FirebaseAuthInvalidUserException | FirebaseAuthInvalidCredentialsException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(LoginActivity.this)
                                            .setTitle(R.string.logowanie)
                                            .setMessage(R.string.nieprawidłowe_dane_logowania)
                                            .setPositiveButton(R.string.submit, (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch (Exception e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(LoginActivity.this)
                                            .setTitle(R.string.logowanie)
                                            .setMessage(task.getException().getLocalizedMessage())
                                            .setPositiveButton(R.string.submit, (dialog, which) -> dialog.cancel());
                                    progresbilder.show();
                                }
                            }
                            progers.cancel();
                        }
                    });
        } else {
            progers.cancel();
        }

    }

    boolean validateData(String eMail, String haslo) {
        boolean status = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
            this.eMail.setError(getString(R.string.niepoprawny_e_mail));
            status = false;
        } else
            this.eMail.setErrorEnabled(false);

        if (haslo.length() < 6) {
            this.haslo.setError(getString(R.string.minimum_6_znak_w));
            status = false;
        } else
            this.haslo.setErrorEnabled(false);


        return status;
    }

}