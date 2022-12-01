package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisy;


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
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;

public class WpisPomiarAdapter extends FirestoreRecyclerAdapter<
        WpisPomiar, WpisPomiarAdapter.wpisPomiarViewholder> {

    private PomiarRepository pomiarRepository;
    private JednostkiRepository jednostkiRepository;
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
    if(listaJednostek.isEmpty())
        listaJednostek = jednostkiRepository.getAll();
    if(listaPomiaruw.isEmpty())
        listaPomiaruw = pomiarRepository.getAll();

    Pomiar pomiar = null;
    for(Pomiar tmp : listaPomiaruw){
        if(tmp.getId().equals(model.getIdPomiar()))
            pomiar = tmp;
    }
    Jednostka jednostka = null;
    for(Jednostka tmp : listaJednostek){
        if(tmp.getId().equals(pomiar.getIdJednostki()))
            jednostka = tmp;
    }

    holder.obiektNazwa.setText(pomiar.getNazwa());
    holder.obiektOpis.setText(model.getWynikPomiary()+" "+jednostka.getWartosc());
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    holder.obiektData.setText(sdf.format(model.getDataWykonania()));
    holder.view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(holder.view.getContext(), WpisyEdytuj.class);
            intent.putExtra(WpisyEdytuj.EXTRA_Wpisu_ID, (String) model.getId());
            holder.view.getContext().startActivity(intent);
        }
    });
}

@NonNull
@Override
public WpisPomiarAdapter.wpisPomiarViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.activity_wpisy_cardview, parent, false);
        return new WpisPomiarAdapter.wpisPomiarViewholder(view);
        }

class wpisPomiarViewholder
        extends RecyclerView.ViewHolder {
    TextView obiektNazwa, obiektOpis, obiektData;
    View view;
    public wpisPomiarViewholder(@NonNull View itemView)
    {
        super(itemView);
        view = itemView;
        obiektNazwa = (TextView) itemView.findViewById(R.id.nazwa);
        obiektOpis = (TextView) itemView.findViewById(R.id.opis);
        obiektData = (TextView) itemView.findViewById(R.id.data);
    }
}
}