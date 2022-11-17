package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import android.content.Intent;
import android.content.res.Configuration;
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
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PomiarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PomiarFragment extends Fragment {

    private RecyclerView rvPomiary;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private UsersRoomDatabase database;

    public PomiarFragment() {
        // Required empty public constructor
    }


    public static PomiarFragment newInstance() {
        PomiarFragment fragment = new PomiarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomiar, container, false);

        AdapterView.OnClickListener buttonClickListener = new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PomiaryDopisz.class);
                startActivity(intent);
            }
        };

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fab);
        button.setOnClickListener(buttonClickListener);

        rvPomiary = (RecyclerView) view.findViewById(R.id.recycleView);
        rvPomiary.setHasFixedSize(true);

        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        } else
            layoutManager = new LinearLayoutManager(getContext());


        rvPomiary.setLayoutManager(layoutManager);
        rvPomiary.setItemAnimator(new DefaultItemAnimator());

        rvPomiary = (RecyclerView) view.findViewById(R.id.recycleView);


        database = UsersRoomDatabase.getInstance(getContext());

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(database != null) {
            List<Pomiar> listapomiarow = database.localPomiarDao().getAll();
            Toast.makeText(getContext(), "posiadasz: " + database.localPomiarDao().countAll() + " pomiarów", Toast.LENGTH_SHORT).show();

            adapter = new PomiarAdapter(listapomiarow);
            rvPomiary.setAdapter(adapter);
        }

    }
}