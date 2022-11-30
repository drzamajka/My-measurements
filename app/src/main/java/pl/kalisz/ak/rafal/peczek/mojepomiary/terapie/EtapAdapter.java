package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.RVAdapter;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;

public class EtapAdapter extends FirestoreRecyclerAdapter<
        EtapTerapa, EtapAdapter.etapViewholder> {

    EtapTerapiaRepository etapTerapiaRepository;

    public EtapAdapter(@NonNull FirestoreRecyclerOptions<EtapTerapa> options) {
        super(options);
        etapTerapiaRepository = new EtapTerapiaRepository(FirebaseAuth.getInstance().getUid());
    }

    @Override
    protected void onBindViewHolder(@NonNull EtapAdapter.etapViewholder holder, int position, @NonNull EtapTerapa model) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        holder.obiektNazwa.setText(position+1+". "+sdf.format(model.getDataZaplanowania()));
        if(model.getDataWykonania() != null)
            holder.obiektOpis.setText( "wykonany: "+sdf.format(model.getDataWykonania()));
        else{
            holder.obiektOpis.setText( "Jescze nie wykonano etapu");
        }

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
