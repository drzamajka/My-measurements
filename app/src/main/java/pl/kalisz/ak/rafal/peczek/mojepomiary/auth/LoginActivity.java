package pl.kalisz.ak.rafal.peczek.mojepomiary.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;


import java.util.Date;
import java.util.regex.Pattern;

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

    public void reg(View view){
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }

    public void reset(View view){
        startActivity(new Intent(getApplicationContext(),ZresetujHasloActivity.class));
    }

    public void login(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }


        View elementView = getLayoutInflater().inflate(R.layout.progres_bar, null, false);
        MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(LoginActivity.this)
                .setCancelable(false)
                .setTitle("Logowanie")
                .setView(elementView);
        AlertDialog progers = progresbilder.show();

        String eMail = this.eMail.getEditText().getText().toString().trim();
        String haslo = this.haslo.getEditText().getText().toString().trim();


        if(validateData(eMail, haslo)) {
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
                                } catch(FirebaseNetworkException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(LoginActivity.this)
                                            .setTitle("Logowanie")
                                            .setMessage("Brak dostępu do internetu")
                                            .setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch(FirebaseAuthInvalidUserException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(LoginActivity.this)
                                            .setTitle("Logowanie")
                                            .setMessage("Nieprawidłowe dane logowania")
                                            .setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(LoginActivity.this)
                                            .setTitle("Logowanie")
                                            .setMessage("Nieprawidłowe dane logowania")
                                            .setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                } catch(Exception e) {
                                    MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(LoginActivity.this)
                                            .setTitle("Logowanie")
                                            .setMessage(task.getException().getLocalizedMessage())
                                            .setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                dialog.cancel();
                                            });
                                    progresbilder.show();
                                }
                            }
                            progers.cancel();
                        }
                    });
        }else{
            progers.cancel();
        }

    }

    boolean validateData(String eMail, String haslo){
        boolean status = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()){
            this.eMail.setError("Niepoprawny adres email");
            status = false;
        }else
            this.eMail.setErrorEnabled(false);

        if(haslo.length()<6){
            this.haslo.setError("Minimum 6 znaków");
            status = false;
        }else
            this.haslo.setErrorEnabled(false);


        return status;
    }

}