package pl.kalisz.ak.rafal.peczek.mojepomiary.recivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.EtapTerapiActivity;

public class OdbiornikPowiadomien  extends BroadcastReceiver {

    public static String EXTRA_Etap_ID = "etapId";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Tag-powiadomienie", "intent:"+intent.toString());
        String etapId = (String) intent.getExtras().get("EXTRA_Etap_ID");
        EtapTerapiaRepository etapTerapiaRepository = new EtapTerapiaRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        TerapiaRepository terapiaRepository = new TerapiaRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        PomiarRepository pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());


        EtapTerapa etapTerapa = etapTerapiaRepository.findById(etapId);

        if(etapTerapa.getDataWykonania() == null) {
            Intent i = new Intent(context, EtapTerapiActivity.class);
            i.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, etapId);
            i.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)etapTerapa.getDataZaplanowania().getTime(), i, PendingIntent.FLAG_MUTABLE);

            ArrayList<String> listaElementow = terapiaRepository.findById(etapTerapa.getIdTerapi()).getIdsCzynnosci();
            String opis = "";
            for (String id: listaElementow) {
                Pomiar pomiar = pomiarRepository.findById(id);
                if(id != listaElementow.get(0))
                    opis += ", "+pomiar.getNazwa();
                else
                    opis = pomiar.getNazwa();
            }

            NotificationCompat.Builder bilder = new NotificationCompat.Builder(context, "mojepomiary")
                    .setSmallIcon(R.drawable.ic_notyfication_deafalt)
                    .setContentTitle("Wykonaj etap terapii")
                    .setContentText(opis)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(25647, bilder.build());
        }

    }
}
