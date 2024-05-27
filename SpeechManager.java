package org.example.model;

import java.io.IOException;
import java.util.Objects;

public class SpeechManager {
    private Process speechProcess = null;
    private static SpeechManager instance;
    private String language;
    private String dialect;
    private String voiceType;
    private String speed;
    private String command;

    private SpeechManager() {}

    public static SpeechManager getInstance() {
        if (instance == null) {
            instance = new SpeechManager();
        }
        return instance;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void readText(String text, String speed, String language, String dialect, String gender, String timer) {
        if (stopReading()) return;
        new Thread(() -> {
            read(text, speed, language, dialect, gender, timer);
        }).start();
    }

    public void setCommand(String language, String dialect, String voiceType, String speed, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"C:\\Program Files\\eSpeak NG\\espeak-ng.exe\" ");
        sb.append("-v ").append(language).append("-").append(dialect).append("+").append(voiceType).append(" ");
        sb.append("-s ").append(speed).append(" ");
        sb.append("\"").append(text).append("\"");
        this.command = sb.toString();
    }

    private void read(String text, String speed, String language, String dialect, String gender, String timer) {
        try {
            setCommand(language, dialect, gender, speed, text);
            speechProcess = Runtime.getRuntime().exec(command);

            if (!Objects.equals(timer, "")) {
                long startTime = System.currentTimeMillis();
                long maxTimeMillis = Long.parseLong(timer) * 1000;

                while (speechProcess.isAlive()) {
                    long elapsedTime = System.currentTimeMillis() - startTime;

                    if (elapsedTime >= maxTimeMillis) {
                        stopReading();
                        break;
                    }
                    Thread.sleep(100);
                }
            }

            if (speechProcess != null) {
                speechProcess.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (speechProcess != null) {
                speechProcess = null;
            }
        }
    }

    private boolean stopReading() {
        if (speechProcess != null) {
            speechProcess.destroy();
            speechProcess = null;
            return true;
        }
        return false;
    }
}
