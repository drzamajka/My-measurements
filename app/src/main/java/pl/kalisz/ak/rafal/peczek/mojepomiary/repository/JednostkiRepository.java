package pl.kalisz.ak.rafal.peczek.mojepomiary.repository;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Jednostka;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Uzytkownik;

public class JednostkiRepository {

    private DatabaseReference mDatabase;
    String userUid;

    public JednostkiRepository(@NonNull String uid) {
        mDatabase = FirebaseDatabase.getInstance("https://mojepomiary-fa7e0-default-rtdb.europe-west1.firebasedatabase.app").getReference("Jednostki");
        userUid = uid;
    }

    public Query getQuery(){
        return mDatabase.orderByChild("idUzytkownika").equalTo(userUid);
    }

    public List<Jednostka> getAll() {
        List<Jednostka> lista = new ArrayList<>();

        Task<DataSnapshot> task = mDatabase.get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            for (DataSnapshot postSnapshot: task.getResult().getChildren()) {
                Jednostka jednostka = postSnapshot.getValue(Jednostka.class);
                lista.add(jednostka);
            }
        }

        return lista;
    }

    public int countAll() {
        Task<DataSnapshot> task = mDatabase.get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful())
            return (int) task.getResult().getChildrenCount();
        return 0;
    }

    public void insert(@NonNull Jednostka jednostka) {
        jednostka.setId(mDatabase.push().getKey());
        mDatabase.child(jednostka.getId()).setValue(jednostka);
    }

    public Jednostka findById(@NonNull String jednostkaId) {
        Jednostka jednostka = null;
        Task<DataSnapshot> task = mDatabase.child(jednostkaId).get();
        while(!task.isComplete()) {

        }
        if (task.isSuccessful()) {
            jednostka = task.getResult().getValue(Jednostka.class);
        }
        return jednostka;
    }

    public void delete(@NonNull Jednostka jednostka) {
        mDatabase.child(jednostka.getId()).removeValue();
    }

    public void update(@NonNull Jednostka jednostka) {
        mDatabase.child(jednostka.getId()).setValue(jednostka);
    }
}
