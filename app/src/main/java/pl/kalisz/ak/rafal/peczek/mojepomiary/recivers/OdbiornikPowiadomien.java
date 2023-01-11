package pl.kalisz.ak.rafal.peczek.mojepomiary.recivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.EtapTerapa;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Lek;
import pl.kalisz.ak.rafal.peczek.mojepomiary.entity.Pomiar;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.EtapTerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.LekRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.PomiarRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.repository.TerapiaRepository;
import pl.kalisz.ak.rafal.peczek.mojepomiary.terapie.EtapTerapiActivity;

public class OdbiornikPowiadomien extends BroadcastReceiver {

    public static String EXTRA_Etap_ID = "etapId";

    @Override
    public void onReceive(Context context, Intent intent) {
        String etapId = (String) intent.getExtras().get("EXTRA_Etap_ID");
        EtapTerapiaRepository etapTerapiaRepository = new EtapTerapiaRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        TerapiaRepository terapiaRepository = new TerapiaRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        PomiarRepository pomiarRepository = new PomiarRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());
        LekRepository lekRepository = new LekRepository(FirebaseAuth.getInstance().getCurrentUser().getUid());

        EtapTerapa etapTerapa = etapTerapiaRepository.findById(etapId);

        if (etapTerapa.getDataWykonania() == null) {
            Intent intent1 = new Intent(context, EtapTerapiActivity.class);
            intent1.putExtra(EtapTerapiActivity.EXTRA_Etap_ID, etapId);
            intent1.putExtra(EtapTerapiActivity.EXTRA_Aktywnosc, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) etapTerapa.getDataZaplanowania().getTime(), intent1, PendingIntent.FLAG_MUTABLE);
            String opis = "";
            try {
                ArrayList<String> listaElementow = terapiaRepository.findById(etapTerapa.getIdTerapi()).getIdsCzynnosci();
                for (int i = 0; i < listaElementow.size(); i++) {
                    if (i != 0) {
                        opis += "\n";
                    }
                    JSONObject czynnosc = new JSONObject(listaElementow.get(i));
                    String szukaneId = (String) czynnosc.get("id");
                    if (czynnosc.get("typ").equals(Pomiar.class.getName())) {
                        Pomiar pomiar = pomiarRepository.findById(szukaneId);
                        opis += pomiar.getNazwa();
                    } else if (czynnosc.get("typ").equals(Lek.class.getName())) {
                        Lek lek = lekRepository.findById(szukaneId);
                        opis += lek.getNazwa();
                    }
                }
            } catch (
                    JSONException e) {
                e.printStackTrace();
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
