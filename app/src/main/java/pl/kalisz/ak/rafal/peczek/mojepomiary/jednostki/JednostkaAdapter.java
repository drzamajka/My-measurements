package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;


public class JednostkaAdapter extends FirestoreRecyclerAdapter<
        Jednostka, JednostkaAdapter.jednostkaViewholder> {

    public JednostkaAdapter(@NonNull FirestoreRecyclerOptions<Jednostka> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull JednostkaAdapter.jednostkaViewholder holder, int position, @NonNull Jednostka model) {
        holder.obiektNazwa.setText(model.getNazwa());
        holder.obiektOpis.setText(model.getWartosc());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.view.getContext(), JednostkiEdytuj.class);
                intent.putExtra(JednostkiEdytuj.EXTRA_JEDNOSTKA_ID, model.getId());
                holder.view.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public JednostkaAdapter.jednostkaViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_jednostki_cardview, parent, false);
        return new JednostkaAdapter.jednostkaViewholder(view);
    }

    class jednostkaViewholder
            extends RecyclerView.ViewHolder {
        TextView obiektNazwa, obiektOpis;
        View view;

        public jednostkaViewholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            obiektNazwa = itemView.findViewById(R.id.nazwa);
            obiektOpis = itemView.findViewById(R.id.opis);
        }
    }
}
