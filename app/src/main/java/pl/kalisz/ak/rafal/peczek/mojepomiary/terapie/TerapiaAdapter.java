package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

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
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;

public class TerapiaAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<Terapia> listaTerapi;

    public TerapiaAdapter(List<Terapia> listaTerapi) {
        this.listaTerapi = listaTerapi;
    }

    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_terapia_cardview, parent, false);
        return new RVAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(RVAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView obiektNazwa = (TextView) cardView.findViewById(R.id.nazwa);
        obiektNazwa.setText(listaTerapi.get(position).getId()+"");
        TextView obiektOpis = (TextView) cardView.findViewById(R.id.opis);
        obiektOpis.setText("rozpoczyna się: "+listaTerapi.get(position).getDataRozpoczecia().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(cardView.getContext(), "kliknieto:"+listaJednostek.get(position).getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(cardView.getContext(), TerapiaEdytuj.class);
                intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (int) listaTerapi.get(position).getId());
                cardView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return listaTerapi.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder( CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }
}
