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
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;

public class WpisLekRepository {

    private CollectionReference mDatabase;
    String userUid;
    WpisLekRepository wpisPomiarRepository;

    public WpisLekRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("WpisyLeki");
        userUid = uid;
    }

    public Query getQuery(){
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public List<WpisLek> findByEtapId(@NonNull String idEtapuTerapi) {
        List<WpisLek> lista = new ArrayList<>();

        Task<QuerySnapshot> task = mDatabase.whereEqualTo("idEtapTerapi", idEtapuTerapi).get();

        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            lista = task.getResult().toObjects(WpisLek.class);
        }
        return lista;
    }

    public Task<QuerySnapshot> getByEtapId(@NonNull String idEtapuTerapi) {
        Task<QuerySnapshot> task = mDatabase.whereEqualTo("idEtapTerapi", idEtapuTerapi).get();
        return task;
    }

    public Query getByLekId(@NonNull String idLeku) {
        Query task = mDatabase.whereEqualTo("idLeku", idLeku).orderBy("dataWykonania", Query.Direction.DESCENDING).limit(1);
        return task;
    }

    public void insert(@NonNull WpisLek wpisLek) {
        mDatabase.add(wpisLek);
    }



    public WpisLek findById(@NonNull String wpisId) {
        WpisLek wpisLek = null;
        Task<DocumentSnapshot> task = mDatabase.document(wpisId).get();

        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            wpisLek = task.getResult().toObject(WpisLek.class);
        }
        return wpisLek;
    }

    public void update(@NonNull WpisLek wpisLek) {
        mDatabase.document(wpisLek.getId()).set(wpisLek);
    }

    public void delete(@NonNull WpisLek wpisLek) {
        mDatabase.document(wpisLek.getId()).delete();
    }


    public List<WpisLek> getAll() {
        List<WpisLek> lista = new ArrayList<>();

        Task<QuerySnapshot> task = mDatabase.get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                WpisLek wpisLek = queryDocumentSnapshot.toObject(WpisLek.class);
                lista.add(wpisLek);
            }
        }

        return lista;
    }
}
