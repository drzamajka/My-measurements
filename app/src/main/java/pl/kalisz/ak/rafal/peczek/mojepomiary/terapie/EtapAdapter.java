package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import java.util.Date;
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
                if (value != null && !value.getDocumentChanges().isEmpty()) {
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
                if (value != null && !value.getDocumentChanges().isEmpty()) {
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
        SimpleDateFormat sdf = new SimpleDateFormat(holder.view.getContext().getString(R.string.format_czasu) + holder.view.getContext().getString(R.string.spacia) + holder.view.getContext().getString(R.string.format_daty));
        holder.obiektNazwa.setText(position + 1 + ". " + sdf.format(model.getDataZaplanowania()));


        terapiaRepository.getById(model.getIdTerapi()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Terapia terapia = documentSnapshot.toObject(Terapia.class);
                if (terapia != null) {
                    if (model.getDataWykonania() == null) {
                        if (model.getDataZaplanowania().before(new Date())) {
                            holder.obiektOpis.setText(R.string.pominiento_etap);
                            holder.dotIndicator.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                        } else {
                            holder.obiektOpis.setText(R.string.etap_nie_wykonany);
                            holder.dotIndicator.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                        }
                    } else {
                        holder.obiektOpis.setText(holder.view.getContext().getString(R.string.wykonany_) + sdf.format(model.getDataWykonania()) + "\n");
                        try {
                            holder.dotIndicator.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                            ArrayList<String> listaElementow = terapia.getIdsCzynnosci();
                            for (int i = 0; i < listaElementow.size(); i++) {
                                JSONObject czynnosc = new JSONObject(listaElementow.get(i));
                                String szukaneId = (String) czynnosc.get("id");
                                if (i != 0 && i < listaElementow.size()) {
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
                                        if (model.getDataWykonania() != null) {
                                            WpisPomiar wpisPomiar = wpisPomiarRepository.findByEtapIdPomiarId(model.getId(), pomiar.getId());
                                            if (wpisPomiar != null) {
                                                if (pomiar.getIdJednostki() != null) {
                                                    Jednostka jednostka = null;
                                                    for (Jednostka tmp : listaJednostek) {
                                                        if (tmp.getId().equals(pomiar.getIdJednostki()))
                                                            jednostka = tmp;
                                                    }
                                                    holder.obiektOpis.setText(holder.obiektOpis.getText() + pomiar.getNazwa() + ": " + wpisPomiar.getWynikPomiary() +holder.view.getContext().getString(R.string.spacia) + jednostka.getWartosc());
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
                                        if (model.getDataWykonania() != null) {
                                            WpisLek wpisLek = wpisLekRepository.findByEtapIdLekId(model.getId(), lek.getId());
                                            if (wpisLek != null) {
                                                holder.obiektOpis.setText(holder.obiektOpis.getText() + holder.view.getContext().getString(R.string.pobrano_) + lek.getNazwa());
                                            } else {
                                                holder.obiektOpis.setText(holder.obiektOpis.getText() + holder.view.getContext().getString(R.string.pominiento_) + lek.getNazwa());
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                        holder.obiektOpis.setText(holder.obiektOpis.getText() + "\n" + holder.view.getContext().getString(R.string.notatka) + ": " + model.getNotatka());
                    }
                }
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] colors = {holder.view.getContext().getString(R.string.edytuj), holder.view.getContext().getString(R.string.usu)};

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.view.getContext());
                builder.setTitle(holder.view.getContext().getString(R.string.etap_) + (position + 1) + ". " + sdf.format(model.getDataZaplanowania()));
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: {
                                Intent intent5 = new Intent(holder.view.getContext(), EtapTerapiActivity.class);
                                intent5.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, model.getId());
                                intent5.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 1);
                                holder.view.getContext().startActivity(intent5);
                                break;
                            }
                            case 1: {
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(holder.view.getContext());
                                builder.setMessage(holder.view.getContext().getString(R.string.czy_na_pewno_usun__etap_z) + sdf.format(model.getDataZaplanowania()));
                                builder.setCancelable(false);
                                builder.setPositiveButton(holder.view.getContext().getString(R.string.tak), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        etapTerapiaRepository.delete(model);
                                    }
                                });
                                builder.setNegativeButton(holder.view.getContext().getString(R.string.nie), new DialogInterface.OnClickListener() {
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
        LinearLayout dotIndicator;
        View view;

        public etapViewholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            obiektNazwa = itemView.findViewById(R.id.nazwa);
            obiektOpis = itemView.findViewById(R.id.opis);
            dotIndicator = itemView.findViewById(R.id.dotIndicator);
        }
    }
}
