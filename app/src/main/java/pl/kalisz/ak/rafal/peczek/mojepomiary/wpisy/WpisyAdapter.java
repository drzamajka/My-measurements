package pl.kalisz.ak.rafal.peczek.mojepomiary.wpisy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.RVAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.WpisPomiarPosiadaPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class WpisyAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<WpisPomiarPosiadaPomiar> listaWpisow;
    private UsersRoomDatabase database;

    public WpisyAdapter(List<WpisPomiarPosiadaPomiar> listaWpisow, Context context) {
        this.listaWpisow = listaWpisow;
        database = UsersRoomDatabase.getInstance(context);
    }

    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_wpisy_cardview, parent, false);
        return new RVAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(RVAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        Log.i("Tag-WpisDodaj", listaWpisow.get(position).toString());
        TextView obiektNazwa = (TextView) cardView.findViewById(R.id.nazwa);

        obiektNazwa.setText(listaWpisow.get(position).wpis.getId()+": "+listaWpisow.get(position).pomiary.getNazwa());
        TextView obiektOpis = (TextView) cardView.findViewById(R.id.opis);

        Jednostka jednostka = database.localJednostkaDao().findById(listaWpisow.get(position).pomiary.getIdJednostki());

        obiektOpis.setText("wynik pomiaru: "+listaWpisow.get(position).wpis.getWynikPomiary()+ " "+jednostka.getWartosc());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Date dataWykonania =  listaWpisow.get(position).wpis.getDataWykonania();
        TextView obiektData = (TextView) cardView.findViewById(R.id.data);
        if(new Date().getYear() == dataWykonania.getYear())
            simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM");
        obiektData.setText(simpleDateFormat.format(dataWykonania));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(cardView.getContext(), "kliknieto:"+listaJednostek.get(position).getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(cardView.getContext(), WpisyEdytuj.class);
                intent.putExtra(WpisyEdytuj.EXTRA_Wpisu_ID, (int) listaWpisow.get(position).wpis.getId());
                cardView.getContext().startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return listaWpisow.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder( CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }
}