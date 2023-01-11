package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;

public class JednostkiRepository {

    private final CollectionReference mDatabase;
    String userUid;
    private final LekRepository lekRepository;
    private final PomiarRepository pomiarRepository;


    public JednostkiRepository(@NonNull String uid) {
        mDatabase = FirebaseFirestore.getInstance().collection("Jednostki");
        userUid = uid;
        lekRepository = new LekRepository(userUid);
        pomiarRepository = new PomiarRepository(userUid);
    }

    public Query getQuery() {
        return mDatabase.whereEqualTo("idUzytkownika", userUid);
    }

    public List<Jednostka> getAll() {
        List<Jednostka> lista = new ArrayList<>();

        Task<QuerySnapshot> task = mDatabase.get();
        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                Jednostka jednostka = queryDocumentSnapshot.toObject(Jednostka.class);
                lista.add(jednostka);
            }
        }

        return lista;
    }

    public int countAll() {
        Task<QuerySnapshot> task = mDatabase.get();
        while (!task.isComplete()) {

        }
        if (task.isSuccessful())
            return task.getResult().toObjects(Jednostka.class).size();
        return 0;
    }

    public void insert(@NonNull Jednostka jednostka) {
        mDatabase.add(jednostka);
    }

    public DocumentReference queryById(@NonNull String jednostkaId) {
        return mDatabase.document(jednostkaId);
    }

    public Jednostka findById(@NonNull String jednostkaId) {
        Jednostka jednostka = null;
        Task<DocumentSnapshot> task = mDatabase.document(jednostkaId).get();

        while (!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            jednostka = task.getResult().toObject(Jednostka.class);
        }

        return jednostka;
    }

    public void update(@NonNull Jednostka jednostka) {
        mDatabase.document(jednostka.getId()).set(jednostka);
    }

    public void delete(@NonNull Jednostka jednostka) {
        lekRepository.getQuery().whereEqualTo("idJednostki", jednostka.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Lek> listaLekow = task.getResult().toObjects(Lek.class);
                    for (Lek lek : listaLekow) {
                        lekRepository.delete(lek);
                    }
                }
            }
        });
        pomiarRepository.getQuery().whereEqualTo("idJednostki", jednostka.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Pomiar> listaPomiarow = task.getResult().toObjects(Pomiar.class);
                    for (Pomiar pomiar : listaPomiarow) {
                        pomiarRepository.delete(pomiar);
                    }
                }
            }
        });

        mDatabase.document(jednostka.getId()).delete();
    }

}
