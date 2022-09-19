package pl.kalisz.ak.rafal.peczek.mojepomiary.terapie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import pl.kalisz.ak.rafal.peczek.mojepomiary.R;

public class TerapiaEdytuj extends AppCompatActivity {

    public static final String EXTRA_Terapia_ID = "terapiaId";
    private int terapiaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terapia_edytuj);

        terapiaId = (Integer) getIntent().getExtras().get(EXTRA_Terapia_ID);
    }
}