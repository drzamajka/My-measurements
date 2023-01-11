package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisLek;

public class LekRepository {

    private CollectionReference mDatabase;
    String userUid;
    private WpisLekRepository wpisLekRepository;

    public LekRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("Leki");
        userUid = uid;
        wpisLekRepository = new WpisLekRepository(userUid);
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

    public Lek findByName(String name) {
        Lek lek = null;
        Task<QuerySnapshot> task = mDatabase.whereEqualTo("nazwa", name).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            List<Lek> list = task.getResult().toObjects(Lek.class);
            if(!list.isEmpty())
                lek = list.get(0);
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

    public void update(@NonNull Lek lek) {
        mDatabase.document(lek.getId()).set(lek);
    }

    public void delete(@NonNull Lek lek) {
        wpisLekRepository.getQueryByLekId(lek.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<WpisLek> listaWpisow = task.getResult().toObjects(WpisLek.class);
                    for (WpisLek wpisPomiar : listaWpisow){
                        wpisLekRepository.delete(wpisPomiar);
                    }
                }
            }
        });

        mDatabase.document(lek.getId()).delete();
    }
}
