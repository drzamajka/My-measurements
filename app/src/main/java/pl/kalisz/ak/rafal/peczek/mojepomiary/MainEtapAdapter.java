package pl.kalisz.ak.rafal.peczek.mojepomiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.EtapTerapiActivity;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.TerapiaEdytuj;

public class MainEtapAdapter extends FirestoreRecyclerAdapter<
        EtapTerapa, MainEtapAdapter.etapViewholder> {

    static String userUid;
    static PomiarRepository pomiarRepository;
    static TerapiaRepository terapiaRepository;
    static WpisPomiarRepository wpisPomiarRepository;
    static WpisLekRepository wpisLekRepository;
    static JednostkiRepository jednostkiRepository;
    private LekRepository lekRepository;
    static List<Jednostka> listaJednostek;
    static List<Pomiar> listaPomiarow;
    static List<Lek> listaLekow;

    public MainEtapAdapter(@NonNull FirestoreRecyclerOptions<EtapTerapa> options) {
        super(options);
        userUid = FirebaseAuth.getInstance().getUid();
        pomiarRepository = new PomiarRepository(userUid);
        terapiaRepository = new TerapiaRepository(userUid);
        wpisPomiarRepository = new WpisPomiarRepository(userUid);
        wpisLekRepository = new WpisLekRepository(userUid);
        jednostkiRepository = new JednostkiRepository(userUid);
        jednostkiRepository.getQuery().get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaJednostek = queryDocumentSnapshots.toObjects(Jednostka.class);
            }
        });
        jednostkiRepository.getQuery().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!= null && !value.getDocumentChanges().isEmpty()){
                    listaJednostek = value.toObjects(Jednostka.class);
                }
            }
        });

        pomiarRepository.getQuery().get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaPomiarow = queryDocumentSnapshots.toObjects(Pomiar.class);
            }
        });
        pomiarRepository.getQuery().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!= null && !value.getDocumentChanges().isEmpty()){
                    listaPomiarow = value.toObjects(Pomiar.class);
                }
            }
        });

        lekRepository = new LekRepository(FirebaseAuth.getInstance().getUid());
        lekRepository.getQuery().get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listaLekow = queryDocumentSnapshots.toObjects(Lek.class);
            }
        });

    }

    @Override
    protected void onBindViewHolder(@NonNull MainEtapAdapter.etapViewholder holder, int position, @NonNull EtapTerapa model) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        holder.obiektData.setText(sdf.format(model.getDataZaplanowania()));
        holder.obiektNazwa.setText("");
        holder.obiektOpis.setText("");
        if(listaJednostek.isEmpty())
            listaJednostek = jednostkiRepository.getAll();
        if(listaPomiarow.isEmpty())
            listaPomiarow = pomiarRepository.getAll();
        if(listaLekow.isEmpty())
            listaLekow = lekRepository.getAll();

        terapiaRepository.getById(model.getIdTerapi()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Terapia terapia = documentSnapshot.toObject(Terapia.class);
                if (terapia != null) {
                    String tytul = "";
                    try{
                        ArrayList<String> listaElementow = terapia.getIdsCzynnosci();
                        for(int i=0; i<listaElementow.size();i++){
                            JSONObject czynnosc = new JSONObject(listaElementow.get(i));
                            String szukaneId = (String) czynnosc.get("id");
                            if (i != 0 && i < listaElementow.size()) {
                                tytul += ", ";
                                if(model.getDataWykonania() != null)
                                    holder.obiektOpis.setText(holder.obiektOpis.getText()+"\n");
                            }

                            if(czynnosc.get("typ").equals(Pomiar.class.getName())) {
                                Pomiar pomiar = null;
                                for (Pomiar tmp : listaPomiarow) {
                                    if (tmp.getId().equals(szukaneId))
                                        pomiar = tmp;
                                }
                                if(pomiar!= null) {
                                    tytul += pomiar.getNazwa();
                                    if (model.getDataWykonania() != null) {
                                        WpisPomiar wpisPomiar = wpisPomiarRepository.findByEtapIdPomiarId(model.getId(), pomiar.getId());
                                        if(wpisPomiar!=null) {
                                            if (pomiar.getIdJednostki() != null) {
                                                Jednostka jednostka = null;
                                                for (Jednostka tmp : listaJednostek) {
                                                    if (tmp.getId().equals(pomiar.getIdJednostki()))
                                                        jednostka = tmp;
                                                }
                                                holder.obiektOpis.setText(holder.obiektOpis.getText() + wpisPomiar.getWynikPomiary() + " " + jednostka.getWartosc());
                                            } else {
                                                holder.obiektOpis.setText(holder.obiektOpis.getText() + wpisPomiar.getWynikPomiary());
                                            }
                                        }
                                    }
                                }
                            }
                            else if(czynnosc.get("typ").equals(Lek.class.getName())) {
                                Lek lek = null;
                                for (Lek tmp : listaLekow) {
                                    if (tmp.getId().equals(szukaneId))
                                        lek = tmp;
                                }
                                if(lek!=null) {
                                    tytul += lek.getNazwa();
                                    if (model.getDataWykonania() != null) {
                                        WpisLek wpisLek = wpisLekRepository.findByEtapIdLekId(model.getId(), lek.getId());
                                        if (wpisLek != null) {
                                            holder.obiektOpis.setText(holder.obiektOpis.getText() + "Pobrano " + lek.getNazwa());
                                        } else {
                                            holder.obiektOpis.setText(holder.obiektOpis.getText() + "Pominiento " + lek.getNazwa());
                                        }
                                    }
                                }
                            }
                        }
                        holder.obiektNazwa.setText(tytul);
                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }
                    if(model.getDataWykonania() == null) {
                        holder.obiektOpis.setText( "Jescze nie wykonano etapu");
                    }

                    String finalNazwa = tytul;
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Date za3Godziny = Date.from(LocalDateTime.now().plusHours(3).atZone(ZoneId.systemDefault()).toInstant());
                            Date dzienTemu = Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
                            if (model.getDataZaplanowania().after(dzienTemu) && model.getDataZaplanowania().before(za3Godziny)) {
                                if (model.getDataWykonania() == null) {
                                    String[] akcie = {"Wykonaj", "Wyświetl sczegóły terapi"};

                                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(holder.view.getContext());
                                    builder.setTitle(finalNazwa);
                                    builder.setItems(akcie, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            switch (which) {
                                                case 0: {
                                                    Intent intent4 = new Intent(holder.view.getContext(), EtapTerapiActivity.class);
                                                    intent4.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, (String) model.getId());
                                                    intent4.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 0);
                                                    holder.view.getContext().startActivity(intent4);
                                                    break;
                                                }
                                                case 1: {
                                                    Intent intent = new Intent(holder.view.getContext(), TerapiaEdytuj.class);
                                                    intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (String) model.getIdTerapi());
                                                    holder.view.getContext().startActivity(intent);
                                                    break;
                                                }
                                            }
                                        }
                                    });
                                    builder.show();
                                } else {
                                    String[] akcie = {"Edytuj", "Wyświetl sczegóły terapi"};
                                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(holder.view.getContext());
                                    builder.setTitle(finalNazwa);
                                    builder.setItems(akcie, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            switch (which) {
                                                case 0: {
                                                    Intent intent5 = new Intent(holder.view.getContext(), EtapTerapiActivity.class);
                                                    intent5.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, (String) model.getId());
                                                    intent5.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 1);
                                                    holder.view.getContext().startActivity(intent5);
                                                    break;
                                                }
                                                case 1: {
                                                    Intent intent = new Intent(holder.view.getContext(), TerapiaEdytuj.class);
                                                    intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (String) model.getIdTerapi());
                                                    holder.view.getContext().startActivity(intent);
                                                    break;
                                                }
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            } else {
                                String[] akcie = {"Wyświetl sczegóły terapi"};

                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(holder.view.getContext());
                                builder.setTitle(finalNazwa);
                                builder.setItems(akcie, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case 0: {
                                                Intent intent = new Intent(holder.view.getContext(), TerapiaEdytuj.class);
                                                intent.putExtra(TerapiaEdytuj.EXTRA_Terapia_ID, (String) model.getIdTerapi());
                                                holder.view.getContext().startActivity(intent);
                                                break;
                                            }
                                        }
                                    }
                                });
                                builder.show();
                            }
                        }
                    });
                }
            }
        });



//        if(model.getDataWykonania() != null) {
//            wpisPomiarRepository.getByEtapId(model.getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                @Override
//                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                    List<WpisPomiar> listaWpisow = queryDocumentSnapshots.toObjects(WpisPomiar.class);
//                    String opis = "";
//                    int i = 0;
//                    for (WpisPomiar wpis : listaWpisow) {
//                        Pomiar pomiar = null;
//                        for(Pomiar tmp : listaPomiarow){
//                            if(tmp.getId().equals(wpis.getIdPomiar()))
//                                pomiar = tmp;
//                        }
//                        if(i!=0)
//                            opis += "\n";
//                        if(pomiar.getIdJednostki()!=null) {
//                            Jednostka jednostka = null;
//                            for (Jednostka tmp : listaJednostek) {
//                                if (tmp.getId().equals(pomiar.getIdJednostki()))
//                                    jednostka = tmp;
//                            }
//                            opis += wpis.getWynikPomiary() + " " + jednostka.getWartosc();
//                        }
//                        else{
//                            opis += wpis.getWynikPomiary();
//                        }
//                        i++;
//                    }
//                    holder.obiektOpis.setMinLines(i);
//                    holder.obiektOpis.setText(opis);
//                }
//            });
//        }
//        else{
//            holder.obiektOpis.setText( "Jescze nie wykonano etapu");
//        }


    }

    @NonNull
    @Override
    public MainEtapAdapter.etapViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_wpis_pomiar_cardview, parent, false);
        return new MainEtapAdapter.etapViewholder(view);
    }

    class etapViewholder
            extends RecyclerView.ViewHolder {
        TextView obiektNazwa, obiektOpis, obiektData;
        View view;

        public etapViewholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            obiektNazwa = (TextView) itemView.findViewById(R.id.nazwa);
            obiektOpis = (TextView) itemView.findViewById(R.id.opis);
            obiektData = (TextView) itemView.findViewById(R.id.data);
        }
    }
}