package netlis.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import netlis.core.thread.AudioInputListener;

public class NetlisUI {

  private static Logger logger = LogManager.getLogger(NetlisUI.class);

  private final JFrame frame = new JFrame();

  private JButton startButton;

  private JButton stopButton;

  private JComboBox<Mixer.Info> mixerComboBox;

  private JComboBox<Line.Info> lineComboBox;

  private JComboBox<AudioFormat> audioFormatComboBox;

  private Thread thread;
  
  private AudioFormat selectedAudioFormat;

  public NetlisUI() {
    super();
  }

  private JButton getStartButton() {
    startButton = new JButton("Start");
    startButton.setEnabled(false);
    startButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        thread = new Thread(new AudioInputListener(selectedAudioFormat));
        thread.start();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
      }
    });

    return startButton;
  }

  private JButton getStopButton() {
    stopButton = new JButton("Stop");
    stopButton.setEnabled(false);
    stopButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        thread.interrupt();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
      }
    });

    return stopButton;
  }

  private JComboBox<Mixer.Info> getMixerComboBox() {
    mixerComboBox = new JComboBox<>(AudioSystem.getMixerInfo());
    mixerComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox<Mixer.Info> comboBox = (JComboBox<Mixer.Info>) e.getSource();
        Mixer.Info mixerInfo = (Mixer.Info) comboBox.getSelectedItem();
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
        Line.Info[] linesInfo = mixer.getSourceLineInfo();

        lineComboBox.removeAllItems();
        for (Line.Info lineInfo : linesInfo) {
          lineComboBox.addItem(lineInfo);
        }
        lineComboBox.setEnabled(linesInfo.length > 0);
      }
    });

    return mixerComboBox;
  }

  private JComboBox<Line.Info> getLineComboBox() {
    lineComboBox = new JComboBox<>();
    lineComboBox.setEnabled(false);
    lineComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox<Line.Info> comboBox = (JComboBox<Line.Info>) e.getSource();
        Line.Info lineInfo = (Line.Info) comboBox.getSelectedItem();
        AudioFormat[] formats = ((DataLine.Info) lineInfo).getFormats();

        audioFormatComboBox.removeAllItems();
        for (AudioFormat audioFormat : formats) {
          audioFormatComboBox.addItem(audioFormat);
        }
        audioFormatComboBox.setEnabled(formats.length > 0);
      }
    });
    return lineComboBox;
  }

  private JComboBox<AudioFormat> getAudioFormatComboBox() {
    audioFormatComboBox = new JComboBox<>();
    audioFormatComboBox.setEnabled(false);
    audioFormatComboBox.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        JComboBox<AudioFormat> comboBox = (JComboBox<AudioFormat>) e.getSource();
        selectedAudioFormat = (AudioFormat) comboBox.getSelectedItem();
        startButton.setEnabled(selectedAudioFormat != null);
      }
    });
    return audioFormatComboBox;
  }

  private JPanel getComboBoxPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.fill = GridBagConstraints.HORIZONTAL;

    panel.add(new JLabel("Mixer: "), c);
    panel.add(getMixerComboBox(), c);
    c.gridy = 1;
    panel.add(new JLabel("Line: "), c);
    panel.add(getLineComboBox(), c);
    c.gridy = 2;
    panel.add(new JLabel("Format: "), c);
    panel.add(getAudioFormatComboBox(), c);

    return panel;
  }

  private JPanel getButtonPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.fill = GridBagConstraints.HORIZONTAL;

    panel.add(getStartButton(), c);
    panel.add(getStopButton(), c);

    return panel;
  }

  private JPanel getContent() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = 0;
    panel.add(getButtonPanel(), c);
    c.gridy = 1;
    panel.add(getComboBoxPanel(), c);

    return panel;
  }

  public void render() {
    logger.debug("Render");

    frame.setTitle(Parameter.UI_TITLE);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(0, 0, (int) Parameter.UI_SIZE.getWidth(), (int) Parameter.UI_SIZE.getHeight());

    frame.add(getContent());

    frame.setVisible(true);
  }
}
