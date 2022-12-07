package pl.kalisz.ak.rafal.peczek.mojepomiary.leki;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;

public class LekAdapter extends FirestoreRecyclerAdapter<
        Lek, LekAdapter.lekViewholder> {

    WpisLekRepository wpisLekRepository;
    JednostkiRepository jednostkiRepository;
    List<WpisLek> listaWpisowLekow;
    List<Jednostka> listaJednostek;

public LekAdapter(@NonNull FirestoreRecyclerOptions<Lek> options) {
    super(options);
    wpisLekRepository = new WpisLekRepository(FirebaseAuth.getInstance().getUid());
    jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getUid());
    Query lekRepositoryQuery = wpisLekRepository.getQuery();
    lekRepositoryQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(!value.getDocumentChanges().isEmpty()){
                listaWpisowLekow = value.toObjects(WpisLek.class);
            }
        }
    });
    lekRepositoryQuery.get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            listaWpisowLekow = queryDocumentSnapshots.toObjects(WpisLek.class);
        }
    });

    Query jednostkiRepositoryQuery = jednostkiRepository.getQuery();
    jednostkiRepositoryQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(!value.getDocumentChanges().isEmpty()){
                listaJednostek = value.toObjects(Jednostka.class);
            }
        }
    });
    jednostkiRepositoryQuery.get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            listaJednostek = queryDocumentSnapshots.toObjects(Jednostka.class);
        }
    });



}

@Override
protected void onBindViewHolder(@NonNull lekViewholder holder, int position, @NonNull Lek model) {

    holder.obiektNazwa.setText(model.getNazwa());
    holder.obiektOpis.setText("Ładuje zapas leku");
    holder.view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(holder.view.getContext(), LekEdytuj.class);
            intent.putExtra(LekEdytuj.EXTRA_Lek_ID, (String) model.getId());
            holder.view.getContext().startActivity(intent);
        }
    });

    if(listaWpisowLekow.isEmpty())
        listaWpisowLekow = wpisLekRepository.getAll();
    if(listaJednostek.isEmpty())
        listaJednostek = jednostkiRepository.getAll();

    WpisLek najnowszyWpis = null;
    for(WpisLek wpisLek : listaWpisowLekow){
        if(wpisLek.getIdLeku().equals(model.getId())){
            if(najnowszyWpis == null || najnowszyWpis.getDataWykonania().before(wpisLek.getDataWykonania())){
                najnowszyWpis = wpisLek;
            }
        }
    }
    Log.w("TAG", "najnowszyWpis: "+najnowszyWpis);

    if(najnowszyWpis != null) {
        for(Jednostka jednostka : listaJednostek) {
            if(jednostka.getId().equals(model.getIdJednostki())){
                holder.obiektOpis.setText("W zapasie pozostało:"+najnowszyWpis.getPozostalyZapas()+" "+jednostka.getWartosc());
            }
        }
    }
    else {
        holder.obiektOpis.setText("Lek nie posiada jescze zapasu");
    }

}

@NonNull
@Override
public lekViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.activity_leki_cardview, parent, false);
        return new lekViewholder(view);
        }

class lekViewholder
        extends RecyclerView.ViewHolder {
    TextView obiektNazwa, obiektOpis;
    View view;
    public lekViewholder(@NonNull View itemView)
    {
        super(itemView);
        view = itemView;
        obiektNazwa = (TextView) itemView.findViewById(R.id.nazwa);
        obiektOpis = (TextView) itemView.findViewById(R.id.opis);
    }
}
}