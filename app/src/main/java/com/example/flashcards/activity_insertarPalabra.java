package com.example.flashcards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class activity_insertarPalabra extends AppCompatActivity {
    EditText etpalabra;
    Button binsertar;

    Button boton_grabar;
    Button boton_reproducir;
    Button boton_detenerg;
    Button boton_detenerr;

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_palabra);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        etpalabra = (EditText) findViewById(R.id.etpalabra);
        binsertar= (Button) findViewById(R.id.binsertar);
        boton_grabar = findViewById(R.id.boton_grabar);
        boton_reproducir = findViewById(R.id.boton_reproducir);
        boton_detenerg = findViewById(R.id.boton_detenerg);
        boton_detenerr = findViewById(R.id.boton_detenerr);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/Audio " + System.currentTimeMillis() + ".3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        Toast.makeText(this,"" + etpalabra.getText().toString(),Toast.LENGTH_SHORT).show();

        binsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Insertar();
            }
        });

        boton_grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setOutputFile(fileName);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    recorder.prepare();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }

                recorder.start();
            }
        });

        boton_detenerg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
                System.out.println(fileName);
                System.out.println(etpalabra.getText().toString());
            }
        });

        boton_reproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = new MediaPlayer();
                try {
                    player.setDataSource(fileName);
                    player.prepare();
                    player.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });

        boton_detenerr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.release();
                player = null;
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recorder != null){
            recorder.release();
            recorder = null;
        }
        if (player != null){
            player.release();
            player = null;
        }
    }

    private void Insertar() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Palabras",null,1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String palabra= etpalabra.getText().toString();

        if (!palabra.isEmpty()){
            ContentValues registro= new ContentValues();
            registro.put("palabra",palabra);
            registro.put("audio",fileName);

            BaseDeDatos.insert("palabras",null,registro);

            BaseDeDatos.close();
            etpalabra.setText(" ");

            Toast.makeText(this,"Se agrego la palabra",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Debe llenar todos los campos",Toast.LENGTH_SHORT).show();

        }
    }
}
