package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import com.google.android.gms.tasks.OnCompleteListener;
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
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.WpisPomiar;

public class PomiarRepository {

    private final CollectionReference mDatabase;
    private final WpisPomiarRepository wpisPomiarRepository;
    String userUid;

    public PomiarRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("Pomiary");
        userUid = uid;
        wpisPomiarRepository = new WpisPomiarRepository(userUid);
    }

    public Query getQuery() {
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public void insert(@NonNull Pomiar pomiar) {
        mDatabase.add(pomiar);
    }

    public Pomiar findById(@NonNull String pomiarId) {
        Pomiar pomiar = null;
        Task<DocumentSnapshot> task = mDatabase.document(pomiarId).get();
        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            pomiar = task.getResult().toObject(Pomiar.class);
        }
        return pomiar;
    }

    public Pomiar findByName(String name) {
        Pomiar pomiar = null;
        Task<QuerySnapshot> task = mDatabase.whereEqualTo("nazwa", name).get();
        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            pomiar = task.getResult().toObjects(Pomiar.class).get(0);
        }
        return pomiar;
    }

    public List<Pomiar> getAll() {
        List<Pomiar> lista = new ArrayList<>();

        Task<QuerySnapshot> task = mDatabase.get();
        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                Pomiar pomiar = queryDocumentSnapshot.toObject(Pomiar.class);
                lista.add(pomiar);
            }
        }

        return lista;
    }

    public void update(@NonNull Pomiar pomiar) {
        mDatabase.document(pomiar.getId()).set(pomiar);
    }

    public void delete(@NonNull Pomiar pomiar) {
        wpisPomiarRepository.getQueryByPomiarId(pomiar.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<WpisPomiar> listaWpisow = task.getResult().toObjects(WpisPomiar.class);
                    for (WpisPomiar wpisPomiar : listaWpisow) {
                        wpisPomiarRepository.delete(wpisPomiar);
                    }
                }
            }
        });

        mDatabase.document(pomiar.getId()).delete();
    }
}
