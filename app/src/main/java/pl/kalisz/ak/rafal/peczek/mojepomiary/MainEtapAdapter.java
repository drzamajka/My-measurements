package pl.kalisz.ak.rafal.peczek.mojepomiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.EtapTerapiPosiaRelacie;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.PomiarPosiadRelacie;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.EtapTerapiActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.TerapiaEdytuj;

public class MainEtapAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<EtapTerapiPosiaRelacie> listaEtapow;
    private UsersRoomDatabase database;

    public MainEtapAdapter(List<EtapTerapiPosiaRelacie> listaEtapow, Context context) {
        this.listaEtapow = listaEtapow;
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
        TextView obiektNazwa = (TextView) cardView.findViewById(R.id.nazwa);

        ArrayList<Integer> listaElementow = listaEtapow.get(position).terapia.getIdsCzynnosci();
        String nazwa = "";
        for (int id: listaElementow) {
            Pomiar pomiar = database.localPomiarDao().findById(id);
            if(id != listaElementow.get(0))
                nazwa += obiektNazwa.getText()+",\n"+pomiar.getNazwa();
            else
                nazwa = pomiar.getNazwa();
        }
        obiektNazwa.setText(nazwa);

        TextView obiektData = (TextView) cardView.findViewById(R.id.data);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        obiektData.setText(sdf.format(listaEtapow.get(position).etapTerapa.getDataZaplanowania()));
        TextView obiektOpis = (TextView) cardView.findViewById(R.id.opis);

        if(listaEtapow.get(position).etapTerapa.getDataWykonania() != null) {
            List<WpisPomiar> listaWpisow = listaEtapow.get(position).wpisy;
            Log.i("Tag-main-RV", "lista wpisów:" + listaWpisow);
            String opis = "";
            int i = 0;
            for (WpisPomiar wpis : listaWpisow) {
                PomiarPosiadRelacie pomiar = database.localPomiarDao().findPomiarPosiadRelacieById(wpis.getIdPomiar());
                if(i!=0)
                    opis += "\n";
                opis += " "+wpis.getWynikPomiary()+" "+pomiar.jednostka.getWartosc();
                i++;
            }
            obiektOpis.setText(opis);
            //obiektOpis.setText("wykonany: " + sdf.format(listaEtapow.get(position).etapTerapa.getDataWykonania()));
        }
        else{
            obiektOpis.setText( "Jescze nie wykonano etapu");
        }

        Calendar dataZaplanowana = Calendar.getInstance();
        dataZaplanowana.setTime(listaEtapow.get(position).etapTerapa.getDataZaplanowania());
        Calendar dataAktualna = Calendar.getInstance();
        String finalNazwa = nazwa;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(cardView.getContext(), "kliknieto:"+listaJednostek.get(position).getId(), Toast.LENGTH_SHORT).show();

                if(listaEtapow.get(position).etapTerapa.getDataWykonania()==null) {
                    String[] akcie = {"Wykonaj", "Wyświetl sczegóły terapi"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(cardView.getContext());
                    builder.setTitle(finalNazwa);
                    builder.setItems(akcie, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case 0: {
                                    Intent intent4 = new Intent(cardView.getContext(), EtapTerapiActivity.class);
                                    intent4.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, (int) listaEtapow.get(position).etapTerapa.getId());
                                    intent4.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 0);
                                    cardView.getContext().startActivity(intent4);
                                    break;
                                }
                                case 1: {
                                    Intent intent = new Intent(cardView.getContext(), TerapiaEdytuj.class);
                                    intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (int) listaEtapow.get(position).terapia.getId());
                                    cardView.getContext().startActivity(intent);
                                    break;
                                }
                            }
                        }
                    });
                    builder.show();
                }
                else {
                    String[] akcie = {"Edytuj", "Wyświetl sczegóły terapi"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(cardView.getContext());
                    builder.setTitle(finalNazwa);
                    builder.setItems(akcie, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case 0: {
                                    Intent intent5 = new Intent(cardView.getContext(), EtapTerapiActivity.class);
                                    intent5.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, (int) listaEtapow.get(position).etapTerapa.getId());
                                    intent5.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 1);
                                    cardView.getContext().startActivity(intent5);
                                    break;
                                }
                                case 1: {
                                    Intent intent = new Intent(cardView.getContext(), TerapiaEdytuj.class);
                                    intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (int) listaEtapow.get(position).terapia.getId());
                                    cardView.getContext().startActivity(intent);
                                    break;
                                }
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        if(dataZaplanowana.get(Calendar.DATE)==dataAktualna.get(Calendar.DATE)){

        }
    }



    @Override
    public int getItemCount() {
        return listaEtapow.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder( CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }
}