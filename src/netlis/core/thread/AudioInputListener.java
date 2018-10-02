package netlis.core.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
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

  private static String SONG_FILE = "song.mp3";

  private static String OUTPUT_FILE = "P:\\ESOUND\\Trip Hop\\Cut Chemist - The audiences listings\\01 - Cut Chemist - The Audience's Listening - Motivational Speaker.mp3";

  // private static String
  // RECORD_PORT_SELECT =
  // "\"What U Hear\"";
  private static String RECORD_PORT_SELECT = "MICROPHONE";

  private static float RECORD_VOLUME_LEVEL = 0.8f;

  public static void main(String[] args) {
    //    Mixer.Info[] infos = AudioSystem.getMixerInfo();
    //
    //    for (Mixer.Info mixerInfo : infos) {
    //      Mixer mixer = AudioSystem.getMixer(mixerInfo);
    //      Line.Info[] linesInfo = mixer.getSourceLineInfo();
    //
    //      for (Line.Info lineInfo : linesInfo) {
    //        AudioFormat[] formats = ((DataLine.Info) lineInfo).getFormats();
    //
    //        for (AudioFormat format : formats) {
    //          try {
    //            TargetDataLine line;
    //            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    //            //            if (AudioSystem.isLineSupported(info)) {
    //            try {
    //              line = (TargetDataLine) AudioSystem.getLine(info);
    //              line.open(format);
    //              logger.info("Works for: " + format);
    //            } catch (LineUnavailableException e) {
    //              e.printStackTrace();
    //            }
    //            //            }
    //          } catch (Exception e) {
    //            e.printStackTrace();
    //          }
    //        }
    //      }
    //    }
    //    showMixers();
    //    try {
    //      probePort();
    //    } catch (Exception e) {
    //      logger.error(e);
    //    }

    try {
      File outputFile = new File(OUTPUT_FILE);
      AudioFormat recordingFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
      TargetDataLine recordLine = null;
      recordLine = (TargetDataLine) AudioSystem.getTargetDataLine(recordingFormat);
      recordLine.open(recordingFormat);
      adjustRecordingVolume();
      AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
      AudioRecorder recorder = new AudioRecorder(recordLine, fileType, outputFile);
      recorder.start();
      System.out.println("Playing Song and Recording...");
      playAudio(SONG_FILE);

      recorder.stopRecording();
      System.out.println("Recording stopped.");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void adjustRecordingVolume() throws Exception {
    Port.Info recPortInfo = new Port.Info(Port.class, RECORD_PORT_SELECT, true);
    Port recPort = (Port) AudioSystem.getLine(recPortInfo);
    setRecControlValue(recPort);
  }

  private static void setRecControlValue(Port inPort) throws Exception {
    inPort.open();
    Control[] controls = inPort.getControls();
    for (int i = 0; i < controls.length; i++) {
      if (controls[i] instanceof CompoundControl) {
        Control[] members = ((CompoundControl) controls[i]).getMemberControls();
        for (int j = 0; j < members.length; j++) {
          setCtrl(members[j]);
        } // for int j
      } // if
      else
        setCtrl(controls[i]);
    } // for i
    inPort.close();
  }

  private static void setCtrl(Control ctl) {
    if (ctl.getType().toString().equals("Select")) {
      ((BooleanControl) ctl).setValue(true);
    }
    if (ctl.getType().toString().equals("Volume")) {
      FloatControl vol = (FloatControl) ctl;
      float setVal = vol.getMinimum() + (vol.getMaximum() - vol.getMinimum()) * RECORD_VOLUME_LEVEL;
      vol.setValue(setVal);
    }
  }

  public static void playAudio(String audioFile) throws Exception {
    File songFile = new File(audioFile);
    AudioInputStream AIS = null;
    AIS = AudioSystem.getAudioInputStream(songFile);
    AudioFormat myFormat = AIS.getFormat();
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, myFormat, AudioSystem.NOT_SPECIFIED);
    if (!AudioSystem.isLineSupported(info)) {
      // mp3 will go through here
      // – decoded to PCM
      AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, myFormat.getSampleRate(), 16, // sample size in bits
          myFormat.getChannels(), myFormat.getChannels() * 2, myFormat.getSampleRate(), false); // big endian?
      // new stream is decoded
      AIS = AudioSystem.getAudioInputStream(decodedFormat, AIS);
      myFormat = AIS.getFormat();
      // new format
      SourceDataLine playbackLine = null;
      playbackLine = AudioSystem.getSourceDataLine(myFormat);
      // get default line
      playbackLine.open(myFormat);
      playbackLine.start();
      int bytesRead = 0;
      byte[] buffer = new byte[128000];
      while (bytesRead != -1) {
        bytesRead = AIS.read(buffer, 0, buffer.length);
        if (bytesRead >= 0)
          playbackLine.write(buffer, 0, bytesRead);
      }
      playbackLine.drain();
      playbackLine.close();
    }
  }

  public static void probePort() throws Exception {
    ArrayList<Mixer.Info> mixerInfos = new ArrayList<Mixer.Info>(Arrays.asList(AudioSystem.getMixerInfo()));
    Line.Info portInfo = new Line.Info(Port.class);
    for (Mixer.Info mixerInfo : mixerInfos) {
      Mixer mixer = AudioSystem.getMixer(mixerInfo);

      if (mixer.isLineSupported(portInfo)) {
        // found a Port Mixer
        disp("Found mixer: " + mixerInfo.getName());
        disp("\t" + mixerInfo.getDescription());
        disp("Source Line Supported:");
        ArrayList<Line.Info> srcInfos = new ArrayList<Line.Info>(Arrays.asList(mixer.getSourceLineInfo()));
        for (Line.Info srcInfo : srcInfos) {
          Port.Info pi = (Port.Info) srcInfo;
          disp("\t" + pi.getName() + ", " + (pi.isSource() ? "source" : "target"));
          showControls(mixer.getLine(srcInfo));
        } // of for Line.Info
        disp("Target Line Supported:");
        ArrayList<Line.Info> targetInfos = new ArrayList<Line.Info>(Arrays.asList(mixer.getTargetLineInfo()));
        for (Line.Info targetInfo : targetInfos) {
          Port.Info pi = (Port.Info) targetInfo;
          disp("\t" + pi.getName() + ", " + (pi.isSource() ? "source" : "taget"));
          showControls(mixer.getLine(targetInfo));
        }
      } // of if
    }
  }

  public static void showMixers() {
    ArrayList<Mixer.Info> mixInfos = new ArrayList<Mixer.Info>(Arrays.asList(AudioSystem.getMixerInfo()));
    Line.Info sourceDLInfo = new Line.Info(SourceDataLine.class);
    Line.Info targetDLInfo = new Line.Info(TargetDataLine.class);
    Line.Info clipInfo = new Line.Info(Clip.class);
    Line.Info portInfo = new Line.Info(Port.class);
    String support;
    for (Mixer.Info mixInfo : mixInfos) {
      Mixer mixer = AudioSystem.getMixer(mixInfo);
      support = ", supports ";
      if (mixer.isLineSupported(sourceDLInfo))
        support += "SourceDataLine ";
      if (mixer.isLineSupported(clipInfo))
        support += "Clip ";
      if (mixer.isLineSupported(targetDLInfo))
        support += "TargetDataLine ";
      if (mixer.isLineSupported(portInfo))
        support += "Port ";
      System.out.println("Mixer: " + mixInfo.getName() + support + ", " + mixInfo.getDescription());
    }
  }

  private static void showControls(Line inLine) throws Exception {
    // must open the line to get
    // at controls
    inLine.open();
    disp("\t\tAvailable controls:");
    ArrayList<Control> ctrls = new ArrayList<Control>(Arrays.asList(inLine.getControls()));
    for (Control ctrl : ctrls) {
      disp("\t\t\t" + ctrl.toString());
      if (ctrl instanceof CompoundControl) {
        CompoundControl cc = ((CompoundControl) ctrl);
        ArrayList<Control> ictrls = new ArrayList<Control>(Arrays.asList(cc.getMemberControls()));
        for (Control ictrl : ictrls)
          disp("\t\t\t\t" + ictrl.toString());
      } // of if (ctrl instanceof)
    } // of for(Control ctrl)
    inLine.close();
  }

  private static void disp(String msg) {
    System.out.println(msg);
  }

}
