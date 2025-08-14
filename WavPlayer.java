import java.io.File;
import javax.sound.sampled.*;

public class WavPlayer {
    private Clip clip;
    
    public void play(String filePath) throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
        
        // Wait for playback to finish unless stopped
        while (!clip.isRunning()) {
            Thread.sleep(10);
        }
        while (clip.isRunning()) {
            Thread.sleep(10);
        }
    }
    
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}