package netlis.ui;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetlisUI {
  private static Logger logger = LogManager.getLogger(NetlisUI.class);

  protected final JFrame frame = new JFrame();
  
  public void render() {
    logger.debug("Render");
    
    frame.setTitle(Parameter.UI_TITLE);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(0, 0, (int) Parameter.UI_SIZE.getWidth(), (int) Parameter.UI_SIZE.getHeight());
    frame.setVisible(true);
  }
}
