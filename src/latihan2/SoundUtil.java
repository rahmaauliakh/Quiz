package latihan2;

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SoundUtil {
    private static Clip loopingClip;
    private static final List<Clip> onceClips = new ArrayList<>();

    public static void playLooping(String soundFilePath) {
        stopLooping();
        try {
            File audioFile = new File(soundFilePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);

            loopingClip = AudioSystem.getClip();
            loopingClip.open(audioIn);
            loopingClip.loop(Clip.LOOP_CONTINUOUSLY);
            loopingClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopLooping() {
        if (loopingClip != null && loopingClip.isRunning()) {
            loopingClip.stop();
            loopingClip.close();
        }
    }

    public static void playOnce(String soundFilePath) {
        new Thread(() -> {
            try {
                File audioFile = new File(soundFilePath);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);

                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                synchronized (onceClips) {
                    onceClips.add(clip);
                }
                clip.start();

                // Tunggu sampai suara selesai
                while (!clip.isRunning()) {
                    Thread.sleep(10);
                }
                while (clip.isRunning()) {
                    Thread.sleep(10);
                }

                clip.close();
                synchronized (onceClips) {
                    onceClips.remove(clip);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void stopOnce() {
        synchronized (onceClips) {
            for (Clip clip : onceClips) {
                if (clip.isRunning()) {
                    clip.stop();
                    clip.close();
                }
            }
            onceClips.clear();
        }
    }
}
