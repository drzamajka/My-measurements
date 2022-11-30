package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;

public class EtapTerapiaRepository {

    private CollectionReference mDatabase;
    String userUid;

    public EtapTerapiaRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("EtapyTerapi");
        userUid = uid;
    }

    public Query getQuery(){
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public List<EtapTerapa> getAllBetwenData(long time, long time1) {
        List<EtapTerapa> lista = new ArrayList<>();
        return lista;
    }

    public Task<DocumentReference> insert(@NonNull EtapTerapa etapTerapa) {
        Task<DocumentReference> task = mDatabase.add(etapTerapa);
        return task;
    }

    public EtapTerapa findById(@NonNull String idEtapuTerapi) {
        EtapTerapa etapTerapa = null;
        Task<DocumentSnapshot> task = mDatabase.document(idEtapuTerapi).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            etapTerapa = task.getResult().toObject(EtapTerapa.class);
        }
        return etapTerapa;
    }

    public void update(@NonNull EtapTerapa etapTerapa) {
        mDatabase.document(etapTerapa.getId()).set(etapTerapa);
    }

    public void delete(@NonNull EtapTerapa etapTerapa) {
        mDatabase.document(etapTerapa.getId()).delete();
    }
}
