package com.pxpepe.jogomultimodal;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Button;

import java.util.Locale;
import java.util.Random;

public class CoresActivity extends PxpepeBaseActivity {

    public static final String NUM_COLOR_ACT = "NUM_COLOR_ACT";
    public static final String NUM_ADIVINHAS_SUCC = "NUM_ADIVINHAS_SUCC";

    private String[] colorNames = {"Azul", "Vermelho", "Cor de Rosa",
            "Amarelo", "Verde", "Branco", "Preto", "Cinzento",
            "Cor de Laranja", "Roxo", "Castanho", "Dourado"};

    private int[] colorValues = {0xFF0000FF, 0xFFFF0000,0xFFFFCCCC,
            0xFFFFFF00, 0xFF009900, 0xFFFFFFFF, 0xFF000000, 0xFFC0C0C0,
            0xFFFF9900, 0xFF9900CC, 0xFF663300, 0xFFCC9900};

    private int adivinhasSucesso = 0;

    private int numAdAct = -1;

    private void paintRandomColorArray() {

        for (int i = 0; i<colorValues.length; i++) {

            Button vistaBtn = findViewById(getResources().
                    getIdentifier("btn"+String.valueOf(i),
                            "id", getPackageName()));

            vistaBtn.setBackgroundColor(colorValues[i]);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cores);

        if (savedInstanceState != null){
            numAdAct = savedInstanceState.getInt(NUM_COLOR_ACT);
            adivinhasSucesso = savedInstanceState.getInt(NUM_ADIVINHAS_SUCC);
        }

        inicializarTts();

        fillUserNameShared();

        paintRandomColorArray();

    }

    private String verificarNovoTeste(boolean force) {

        // Devemos realizar uma nova questão
        if (numAdAct == -1 || force) {
            Random r = new Random();
            numAdAct = r.nextInt(colorNames.length);
        }

        return "Onde está o "+colorNames[numAdAct]+"?";

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

                        // Por defeito na ativação perguntamos sempre qual é a cor
                        tts.speak(verificarNovoTeste(false), TextToSpeech.QUEUE_FLUSH, null);

                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onDone(String utteranceId) {

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
    protected void onResume() {
        super.onResume();
        inicializarTts();
    }

    public void clickBtnCor(View vistaBtn){

        int btnTag = Integer.parseInt(vistaBtn.getTag().toString());

        // Acertou
        if (btnTag==numAdAct) {
            adivinhasSucesso++;
            String coresAcertadas = "";
            if (adivinhasSucesso==1) {
                coresAcertadas = "Acertas-te a tua primeira cor!";
            }else {
                if (adivinhasSucesso==10) {
                    coresAcertadas = "Já acertas-te "+String.valueOf(adivinhasSucesso)+" cores!";
                    coresAcertadas += "Hoje estás imbatível!";
                } else if (adivinhasSucesso==20) {
                    coresAcertadas = "Já acertas-te "+String.valueOf(adivinhasSucesso)+" cores!";
                    coresAcertadas += "Mas o que é que é isto, excelente!";
                } else if (adivinhasSucesso==50) {
                    coresAcertadas = "Já acertas-te "+String.valueOf(adivinhasSucesso)+" cores!";
                    coresAcertadas += "Bem, hoje o ritmo está imparável!";
                } else if (adivinhasSucesso==100) {
                    coresAcertadas = "Já acertas-te "+String.valueOf(adivinhasSucesso)+" cores!";
                    coresAcertadas += "Já se nota que tens as cores bem dominadas, podes jogar a outros jogos!";
                }
            }
            tts.speak("Muito bem "+nomeUser+". " +
                    coresAcertadas+ verificarNovoTeste(true), TextToSpeech.QUEUE_FLUSH, null);
        } else {
            tts.speak(nomeUser+", essa não é a cor. Onde está o "+colorNames[numAdAct]+"?",
                    TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NUM_COLOR_ACT, numAdAct);
        outState.putInt(NUM_ADIVINHAS_SUCC, adivinhasSucesso);
        super.onSaveInstanceState(outState);
    }
}
