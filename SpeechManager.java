package org.example.model;

import javafx.scene.control.Button;
import org.example.controller.PrimaryController;
import org.example.controller.ReadingController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    public void readText(String text, String speed, String language, String dialect, String gender, String timer, Button middleButton, Button moveLeftButton, Button moveRightButton, boolean isPrimaryController) {
        if (stopReading()) return;
        new Thread(() -> {
            read(text, speed, language, dialect, gender, timer, middleButton, moveLeftButton, moveRightButton, isPrimaryController);
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

    private void read(String text, String speed, String language, String dialect, String gender, String timer, Button middleButton, Button moveLeftButton, Button moveRightButton, boolean isPrimaryController) {
        try {
            List<String> sentences = new ArrayList<>(List.of(text.split("\\.")));

            if (!Objects.equals(timer, "")) {
                long startTime = System.currentTimeMillis();
                long maxTimeMillis = Long.parseLong(timer) * 1000;
                long elapsedTime;

                for (int i = 0; i < sentences.size(); i++) {
                    String sentence = sentences.get(i);
                    setCommand(language, dialect, gender, speed, sentence);

                    speechProcess = Runtime.getRuntime().exec(command);

                    i = getI(i, sentences, isPrimaryController);

                    elapsedTime = System.currentTimeMillis() - startTime;
                    if (isPrimaryController) {
                        if (PrimaryController.stopRequest || PrimaryController.leftClicks > 0 || PrimaryController.rightClicks > 0 || elapsedTime >= maxTimeMillis) {
                            PrimaryController.started = false;
                            PrimaryController.stopRequest = false;
                            break;
                        }
                    } else {
                        if (ReadingController.stopRequest || ReadingController.leftClicks > 0 || ReadingController.rightClicks > 0 || elapsedTime >= maxTimeMillis) {
                            ReadingController.started = false;
                            ReadingController.stopRequest = false;
                            break;
                        }
                    }
                }

            } else {
                for (int i = 0; i < sentences.size(); i++) {
                    String sentence = sentences.get(i);
                    setCommand(language, dialect, gender, speed, sentence);

                    speechProcess = Runtime.getRuntime().exec(command);

                    i = getI(i, sentences, isPrimaryController);

                   if (isPrimaryController) {
                       if (PrimaryController.stopRequest || PrimaryController.leftClicks > 0 || PrimaryController.rightClicks > 0) {
                           PrimaryController.started = false;
                           PrimaryController.stopRequest = false;
                           break;
                       }
                   } else {
                       if (ReadingController.stopRequest || ReadingController.leftClicks > 0 || ReadingController.rightClicks > 0) {
                           ReadingController.started = false;
                           ReadingController.stopRequest = false;
                           break;
                       }
                   }
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

    private int getI(int i, List<String> sentences, boolean isPrimaryController) throws InterruptedException {
        if (isPrimaryController) {
            while (true) {
                if (speechProcess.waitFor(100, TimeUnit.MILLISECONDS)) {
                    break;
                }
                if (PrimaryController.stopRequest) {
                    speechProcess.destroy();
                    speechProcess.waitFor();
                    break;
                }

                if (PrimaryController.leftClicks > 0 || PrimaryController.rightClicks > 0) {
                    speechProcess.destroy();
                    speechProcess.waitFor();
                    if (PrimaryController.leftClicks > 0) {
                        int moves = Math.min(PrimaryController.leftClicks, i);
                        i = i - moves - 1;
                        PrimaryController.leftClicks = 0;
                    } else {
                        int moves = Math.min(PrimaryController.rightClicks, sentences.size() - 1 - i);
                        i = i + moves - 1;
                        PrimaryController.rightClicks = 0;
                    }
                    break;
                }
            }
        } else {
            while (true) {
                if (speechProcess.waitFor(100, TimeUnit.MILLISECONDS)) {
                    break;
                }
                if (ReadingController.stopRequest) {
                    speechProcess.destroy();
                    speechProcess.waitFor();
                    break;
                }

                if (ReadingController.leftClicks > 0 || ReadingController.rightClicks > 0) {
                    speechProcess.destroy();
                    speechProcess.waitFor();
                    if (ReadingController.leftClicks > 0) {
                        int moves = Math.min(ReadingController.leftClicks, i);
                        i = i - moves - 1;
                        ReadingController.leftClicks = 0;
                    } else {
                        int moves = Math.min(ReadingController.rightClicks, sentences.size() - 1 - i);
                        i = i + moves - 1;
                        ReadingController.rightClicks = 0;
                    }
                    break;
                }
            }
        }
        return i;
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
