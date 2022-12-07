package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyLeki;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyPomiary.WpisPomiarAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyPomiary.WpisPomiarDopisz;


public class WpisLekFragment extends Fragment {


    private RecyclerView rvWpisLek;
    private WpisLekAdapter wpisLekAdapter;
    private static WpisLekRepository wpisLekRepository;

    public WpisLekFragment() {
        // Required empty public constructor
    }


    public static WpisLekFragment newInstance() {
        WpisLekFragment fragment = new WpisLekFragment();
        wpisLekRepository = new WpisLekRepository(FirebaseAuth.getInstance().getUid());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wpis_lek, container, false);

        AdapterView.OnClickListener buttonClickListener = new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WpisLekDopisz.class);
                startActivity(intent);
            }
        };

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fab);
        button.setOnClickListener(buttonClickListener);



        rvWpisLek = (RecyclerView) view.findViewById(R.id.recycleView);
        rvWpisLek.setHasFixedSize(true);
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvWpisLek.setLayoutManager(
                    new GridLayoutManager(getContext(), 2));
        } else
            rvWpisLek.setLayoutManager(
                    new LinearLayoutManager(getContext()));

        rvWpisLek.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<WpisLek> options
                = new FirestoreRecyclerOptions.Builder<WpisLek>()
                .setQuery(wpisLekRepository.getQuery().orderBy("dataWykonania", Query.Direction.DESCENDING), WpisLek.class)
                .build();

        wpisLekAdapter = new WpisLekAdapter(options);


        rvWpisLek = (RecyclerView) view.findViewById(R.id.recycleView);
        rvWpisLek.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        wpisLekAdapter.startListening();
        rvWpisLek.setAdapter(wpisLekAdapter);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        wpisLekAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}