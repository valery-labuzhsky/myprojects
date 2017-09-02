package language.word;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class WordFrame extends JFrame {
  private JPanel jPanel1 = new JPanel();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JTextField word = new JTextField();
  private JButton read = new JButton();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JTextArea jTextArea1 = new JTextArea();

  public WordFrame() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setTitle("Существительное");
    jPanel1.setLayout(gridBagLayout1);
    read.setText("Прочитать");
    read.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        read_actionPerformed(e);
      }
    });
    word.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        word_keyPressed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(word,    new GridBagConstraints(0, 0, 1, 1, 0.1, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(read,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jScrollPane1,     new GridBagConstraints(0, 1, 2, 1, 0.1, 0.1
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(jTextArea1, null);
  }

  void read_actionPerformed(ActionEvent e) {
    jTextArea1.setText(read(word.getText()));
  }

  String read(String word){
    String ret = "";
    intelligence.Memory mem = null;
    try {
      mem = new intelligence.Memory();
      mem.load(new java.io.File("memory/memory.mem"));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    intelligence.Objects aw = Word.getPossibleWords(mem, word);
    Iterator iter = aw.iterator();
    while (iter.hasNext()) {
      Object item = iter.next();
      ret+= item+"\n";
    }
    return ret;
  }

  public static void main(String[] args){
    WordFrame frame = new WordFrame();
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    frame.validate();
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setSize(600,500);
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        System.exit(0);
      }
    });
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }

  void word_keyPressed(KeyEvent e) {
    if (e.getKeyCode()==e.VK_ENTER){
      jTextArea1.setText(read(word.getText()));
    }
  }
}