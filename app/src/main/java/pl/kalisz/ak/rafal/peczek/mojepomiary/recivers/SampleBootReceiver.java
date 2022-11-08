package pl.kalisz.ak.rafal.peczek.mojepomiary.recivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;
import java.util.List;

import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.relation.EtapTerapiPosiaRelacie;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.UsersRoomDatabase;

public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.PACKAGE_RESTARTED")) {

            UsersRoomDatabase database = UsersRoomDatabase.getInstance(context);
            List<EtapTerapa> etapyTerapi = database.localEtapTerapaDao().getAllAfterData(new Date().getTime());

            for (EtapTerapa etapTerapi: etapyTerapi){
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent i = new Intent(context, OdbiornikPowiadomien.class);
                i.putExtra("EXTRA_Etap_ID", etapTerapi.getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, etapTerapi.getId(), i,PendingIntent.FLAG_MUTABLE);

                alarmManager.setAndAllowWhileIdle (AlarmManager.RTC_WAKEUP,etapTerapi.getDataZaplanowania().getTime()-60*1000, pendingIntent);

            }

        }
    }

}
