package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;

public class PomiarAdapter extends FirestoreRecyclerAdapter<
        Pomiar, PomiarAdapter.pomiarViewholder> {

    public PomiarAdapter(@NonNull FirestoreRecyclerOptions<Pomiar> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PomiarAdapter.pomiarViewholder holder, int position, @NonNull Pomiar model) {

        holder.obiektNazwa.setText(model.getNazwa());
        holder.obiektOpis.setText(model.getNotatka());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.view.getContext(), PomiarEdytuj.class);
                intent.putExtra(PomiarEdytuj.EXTRA_Pomiar_ID, model.getId());
                holder.view.getContext().startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public PomiarAdapter.pomiarViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_pomiary_cardview, parent, false);
        return new PomiarAdapter.pomiarViewholder(view);
    }

    class pomiarViewholder
            extends RecyclerView.ViewHolder {
        TextView obiektNazwa, obiektOpis;
        View view;

        public pomiarViewholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            obiektNazwa = itemView.findViewById(R.id.nazwa);
            obiektOpis = itemView.findViewById(R.id.opis);
        }
    }
}