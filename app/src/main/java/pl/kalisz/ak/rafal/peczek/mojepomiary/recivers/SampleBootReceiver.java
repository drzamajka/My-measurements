package pl.kalisz.ak.rafal.peczek.mojepomiary.recivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;

public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            EtapTerapiaRepository etapTerapiaRepository = new EtapTerapiaRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
            TerapiaRepository terapiaRepository = new TerapiaRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
            PomiarRepository pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

            etapTerapiaRepository.getQuery().whereEqualTo("dataWykonania", null).whereGreaterThanOrEqualTo("dataZaplanowania", new Date()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                        for ( EtapTerapa etapTerapa : task.getResult().toObjects(EtapTerapa.class)){
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                            Intent i = new Intent(context, OdbiornikPowiadomien.class);
                            i.putExtra("EXTRA_Etap_ID", etapTerapa.getId());
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)etapTerapa.getDataZaplanowania().getTime(), i,PendingIntent.FLAG_MUTABLE);

                            alarmManager.setAndAllowWhileIdle (AlarmManager.RTC_WAKEUP,etapTerapa.getDataZaplanowania().getTime()-60*1000, pendingIntent);
                        }
                }
            });


        }
    }

}
