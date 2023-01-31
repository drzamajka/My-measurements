package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisyLeki;


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
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;

public class WpisLekAdapter extends FirestoreRecyclerAdapter<
        WpisLek, WpisLekAdapter.wpisLekViewholder> {

    private final LekRepository lekRepository;
    private final JednostkiRepository jednostkiRepository;
    static List<Jednostka> listaJednostek;
    static List<Lek> listaLekow;

    public WpisLekAdapter(@NonNull FirestoreRecyclerOptions<WpisLek> options) {
        super(options);
        lekRepository = new LekRepository(FirebaseAuth.getInstance().getUid());
        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getUid());
        jednostkiRepository.getQuery().get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaJednostek = queryDocumentSnapshots.toObjects(Jednostka.class);
            }
        });

        lekRepository.getQuery().get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaLekow = queryDocumentSnapshots.toObjects(Lek.class);
            }
        });

    }

    @Override
    protected void onBindViewHolder(@NonNull wpisLekViewholder holder, int position, @NonNull WpisLek model) {
        if (listaJednostek.isEmpty())
            listaJednostek = jednostkiRepository.getAll();
        if (listaLekow.isEmpty())
            listaLekow = lekRepository.getAll();

        Lek lek = null;
        for (Lek tmp : listaLekow) {
            if (tmp.getId().equals(model.getIdLeku()))
                lek = tmp;
        }
        Jednostka jednostka = null;
        for (Jednostka tmp : listaJednostek) {
            if (tmp.getId().equals(lek.getIdJednostki()))
                jednostka = tmp;
        }

        holder.obiektNazwa.setText(lek.getNazwa());

        String sumaObrotu = model.getSumaObrotu();

        if (Double.parseDouble(model.getSumaObrotu()) > 0) {
            if(jednostka.getTypZmiennej() == 0)
                sumaObrotu = ((int)Double.parseDouble(sumaObrotu))+"";
            holder.obiektOpis.setText(holder.view.getContext().getString(R.string.uzupe_niono_o) + sumaObrotu + holder.view.getContext().getString(R.string.spacia) + jednostka.getWartosc() + holder.view.getContext().getString(R.string._leku));
        }
        else {
            if(jednostka.getTypZmiennej() == 0)
                sumaObrotu = ((int)Double.parseDouble(sumaObrotu)*-1)+"";
            else
                sumaObrotu = ((Double)Double.parseDouble(sumaObrotu)*-1)+"";
            holder.obiektOpis.setText(holder.view.getContext().getString(R.string.pobrano_) + sumaObrotu + holder.view.getContext().getString(R.string.spacia) + jednostka.getWartosc() + holder.view.getContext().getString(R.string._leku));
        }

        SimpleDateFormat sdf = new SimpleDateFormat(holder.view.getContext().getString(R.string.format_czasu) + holder.view.getContext().getString(R.string.spacia) + holder.view.getContext().getString(R.string.format_daty));
        holder.obiektData.setText(sdf.format(model.getDataWykonania()));
        holder.view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(holder.view.getContext(), WpisLekEdytuj.class);
            intent.putExtra(WpisLekEdytuj.EXTRA_Wpisu_ID, (String) model.getId());
            holder.view.getContext().startActivity(intent);
        }
    });
    }

    @NonNull
    @Override
    public wpisLekViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_wpis_lek_cardview, parent, false);
        return new wpisLekViewholder(view);
    }

    class wpisLekViewholder
            extends RecyclerView.ViewHolder {
        TextView obiektNazwa, obiektOpis, obiektData;
        View view;

        public wpisLekViewholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            obiektNazwa = itemView.findViewById(R.id.nazwa);
            obiektOpis = itemView.findViewById(R.id.opis);
            obiektData = itemView.findViewById(R.id.data);
        }
    }
}