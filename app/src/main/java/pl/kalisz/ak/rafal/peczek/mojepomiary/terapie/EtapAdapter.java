package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.JednostkiRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisLekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.WpisPomiarRepository;

public class EtapAdapter extends FirestoreRecyclerAdapter<
        EtapTerapa, EtapAdapter.etapViewholder> {

    EtapTerapiaRepository etapTerapiaRepository;
    static String userUid;
    static PomiarRepository pomiarRepository;
    static TerapiaRepository terapiaRepository;
    static WpisPomiarRepository wpisPomiarRepository;
    static WpisLekRepository wpisLekRepository;
    static JednostkiRepository jednostkiRepository;
    static LekRepository lekRepository;
    static List<Jednostka> listaJednostek;
    static List<Pomiar> listaPomiarow;
    static List<Lek> listaLekow;

    public EtapAdapter(@NonNull FirestoreRecyclerOptions<EtapTerapa> options) {
        super(options);
        etapTerapiaRepository = new EtapTerapiaRepository(FirebaseAuth.getInstance().getUid());
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
                if(!value.getDocumentChanges().isEmpty()){
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
                if(!value.getDocumentChanges().isEmpty()){
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
    protected void onBindViewHolder(@NonNull EtapAdapter.etapViewholder holder, int position, @NonNull EtapTerapa model) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        holder.obiektNazwa.setText(position+1+". "+sdf.format(model.getDataZaplanowania()));


        terapiaRepository.getById(model.getIdTerapi()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Terapia terapia = documentSnapshot.toObject(Terapia.class);
                if (terapia != null) {
                    if(model.getDataWykonania() == null) {
                        holder.obiektOpis.setText( "Jescze nie wykonano etapu");
                    }else {
                        holder.obiektOpis.setText("wykonany: " + sdf.format(model.getDataWykonania()) + "\n");
                        try {
                            ArrayList<String> listaElementow = terapia.getIdsCzynnosci();
                            for (int i = 0; i < listaElementow.size(); i++) {
                                JSONObject czynnosc = new JSONObject(listaElementow.get(i));
                                String szukaneId = (String) czynnosc.get("id");
                                if (i != 0 && i < listaElementow.size()) {
                                    ///////////////tytul += ", ";
                                    if (model.getDataWykonania() != null)
                                        holder.obiektOpis.setText(holder.obiektOpis.getText() + "\n");
                                }

                                if (czynnosc.get("typ").equals(Pomiar.class.getName())) {
                                    Pomiar pomiar = null;
                                    for (Pomiar tmp : listaPomiarow) {
                                        if (tmp.getId().equals(szukaneId))
                                            pomiar = tmp;
                                    }
                                    if (pomiar != null) {
                                        /////////////tytul += pomiar.getNazwa();
                                        if (model.getDataWykonania() != null) {
                                            WpisPomiar wpisPomiar = wpisPomiarRepository.findByEtapIdPomiarId(model.getId(), pomiar.getId());
                                            if (wpisPomiar != null) {
                                                if (pomiar.getIdJednostki() != null) {
                                                    Jednostka jednostka = null;
                                                    for (Jednostka tmp : listaJednostek) {
                                                        if (tmp.getId().equals(pomiar.getIdJednostki()))
                                                            jednostka = tmp;
                                                    }
                                                    holder.obiektOpis.setText(holder.obiektOpis.getText() + pomiar.getNazwa() + ": " + wpisPomiar.getWynikPomiary() + " " + jednostka.getWartosc());
                                                } else {
                                                    holder.obiektOpis.setText(holder.obiektOpis.getText() + pomiar.getNazwa() + ": " + wpisPomiar.getWynikPomiary());
                                                }
                                            }
                                        }
                                    }
                                } else if (czynnosc.get("typ").equals(Lek.class.getName())) {
                                    Lek lek = null;
                                    for (Lek tmp : listaLekow) {
                                        if (tmp.getId().equals(szukaneId))
                                            lek = tmp;
                                    }
                                    if (lek != null) {
                                        //////////////////tytul += lek.getNazwa();
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
                            //////////////////holder.obiektNazwa.setText(tytul);
                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

//        if(model.getDataWykonania() != null)
//            holder.obiektOpis.setText( "wykonany: "+sdf.format(model.getDataWykonania()));
//        else{
//            holder.obiektOpis.setText( "Jescze nie wykonano etapu");
//        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] colors = {"Edytuj", "Usuń"};

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.view.getContext());
                builder.setTitle("Etap "+(position+1)+". "+sdf.format(model.getDataZaplanowania()));
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case 0:{
                                Intent intent5 = new Intent(holder.view.getContext(), EtapTerapiActivity.class);
                                intent5.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, (String) model.getId());
                                intent5.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 1);
                                holder.view.getContext().startActivity(intent5);
                                break;
                            }
                            case 1: {
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(holder.view.getContext());
                                builder.setMessage("Czy na pewno usunąć etap z dnia "+sdf.format(model.getDataZaplanowania()) );
                                builder.setCancelable(false);
                                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        etapTerapiaRepository.delete(model);
                                    }
                                });
                                builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();

                                break;
                            }
                        }
                    }
                });
                builder.show();
            }
        });

    }

    @NonNull
    @Override
    public EtapAdapter.etapViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_terapia_cardview, parent, false);
        return new EtapAdapter.etapViewholder(view);
    }

    class etapViewholder
            extends RecyclerView.ViewHolder {
        TextView obiektNazwa, obiektOpis;
        View view;
        public etapViewholder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
            obiektNazwa = (TextView) itemView.findViewById(R.id.nazwa);
            obiektOpis = (TextView) itemView.findViewById(R.id.opis);
        }
    }
}
