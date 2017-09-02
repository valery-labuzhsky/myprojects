package intelligence.util;

import intelligence.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CreateDialog extends JDialog {
  JPanel panel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JTextField jTextField1 = new JTextField();
  JLabel jLabel1 = new JLabel();
  JPanel jPanel1 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JPanel jPanel2 = new JPanel();

  public CreateDialog(Frame frame, boolean modal) {
    super(frame, "Создание объекта", modal);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public CreateDialog() {
    this(null, false);
  }

  String name = null;
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();

  public String getObjectName(){
    return name;
  }

  private void jbInit() throws Exception {
    panel1.setLayout(gridBagLayout1);
    jTextField1.setSelectionStart(11);
    jTextField1.addKeyListener(new CreateDialog_jTextField1_keyAdapter(this));
    jLabel1.setFont(new java.awt.Font("MS Sans Serif", 0, 11));
    jLabel1.setText("Имя");
    jButton1.setAlignmentX((float) 0.5);
    jButton1.setFont(new java.awt.Font("MS Sans Serif", 0, 11));
    jButton1.setText("Отмена");
    jButton1.addActionListener(new CreateDialog_jButton1_actionAdapter(this));
    jButton2.setFont(new java.awt.Font("MS Sans Serif", 0, 11));
    jButton2.setText("Угу");
    jButton2.addActionListener(new CreateDialog_jButton2_actionAdapter(this));
    jPanel2.setLayout(gridBagLayout2);
    jPanel1.setLayout(gridBagLayout3);
    panel1.setMinimumSize(new Dimension(200, 62));
    panel1.setPreferredSize(new Dimension(200, 62));
    getContentPane().add(panel1);
    panel1.add(jPanel1,                new GridBagConstraints(0, 1, 1, 1, 0.1, 0.1
            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jButton2,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jButton1,      new GridBagConstraints(1, 0, 1, 1, 0.1, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    panel1.add(jPanel2,        new GridBagConstraints(0, 0, 1, 1, 0.1, 0.1
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jLabel1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
    jPanel2.add(jTextField1,     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  private void create(){
    if (jTextField1.getText().trim().equals("")){
      JOptionPane.showMessageDialog(this, "Нельзя создать пустой объект", "Внимание!", JOptionPane.WARNING_MESSAGE);
    } else {
      name = jTextField1.getText().trim();
      this.dispose();
    }
  }

  void jButton2_actionPerformed(ActionEvent e) {
    create();
  }

  void jTextField1_keyTyped(KeyEvent e) {
    if (e.getKeyChar()=='\n') create();
  }
}

class CreateDialog_jButton1_actionAdapter implements java.awt.event.ActionListener {
  CreateDialog adaptee;

  CreateDialog_jButton1_actionAdapter(CreateDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class CreateDialog_jButton2_actionAdapter implements java.awt.event.ActionListener {
  CreateDialog adaptee;

  CreateDialog_jButton2_actionAdapter(CreateDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton2_actionPerformed(e);
  }
}

class CreateDialog_jTextField1_keyAdapter extends java.awt.event.KeyAdapter {
  CreateDialog adaptee;

  CreateDialog_jTextField1_keyAdapter(CreateDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.jTextField1_keyTyped(e);
  }
}