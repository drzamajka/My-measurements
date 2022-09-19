package pl.kalisz.ak.rafal.peczek.mojepomiary.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.UzytkownikDao;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void reg(View view){

        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));

    }


    public void login(View view){

        EditText EditTextname = (EditText)findViewById(R.id.name);
        EditText EditTextpassword = (EditText)findViewById(R.id.password);

        new Thread(){
            @Override
            public void run() {

                UzytkownikDao uzytkownikDao = new UzytkownikDao();

                boolean aa = uzytkownikDao.login(EditTextname.getText().toString(),EditTextpassword.getText().toString());
                int msg = 0;
                if(aa){
                    msg = 1;
                }

                hand1.sendEmptyMessage(msg);


            }
        }.start();


    }
    final Handler hand1 = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1)
            {
                Toast.makeText(getApplicationContext(), "login succeeded", Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_LONG).show();
            }
        }
    };

}