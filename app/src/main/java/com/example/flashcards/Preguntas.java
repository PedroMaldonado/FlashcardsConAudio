package com.example.flashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Preguntas extends AppCompatActivity {

    Button bSonido;
    EditText bPalabra;
    String pathsito;

    private static final String LOG_TAG = "AudioRecordTest";
    private MediaPlayer player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        bSonido = findViewById(R.id.sonido);
        bPalabra = findViewById(R.id.palabra);


        bSonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPalabra();
                player = new MediaPlayer();
                try {
                    player.setDataSource(pathsito);
                    player.prepare();
                    player.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });

    }

    private void mPalabra() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Palabras",null,1);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        String word = bPalabra.getText().toString();

        Cursor fila = BaseDeDatabase.rawQuery("select audio from palabras where palabra = '"+ word + "'",null);
        if(fila.moveToFirst()){
            pathsito = fila.getString(0);
            BaseDeDatabase.close();
        }else{
            Toast.makeText(this,"No se encuentran palabras",Toast.LENGTH_SHORT).show();

        }
    }
}
