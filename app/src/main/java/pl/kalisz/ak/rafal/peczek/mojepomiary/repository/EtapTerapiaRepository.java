package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;

public class EtapTerapiaRepository {

    private CollectionReference mDatabase;
    String userUid;
    private WpisPomiarRepository wpisPomiarRepository;
    private WpisLekRepository wpisLekRepository;

    public EtapTerapiaRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("EtapyTerapi");
        userUid = uid;
        wpisPomiarRepository = new WpisPomiarRepository(userUid);
        wpisLekRepository = new WpisLekRepository(userUid);
    }

    public Query getQuery(){
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public Query getQueryByIdTerapi(String idTerapi){
        return mDatabase.whereEqualTo("idUzytkownika", userUid).whereEqualTo("idTerapi", idTerapi);
    }

    public List<EtapTerapa> getAllAfterData(Date date) {
        List<EtapTerapa> lista = new ArrayList<>();
        Task<QuerySnapshot> task = getQuery().whereGreaterThan("dataZaplanowania", date).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            lista = task.getResult().toObjects(EtapTerapa.class);
        }
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
        wpisPomiarRepository.getQuery().whereEqualTo("idEtapTerapi", etapTerapa.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for( WpisPomiar wpisPomiar : queryDocumentSnapshots.toObjects(WpisPomiar.class)){
                    wpisPomiar.setIdEtapTerapi(null);
                    wpisPomiarRepository.update(wpisPomiar);
                }
            }
        });
        wpisLekRepository.getQuery().whereEqualTo("idEtapTerapi", etapTerapa.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for( WpisLek wpisLek : queryDocumentSnapshots.toObjects(WpisLek.class)){
                    wpisLek.setIdEtapTerapi(null);
                    wpisLekRepository.update(wpisLek);
                }
            }
        });

        mDatabase.document(etapTerapa.getId()).delete();
    }
}
