package pl.kalisz.ak.rafal.peczek.mojepomiary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import pl.kalisz.ak.rafal.peczek.mojepomiary.auth.LoginActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;
import pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki.JednostkiFragment;
import pl.kalisz.ak.rafal.peczek.mojepomiary.leki.LekiFragment;
import pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary.PomiarFragment;
import pl.kalisz.ak.rafal.peczek.mojepomiary.recivers.SampleBootReceiver;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.TerapiaFragment;
import pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyLeki.WpisLekFragment;
import pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyPomiary.WpisPomiarFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Snackbar snackbar;
    private FrameLayout frame;
    private NavigationView navigationView;
    private Button wyloguj, konto;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.naw_open, R.string.naw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        konto = (Button) findViewById(R.id.konto);
        wyloguj = (Button) findViewById(R.id.wyloguj);


        navigationView = (NavigationView) findViewById(R.id.nav_viev);
        navigationView.setNavigationItemSelectedListener(this);
        View elementView = getLayoutInflater().inflate(R.layout.activity_terapia_dopisz_element, null, false);

        if(mAuth.getCurrentUser() != null) {
            frame = findViewById(R.id.fooFragment);
            MainFragment mFragment = new MainFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(frame.getId(), mFragment).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        }


        ComponentName receiver = new ComponentName(this, SampleBootReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        else
        {
            mDatabase.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        Uzytkownik user = task.getResult().toObject(Uzytkownik.class);
                        konto.setText(user.getImie() + " " + user.getNazwisko());
                    }
                }
            });

            konto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                Uzytkownik user = task.getResult().toObject(Uzytkownik.class);
                                Toast.makeText(getApplicationContext(), user.getEMail(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            });
            wyloguj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                    builder.setMessage("Czy na pewno wylogować?");
//                builder.setTitle("Alert !");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Tak", (DialogInterface.OnClickListener) (dialog, which) -> {
                        Toast.makeText(getApplicationContext(), "Wylogowanie", Toast.LENGTH_LONG).show();
                        new SampleBootReceiver().cancleAlarmManager(getApplicationContext());
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    });
                    builder.setNegativeButton("Nie", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                }
            });
            Log.w("TAG", "user: "+currentUser.getUid());
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId() ) {

            case R.id.setings:
                Toast.makeText(this, "Ustawienia", Toast.LENGTH_LONG).show();
//                Intent intent0 = new Intent( this, Ustawienia.class);
//                startActivity(intent0);
                return true;
            case R.id.about: {
                snackbar = Snackbar.make(findViewById(android.R.id.content), "Program Moje pomiary napisany przez Rafała Pęczek.\n Aplikacia udostępniona na zasadach wolnej licencji", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.submit, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        item.setChecked(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.fade_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.fade_out  // popExit
        );
        switch (id){
            case R.id.naw_etapy:
                MainFragment mFragment = MainFragment.newInstance();
                ft.replace(frame.getId(), mFragment).commit();
                break;
            case R.id.naw_terapie:
                TerapiaFragment tFragment = TerapiaFragment.newInstance();
                ft.replace(frame.getId(), tFragment).commit();
                break;
            case R.id.naw_jednostki:
                JednostkiFragment jFragment = JednostkiFragment.newInstance();
                ft.replace(frame.getId(), jFragment).commit();
                break;
            case R.id.naw_pomiary:
                PomiarFragment pFragment = PomiarFragment.newInstance();
                ft.replace(frame.getId(), pFragment).commit();
                break;
            case R.id.naw_wpisy_pomiary:
                WpisPomiarFragment wpFragment = WpisPomiarFragment.newInstance();
                ft.replace(frame.getId(), wpFragment).commit();
                break;
            case R.id.naw_leki:
                LekiFragment lFragment = LekiFragment.newInstance();
                ft.replace(frame.getId(), lFragment).commit();
                break;
            case R.id.naw_wpisy_leki:
                WpisLekFragment wlFragment = WpisLekFragment.newInstance();
                ft.replace(frame.getId(), wlFragment).commit();
                break;


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(item);;

        return super.onOptionsItemSelected(item);
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



}