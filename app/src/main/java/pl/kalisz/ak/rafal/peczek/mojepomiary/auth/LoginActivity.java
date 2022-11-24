package pl.kalisz.ak.rafal.peczek.mojepomiary.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.MainActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;

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

    public void login(View view){
        String eMail = this.eMail.getEditText().getText().toString().trim();
        String haslo = this.haslo.getEditText().getText().toString().trim();

        if(validateData(eMail, haslo)) {
            mAuth.signInWithEmailAndPassword(eMail, haslo)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                LoginActivity.this.haslo.setError("Niepoprawne hasło");
                            }
                        }
                    });
        }

    }

    boolean validateData(String eMail, String haslo){
        boolean status = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()){
            this.eMail.setError("Niepoprawny Email");
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