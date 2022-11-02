package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;

public class EtapTerapiWykonaj extends AppCompatActivity {

    public static String EXTRA_Etap_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etap_terapi_wykonaj);
    }
}