package pl.kalisz.ak.rafal.peczek.mojepomiary;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private String[] nazwa;
    private String[] opis;
    private int[] fotoId;

    public RVAdapter(String[] nazwa, String[] opis, int[] fotoId) {
        this.nazwa = nazwa;
        this.opis = opis;
        this.fotoId = fotoId;
    }


    @Override
    public ViewHolder  onCreateViewHolder( ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_obiekty_uczelni_cardview, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView obiektNazwa = (TextView) cardView.findViewById(R.id.nazwa);
        obiektNazwa.setText(nazwa[position]);
        TextView obiektOpis = (TextView) cardView.findViewById(R.id.opis);
        obiektOpis.setText(opis[position]);
        ImageView obiektFoto = (ImageView) cardView.findViewById(R.id.foto);
        Drawable drawable = ContextCompat.getDrawable(cardView.getContext(), fotoId[position]);
        obiektFoto.setImageDrawable(drawable);
    }



    @Override
    public int getItemCount() {
        return nazwa.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder( CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }
}
