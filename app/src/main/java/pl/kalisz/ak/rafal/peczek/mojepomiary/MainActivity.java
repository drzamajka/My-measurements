package pl.kalisz.ak.rafal.peczek.mojepomiary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import pl.kalisz.ak.rafal.peczek.mojepomiary.auth.KontoActivity;
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

    private FrameLayout frame;
    private NavigationView navigationView;
    private Button wyloguj, konto;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.naw_open, R.string.naw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        konto = findViewById(R.id.konto);
        wyloguj = findViewById(R.id.wyloguj);
        navigationView = findViewById(R.id.nav_viev);
        navigationView.setNavigationItemSelectedListener(this);

        if (mAuth.getCurrentUser() != null) {
            frame = findViewById(R.id.fooFragment);
            navigationView.getMenu().getItem(0).setChecked(true);
            zmienFragment(R.id.naw_etapy);
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
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().reload();
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            mDatabase.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Uzytkownik user = task.getResult().toObject(Uzytkownik.class);
                        if(user != null) {
                            konto.setText(user.getImie() + getString(R.string.spacia) + user.getNazwisko());
                        }
                    }
                }
            });

            konto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent0 = new Intent(getApplicationContext(), KontoActivity.class);
                    startActivity(intent0);
                }
            });

            wyloguj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                    builder.setMessage(R.string.czy_napewno_wylogowac);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.tak, (dialog, which) -> {
                        Toast.makeText(getApplicationContext(), (R.string.wylogowanie), Toast.LENGTH_LONG).show();
                        new SampleBootReceiver().cancleAlarmManager(getApplicationContext());
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    });
                    builder.setNegativeButton(R.string.nie, (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                }
            });

            createNotificationChannel();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about: {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                builder.setTitle(R.string.about);
                builder.setMessage(R.string.o_programie);
                builder.setPositiveButton(R.string.submit, (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        zmienFragment(item.getItemId());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(item);
        return super.onOptionsItemSelected(item);
    }

    private void zmienFragment(int id) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
        );
        switch (id) {
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (navigationView.getCheckedItem() != null && navigationView.getCheckedItem().getItemId() != R.id.naw_etapy) {
                navigationView.getMenu().getItem(0).setChecked(true);
                zmienFragment(R.id.naw_etapy);
            } else
                super.onBackPressed();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nazwa = getString(R.string.moje_pomiary_chanel);
            String opis = getString(R.string.zrodlo_powiadomien);
            int znaczenie = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel chanel = new NotificationChannel(getString(R.string.app_name), nazwa, znaczenie);
            chanel.setDescription(opis);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(chanel);
        }
    }


}