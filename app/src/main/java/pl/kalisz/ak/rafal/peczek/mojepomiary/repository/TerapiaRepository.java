package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Terapia;

public class TerapiaRepository {

    private CollectionReference mDatabase;
    String userUid;
    private EtapTerapiaRepository etapTerapiaRepository;

    public TerapiaRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("Terapie");
        userUid = uid;
        etapTerapiaRepository = new EtapTerapiaRepository(userUid);
    }

    public Query getQuery(){
        return mDatabase.whereEqualTo("idUzytkownika", userUid).orderBy("dataUtwozenia", Query.Direction.DESCENDING);
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
        etapTerapiaRepository.getQuery().whereEqualTo("idTerapi", terapia.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for( EtapTerapa etapTerapa : queryDocumentSnapshots.toObjects(EtapTerapa.class)){
                    etapTerapiaRepository.delete(etapTerapa);
                }
            }
        });

        mDatabase.document(terapia.getId()).delete();
    }

}
