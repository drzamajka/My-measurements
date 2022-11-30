package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;

public class PomiarRepository {

    private CollectionReference mDatabase;
    String userUid;

    public PomiarRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("Pomiary");
        userUid = uid;
    }

    public Query getQuery(){
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public void insert(@NonNull Pomiar pomiar) {
        mDatabase.add(pomiar);
    }

    public Pomiar findById(@NonNull String pomiarId) {
        Pomiar pomiar = null;
        Task<DocumentSnapshot> task = mDatabase.document(pomiarId).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            pomiar = task.getResult().toObject(Pomiar.class);
        }
        return pomiar;
    }

    public void update(@NonNull Pomiar pomiar) {
        mDatabase.document(pomiar.getId()).set(pomiar);
    }

    public void delete(@NonNull Pomiar pomiar) {
        mDatabase.document(pomiar.getId()).delete();
    }


    public Pomiar findByName(String name) {
        Pomiar pomiar = null;
        Task<QuerySnapshot> task = mDatabase.whereEqualTo("nazwa", name).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            pomiar = task.getResult().toObjects(Pomiar.class).get(0);
        }
        return pomiar;
    }
}
