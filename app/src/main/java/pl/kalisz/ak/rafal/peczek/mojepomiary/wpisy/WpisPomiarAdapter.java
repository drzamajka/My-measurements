package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisy;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary.PomiaryEdytuj;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;

public class WpisPomiarAdapter extends FirebaseRecyclerAdapter<
        WpisPomiar, WpisPomiarAdapter.wpisPomiarViewholder> {

    private PomiarRepository pomiarRepository;
    private JednostkiRepository jednostkiRepository;

public WpisPomiarAdapter(@NonNull FirebaseRecyclerOptions<WpisPomiar> options) {
        super(options);
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getUid());
        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getUid());
        }

@Override
protected void onBindViewHolder(@NonNull WpisPomiarAdapter.wpisPomiarViewholder holder, int position, @NonNull WpisPomiar model) {
        Pomiar pomiar = pomiarRepository.findById(model.getIdPomiar());
        Jednostka jednostka = jednostkiRepository.findById(pomiar.getIdJednostki());
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