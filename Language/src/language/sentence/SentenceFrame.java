package language.sentence;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import language.*;
import language.letter.*;
import language.phrase.*;
import language.word.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SentenceFrame extends JFrame {
  private final static DateFormat df = new SimpleDateFormat("HH:mm:ss z");
  private intelligence.Ego ego;
  private JPanel jPanel1 = new JPanel();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JScrollPane jScrollPane2 = new JScrollPane();
  private JTextArea jTextArea1 = new JTextArea();
  private JSplitPane jSplitPane1 = new JSplitPane();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JTextArea jTextArea2 = new JTextArea();

  public SentenceFrame() {
    try {
      jbInit();
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public SentenceFrame(intelligence.Ego ego){
    this();
    this.ego = ego;
    this.validate();
    this.setSize(new Dimension(600, 401));
    this.setTitle("Sentence");
    this.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        System.exit(0);
      }
    });
    this.setLocation(430, 0);
    this.setVisible(true);
  }

  private void init(){
    try {
      ObjectInputStream load = new ObjectInputStream(new FileInputStream("session.sav"));
      jTextArea1.setText((String)load.readObject());
      load.close();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    catch (ClassNotFoundException ex){
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLocale(new java.util.Locale("ru", "", ""));
    this.setState(Frame.NORMAL);
    this.setTitle("Предложение");
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
    });
    jPanel1.setLayout(gridBagLayout1);
//    String str = "";
//    str+="другой газ.\n";
//    str+="объект.\n";
//    str+="часть речи - это объект.\n";
//    str+="часть речи - это объект.\n";
//    str+="частица - это часть речи.\n";
//    str+="\"это\" - это частица.\n";
//    str+="\"тире\" - это неизменяемое существительное среднего рода.\n";
//    str+="стул - это мебель.\n";

/*    str+="фраза - это объект.\n";
    str+="роза - это объект.\n";
    str+="газ - это фраза.\n";
    str+="поза - это объект.\n";
    str+="другой газ - это поза.\n";
    str+="роза - это газ.\n";
    str+="фраза.\n";
    str+="роза - это не газ.\n";*/
//    str+="открой папку \"e:\\\".";
//    jTextArea1.setText(str);
    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jTextArea2.setEditable(false);
    jTextArea2.setTabSize(2);
    jTextArea1.setText("");
    jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        jTextArea1_keyTyped(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jSplitPane1,    new GridBagConstraints(0, 0, 1, 1, 0.1, 0.1
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jSplitPane1.add(jScrollPane1, JSplitPane.BOTTOM);
    jSplitPane1.add(jScrollPane2, JSplitPane.TOP);
    jScrollPane2.getViewport().add(jTextArea1, null);
    jScrollPane1.getViewport().add(jTextArea2, null);
    jSplitPane1.setDividerLocation(100);
  }

  private void read(){
    char[] ch = jTextArea1.getText().toCharArray();
    String str = jTextArea2.getText()+System.getProperty("user.name")+" ("+df.format(new Date())+")"+" :\n\t";
    for (int i = 0; i < ch.length; i++) {
      ego.send(new Letter(ch[i]));
      str+=ch[i];
      if (ch[i]=='\n' && i!=ch.length-1)
        str+='\t';
    }
    str+='\n';
    jTextArea2.setText(str);
//    jTextArea1.setText("");
  }

  public static void main(String[] args){
    SentenceFrame frame = new SentenceFrame();
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

  private void jTextArea1_keyTyped(KeyEvent e) {
    if (e.getKeyChar()=='\n' && e.getModifiers()==e.CTRL_MASK){
      read();
    }
  }

  private void this_windowClosing(WindowEvent e) {
    try{
      ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream(
          "session.sav"));
      save.writeObject(jTextArea1.getText());
      save.close();
    } catch (IOException ex){
      ex.printStackTrace();
    }
  }
}