package netlis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import netlis.ui.NetlisUI;

public class Netlis {
  private static Logger logger = LogManager.getLogger(Netlis.class);

  public static void main(String[] args) {
    NetlisUI ui = new NetlisUI();
    
    ui.render();
  }
}
