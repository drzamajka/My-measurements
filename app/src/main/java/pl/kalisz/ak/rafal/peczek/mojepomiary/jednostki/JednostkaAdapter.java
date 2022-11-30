package pl.kalisz.ak.rafal.peczek.mojepomiary.jednostki;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class JednostkaAdapter extends FirebaseRecyclerAdapter<
        Jednostka, JednostkaAdapter.jednostkaViewholder> {

    public JednostkaAdapter(@NonNull FirebaseRecyclerOptions<Jednostka> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull JednostkaAdapter.jednostkaViewholder holder, int position, @NonNull Jednostka model) {
        Log.i("Tag", "jednostka:" + model);
        holder.obiektNazwa.setText(model.getNazwa());
        holder.obiektOpis.setText(model.getWartosc());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(cardView.getContext(), "kliknieto:"+listaJednostek.get(position).getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(holder.view.getContext(), JednostkiEdytuj.class);
                intent.putExtra(JednostkiEdytuj.EXTRA_JEDNOSTKA_ID, (String) model.getId());
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
        public jednostkaViewholder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
            obiektNazwa = (TextView) itemView.findViewById(R.id.nazwa);
            obiektOpis = (TextView) itemView.findViewById(R.id.opis);
        }
    }
}
