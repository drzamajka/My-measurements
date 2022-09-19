package pl.kalisz.ak.rafal.peczek.mojepomiary.pomiary;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.RVAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;

public class PomiarAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<Pomiar> listaPomiarow;

    public PomiarAdapter(List<Pomiar> listaPomiarow) {
        this.listaPomiarow = listaPomiarow;
    }

    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_pomiary_cardview, parent, false);
        return new RVAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(RVAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView obiektNazwa = (TextView) cardView.findViewById(R.id.nazwa);
        obiektNazwa.setText(listaPomiarow.get(position).getId()+": "+ listaPomiarow.get(position).getNazwa());
        TextView obiektOpis = (TextView) cardView.findViewById(R.id.opis);
        obiektOpis.setText(listaPomiarow.get(position).getNotatka());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(cardView.getContext(), "kliknieto:"+listaJednostek.get(position).getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(cardView.getContext(), PomiaryEdytuj.class);
                intent.putExtra(PomiaryEdytuj.EXTRA_Pomiar_ID, (int) listaPomiarow.get(position).getId());
                cardView.getContext().startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return listaPomiarow.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder( CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }
}