package pl.kalisz.ak.rafal.peczek.mojepomiary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.Dao.UzytkownikDao;
import pl.kalisz.ak.rafal.peczek.mojepomiary.auth.RegisterActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.WpisPomiarPosiadaPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki.JednostkiActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.lab12.Ustawienia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary.PomiaryActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UserRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.TerapiaActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.TerapiaDopisz;
import pl.kalisz.ak.rafal.peczek.mojepomiary.wpisy.WpisyActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.wpisy.WpisyAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ShareActionProvider shareActionProvider;
    private Snackbar snackbar;
    private UserRepository localUser;
    private TextView dataWpsiow;

    private RecyclerView rvPomiary;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private UsersRoomDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localUser = new UserRepository(getApplicationContext());

        if(localUser.getLoggedInUser() != null) {
            setContentView(R.layout.activity_main);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.naw_open, R.string.naw_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_viev);
            navigationView.setNavigationItemSelectedListener(this);

            // lista
            dataWpsiow = (TextView) findViewById(R.id.Data);
            dodajDatePicker(dataWpsiow);
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dataWpsiow.setText(simpleDateFormat.format(date));



            rvPomiary = (RecyclerView) findViewById(R.id.recycleView);
            rvPomiary.setHasFixedSize(true);

            Configuration config = getResources().getConfiguration();
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutManager = new GridLayoutManager(this, 2);
            } else
                layoutManager = new LinearLayoutManager(this);

            rvPomiary.setLayoutManager(layoutManager);
            rvPomiary.setItemAnimator(new DefaultItemAnimator());
            rvPomiary = (RecyclerView) findViewById(R.id.recycleView);
            database = UsersRoomDatabase.getInstance(getApplicationContext());
        }
        else{
            setContentView(R.layout.activity_login);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId() ) {
//            case R.id.version:
//                Toast.makeText(this, getAndroidVersion(), Toast.LENGTH_LONG).show();
//                return true;
            case R.id.setings:
                Toast.makeText(this, "Ustawienia", Toast.LENGTH_LONG).show();
                Intent intent0 = new Intent( this, Ustawienia.class);
                startActivity(intent0);
                return true;
            case R.id.about:
                snackbar = Snackbar.make( findViewById(android.R.id.content), "Program Moje pomiary napisany przez Rafała Pęczek.\n Aplikacia udostępniona na zasadach wolnej licencji" , Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.submit, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private String getAndroidVersion() {
        String relese = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return getString(R.string.version)+" "+sdkVersion+" ("+relese+")";
    }





    //    zpais stanu
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences ustawienia = getSharedPreferences("PREFS", 0);
        SharedPreferences.Editor edytor = ustawienia.edit();
        //edytor.putString("wartosc", editText.getText().toString());
        //edytor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        update();
        SharedPreferences ustawienia = getSharedPreferences("PREFS", 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.naw_jednostki:
                Intent intent1 = new Intent( this, JednostkiActivity.class);
                startActivity(intent1);
                break;
            case R.id.naw_pomiary:
                Intent intent2 = new Intent( this, PomiaryActivity.class);
                startActivity(intent2);
                break;
            case R.id.naw_terapie:
                Intent intent3 = new Intent( this, TerapiaActivity.class);
                startActivity(intent3);
                break;
            case R.id.naw_wpisy:
                Intent intent4 = new Intent( this, WpisyActivity.class);
                startActivity(intent4);
                break;

//            case R.id.naw_12:
//                Intent intent12 = new Intent( this, KierunkiActivity.class);
//                startActivity(intent12);
//                break;
//            case R.id.naw_13:
//                Intent intent13 = new Intent( this, WyslijWiadomosc.class);
//                startActivity(intent13);
//                break;
//            case R.id.naw_16:
//                Toast.makeText(this, "Lab.6", Toast.LENGTH_LONG).show();
//                Intent intent16 = new Intent( this, Czujniki.class);
//                startActivity(intent16);
//                break;
//            case R.id.naw_17:
//                Toast.makeText(this, "Lab.7", Toast.LENGTH_LONG).show();
//                Intent intent17 = new Intent( this, Formularz.class);
//                startActivity(intent17);
//                break;
//            case R.id.naw_18:
//                Toast.makeText(this, "Lab.8", Toast.LENGTH_LONG).show();
//                Intent intent18 = new Intent( this, ObiektyUczelni.class);
//                startActivity(intent18);
//                break;
//            case R.id.naw_19:
//                Toast.makeText(this, "Lab.9", Toast.LENGTH_LONG).show();
//                Intent intent19 = new Intent( this, ObiektyLista.class);
//                startActivity(intent19);
//                break;
//            case R.id.naw_110:
//                Toast.makeText(this, "Lab.10", Toast.LENGTH_LONG).show();
//                Intent intent110 = new Intent( this, JednostkiActivity.class);
//                startActivity(intent110);
//                break;
//            case R.id.naw_111:
//                Toast.makeText(this, "Lab.11", Toast.LENGTH_LONG).show();
//                Intent intent111 = new Intent( this, SprawdzajSiec.class);
//                startActivity(intent111);
//                break;
//            case R.id.temat:
//                snackbar = Snackbar.make( findViewById(android.R.id.content), R.string.naw_projekt_temat, Snackbar.LENGTH_LONG);
//                snackbar.show();
//                break;
//            case R.id.ikonta:
//                snackbar = Snackbar.make( findViewById(android.R.id.content), R.string.naw_projekt_ikona, Snackbar.LENGTH_LONG);
//                snackbar.show();
//                break;
//            case R.id.wyloguj:
//                Toast.makeText(this, "Wylogowanie", Toast.LENGTH_LONG).show();
//                localUser.setUserLoggedIn(false);
//                localUser.clearUserData();
//                finish();
//                startActivity(getIntent());
//                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    //    Kontrola obrotu
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, R.string.rotation_land, Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, R.string.rotation_port, Toast.LENGTH_SHORT).show();
        }
    }


    //auth
    public void reg(View view){

        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

    }


    public void login(View view){

        EditText EditTextname = (EditText)findViewById(R.id.name);
        EditText EditTextpassword = (EditText)findViewById(R.id.password);

        new Thread(){
            @Override
            public void run() {

                UzytkownikDao uzytkownikDao = new UzytkownikDao();

                boolean aa = uzytkownikDao.login(EditTextname.getText().toString(),EditTextpassword.getText().toString());
                if(aa){
                    Uzytkownik uzytkownik = uzytkownikDao.findUser(EditTextname.getText().toString());
                    if(uzytkownik != null) {
                        localUser.storeUserData(uzytkownik);
                        localUser.setUserLoggedIn(true);
                    }else
                    {
                        Toast.makeText(getApplicationContext(), "Niepoprawne dane", Toast.LENGTH_LONG).show();
                    }
                    finish();
                    startActivity(getIntent());
                }
            }
        }.start();

    }

    public void update(){
        if(localUser.getLoggedInUser()!=null) {

            new Thread() {
                @Override
                public void run() {

                    UzytkownikDao uzytkownikDao = new UzytkownikDao();

                    Uzytkownik uzytkownik = uzytkownikDao.findUser(localUser.getLoggedInUser().getLogin());
                    if(uzytkownik != null) {
                        if (!localUser.getLoggedInUser().toString().equals(uzytkownik.toString())) {
                            Log.i("Tag-main", "aktualizacia: z" + localUser.getLoggedInUser().toString());
                            Log.i("Tag-main", "aktualizacia: na" + uzytkownik);
                            localUser.storeUserData(uzytkownik);
                            finish();
                            startActivity(getIntent());
                        }
                    }
                }
            }.start();
        }
    }

    // lista wpisów
    @Override
    protected void onResume(){
        super.onResume();

        if(database != null) {
            odswiezListe();
        }

    }

    private void odswiezListe(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dataWpsiow.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        Date dateJutro = c.getTime();


        List<WpisPomiarPosiadaPomiar> listaWpisow = database.localWpisPomiarDao().getAllbetwenData(date.getTime(), dateJutro.getTime());

        adapter = new WpisyAdapter(listaWpisow, getApplicationContext());
        rvPomiary.setAdapter(adapter);
    }

    public void dalej(View view) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dataWpsiow.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        dataWpsiow.setText(simpleDateFormat.format(date));
        odswiezListe();
    }

    public void wczesniej(View view) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dataWpsiow.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        date = c.getTime();
        dataWpsiow.setText(simpleDateFormat.format(date));
        odswiezListe();
    }

    private void dodajDatePicker(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = simpleDateFormat.parse(dataWpsiow.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                textView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                odswiezListe();

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }
}