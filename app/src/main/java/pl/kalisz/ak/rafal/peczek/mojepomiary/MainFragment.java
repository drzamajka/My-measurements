package pl.kalisz.ak.rafal.peczek.mojepomiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private TextView dataWpsiow;
    private RecyclerView rvEtapy;
    private MainEtapAdapter mainEtapAdapter;
    private static EtapTerapiaRepository etapTerapiaRepository;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        etapTerapiaRepository = new EtapTerapiaRepository(FirebaseAuth.getInstance().getUid());

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button wczesniej = view.findViewById(R.id.button1);
        wczesniej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wczesniej();
            }
        });
        Button pozniej = view.findViewById(R.id.button2);
        pozniej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dalej();
            }
        });

        dataWpsiow = view.findViewById(R.id.Data);
        dodajDatePicker(dataWpsiow);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_daty));
        dataWpsiow.setText(simpleDateFormat.format(date));

        try {
            date = simpleDateFormat.parse(dataWpsiow.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        Date dateJutro = c.getTime();
        rvEtapy = view.findViewById(R.id.recycleView);
        rvEtapy.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<EtapTerapa> options
                = new FirestoreRecyclerOptions.Builder<EtapTerapa>()
                .setQuery(etapTerapiaRepository.getQuery().whereGreaterThanOrEqualTo("dataZaplanowania", date).whereLessThanOrEqualTo("dataZaplanowania", dateJutro), EtapTerapa.class)
                .build();
        mainEtapAdapter = new MainEtapAdapter(options);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mainEtapAdapter.startListening();
        rvEtapy.setAdapter(mainEtapAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mainEtapAdapter.stopListening();
    }

    private void odswiezListe() {
        mainEtapAdapter.stopListening();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_daty));
        Date date = null;
        try {
            date = simpleDateFormat.parse(dataWpsiow.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        Date dateJutro = c.getTime();

        FirestoreRecyclerOptions<EtapTerapa> options
                = new FirestoreRecyclerOptions.Builder<EtapTerapa>()
                .setQuery(etapTerapiaRepository.getQuery().whereGreaterThanOrEqualTo("dataZaplanowania", date).whereLessThanOrEqualTo("dataZaplanowania", dateJutro), EtapTerapa.class)
                .build();

        mainEtapAdapter = new MainEtapAdapter(options);
        mainEtapAdapter.startListening();
        rvEtapy.setHasFixedSize(true);
        rvEtapy.setAdapter(mainEtapAdapter);
        rvEtapy.scheduleLayoutAnimation();
    }

    public void dalej() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_daty));
        Date date = null;
        try {
            date = simpleDateFormat.parse(dataWpsiow.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        dataWpsiow.setText(simpleDateFormat.format(date));
        odswiezListe();
    }

    public void wczesniej() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_daty));
        Date date = null;
        try {
            date = simpleDateFormat.parse(dataWpsiow.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        date = c.getTime();
        dataWpsiow.setText(simpleDateFormat.format(date));
        odswiezListe();
    }

    private void dodajDatePicker(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.format_daty));
                Calendar ct = Calendar.getInstance();
                try {
                    ct.setTime(simpleDateFormat.parse(textView.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                c.set(ct.get(Calendar.YEAR), ct.get(Calendar.MONTH), ct.get(Calendar.DAY_OF_MONTH), 0, 0);
                Date dataWybrana = new Date(c.getTimeInMillis());

                MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(dataWybrana.getTime())
                        .build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis((Long) selection);
                        textView.setText(calendar.get(Calendar.DAY_OF_MONTH) + getString(R.string.lacznik_daty) + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.lacznik_daty) + calendar.get(Calendar.YEAR));
                        odswiezListe();
                    }
                });
                materialDatePicker.show(getChildFragmentManager(), "tag");

            }
        });
    }

}