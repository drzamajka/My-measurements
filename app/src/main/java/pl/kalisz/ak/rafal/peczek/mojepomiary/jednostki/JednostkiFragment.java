package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

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
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JednostkiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JednostkiFragment extends Fragment {

    private RecyclerView rvJednostki;
    private JednostkaAdapter jednostkaAdapter;
    private static JednostkiRepository jednostkiRepository;


    public JednostkiFragment() {
    }

    public static JednostkiFragment newInstance() {
        JednostkiFragment fragment = new JednostkiFragment();
        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getUid());
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
        FloatingActionButton button = view.findViewById(R.id.fab);
        button.setOnClickListener(buttonClickListener);


        rvJednostki = view.findViewById(R.id.recycleView);
        rvJednostki.setHasFixedSize(true);
        rvJednostki.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<Jednostka> options
                = new FirestoreRecyclerOptions.Builder<Jednostka>()
                .setQuery(jednostkiRepository.getQuery(), Jednostka.class)
                .build();

        jednostkaAdapter = new JednostkaAdapter(options);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        jednostkaAdapter.startListening();
        rvJednostki.setAdapter(jednostkaAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        jednostkaAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}