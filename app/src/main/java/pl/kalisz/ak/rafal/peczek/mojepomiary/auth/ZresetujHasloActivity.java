package pl.kalisz.ak.rafal.peczek.mojepomiary.auth;

import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;

public class ZresetujHasloActivity extends AppCompatActivity {

    private TextInputLayout eMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zresetuj_haslo);

        eMail = findViewById(R.id.eMailLayout);
    }

    public void reset(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }

        View elementView = getLayoutInflater().inflate(R.layout.progres_bar, null, false);
        MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(ZresetujHasloActivity.this)
                .setCancelable(false)
                .setTitle(R.string.resetowanie)
                .setView(elementView);
        AlertDialog progers = progresbilder.show();

        String eMail = this.eMail.getEditText().getText().toString();
        if (validateData(eMail)) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            TextInputLayout eMailStatic = this.eMail;

            mAuth.sendPasswordResetEmail(eMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progers.cancel();
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseNetworkException e) {
                            MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(ZresetujHasloActivity.this)
                                    .setTitle(R.string.resetowanie)
                                    .setMessage(R.string.brak_dostępu_do_internetu)
                                    .setPositiveButton(R.string.submit, (dialog, which) -> {
                                        dialog.cancel();
                                    });
                            progresbilder.show();
                        } catch (FirebaseAuthInvalidUserException e) {
                            eMailStatic.setError(getString(R.string.Nie_znaleziono_urzytkownika_o_takim_adresie_email));
                        } catch (Exception e) {
                            MaterialAlertDialogBuilder progresbilder = new MaterialAlertDialogBuilder(ZresetujHasloActivity.this)
                                    .setTitle(R.string.resetowanie)
                                    .setMessage(task.getException().getLocalizedMessage())
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
            progers.cancel();
        }
    }

    boolean validateData(String eMail) {
        boolean status = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
            this.eMail.setError(getString(R.string.niepoprawny_e_mail));
            status = false;
        } else
            this.eMail.setErrorEnabled(false);

        return status;
    }
}