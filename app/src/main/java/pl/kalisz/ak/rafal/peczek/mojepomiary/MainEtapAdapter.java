package pl.kalisz.ak.rafal.peczek.mojepomiary;

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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;
//import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.EtapTerapiActivity;
//import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.TerapiaEdytuj;

public class MainEtapAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<EtapTerapa> listaEtapow;
    private PomiarRepository pomiarRepository;
    private TerapiaRepository terapiaRepository;
    private WpisPomiarRepository wpisPomiarRepository;
    private JednostkiRepository jednostkiRepository;


    public MainEtapAdapter(List<EtapTerapa> listaEtapow, Context context) {
        this.listaEtapow = listaEtapow;
        pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        terapiaRepository = new TerapiaRepository();
        wpisPomiarRepository = new WpisPomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        jednostkiRepository = new JednostkiRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

        ArrayList<String> listaElementow = terapiaRepository.findById(listaEtapow.get(position).getIdTerapi()).getIdsCzynnosci();
        String nazwa = "";
        for (String id: listaElementow) {
            Pomiar pomiar = pomiarRepository.findById(id);
            if(id != listaElementow.get(0))
                nazwa += obiektNazwa.getText()+",\n"+pomiar.getNazwa();
            else
                nazwa = pomiar.getNazwa();
        }
        obiektNazwa.setText(nazwa);

        TextView obiektData = (TextView) cardView.findViewById(R.id.data);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        obiektData.setText(sdf.format(listaEtapow.get(position).getDataZaplanowania()));
        TextView obiektOpis = (TextView) cardView.findViewById(R.id.opis);

        if(listaEtapow.get(position).getDataWykonania() != null) {
            List<WpisPomiar> listaWpisow = wpisPomiarRepository.findByEtapId(listaEtapow.get(position).getId());
            Log.i("Tag-main-RV", "lista wpisów:" + listaWpisow);
            String opis = "";
            int i = 0;
            for (WpisPomiar wpis : listaWpisow) {
                Pomiar pomiar =pomiarRepository.findById(wpis.getIdPomiar());
                if(i!=0)
                    opis += "\n";
                opis += " "+wpis.getWynikPomiary()+" "+jednostkiRepository.findById(pomiar.getIdJednostki()).getWartosc();
                i++;
            }
            obiektOpis.setText(opis);
            //obiektOpis.setText("wykonany: " + sdf.format(listaEtapow.get(position).etapTerapa.getDataWykonania()));
        }
        else{
            obiektOpis.setText( "Jescze nie wykonano etapu");
        }

        Calendar dataZaplanowana = Calendar.getInstance();
        dataZaplanowana.setTime(listaEtapow.get(position).getDataZaplanowania());
        Calendar dataAktualna = Calendar.getInstance();
        String finalNazwa = nazwa;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date za3Godziny = Date.from( LocalDateTime.now().plusHours(3).atZone(ZoneId.systemDefault()).toInstant());
                Date dzienTemu = Date.from( LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
                if(listaEtapow.get(position).getDataZaplanowania().after(dzienTemu) && listaEtapow.get(position).getDataZaplanowania().before(za3Godziny)) {
                    if (listaEtapow.get(position).getDataWykonania() == null) {
                        String[] akcie = {"Wykonaj", "Wyświetl sczegóły terapi"};

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(cardView.getContext());
                        builder.setTitle(finalNazwa);
                        builder.setItems(akcie, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                                switch (which) {
//                                    case 0: {
//                                        Intent intent4 = new Intent(cardView.getContext(), EtapTerapiActivity.class);
//                                        intent4.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, (String) listaEtapow.get(position).getId());
//                                        intent4.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 0);
//                                        cardView.getContext().startActivity(intent4);
//                                        break;
//                                    }
//                                    case 1: {
//                                        Intent intent = new Intent(cardView.getContext(), TerapiaEdytuj.class);
//                                        intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (String) listaEtapow.get(position).getIdTerapi());
//                                        cardView.getContext().startActivity(intent);
//                                        break;
//                                    }
//                                }
                            }
                        });
                        builder.show();
                    } else {
                        String[] akcie = {"Edytuj", "Wyświetl sczegóły terapi"};
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(cardView.getContext());
                        builder.setTitle(finalNazwa);
                        builder.setItems(akcie, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                                switch (which) {
//                                    case 0: {
//                                        Intent intent5 = new Intent(cardView.getContext(), EtapTerapiActivity.class);
//                                        intent5.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, (String) listaEtapow.get(position).getId());
//                                        intent5.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 1);
//                                        cardView.getContext().startActivity(intent5);
//                                        break;
//                                    }
//                                    case 1: {
//                                        Intent intent = new Intent(cardView.getContext(), TerapiaEdytuj.class);
//                                        intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (String) listaEtapow.get(position).getIdTerapi());
//                                        cardView.getContext().startActivity(intent);
//                                        break;
//                                    }
//                                }
                            }
                        });
                        builder.show();
                    }
                }else{
                    String[] akcie = {"Wyświetl sczegóły terapi"};

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(cardView.getContext());
                    builder.setTitle(finalNazwa);
                    builder.setItems(akcie, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

//                            switch (which) {
//                                case 0: {
//                                    Intent intent = new Intent(cardView.getContext(), TerapiaEdytuj.class);
//                                    intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (String) listaEtapow.get(position).getIdTerapi());
//                                    cardView.getContext().startActivity(intent);
//                                    break;
//                                }
//                            }
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