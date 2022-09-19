package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class PomiaryActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;


    private RecyclerView rvPomiary;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private UsersRoomDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomiary);

        AdapterView.OnClickListener buttonClickListener = new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PomiaryActivity.this, PomiaryDopisz.class);
                startActivity(intent);
            }
        };

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(buttonClickListener);

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

    @Override
    protected void onResume(){
        super.onResume();

        if(database != null) {
            List<Pomiar> listapomiarow = database.localPomiarDao().getAll();
            Toast.makeText(this, "posiadasz: " + database.localPomiarDao().countAll() + " pomiarów", Toast.LENGTH_SHORT).show();

            adapter = new PomiarAdapter(listapomiarow);
            rvPomiary.setAdapter(adapter);
        }

    }


}