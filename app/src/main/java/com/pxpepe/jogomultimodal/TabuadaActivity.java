package com.pxpepe.jogomultimodal;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TabuadaActivity extends PxpepeBaseActivity {

    public static final String NUM_ESQUERDA_ACT = "NUM_ESQUERDA_ACT";
    public static final String NUM_DIREITA_ACT = "NUM_DIREITA_ACT";
    public static final String NUM_ACERTOS_SUCC = "NUM_ACERTOS_SUCC";

    public static final String NUMER_ANSWER = "NUMER_ANSWER";
    public static final String WIN_ANSWER = "WIN_ANSWER";

    public static final int WIN_TOP = 9;


    private int esquerdaAct = -1;
    private int direitaAct = -1;
    private int numAcertos = 0;
    private boolean isVitoria = false;

    private String preTextSaved = "";

    private int alfaColor = 0x00000000;
    private int whiteColor = 0xC8FFFFFF;

    private void obterPerguntaTabuada(boolean force, String preText) {

        // Devemos realizar uma nova questão
        if (esquerdaAct == -1 || force) {
            Random r = new Random();
            esquerdaAct = r.nextInt(10)+1;
            r = new Random();
            direitaAct = r.nextInt(10)+1;
        }

        HashMap<String, String> myHash = new HashMap();
        myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,NUMER_ANSWER);

        if (preText==null) {
            preText="";
        }

        if (tts!=null) {
            tts.speak(preText +
                            "Quanto é " + String.valueOf(esquerdaAct) + " vezes "
                            + String.valueOf(direitaAct) + "?",
                    TextToSpeech.QUEUE_FLUSH, myHash);
            preTextSaved = "";
        } else {
            preTextSaved = preText;
        }

    }

    private void obterPerguntaVitoria(String preText) {

        if (preText==null) {
            preText="";
        }

        if (tts!=null) {
            HashMap<String, String> myHash = new HashMap();
            myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,WIN_ANSWER);
            tts.speak(preText +
                            "Desejas jogar de novo?",
                    TextToSpeech.QUEUE_FLUSH, myHash);
            preTextSaved = "";
        } else {
            preTextSaved = preText;
        }

    }

    private void inicializarTts() {

        if (tts==null) {

            tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        tts.setLanguage(new Locale("pt", "PT"));
                    }
                    if(status == TextToSpeech.SUCCESS) {

                        if (isVitoria) {
                            obterPerguntaVitoria(preTextSaved);
                        } else {
                            obterPerguntaTabuada(false, preTextSaved);
                        }

                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onDone(String utteranceId) {
                                if (NUMER_ANSWER.equalsIgnoreCase(utteranceId)) {
                                    displaySpeechRecognizer(NUMBER_REQUEST_CODE);
                                } else if (WIN_ANSWER.equalsIgnoreCase(utteranceId)) {
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

    private void tratarResultado(String spokenText) {

        boolean isOk = false;

        try {

            isOk = esquerdaAct * direitaAct == Integer.parseInt(spokenText);

        } catch (Exception e) {}

        if (isOk) {
            if (numAcertos<WIN_TOP) {
                numAcertos++;
                mudarCorImg(numAcertos, alfaColor);

                String incentivo = "";
                if (numAcertos==3) {
                    incentivo = "Excelente "+nomeUser+", já acertas-te três!";
                } else if (numAcertos==6) {
                    incentivo = "Imbatível "+nomeUser+", continua assim!";
                } else  if (numAcertos==9) {
                    incentivo = "Borá lá "+nomeUser+", este é o da vitória!";
                } else {
                    incentivo = "Muito bem "+nomeUser+".  Vamos ao seguinte!";
                }

                obterPerguntaTabuada(true, incentivo);
            } else {
                isVitoria = true;
                obterPerguntaVitoria("Muitos parabéns "+nomeUser+". És uma máquina! " );
            }
        } else {
            if (numAcertos>0) {
                mudarCorImg(numAcertos, whiteColor);
                numAcertos--;
            }
            obterPerguntaTabuada(false, "Incorreto! ");
        }

    }

    private void mudarCorImg(int idImg, int corImg) {
        ImageView viewImg = findViewById(getResources().
                getIdentifier("img0"+String.valueOf(idImg),
                        "id", getPackageName()));

        viewImg.setBackgroundColor(corImg);
    }

    private void inicializarImagensAcertos() {
        for (int i = 1; i<=numAcertos;i++) {
            mudarCorImg(i, alfaColor);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabuada);

        if (savedInstanceState != null){
            esquerdaAct = savedInstanceState.getInt(NUM_ESQUERDA_ACT);
            direitaAct = savedInstanceState.getInt(NUM_DIREITA_ACT);
            numAcertos = savedInstanceState.getInt(NUM_ACERTOS_SUCC);
            inicializarImagensAcertos();
        }

        fillUserNameShared();

        inicializarTts();

    }

    // Inicializamos também no onresume
    @Override
    protected void onResume() {
        super.onResume();
        inicializarTts();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NUM_ESQUERDA_ACT, esquerdaAct);
        outState.putInt(NUM_DIREITA_ACT, direitaAct);
        outState.putInt(NUM_ACERTOS_SUCC, numAcertos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            if (results!=null && results.size()>0) {

                String spokenText = results.get(0);

                if (requestCode == STT_ATV_REQUEST_CODE) {

                    // O utilixador quer continuar a jogar
                    if ("sim".equalsIgnoreCase(spokenText)) {
                        esquerdaAct = -1;
                        direitaAct = -1;
                        numAcertos = 0;
                        isVitoria = false;
                        preTextSaved = "";
                        for (int i = 1; i<=WIN_TOP;i++) {
                            mudarCorImg(i, whiteColor);
                        }
                    } else {
                        finish();
                    }

                } else if (requestCode == NUMBER_REQUEST_CODE) {

                    tratarResultado(spokenText);

                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
