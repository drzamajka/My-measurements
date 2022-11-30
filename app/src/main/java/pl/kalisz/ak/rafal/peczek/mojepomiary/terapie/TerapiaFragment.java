package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary.PomiarAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TerapiaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TerapiaFragment extends Fragment {


    private RecyclerView rvTerapie;
    private TerapiaAdapter terapiaAdapter;
    private static TerapiaRepository terapiaRepository;

    public TerapiaFragment() {
        // Required empty public constructor
    }


    public static TerapiaFragment newInstance() {
        TerapiaFragment fragment = new TerapiaFragment();
        terapiaRepository = new TerapiaRepository(FirebaseAuth.getInstance().getUid());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_terapia, container, false);

        AdapterView.OnClickListener buttonClickListener = new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TerapiaDopisz.class);
                startActivity(intent);
            }
        };

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fab);
        button.setOnClickListener(buttonClickListener);

        rvTerapie = (RecyclerView) view.findViewById(R.id.recycleView);
        rvTerapie.setHasFixedSize(true);
        rvTerapie.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<Terapia> options
                = new FirestoreRecyclerOptions.Builder<Terapia>()
                .setQuery(terapiaRepository.getQuery(), Terapia.class)
                .build();

        terapiaAdapter = new TerapiaAdapter(options);


        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        terapiaAdapter.startListening();
        rvTerapie.setAdapter(terapiaAdapter);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        terapiaAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}