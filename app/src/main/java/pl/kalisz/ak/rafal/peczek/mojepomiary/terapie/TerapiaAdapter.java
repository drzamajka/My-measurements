package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.RVAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class TerapiaAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<Terapia> listaTerapi;
    private UsersRoomDatabase database;

    public TerapiaAdapter(List<Terapia> listaTerapi, UsersRoomDatabase usersRoomDatabase) {

        this.listaTerapi = listaTerapi;
        database = usersRoomDatabase;
    }

    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_terapia_cardview, parent, false);
        return new RVAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(RVAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        Terapia terapia = listaTerapi.get(position);
        ArrayList<Pomiar> listaCzynnosci = new ArrayList<>();
        String tytuł = "";
        ArrayList<Integer> idsCzynnosci = terapia.getIdsCzynnosci();
        for(int i=0; i<idsCzynnosci.size();i++){
            if(i>0 && i<idsCzynnosci.size())
                tytuł += ", ";
            tytuł += database.localPomiarDao().findById(idsCzynnosci.get(i)).getNazwa();
        }

        TextView obiektNazwa = (TextView) cardView.findViewById(R.id.nazwa);
        obiektNazwa.setText(terapia.getId()+". "+tytuł);
        TextView obiektOpis = (TextView) cardView.findViewById(R.id.opis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        obiektOpis.setText("trwa od: "+sdf.format(terapia.getDataRozpoczecia())+" do: "+sdf.format(terapia.getDataZakonczenia()));

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
