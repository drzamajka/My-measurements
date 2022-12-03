package pl.kalisz.ak.rafal.peczek.mojepomiary.leki;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;

public class LekAdapter extends FirestoreRecyclerAdapter<
        Lek, LekAdapter.lekViewholder> {

public LekAdapter(@NonNull FirestoreRecyclerOptions<Lek> options) {
        super(options);
        }

@Override
protected void onBindViewHolder(@NonNull lekViewholder holder, int position, @NonNull Lek model) {

        holder.obiektNazwa.setText(model.getNazwa());
        holder.obiektOpis.setText(model.getNotatka());
        holder.view.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.view.getContext(), LekEdytuj.class);
                intent.putExtra(LekEdytuj.EXTRA_Lek_ID, (String) model.getId());
                holder.view.getContext().startActivity(intent);
            }
        });

        }

@NonNull
@Override
public lekViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.activity_leki_cardview, parent, false);
        return new lekViewholder(view);
        }

class lekViewholder
        extends RecyclerView.ViewHolder {
    TextView obiektNazwa, obiektOpis;
    View view;
    public lekViewholder(@NonNull View itemView)
    {
        super(itemView);
        view = itemView;
        obiektNazwa = (TextView) itemView.findViewById(R.id.nazwa);
        obiektOpis = (TextView) itemView.findViewById(R.id.opis);
    }
}
}