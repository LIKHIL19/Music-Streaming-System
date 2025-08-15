import java.io.File;
import javax.sound.sampled.*;

public class AudioPlayer {
    private Clip clip;
    
    public void play(String filePath) throws Exception {
        try {
            // Convert file path to URL format
            File file = new File(filePath);
            if (!file.exists()) {
                throw new Exception("File not found: " + filePath);
            }
            
            // Register MP3SPI providers
            System.setProperty("javax.sound.sampled.Clip", "com.sun.media.sound.DirectAudioDeviceProvider");
            System.setProperty("javax.sound.sampled.Port", "com.sun.media.sound.PortMixerProvider");
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            AudioFormat baseFormat = audioStream.getFormat();
            
            // Create decoded format
            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false);
            
            // Get decoded stream
            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);
            
            // Get and open clip
            DataLine.Info info = new DataLine.Info(Clip.class, decodedFormat);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(decodedStream);
            clip.start();
            
            // Wait for playback to complete
            while (!clip.isRunning()) Thread.sleep(10);
            while (clip.isRunning()) Thread.sleep(10);
            
        } finally {
            if (clip != null) {
                clip.close();
            }
        }
    }
    
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}