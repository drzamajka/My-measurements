package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;

public class PomiarAdapter extends FirebaseRecyclerAdapter<
        Pomiar, PomiarAdapter.pomiarViewholder> {

public PomiarAdapter(@NonNull FirebaseRecyclerOptions<Pomiar> options) {
        super(options);
        }

@Override
protected void onBindViewHolder(@NonNull PomiarAdapter.pomiarViewholder holder, int position, @NonNull Pomiar model) {

        holder.obiektNazwa.setText(model.getNazwa());
        holder.obiektOpis.setText(model.getNotatka());
        holder.view.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.view.getContext(), PomiaryEdytuj.class);
                intent.putExtra(PomiaryEdytuj.EXTRA_Pomiar_ID, (String) model.getId());
                holder.view.getContext().startActivity(intent);
            }
        });

        }

@NonNull
@Override
public PomiarAdapter.pomiarViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.activity_jednostki_cardview, parent, false);
        return new PomiarAdapter.pomiarViewholder(view);
        }

class pomiarViewholder
        extends RecyclerView.ViewHolder {
    TextView obiektNazwa, obiektOpis;
    View view;
    public pomiarViewholder(@NonNull View itemView)
    {
        super(itemView);
        view = itemView;
        obiektNazwa = (TextView) itemView.findViewById(R.id.nazwa);
        obiektOpis = (TextView) itemView.findViewById(R.id.opis);
    }
}
}