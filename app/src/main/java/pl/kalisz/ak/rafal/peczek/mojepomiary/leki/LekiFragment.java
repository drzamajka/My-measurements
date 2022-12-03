package pl.kalisz.ak.rafal.peczek.mojepomiary.leki;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary.PomiarDopisz;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LekiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LekiFragment extends Fragment {

    private RecyclerView rvLeki;
    private LekAdapter lekAdapter;
    private static LekRepository lekRepository;

    public LekiFragment() {
    }


    public static LekiFragment newInstance() {
        LekiFragment fragment = new LekiFragment();
        lekRepository = new LekRepository(FirebaseAuth.getInstance().getUid());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lek, container, false);

        AdapterView.OnClickListener buttonClickListener = new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LekDopisz.class);
                startActivity(intent);
            }
        };

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fab);
        button.setOnClickListener(buttonClickListener);

        rvLeki = (RecyclerView) view.findViewById(R.id.recycleView);
        rvLeki.setHasFixedSize(true);
        rvLeki.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<Lek> options
                = new FirestoreRecyclerOptions.Builder<Lek>()
                .setQuery(lekRepository.getQuery(), Lek.class)
                .build();

        lekAdapter = new LekAdapter(options);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        lekAdapter.startListening();
        rvLeki.setAdapter(lekAdapter);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        lekAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}