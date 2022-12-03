package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;

public class LekRepository {

    private CollectionReference mDatabase;
    String userUid;

    public LekRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("Leki");
        userUid = uid;
    }

    public Query getQuery(){
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public void insert(@NonNull Lek lek) {
        mDatabase.add(lek);
    }

    public Lek findById(@NonNull String lekId) {
        Lek lek = null;
        Task<DocumentSnapshot> task = mDatabase.document(lekId).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            lek = task.getResult().toObject(Lek.class);
        }
        return lek;
    }

    public void update(@NonNull Lek lek) {
        mDatabase.document(lek.getId()).set(lek);
    }

    public void delete(@NonNull Lek lek) {
        mDatabase.document(lek.getId()).delete();
    }


    public Lek findByName(String name) {
        Lek lek = null;
        Task<QuerySnapshot> task = mDatabase.whereEqualTo("nazwa", name).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            lek = task.getResult().toObjects(Lek.class).get(0);
        }
        return lek;
    }

    public List<Lek> getAll() {
        List<Lek> lista = new ArrayList<>();

        Task<QuerySnapshot> task = mDatabase.get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            lista = task.getResult().toObjects(Lek.class);
        }

        return lista;
    }
}
