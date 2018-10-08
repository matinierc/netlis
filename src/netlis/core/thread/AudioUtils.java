package netlis.core.thread;

import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class AudioUtils extends Thread {

  public static ArrayList<Mixer> getMixers() {
    Mixer.Info[] infos = AudioSystem.getMixerInfo();
    ArrayList<Mixer> mixers = new ArrayList<Mixer>();

    for (Mixer.Info info : infos) {
      mixers.add(AudioSystem.getMixer(info));
    }

    return mixers;
  }
}
