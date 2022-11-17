package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JednostkiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JednostkiFragment extends Fragment {

    private RecyclerView rvJednostki;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private UsersRoomDatabase database;


    public JednostkiFragment() {
        // Required empty public constructor
    }

    public static JednostkiFragment newInstance() {
        JednostkiFragment fragment = new JednostkiFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_jednostki, container, false);

        AdapterView.OnClickListener buttonClickListener = new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), JednostkiDopisz.class);
                startActivity(intent);
            }
        };

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fab);
        button.setOnClickListener(buttonClickListener);

        rvJednostki = (RecyclerView) view.findViewById(R.id.recycleView);
        rvJednostki.setHasFixedSize(true);

        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        } else
            layoutManager = new LinearLayoutManager(getContext());


        rvJednostki.setLayoutManager(layoutManager);
        rvJednostki.setItemAnimator(new DefaultItemAnimator());




        database = UsersRoomDatabase.getInstance(getContext());



        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(database != null) {
            List<Jednostka> listaJednostek = database.localJednostkaDao().getAll();
            Toast.makeText(getContext(), "posiadasz: " + database.localJednostkaDao().countAll() + " jednostek", Toast.LENGTH_SHORT).show();

            adapter = new JednostkiAdapter(listaJednostek);
            rvJednostki.setAdapter(adapter);
        }

    }
}