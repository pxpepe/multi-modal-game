package com.pxpepe.jogomultimodal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

public class PxpepeBaseActivity extends AppCompatActivity {

    public static final String MULTI_MODAL_APP_PREFERENCES = "MULTI_MODAL_APP_PREFERENCES";
    public static final String MMAP_USERNAME = "MMAP_USERNAME";

    public static final int STT_ATV_REQUEST_CODE = 0;
    public static final int NEW_NAME_REQUEST_CODE = 1;
    public static final int NUMBER_REQUEST_CODE = 2;

    public String nomeUser = "convidado";

    TextToSpeech tts = null;

    public boolean fillUserNameShared() {

        boolean hasUsername = false;

        SharedPreferences prefs = getSharedPreferences(MULTI_MODAL_APP_PREFERENCES, MODE_PRIVATE);
        String nome = prefs.getString(MMAP_USERNAME, null);

        if (nome!=null) {
            hasUsername=true;
            nomeUser = nome;
        }

        return hasUsername;

    }

    public void displaySpeechRecognizer(int requestCode) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");

        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onPause() {
        if(tts !=null){
            tts.stop();
            tts.shutdown();
            tts =null;
        }
        super.onPause();
    }
}
