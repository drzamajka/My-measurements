package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;

public class WpisPomiarRepository {

    private final CollectionReference mDatabase;
    String userUid;

    public WpisPomiarRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("WpisyPomiary");
        userUid = uid;
    }

    public Query getQuery() {
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public List<WpisPomiar> findByEtapId(@NonNull String idEtapuTerapi) {
        List<WpisPomiar> lista = new ArrayList<>();

        Task<QuerySnapshot> task = mDatabase.whereEqualTo("idEtapTerapi", idEtapuTerapi).get();

        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            lista = task.getResult().toObjects(WpisPomiar.class);
        }
        return lista;
    }

    public WpisPomiar findByEtapIdPomiarId(@NonNull String idEtapuTerapi, @NonNull String idPomiaru) {
        Task<QuerySnapshot> task = mDatabase.whereEqualTo("idEtapTerapi", idEtapuTerapi).whereEqualTo("idPomiar", idPomiaru).get(Source.DEFAULT);

        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            for (WpisPomiar wpisPomiar : task.getResult().toObjects(WpisPomiar.class)) {
                if (wpisPomiar.getIdPomiar().equals(idPomiaru))
                    return wpisPomiar;
            }
        }
        return null;
    }

    public Query getQueryByEtapId(@NonNull String idEtapuTerapi) {
        Query query = mDatabase.whereEqualTo("idEtapTerapi", idEtapuTerapi);
        return query;
    }

    public Query getQueryByPomiarId(@NonNull String idPomiar, int limit) {
        Query query = mDatabase.whereEqualTo("idPomiar", idPomiar).orderBy("dataWykonania", Query.Direction.DESCENDING).limit(limit);
        return query;
    }

    public Query getQueryByPomiarId(@NonNull String idPomiar) {
        Query query = mDatabase.whereEqualTo("idPomiar", idPomiar).orderBy("dataWykonania", Query.Direction.DESCENDING);
        return query;
    }


    public void insert(@NonNull WpisPomiar wpisPomiar) {
        mDatabase.add(wpisPomiar);
    }


    public WpisPomiar findById(@NonNull String wpisId) {
        WpisPomiar wpisPomiar = null;
        Task<DocumentSnapshot> task = mDatabase.document(wpisId).get();

        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            wpisPomiar = task.getResult().toObject(WpisPomiar.class);
        }
        return wpisPomiar;
    }

    public void update(@NonNull WpisPomiar wpisPomiar) {
        mDatabase.document(wpisPomiar.getId()).set(wpisPomiar);
    }

    public void delete(@NonNull WpisPomiar wpisPomiar) {
        mDatabase.document(wpisPomiar.getId()).delete();
    }


}
