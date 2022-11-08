package pl.kalisz.ak.rafal.peczek.mojepomiary;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.EtapTerapiPosiaRelacie;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.EtapTerapiActivity;

public class OdbiornikPowiadomien  extends BroadcastReceiver {

    public static String EXTRA_Etap_ID = "etapId";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Tag-powiadomienie", "intent:"+intent.toString());
        int etapId = (int) intent.getExtras().get("EXTRA_Etap_ID");
        UsersRoomDatabase database = UsersRoomDatabase.getInstance(context);
        EtapTerapiPosiaRelacie etapTerapa = database.localEtapTerapaDao().findByIdWithRelations(etapId);

        if(etapTerapa.etapTerapa.getDataWykonania() == null) {
            Intent i = new Intent(context, EtapTerapiActivity.class);
            i.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, etapId);
            i.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, etapId, i, PendingIntent.FLAG_MUTABLE);

            ArrayList<Integer> listaElementow = etapTerapa.terapia.getIdsCzynnosci();
            String opis = "";
            for (int id: listaElementow) {
                Pomiar pomiar = database.localPomiarDao().findById(id);
                if(id != listaElementow.get(0))
                    opis += ", "+pomiar.getNazwa();
                else
                    opis = pomiar.getNazwa();
            }

            NotificationCompat.Builder bilder = new NotificationCompat.Builder(context, "mojepomiary")
                    .setSmallIcon(R.mipmap.ic_launcher_wlasna_round)
                    .setContentTitle("Wykonaj etap terapii: "+etapId)
                    .setContentText(opis)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(etapId, bilder.build());
        }

    }
}
