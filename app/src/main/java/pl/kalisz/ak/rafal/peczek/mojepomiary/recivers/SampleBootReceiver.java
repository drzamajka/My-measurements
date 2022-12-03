package pl.kalisz.ak.rafal.peczek.mojepomiary.recivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Date;
import java.util.List;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            goAsync();
            renewAlarmManager(context);
        }
    }


    public void renewAlarmManager(Context context){
        EtapTerapiaRepository etapTerapiaRepository = new EtapTerapiaRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        List<EtapTerapa> list = etapTerapiaRepository.getAllAfterData(new Date());
        Log.w("TAG-renewAlarmManager", "lista size: "+list.size());
        if(!list.isEmpty()){
            for ( EtapTerapa etapTerapa : list){
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(context, OdbiornikPowiadomien.class);
                    i.putExtra("EXTRA_Etap_ID", etapTerapa.getId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)etapTerapa.getDataZaplanowania().getTime(), i,PendingIntent.FLAG_MUTABLE);
                    alarmManager.setAndAllowWhileIdle (AlarmManager.RTC_WAKEUP,etapTerapa.getDataZaplanowania().getTime(), pendingIntent);
                }
        }
    }

    public void cancleAlarmManager(Context context){
        EtapTerapiaRepository etapTerapiaRepository = new EtapTerapiaRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        List<EtapTerapa> list = etapTerapiaRepository.getAllAfterData(new Date());
        Log.w("TAG-renewAlarmManager", "lista size: "+list.size());
        if(!list.isEmpty()){
            for ( EtapTerapa etapTerapa : list){
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(context, OdbiornikPowiadomien.class);
                i.putExtra("EXTRA_Etap_ID", etapTerapa.getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)etapTerapa.getDataZaplanowania().getTime(), i,PendingIntent.FLAG_MUTABLE);
                alarmManager.cancel (pendingIntent);
            }
        }
    }

}
