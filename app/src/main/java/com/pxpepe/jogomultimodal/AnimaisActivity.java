package com.pxpepe.jogomultimodal;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class AnimaisActivity extends PxpepeBaseActivity {

    public static final String CHOSEN_NAME_ACT = "CHOSEN_NAME_ACT";
    public static final String ANIMAL_ACT = "ANIMAL_ACT";
    public static final String SUCCESS_ACT = "SUCCESS_ACT";

    public static final String ANIMAL_SOUND = "ANIMAL_SOUND";

    private String[] names01 = {"bee", "cat", "dog", "owl", "rooster", "lion"};
    private String[] names02 = {"cow", "duck", "elephant", "horse", "pig", "sheep"};

    private int chosenName = -1;
    private int aninimalsSuccess = 0;
    private int numAnimalAct = -1;

    private AudioPlayer ap;

    private void verificarNovoTeste(boolean force, String preText) {

        // Devemos realizar uma nova questão
        if (numAnimalAct == -1 || force) {
            Random r = new Random();
            numAnimalAct = r.nextInt(names01.length);
        }

        HashMap<String, String> myHash = new HashMap();
        myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,ANIMAL_SOUND);

        if (preText==null) {
            preText="";
        }

        tts.speak(preText+" Que animal faz ", TextToSpeech.QUEUE_FLUSH, myHash);

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

                        verificarNovoTeste(false, "");

                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onDone(String utteranceId) {
                                if (ANIMAL_SOUND.equalsIgnoreCase(utteranceId)) {
                                    ap.play(getApplicationContext(), getResources()
                                            .getIdentifier(obterArraySel()[numAnimalAct],
                                                    "raw", getPackageName()));
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

    private String[] obterArraySel() {

        String[] arraySel = null;

        if (chosenName==0) {
            arraySel=names01;
        } else {
            arraySel=names02;
        }

        return arraySel;

    }

    private void pintarImagensSelecionadas() {

        String[] arraySel = obterArraySel();

        for (int i=0; i<arraySel.length;i++) {

            ImageView viewImg = findViewById(getResources().
                    getIdentifier("img"+String.valueOf(i),
                            "id", getPackageName()));

            viewImg.setImageResource(getResources().getIdentifier(arraySel[i],
                    "drawable", getPackageName()));

            viewImg.setBackgroundColor(0xFFFFFFFF);

        }

    }

    public void imageClick(View vistaImg) {

        int btnTag = Integer.parseInt(vistaImg.getTag().toString());

        // Acertou
        if (btnTag==numAnimalAct) {
            aninimalsSuccess++;
            String textoSpeak = "";
            if (aninimalsSuccess==1) {
                textoSpeak = "Acertas-te o teu primeiro animal!";
            }else {
                if (aninimalsSuccess==10) {
                    textoSpeak = "Já acertas-te "+String.valueOf(aninimalsSuccess)+" animais!";
                    textoSpeak += "Hoje estás imbatível!";
                } else if (aninimalsSuccess==20) {
                    textoSpeak = "Já acertas-te "+String.valueOf(aninimalsSuccess)+" animais!";
                    textoSpeak += "Mas o que é que é isto, excelente!";
                } else if (aninimalsSuccess==50) {
                    textoSpeak = "Já acertas-te "+String.valueOf(aninimalsSuccess)+" animais!";
                    textoSpeak += "Bem, hoje o ritmo está imparável!";
                } else if (aninimalsSuccess==100) {
                    textoSpeak = "Já acertas-te "+String.valueOf(aninimalsSuccess)+" animais!";
                    textoSpeak += "Já se nota que tens os animais bem dominados, podes jogar a outros jogos!";
                }
            }
            verificarNovoTeste(true, "Muito bem "+nomeUser+". "+textoSpeak);
        } else {
            verificarNovoTeste(false, nomeUser+" esse não é o animal. ");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animais);

        if (savedInstanceState != null){
            chosenName = savedInstanceState.getInt(CHOSEN_NAME_ACT);
            numAnimalAct = savedInstanceState.getInt(ANIMAL_ACT);
            aninimalsSuccess = savedInstanceState.getInt(SUCCESS_ACT);
        } else {
            chosenName = (int) Math.round( Math.random() );
        }

        ap = new AudioPlayer();

        fillUserNameShared();

        inicializarTts();

        pintarImagensSelecionadas();


    }

    @Override
    protected void onResume() {
        super.onResume();
        inicializarTts();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CHOSEN_NAME_ACT, chosenName);
        outState.putInt(ANIMAL_ACT, numAnimalAct);
        outState.putInt(SUCCESS_ACT, aninimalsSuccess);
        super.onSaveInstanceState(outState);
    }

}
