package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;

public class WpisPomiarRepository {

    private DatabaseReference mDatabase;
    String userUid;

    public WpisPomiarRepository(@NonNull String uid) {
        mDatabase = FirebaseDatabase.getInstance("https://mojepomiary-fa7e0-default-rtdb.europe-west1.firebasedatabase.app").getReference("WpisyPomiary");
        userUid = uid;
    }

    public Query getQuery(){
        return mDatabase.orderByChild("idUzytkownika").equalTo(userUid);
    }

    public List<WpisPomiar> findByEtapId(@NonNull String id) {
        List<WpisPomiar> lista = new ArrayList<>();
        return lista;
    }

    public void insert(@NonNull WpisPomiar wpisPomiar) {
        wpisPomiar.setId(mDatabase.push().getKey());
        mDatabase.child(wpisPomiar.getId()).setValue(wpisPomiar);
    }

    public void update(@NonNull WpisPomiar wpisPomiar) {
        mDatabase.child(wpisPomiar.getId()).setValue(wpisPomiar);
    }

    public WpisPomiar findById(@NonNull String wpisId) {
        WpisPomiar wpisPomiar = null;
        Task<DataSnapshot> task = mDatabase.child(wpisId).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            wpisPomiar = task.getResult().getValue(WpisPomiar.class);
        }
        return wpisPomiar;
    }

    public void delete(@NonNull WpisPomiar wpisPomiar) {
        mDatabase.child(wpisPomiar.getId()).removeValue();
    }


}
