package com.pxpepe.jogomultimodal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends PxpepeBaseActivity {

    private int lastWindow = -1;

    public static final int INTENT_OUVIR_ANIMAIS = 0;
    public static final int INTENT_OUVIR_CORES = 1;
    public static final int INTENT_OUVIR_TABUADA = 2;
    public static final int INTENT_OUVIR_ESCREVER = 3;

    public static final String NAME_DEFINITION = "name_def";
    public static final String MAIN_ANSWER = "MAIN_ANSWER";

    private void inicializarTts() {

        if (tts==null) {
            tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        tts.setLanguage(new Locale("pt", "PT"));
                    }
                    if(status == TextToSpeech.SUCCESS) {
                        perguntarNomeUser();
                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onDone(String utteranceId) {

                                if (NAME_DEFINITION.equalsIgnoreCase(utteranceId)) {
                                    displaySpeechRecognizer(NEW_NAME_REQUEST_CODE);
                                } else if (MAIN_ANSWER.equalsIgnoreCase(utteranceId)) {
                                    displaySpeechRecognizer(STT_ATV_REQUEST_CODE);
                                }

                            }

                            @Override
                            public void onError(String utteranceId) {
                            }

                            @Override
                            public void onStart(String utteranceId) {
                            }
                        });
                    }
                }
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarTts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inicializarTts();
    }


    private void perguntarNomeUser() {
        boolean hasUsername = fillUserNameShared();
        // Devemos perguntar o nome que o utilizador deseja ter
        if (!hasUsername) {
            btnChangeName(null);
        }

    }

    public void btnAnimaisClick(View vistaBtn) {

        lastWindow = INTENT_OUVIR_ANIMAIS;
        openLastActivity();

    }

    public void btnCoresClick(View vistaBtn) {

        lastWindow = INTENT_OUVIR_CORES;
        openLastActivity();

    }

    public void btnTabuadaClick(View vistaBtn) {

        lastWindow = INTENT_OUVIR_TABUADA;
        openLastActivity();

    }

    public void btnEscreverClick(View vistaBtn) {

        lastWindow = INTENT_OUVIR_ESCREVER;
        openLastActivity();

    }

    public void btnOuvirAnimaisClick(View vistaBtn) {

        reproduzirFrazeOuvir("de adivinhar animais", INTENT_OUVIR_ANIMAIS);

    }

    public void btnOuvirCoresClick(View vistaBtn) {

        reproduzirFrazeOuvir("de adivinhar cores", INTENT_OUVIR_CORES);

    }

    public void btnOuvirTabuadaClick(View vistaBtn) {

        reproduzirFrazeOuvir("da tabuada", INTENT_OUVIR_TABUADA);

    }

    public void btnOuvirEscreverClick(View vistaBtn) {

        reproduzirFrazeOuvir("de escrever", INTENT_OUVIR_ESCREVER);

    }

    public void btnChangeName(View vistaImg) {
        HashMap<String, String> myHash = new HashMap();
        myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,NAME_DEFINITION);
        tts.speak("Olá, como te chamas?", TextToSpeech.QUEUE_FLUSH, myHash);
    }

    private void reproduzirFrazeOuvir(String textReproduzir, int ouvirSelect) {

        lastWindow = ouvirSelect;

        HashMap<String, String> myHash = new HashMap();
        myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,MAIN_ANSWER);

        tts.speak("Olá, "+nomeUser+", Gostavas de jogar ao jogo "+textReproduzir+"?", TextToSpeech.QUEUE_FLUSH, myHash);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            if (results!=null && results.size()>0) {

                String spokenText = results.get(0);

                if (requestCode == STT_ATV_REQUEST_CODE) {

                    // O utilixador aceitou iniciar o jogo
                    if ("sim".equalsIgnoreCase(spokenText)) {
                        openLastActivity();
                    }

                } else if (requestCode == NEW_NAME_REQUEST_CODE) {

                    SharedPreferences.Editor editor = getSharedPreferences(MULTI_MODAL_APP_PREFERENCES, MODE_PRIVATE).edit();
                    editor.putString(MMAP_USERNAME, spokenText);
                    editor.apply();

                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openLastActivity() {

        Intent intent = null;

        switch (lastWindow) {
            case INTENT_OUVIR_ANIMAIS:
                intent = new Intent(getApplicationContext(),AnimaisActivity.class);
                break;
            case INTENT_OUVIR_CORES:
                intent = new Intent(getApplicationContext(),CoresActivity.class);
                break;
            case INTENT_OUVIR_TABUADA:
                intent = new Intent(getApplicationContext(),TabuadaActivity.class);
                break;
            case INTENT_OUVIR_ESCREVER:
                intent = new Intent(getApplicationContext(),EscreverActivity.class);
                break;
        }

        if (intent!=null) {
            startActivity(intent);
        }

    }

}
