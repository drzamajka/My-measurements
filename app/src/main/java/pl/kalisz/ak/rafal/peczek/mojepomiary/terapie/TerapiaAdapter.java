package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.RVAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary.PomiarAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary.PomiaryEdytuj;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;

public class TerapiaAdapter extends FirestoreRecyclerAdapter<
        Terapia, TerapiaAdapter.terapiaViewholder> {

    public TerapiaAdapter(@NonNull FirestoreRecyclerOptions<Terapia> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TerapiaAdapter.terapiaViewholder holder, int position, @NonNull Terapia model) {
        PomiarRepository pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getUid());
        ArrayList<Pomiar> listaCzynnosci = new ArrayList<>();
        String tytul = "";
        ArrayList<String> idsCzynnosci = model.getIdsCzynnosci();
        for(int i=0; i<idsCzynnosci.size();i++){
            if(i>0 && i<idsCzynnosci.size())
                tytul += ", ";
            tytul += pomiarRepository.findById(idsCzynnosci.get(i)).getNazwa();
        }
        holder.obiektNazwa.setText(tytul);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        holder.obiektOpis.setText("trwa od: "+sdf.format(model.getDataRozpoczecia())+" do: "+sdf.format(model.getDataZakonczenia()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.view.getContext(), TerapiaEdytuj.class);
                intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (String) model.getId());
                holder.view.getContext().startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public TerapiaAdapter.terapiaViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_pomiary_cardview, parent, false);
        return new TerapiaAdapter.terapiaViewholder(view);
    }

    class terapiaViewholder
            extends RecyclerView.ViewHolder {
        TextView obiektNazwa, obiektOpis;
        View view;
        public terapiaViewholder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
            obiektNazwa = (TextView) itemView.findViewById(R.id.nazwa);
            obiektOpis = (TextView) itemView.findViewById(R.id.opis);
        }
    }
}
