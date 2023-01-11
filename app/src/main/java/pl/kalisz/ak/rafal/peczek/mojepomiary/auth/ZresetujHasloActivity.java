package pl.kalisz.ak.rafal.peczek.mojepomiary.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;

public class ZresetujHasloActivity extends AppCompatActivity {

    private TextInputLayout eMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zresetuj_haslo);

        eMail = findViewById(R.id.eMailLayout);
    }

    public void reset(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }

        View elementView = getLayoutInflater().inflate(R.layout.progres_bar, null, false);
        MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(ZresetujHasloActivity.this)
                .setCancelable(false)
                .setTitle("Resetowanie")
                .setView(elementView);
        AlertDialog progers = progresbilder.show();

        String eMail = this.eMail.getEditText().getText().toString();
        if(validateData(eMail)) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            TextInputLayout eMailStatic = this.eMail;

            mAuth.sendPasswordResetEmail(eMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        progers.cancel();
                        finish();
                    }
                    else {
                        try {
                            throw task.getException();
                        } catch(FirebaseNetworkException e) {
                            MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(ZresetujHasloActivity.this)
                                    .setTitle("Resetowanie")
                                    .setMessage("Brak dostępu do internetu")
                                    .setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                                        dialog.cancel();
                                    });
                            progresbilder.show();
                        } catch(FirebaseAuthInvalidUserException e) {
                            eMailStatic.setError("Nie znaleziono urzytkownika o takim adresie email!");
                        } catch(Exception e) {
                            MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(ZresetujHasloActivity.this)
                                    .setTitle("Resetowanie")
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
        }
        else {
            progers.cancel();
        }
    }

    boolean validateData(String eMail){
        boolean status = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()){
            this.eMail.setError("Niepoprawny Email");
            status = false;
        }else
            this.eMail.setErrorEnabled(false);

        return status;
    }
}