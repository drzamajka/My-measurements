package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;

public class WpisLekRepository {

    private final CollectionReference mDatabase;
    String userUid;
    WpisLekRepository wpisPomiarRepository;

    public WpisLekRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("WpisyLeki");
        userUid = uid;
    }

    public Query getQuery() {
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public List<WpisLek> findByEtapId(@NonNull String idEtapuTerapi) {
        List<WpisLek> lista = new ArrayList<>();

        Task<QuerySnapshot> task = mDatabase.whereEqualTo("idEtapTerapi", idEtapuTerapi).get();

        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            lista = task.getResult().toObjects(WpisLek.class);
        }
        return lista;
    }

    public WpisLek findByEtapIdLekId(@NonNull String idEtapuTerapi, @NonNull String idLeku) {


        Task<QuerySnapshot> task = mDatabase.whereEqualTo("idEtapTerapi", idEtapuTerapi).get(Source.DEFAULT);


        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            for (WpisLek wpisLek : task.getResult().toObjects(WpisLek.class)) {
                if (wpisLek.getIdLeku().equals(idLeku))
                    return wpisLek;
            }
        }
        Log.w("TAG-repo", "idEtapuTerapi: " + idEtapuTerapi + "  idLeku: " + idLeku);
        Log.w("TAG-repo", "lista findByEtapIdLekId: " + task.isSuccessful());
        return null;
    }

    public Task<QuerySnapshot> getByEtapId(@NonNull String idEtapuTerapi) {
        Task<QuerySnapshot> task = mDatabase.whereEqualTo("idEtapTerapi", idEtapuTerapi).get();
        return task;
    }

    public Query getQueryByLekId(@NonNull String idLeku, int limit) {
        Query task = mDatabase.whereEqualTo("idLeku", idLeku).orderBy("dataWykonania", Query.Direction.DESCENDING).limit(limit);
        return task;
    }

    public Query getQueryByLekId(@NonNull String idLeku) {
        Query task = mDatabase.whereEqualTo("idLeku", idLeku).orderBy("dataWykonania", Query.Direction.DESCENDING);
        return task;
    }

    public void insert(@NonNull WpisLek wpisLek) {
        mDatabase.add(wpisLek);
        mDatabase.whereEqualTo("idLeku", wpisLek.getIdLeku()).whereGreaterThan("dataWykonania", wpisLek.getDataWykonania()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<WpisLek> lista = task.getResult().toObjects(WpisLek.class);
                    for (WpisLek tmpWpisLek : lista) {
                        tmpWpisLek.setPozostalyZapas(((Double) (Double.parseDouble(tmpWpisLek.getPozostalyZapas()) + Double.parseDouble(wpisLek.getSumaObrotu()))).toString());
                        update(tmpWpisLek);
                    }
                }
            }
        });
    }


    public WpisLek findById(@NonNull String wpisId) {
        WpisLek wpisLek = null;
        Task<DocumentSnapshot> task = mDatabase.document(wpisId).get();

        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            wpisLek = task.getResult().toObject(WpisLek.class);
        }
        return wpisLek;
    }

    public void update(@NonNull WpisLek wpisLek) {
        mDatabase.document(wpisLek.getId()).set(wpisLek);
    }

    public Task<QuerySnapshot> delete(@NonNull WpisLek wpisLek) {
        mDatabase.document(wpisLek.getId()).delete();
        return mDatabase.whereEqualTo("idLeku", wpisLek.getIdLeku()).whereGreaterThan("dataWykonania", wpisLek.getDataWykonania()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<WpisLek> lista = task.getResult().toObjects(WpisLek.class);
                    for (WpisLek tmpWpisLek : lista) {
                        tmpWpisLek.setPozostalyZapas(((Double) (Double.parseDouble(tmpWpisLek.getPozostalyZapas()) - Double.parseDouble(wpisLek.getSumaObrotu()))).toString());
                        update(tmpWpisLek);
                    }
                }
            }
        });
    }


    public List<WpisLek> getAll() {
        List<WpisLek> lista = new ArrayList<>();

        Task<QuerySnapshot> task = mDatabase.get();
        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                WpisLek wpisLek = queryDocumentSnapshot.toObject(WpisLek.class);
                lista.add(wpisLek);
            }
        }

        return lista;
    }
}
