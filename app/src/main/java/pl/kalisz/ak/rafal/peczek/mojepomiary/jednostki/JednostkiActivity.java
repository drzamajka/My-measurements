package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class JednostkiActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;


    private RecyclerView rvJednostki;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private Cursor cursor;
    private UsersRoomDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jednostki);

        AdapterView.OnClickListener buttonClickListener = new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JednostkiActivity.this, JednostkiDopisz.class);
                startActivity(intent);
            }
        };

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(buttonClickListener);

        rvJednostki = (RecyclerView) findViewById(R.id.recycleView);
        rvJednostki.setHasFixedSize(true);

        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
        } else
            layoutManager = new LinearLayoutManager(this);


        rvJednostki.setLayoutManager(layoutManager);
        rvJednostki.setItemAnimator(new DefaultItemAnimator());

        rvJednostki = (RecyclerView) findViewById(R.id.recycleView);



        try{
            database = UsersRoomDatabase.getInstance(getApplicationContext());
        }catch (SQLException e){
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }




    }

    @Override
    protected void onResume(){
        super.onResume();

        if(database != null) {
            List<Jednostka> listaJednostek = database.localJednostkaDao().getAll();
            Toast.makeText(this, "posiadasz: " + database.localJednostkaDao().countAll() + " jednostek", Toast.LENGTH_SHORT).show();

            adapter = new JednostkiAdapter(listaJednostek);
            rvJednostki.setAdapter(adapter);
        }

    }


}