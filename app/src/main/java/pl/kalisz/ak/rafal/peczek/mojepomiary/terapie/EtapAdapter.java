package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.RVAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class EtapAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<EtapTerapa> listaEtapow;
    private UsersRoomDatabase database;

    public EtapAdapter(List<EtapTerapa> listaEtapow, UsersRoomDatabase usersRoomDatabase) {

        this.listaEtapow = listaEtapow;
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
        EtapTerapa etapTerapa = listaEtapow.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        TextView obiektNazwa = (TextView) cardView.findViewById(R.id.nazwa);
        obiektNazwa.setText(position+1+". "+sdf.format(etapTerapa.getDataZaplanowania()));
        TextView obiektOpis = (TextView) cardView.findViewById(R.id.opis);
        if(etapTerapa.getDataWykonania() != null)
            obiektOpis.setText( "wykonany: "+sdf.format(etapTerapa.getDataWykonania()));
        else{
            obiektOpis.setText( "Jescze nie wykonano etapu");
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(cardView.getContext(), "kliknieto:"+listaJednostek.get(position).getId(), Toast.LENGTH_SHORT).show();
                String[] colors = {"Edytuj", "Usuń"};

                AlertDialog.Builder builder = new AlertDialog.Builder(cardView.getContext());
                builder.setTitle("Etap "+(position+1)+". "+sdf.format(etapTerapa.getDataZaplanowania()));
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case 0:{
                                if(etapTerapa.getDataWykonania()==null){
                                    Intent intent1 = new Intent( cardView.getContext(), EtapTerapiWykonaj.class);
                                    cardView.getContext().startActivity(intent1);
                                }
                                Intent intent = new Intent(cardView.getContext(), EtapTerapiEdytuj.class);
                                cardView.getContext().startActivity(intent);
                                break;
                            }
                            case 1: {
                                // usuń
                                break;
                            }
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() { return listaEtapow.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder( CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }
}
