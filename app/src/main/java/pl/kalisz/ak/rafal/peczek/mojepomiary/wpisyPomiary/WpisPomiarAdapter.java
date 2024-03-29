package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyPomiary;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.text.SimpleDateFormat;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;

public class WpisPomiarAdapter extends FirestoreRecyclerAdapter<
        WpisPomiar, WpisPomiarAdapter.wpisPomiarViewholder> {

    private final PomiarRepository pomiarRepository;
    private final JednostkiRepository jednostkiRepository;
    static List<Jednostka> listaJednostek;
    static List<Pomiar> listaPomiaruw;

    public WpisPomiarAdapter(@NonNull FirestoreRecyclerOptions<WpisPomiar> options) {
        super(options);
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getUid());
        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getUid());
        jednostkiRepository.getQuery().get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaJednostek = queryDocumentSnapshots.toObjects(Jednostka.class);
            }
        });

        pomiarRepository.getQuery().get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaPomiaruw = queryDocumentSnapshots.toObjects(Pomiar.class);
            }
        });

    }

    @Override
    protected void onBindViewHolder(@NonNull WpisPomiarAdapter.wpisPomiarViewholder holder, int position, @NonNull WpisPomiar model) {
        if (listaJednostek.isEmpty())
            listaJednostek = jednostkiRepository.getAll();
        if (listaPomiaruw.isEmpty())
            listaPomiaruw = pomiarRepository.getAll();

        Pomiar pomiar = null;
        for (Pomiar tmp : listaPomiaruw) {
            if (tmp.getId().equals(model.getIdPomiar()))
                pomiar = tmp;
        }
        holder.obiektNazwa.setText(pomiar.getNazwa());
        if (pomiar.getIdJednostki() != null) {
            Jednostka jednostka = null;
            for (Jednostka tmp : listaJednostek) {
                if (tmp.getId().equals(pomiar.getIdJednostki()))
                    jednostka = tmp;
            }
            if (jednostka.getTypZmiennej() == 0) {
                holder.obiektOpis.setText((((int) Double.parseDouble(model.getWynikPomiary())) + "") + holder.view.getContext().getString(R.string.spacia) + jednostka.getWartosc());
            } else {
                holder.obiektOpis.setText((Double.parseDouble(model.getWynikPomiary())+"") + holder.view.getContext().getString(R.string.spacia) + jednostka.getWartosc());
            }
        } else {
            holder.obiektOpis.setText(model.getWynikPomiary());
        }


        SimpleDateFormat sdf = new SimpleDateFormat(holder.view.getContext().getString(R.string.format_czasu) + holder.view.getContext().getString(R.string.spacia) + holder.view.getContext().getString(R.string.format_daty));
        holder.obiektData.setText(sdf.format(model.getDataWykonania()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.view.getContext(), WpisPomiarEdytuj.class);
                intent.putExtra(WpisPomiarEdytuj.EXTRA_Wpisu_ID, model.getId());
                holder.view.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public WpisPomiarAdapter.wpisPomiarViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_wpis_pomiar_cardview, parent, false);
        return new WpisPomiarAdapter.wpisPomiarViewholder(view);
    }

    class wpisPomiarViewholder
            extends RecyclerView.ViewHolder {
        TextView obiektNazwa, obiektOpis, obiektData;
        View view;

        public wpisPomiarViewholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            obiektNazwa = itemView.findViewById(R.id.nazwa);
            obiektOpis = itemView.findViewById(R.id.opis);
            obiektData = itemView.findViewById(R.id.data);
        }
    }
}