package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;

public class PomiarRepository {

    private DatabaseReference mDatabase;
    String userUid;

    public PomiarRepository(@NonNull String uid) {
        mDatabase = FirebaseDatabase.getInstance("https://mojepomiary-fa7e0-default-rtdb.europe-west1.firebasedatabase.app").getReference("Pomiary");
        userUid = uid;
    }

    public Query getQuery(){
        return mDatabase.orderByChild("idUzytkownika").equalTo(userUid);
    }

    public void insert(@NonNull Pomiar pomiar) {
        pomiar.setId(mDatabase.push().getKey());
        mDatabase.child(pomiar.getId()).setValue(pomiar);
    }

    public Pomiar findById(@NonNull String pomiarId) {
        Pomiar pomiar = null;
        Task<DataSnapshot> task = mDatabase.child(pomiarId).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            pomiar = task.getResult().getValue(Pomiar.class);
        }
        return pomiar;
    }

    public void delete(@NonNull Pomiar pomiar) {
        mDatabase.child(pomiar.getId()).removeValue();
    }

    public void update(@NonNull Pomiar pomiar) {
        mDatabase.child(pomiar.getId()).setValue(pomiar);
    }
}
