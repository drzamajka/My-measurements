package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisy;

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

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary.PomiarAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary.PomiarFragment;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;


public class WpisPomiarFragment extends Fragment {


    private RecyclerView rvWpisPomiary;
    private WpisPomiarAdapter wpisPomiarAdapter;
    private static WpisPomiarRepository wpisPomiarRepository;

    public WpisPomiarFragment() {
        // Required empty public constructor
    }


    public static WpisPomiarFragment newInstance() {
        WpisPomiarFragment fragment = new WpisPomiarFragment();
        wpisPomiarRepository = new WpisPomiarRepository(FirebaseAuth.getInstance().getUid());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wpis_pomiar, container, false);

        AdapterView.OnClickListener buttonClickListener = new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WpisyDopisz.class);
                startActivity(intent);
            }
        };

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fab);
        button.setOnClickListener(buttonClickListener);



        rvWpisPomiary = (RecyclerView) view.findViewById(R.id.recycleView);
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvWpisPomiary.setLayoutManager(
                    new GridLayoutManager(getContext(), 2));
        } else
            rvWpisPomiary.setLayoutManager(
                    new LinearLayoutManager(getContext()));

        rvWpisPomiary.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<WpisPomiar> options
                = new FirebaseRecyclerOptions.Builder<WpisPomiar>()
                .setQuery(wpisPomiarRepository.getQuery(), WpisPomiar.class)
                .build();

        wpisPomiarAdapter = new WpisPomiarAdapter(options);


        rvWpisPomiary = (RecyclerView) view.findViewById(R.id.recycleView);
        rvWpisPomiary.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        wpisPomiarAdapter.startListening();
        rvWpisPomiary.setAdapter(wpisPomiarAdapter);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        wpisPomiarAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}