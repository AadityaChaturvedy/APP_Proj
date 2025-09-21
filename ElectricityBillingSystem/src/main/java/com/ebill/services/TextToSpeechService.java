package com.ebill.services;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class TextToSpeechService {
    private static TextToSpeechService instance;
    private final Voice voice;

    private TextToSpeechService() {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice("kevin16");
        if (voice != null) {
            voice.allocate();
        } else {
            System.err.println("Could not find kevin16 voice. TTS will not work.");
        }
    }

    public static synchronized TextToSpeechService getInstance() {
        if (instance == null) {
            instance = new TextToSpeechService();
        }
        return instance;
    }

    public void speak(String text) {
        if (voice == null) {
            System.err.println("TTS voice not allocated. Cannot speak.");
            return;
        }
        new Thread(() -> {
            try {
                voice.speak(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void shutdown() {
        if (voice != null) {
            voice.deallocate();
        }
    }
}