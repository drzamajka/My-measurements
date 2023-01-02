package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;

public class TerapiaAdapter extends FirestoreRecyclerAdapter<
        Terapia, TerapiaAdapter.terapiaViewholder> {

    private PomiarRepository pomiarRepository;
    private LekRepository lekRepository;
    private static List<Pomiar> listaPomiarow;
    private static List<Lek> listaLekow;

    public TerapiaAdapter(@NonNull FirestoreRecyclerOptions<Terapia> options) {
        super(options);
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getUid());
        pomiarRepository.getQuery().get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaPomiarow = queryDocumentSnapshots.toObjects(Pomiar.class);
            }
        });
        lekRepository = new LekRepository(FirebaseAuth.getInstance().getUid());
        lekRepository.getQuery().get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaLekow = queryDocumentSnapshots.toObjects(Lek.class);
            }
        });

    }

    @Override
    protected void onBindViewHolder(@NonNull TerapiaAdapter.terapiaViewholder holder, int position, @NonNull Terapia model) {
        if(listaPomiarow.isEmpty())
            listaPomiarow = pomiarRepository.getAll();
        if(listaLekow.isEmpty())
            listaLekow = lekRepository.getAll();
        try{
            String tytul = "";
            ArrayList<String> idsCzynnosci = model.getIdsCzynnosci();
            for(int i=0; i<idsCzynnosci.size();i++){
                JSONObject czynnosc = new JSONObject(idsCzynnosci.get(i));
                String szukaneId = (String) czynnosc.get("id");
                if(czynnosc.get("typ").equals(Pomiar.class.getName())) {
                    Pomiar pomiar = null;
                    for (Pomiar tmp : listaPomiarow) {
                        if (tmp.getId().equals(szukaneId))
                            pomiar = tmp;
                    }
                    if (i > 0 && i < idsCzynnosci.size())
                        tytul += ", ";
                    tytul += pomiar.getNazwa();
                }
                else if(czynnosc.get("typ").equals(Lek.class.getName())) {
                    Lek lek = null;
                    for (Lek tmp : listaLekow) {
                        if (tmp.getId().equals(szukaneId))
                            lek = tmp;
                    }
                    if (i > 0 && i < idsCzynnosci.size())
                        tytul += ", ";
                    tytul += lek.getNazwa();
                }
            }
            holder.obiektNazwa.setText(tytul);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

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
