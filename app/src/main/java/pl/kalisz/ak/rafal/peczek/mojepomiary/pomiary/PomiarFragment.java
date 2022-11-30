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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki.JednostkaAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PomiarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PomiarFragment extends Fragment {

    private RecyclerView rvPomiary;
    private PomiarAdapter pomiarAdapter;
    private static PomiarRepository pomiarRepository;

    public PomiarFragment() {
    }


    public static PomiarFragment newInstance() {
        PomiarFragment fragment = new PomiarFragment();
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getUid());
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
        rvPomiary.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<Pomiar> options
                = new FirebaseRecyclerOptions.Builder<Pomiar>()
                .setQuery(pomiarRepository.getQuery(), Pomiar.class)
                .build();

        pomiarAdapter = new PomiarAdapter(options);


        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        pomiarAdapter.startListening();
        rvPomiary.setAdapter(pomiarAdapter);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        pomiarAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}