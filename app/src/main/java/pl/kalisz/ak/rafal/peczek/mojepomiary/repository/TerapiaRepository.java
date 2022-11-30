package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;

public class TerapiaRepository {

    private CollectionReference mDatabase;
    String userUid;

    public TerapiaRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("Terapie");
        userUid = uid;
    }

    public Query getQuery(){
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public Task<DocumentReference> insert(@NonNull Terapia terapia) {
        Task<DocumentReference> task = mDatabase.add(terapia);
        return task;
    }

    public Task<DocumentSnapshot> getById(@NonNull String idTerapi) {
        Task<DocumentSnapshot> task = mDatabase.document(idTerapi).get();
        return task;
    }

    public Terapia findById(@NonNull String idTerapi) {
        Terapia terapia = null;
        Task<DocumentSnapshot> task = mDatabase.document(idTerapi).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            terapia = task.getResult().toObject(Terapia.class);
        }
        return terapia;
    }

    public void update(@NonNull Terapia terapia) {
        mDatabase.document(terapia.getId()).set(terapia);
    }

    public void delete(@NonNull Terapia terapia) {
        mDatabase.document(terapia.getId()).delete();
    }

}
