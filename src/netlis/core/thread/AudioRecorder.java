package netlis.core.thread;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder extends Thread {

  private TargetDataLine recordLine;

  private AudioFileFormat.Type recFormat;

  private AudioInputStream myAIS;

  private File myOutFile;

  public AudioRecorder(TargetDataLine inLine, AudioFileFormat.Type tgtFmt, File inFile) {
    recordLine = inLine;
    myAIS = new AudioInputStream(inLine);
    recFormat = tgtFmt;
    myOutFile = inFile;
  }

  public void run() {
    try {
      AudioSystem.write(myAIS, recFormat, myOutFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() {
    recordLine.start();
    super.start();
  }

  public void stopRecording() {
    recordLine.stop();
    recordLine.close();
  }
}
