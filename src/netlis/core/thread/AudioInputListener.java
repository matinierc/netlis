package netlis.core.thread;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AudioInputListener implements Runnable {

  private static Logger logger = LogManager.getLogger(AudioInputListener.class);

  private AudioFormat audioFormat;

  public AudioInputListener(AudioFormat audioFormat) {
    super();
    this.audioFormat = audioFormat;
  }

  @Override
  public void run() {
    logger.debug("Input listener running for: " + audioFormat);
    //    while (!Thread.currentThread().isInterrupted()) {

    TargetDataLine line;
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, this.audioFormat);
    if (!AudioSystem.isLineSupported(info)) {
      logger.error("Unsuported format: " + this.audioFormat);
    }
    // Obtain and open the line.
    try {
      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(this.audioFormat);
      logger.info("YES !!!!!!!!!!!!!!!!!!");
    } catch (LineUnavailableException e) {
      logger.error("Can't open the line", e);
    }

    //    }
    //    logger.debug("Input listener interrupted");

  }
  
  public static void main(String[] args) {
    try {
      for(Mixer.Info mixerInfo: AudioSystem.getMixerInfo()) {
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
        for (Line.Info lineInfo: mixer.getSourceLineInfo()) {
          if (!AudioSystem.isLineSupported(lineInfo)) {
            logger.error("Unsuported format: " + lineInfo);
          }

          AudioFormat[] formats = ((DataLine.Info) lineInfo).getFormats();
          for(AudioFormat format: formats) {
            
            try {
              DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
              TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
              line.open(format);
              logger.info("YES !!!!!!!!!!!!!!!!!!");
            } catch (LineUnavailableException e) {
              logger.error("Can't open the line", e);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
