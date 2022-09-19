package pl.kalisz.ak.rafal.peczek.mojepomiary.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.UzytkownikDao;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;

public class RegisterActivity extends AppCompatActivity {

    private EditText login;
    private EditText imie;
    private EditText nazwisko;
    private EditText haslo;
    private DatePicker dataUrodzenia;
    private EditText eMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = findViewById(R.id.login);
        imie = findViewById(R.id.imie);
        nazwisko = findViewById(R.id.nazwisko);
        haslo = findViewById(R.id.haslo);
        dataUrodzenia = findViewById(R.id.dataUrodzenia);
        eMail = findViewById(R.id.eMail);
    }


    public void register(View view){

        String login = this.login.getText().toString();
        String imie = this.imie.getText().toString();
        String nazwisko = this.nazwisko.getText().toString();
        String haslo = this.haslo.getText().toString();
        //Data
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.dataUrodzenia.getYear(), this.dataUrodzenia.getMonth(), this.dataUrodzenia.getDayOfMonth());
        Date dataUrodzenia = calendar.getTime();

        String eMail = this.eMail.getText().toString();



        if(login.length() < 2 || haslo.length() < 2 || eMail.length() < 2 ){
            Toast.makeText (getApplicationContext(), "the input information does not meet the requirements, please re-enter", Toast.LENGTH_LONG). show();
            return;

        }


        Uzytkownik uzytkownik = new Uzytkownik();
        uzytkownik.setLogin(login);
        uzytkownik.setImie(imie);
        uzytkownik.setNazwisko(nazwisko);
        uzytkownik.setHaslo(haslo);
        uzytkownik.setDataUrodzenia(dataUrodzenia);
        uzytkownik.setEMail(eMail);
        uzytkownik.setDataUtwozenia(new Date());
        uzytkownik.setDataAktualizacji(new Date());


        new Thread(){
            @Override
            public void run() {

                int msg = 0;

                UzytkownikDao uzytkownikDao = new UzytkownikDao();

                Uzytkownik uu = uzytkownikDao.findUser(uzytkownik.getLogin());

                if(uu != null){
                    msg = 1;
                }

                boolean flag = uzytkownikDao.register(uzytkownik);
                if(flag){
                    msg = 2;
                }
                hand.sendEmptyMessage(msg);

            }
        }.start();


    }
    final Handler hand = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0)
            {
                Toast.makeText(getApplicationContext(), "registration failed", Toast.LENGTH_LONG). show();

            }
            if(msg.what == 1)
            {
                Toast.makeText(getApplicationContext(), "this account already exists, please change another account", Toast.LENGTH_LONG). show();

            }
            if(msg.what == 2)
            {
                //startActivity(new Intent(getApplication(),MainActivity.class));

                Intent intent = new Intent();
                //Encapsulate the data you want to transfer in intent with putextra
                intent.putExtra("a", "registration");
                setResult(RESULT_CANCELED,intent);
                finish();
            }

        }
    };
}
