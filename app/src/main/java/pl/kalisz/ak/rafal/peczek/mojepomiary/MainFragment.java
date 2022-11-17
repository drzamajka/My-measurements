package pl.kalisz.ak.rafal.peczek.mojepomiary;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.EtapTerapiPosiaRelacie;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.TerapiaAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private TextView dataWpsiow;
    private RecyclerView rvPomiary;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private UsersRoomDatabase database;


    public MainFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button wczesniej = (Button) view.findViewById(R.id.button1);
        wczesniej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wczesniej();
            }
        });
        Button pozniej = (Button) view.findViewById(R.id.button2);
        pozniej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dalej();
            }
        });


                    // lista
            dataWpsiow = (TextView) view.findViewById(R.id.Data);
            dodajDatePicker(dataWpsiow);
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dataWpsiow.setText(simpleDateFormat.format(date));



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

        //database = UsersRoomDatabase.getInstance(getContext());

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(database != null) {
            this.odswiezListe();
        }

    }

    private void odswiezListe(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
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


        List<EtapTerapiPosiaRelacie> listaEtapow = database.localEtapTerapaDao().getAllWithRelationsBetwenData(date.getTime(), dateJutro.getTime());

        adapter = new MainEtapAdapter(listaEtapow, getContext());
        rvPomiary.setAdapter(adapter);
    }

    public void dalej() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
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

    private void dodajDatePicker(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = simpleDateFormat.parse(dataWpsiow.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                textView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                odswiezListe();

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

}