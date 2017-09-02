package intelligence.util;

import intelligence.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SelectDialog extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTree jTree1 = new JTree();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  Objects objects = null;
  Memory memory;

  public SelectDialog(Frame frame, String title, boolean modal, Memory memory) {
    super(frame, title, modal);
    this.memory = memory;
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public SelectDialog() {
    this(null, "", false, new Memory());
  }
  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jButton1.setFont(new java.awt.Font("MS Sans Serif", 0, 11));
    jButton1.setText("Угу");
    jButton1.addActionListener(new SelectDialog_jButton1_actionAdapter(this));
    jButton2.setFont(new java.awt.Font("MS Sans Serif", 0, 11));
    jButton2.setText("Отмена");
    jButton2.addActionListener(new SelectDialog_jButton2_actionAdapter(this));
    jPanel1.setLayout(gridBagLayout1);
    panel1.setMinimumSize(new Dimension(300, 400));
    panel1.setPreferredSize(new Dimension(300, 400));
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(jButton1,   new GridBagConstraints(0, 0, 1, 1, 0.1, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jButton2,    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    panel1.add(jScrollPane1, BorderLayout.CENTER);
    jTree1.setModel(new DefaultTreeModel(new ClassTreeNode(null, memory.getRootObject())));
    jScrollPane1.getViewport().add(jTree1, null);
  }

  void jButton2_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  public Objects getSelectedObjects(){
    return objects;
  }

  void jButton1_actionPerformed(ActionEvent e) {
    TreePath[] pathes = jTree1.getSelectionPaths();
    if (pathes==null) return;
    objects = new Objects();
    for (int i = 0; i < pathes.length; i++) {
      objects.add(((ClassTreeNode)pathes[i].getLastPathComponent()).getObject());
    }
    this.dispose();
  }
}

class SelectDialog_jButton2_actionAdapter implements java.awt.event.ActionListener {
  SelectDialog adaptee;

  SelectDialog_jButton2_actionAdapter(SelectDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton2_actionPerformed(e);
  }
}

class SelectDialog_jButton1_actionAdapter implements java.awt.event.ActionListener {
  SelectDialog adaptee;

  SelectDialog_jButton1_actionAdapter(SelectDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}