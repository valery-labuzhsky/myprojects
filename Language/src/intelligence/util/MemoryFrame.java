package intelligence.util;

import intelligence.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MemoryFrame extends JFrame {
  private Memory memory;

  private JSplitPane jSplitPane1 = new JSplitPane();
  private JTree jTree1 = new JTree();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JTree jTree2 = new JTree();
  private JScrollPane jScrollPane2 = new JScrollPane();
  private JMenuBar jMenuBar1 = new JMenuBar();
  private JMenu jMenu1 = new JMenu();
  private JMenuItem jMenuItem1 = new JMenuItem();
  private JMenuItem jMenuItem2 = new JMenuItem();
  private JMenuItem jMenuItem3 = new JMenuItem();

  public MemoryFrame(Memory memory) {
    this.memory = memory;
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.validate();
    this.setSize(new Dimension(600, 401));
    this.setTitle("Память");
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    this.setVisible(true);
  }

  private void jbInit() throws Exception {
    jSplitPane1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    jSplitPane1.setLocale(java.util.Locale.getDefault());
    jSplitPane1.setName("");
    jSplitPane1.setMaximumSize(new Dimension(2147483647, 2147483647));
    jSplitPane1.setMinimumSize(new Dimension(49, 23));
    jSplitPane1.setPreferredSize(new Dimension(181, 66));
    jSplitPane1.setContinuousLayout(false);
    jSplitPane1.setOneTouchExpandable(false);
    jTree1.setMaximumSize(new Dimension(150, 100));
    jTree1.setMinimumSize(new Dimension(1000, 100));
    jTree1.setOpaque(true);
    jTree1.setPreferredSize(new Dimension(150, 100));
    jTree1.setRowHeight(16);
    jTree1.setScrollsOnExpand(false);
    jTree1.setVisibleRowCount(20);
    jTree1.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
      public void treeExpanded(TreeExpansionEvent e) {
        jTree1_treeExpanded(e);
      }
      public void treeCollapsed(TreeExpansionEvent e) {
	jTree1_treeCollapsed(e);
      }
    });
    jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jTree1_mouseClicked(e);
      }
    });
    jMenu1.setFont(new java.awt.Font("Tahoma", 0, 11));
    jMenu1.setText("Файл");
    jMenuItem1.setFont(new java.awt.Font("Tahoma", 0, 11));
    jMenuItem1.setText("Сохранить");
    jMenuItem1.addActionListener(new MemoryFrame_jMenuItem1_actionAdapter(this));
    jMenuItem2.setFont(new java.awt.Font("Tahoma", 0, 11));
    jMenuItem2.setText("Сохранить как ...");
    jMenuItem2.addActionListener(new MemoryFrame_jMenuItem2_actionAdapter(this));
    jMenuItem3.setFont(new java.awt.Font("Tahoma", 0, 11));
    jMenuItem3.setText("Открыть");
    jMenuItem3.addActionListener(new MemoryFrame_jMenuItem3_actionAdapter(this));
    jMenuItem4.setFont(new java.awt.Font("Tahoma", 0, 11));
    jMenuItem4.setText("Новый");
    jMenuItem4.addActionListener(new MemoryFrame_jMenuItem4_actionAdapter(this));
    jMenuItem5.setFont(new java.awt.Font("Tahoma", 0, 11));
    jMenuItem5.setText("Создать");
    jMenuItem5.addActionListener(new MemoryFrame_jMenuItem5_actionAdapter(this));
    jMenuItem6.setFont(new java.awt.Font("Tahoma", 0, 11));
    jMenuItem6.setText("addSuperObject");
    jMenuItem6.addActionListener(new MemoryFrame_jMenuItem6_actionAdapter(this));
    jMenuItem7.setFont(new java.awt.Font("Tahoma", 0, 11));
    jMenuItem7.setText("addClassAttribute");
    jMenuItem7.addActionListener(new MemoryFrame_jMenuItem7_actionAdapter(this));
    jMenuItem8.setFont(new java.awt.Font("Tahoma", 0, 11));
    jMenuItem8.setText("addAttribute");
    jMenuItem8.addActionListener(new MemoryFrame_jMenuItem8_actionAdapter(this));
    this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
    this.getContentPane().add(jMenuBar1, BorderLayout.NORTH);
    jTree1.setModel(new DefaultTreeModel(new ClassTreeNode(null, intelligence.Object.unknown)));
    jTree2.setModel(new DefaultTreeModel(new SentenceTreeNode(null, "", intelligence.Object.unknown)));
    jScrollPane1.getViewport().add(jTree1);
    jSplitPane1.add(jScrollPane1, JSplitPane.TOP);
    jScrollPane2.getViewport().add(jTree2);
    jSplitPane1.add(jScrollPane2, JSplitPane.BOTTOM);
    jMenuBar1.add(jMenu1);
    jMenu1.add(jMenuItem4);
    jMenu1.add(jMenuItem1);
    jMenu1.add(jMenuItem2);
    jMenu1.add(jMenuItem3);
    jPopupMenu1.add(jMenuItem5);
    jPopupMenu1.add(jMenuItem6);
    jPopupMenu1.add(jMenuItem7);
    jPopupMenu1.add(jMenuItem8);
    jSplitPane1.setDividerLocation(300);
  }

  public static void main(String[] args) {
    MemoryFrame mf = new MemoryFrame(new Memory());
    while (true) {
      try {
//        mf.updateUI();
//        mf.dispatchEvent(new WindowEvent(mf, WindowEvent.WINDOW_STATE_CHANGED));
        Thread.currentThread().sleep(1000);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private intelligence.Object selected = null;

  private void jTree1_mouseClicked(MouseEvent e) {
    if (e.getButton()==e.BUTTON1) {
      TreePath path = jTree1.getPathForLocation(e.getX(), e.getY());
      if (path!=null) {
        intelligence.Object obj = ((ClassTreeNode)path.getLastPathComponent()).
                                  getObject();
        jTree2.setModel(new DefaultTreeModel(new SentenceTreeNode(null, "", obj)));
      }
    } else if (e.getButton()==e.BUTTON3) {
      TreePath path = jTree1.getPathForLocation(e.getX(), e.getY());
      if (path!=null) {
        selected = ((ClassTreeNode)path.getLastPathComponent()).getObject();
        jPopupMenu1.show(this, e.getX(),
                         e.getY()+jPopupMenu1.getPreferredSize().height/2);
      }
    }
  }

  public void updateUI() {
    jTree1.setModel(new DefaultTreeModel(new ClassTreeNode(null, memory.getRootObject())));
    jTree2.setModel(new DefaultTreeModel(new SentenceTreeNode(null, "", memory.getRootObject())));
    setSizeTo_jTree1();
  }

  public void setFile(File file) {
    this.file = file;
  }

  File file = null;
  JMenuItem jMenuItem4 = new JMenuItem();
  JPopupMenu jPopupMenu1 = new JPopupMenu();
  JMenuItem jMenuItem5 = new JMenuItem();
  JMenuItem jMenuItem6 = new JMenuItem();
  JMenuItem jMenuItem7 = new JMenuItem();
  JMenuItem jMenuItem8 = new JMenuItem();
  void jMenuItem2_actionPerformed(ActionEvent e) {
    FileDialog fd = new FileDialog(this, "Сохранить", FileDialog.SAVE);
    fd.setDirectory("memory");
    fd.setFilenameFilter(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".mem");
      }
    });
    fd.show();
    if (fd.getFile()==null) {
      return;
    }
    String name = fd.getDirectory()+fd.getFile();
    if (!name.endsWith(".mem")) {
      name += ".mem";
    }
    file = new File(name);
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      memory.save(file);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  void jMenuItem3_actionPerformed(ActionEvent e) {
    FileDialog fd = new FileDialog(this, "Открыть", FileDialog.LOAD);
    fd.setDirectory("memory");
    fd.show();
    if (fd.getFile()==null) {
      return;
    }
    file = new File(fd.getDirectory()+fd.getFile());
    try {
      memory.load(file);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  void jMenuItem1_actionPerformed(ActionEvent e) {
    if (file==null) {
      jMenuItem2_actionPerformed(e);
    }
    try {
      memory.save(file);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  void jMenuItem4_actionPerformed(ActionEvent e) {
    memory.clear();
    file = null;
  }

  void jMenuItem5_actionPerformed(ActionEvent e) {
    intelligence.Object sel = selected;
    CreateDialog cd = new CreateDialog(this, true);
    int x = this.getLocation().x+this.getWidth()/2-cd.getPreferredSize().width/2;
    int y = this.getLocation().y+this.getHeight()/2-cd.getPreferredSize().height/2;
    cd.setLocation(x, y);
    cd.show();
    String name = cd.getObjectName();
    if (name!=null) {
      new intelligence.Object(name).addSuperObject(sel);
      updateUI();
    }
  }

  void jMenuItem6_actionPerformed(ActionEvent e) {
    intelligence.Object sel = selected;
    SelectDialog sd = new SelectDialog(this, "SuperObject", true, memory);
    int x = this.getLocation().x+this.getWidth()/2-sd.getPreferredSize().width/2;
    int y = this.getLocation().y+this.getHeight()/2-sd.getPreferredSize().height/2;
    sd.setLocation(x, y);
    sd.show();
    Objects so = sd.getSelectedObjects();
    if (so!=null) {
      Iterator iter = so.iterator();
      while (iter.hasNext()) {
        intelligence.Object item = (intelligence.Object)iter.next();
        try {
          sel.addSuperObject(item);
        } catch (CyclicInheritanceInvolvingClassException ex) {
          JOptionPane.showMessageDialog(this, item, "Cyclic Inheritance Involving Class", JOptionPane.ERROR_MESSAGE);
        }
      }
      updateUI();
    }
  }

  void jMenuItem7_actionPerformed(ActionEvent e) {
    intelligence.Object sel = selected;
    SelectDialog sd = new SelectDialog(this, "Ключь", true, memory);
    int x = this.getLocation().x+this.getWidth()/2-sd.getPreferredSize().width/2;
    int y = this.getLocation().y+this.getHeight()/2-sd.getPreferredSize().height/2;
    sd.setLocation(x, y);
    sd.show();
    Objects objs = sd.getSelectedObjects();
    if (objs==null) {
      return;
    }
    intelligence.Object key = objs.getFirst();
    sd = new SelectDialog(this, "Значение", true, memory);
    x = this.getLocation().x+this.getWidth()/2-sd.getPreferredSize().width/2;
    y = this.getLocation().y+this.getHeight()/2-sd.getPreferredSize().height/2;
    sd.setLocation(x, y);
    sd.show();
    objs = sd.getSelectedObjects();
    if (objs==null) {
      return;
    }
    intelligence.Object value = objs.getFirst();
    sel.addClassAttribute(key, value);
    updateUI();
  }

  void jMenuItem8_actionPerformed(ActionEvent e) {
    intelligence.Object sel = selected;
    SelectDialog sd = new SelectDialog(this, "Ключь", true, memory);
    int x = this.getLocation().x+this.getWidth()/2-sd.getPreferredSize().width/2;
    int y = this.getLocation().y+this.getHeight()/2-sd.getPreferredSize().height/2;
    sd.setLocation(x, y);
    sd.show();
    Objects objs = sd.getSelectedObjects();
    if (objs==null) {
      return;
    }
    intelligence.Object key = objs.getFirst();
    sd = new SelectDialog(this, "Значение", true, memory);
    x = this.getLocation().x+this.getWidth()/2-sd.getPreferredSize().width/2;
    y = this.getLocation().y+this.getHeight()/2-sd.getPreferredSize().height/2;
    sd.setLocation(x, y);
    sd.show();
    objs = sd.getSelectedObjects();
    if (objs==null) {
      return;
    }
    intelligence.Object value = objs.getFirst();
    sel.addAttribute(key, value);
    updateUI();
  }

  private void jTree1_treeCollapsed(TreeExpansionEvent e) {
    setSizeTo_jTree1();
  }

  private void jTree1_treeExpanded(TreeExpansionEvent e) {
    setSizeTo_jTree1();
  }

  private void setSizeTo_jTree1(){
    int y = jTree1.getRowCount()*jTree1.getRowHeight();
    int x = 0;
    for (int i=0; i<jTree1.getRowCount(); i++) {
      x = Math.max(x, (int)jTree1.getRowBounds(i).getWidth()+(int)jTree1.getRowBounds(i).getX());
    }
    jTree1.setPreferredSize(new Dimension(x, y));
  }
}

class MemoryFrame_jMenuItem2_actionAdapter implements java.awt.event.ActionListener {
  MemoryFrame adaptee;

  MemoryFrame_jMenuItem2_actionAdapter(MemoryFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem2_actionPerformed(e);
  }
}

class MemoryFrame_jMenuItem3_actionAdapter implements java.awt.event.ActionListener {
  MemoryFrame adaptee;

  MemoryFrame_jMenuItem3_actionAdapter(MemoryFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem3_actionPerformed(e);
  }
}

class MemoryFrame_jMenuItem1_actionAdapter implements java.awt.event.ActionListener {
  MemoryFrame adaptee;

  MemoryFrame_jMenuItem1_actionAdapter(MemoryFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem1_actionPerformed(e);
  }
}

class MemoryFrame_jMenuItem4_actionAdapter implements java.awt.event.ActionListener {
  MemoryFrame adaptee;

  MemoryFrame_jMenuItem4_actionAdapter(MemoryFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem4_actionPerformed(e);
  }
}

class MemoryFrame_jMenuItem5_actionAdapter implements java.awt.event.ActionListener {
  MemoryFrame adaptee;

  MemoryFrame_jMenuItem5_actionAdapter(MemoryFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem5_actionPerformed(e);
  }
}

class MemoryFrame_jMenuItem6_actionAdapter implements java.awt.event.ActionListener {
  MemoryFrame adaptee;

  MemoryFrame_jMenuItem6_actionAdapter(MemoryFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem6_actionPerformed(e);
  }
}

class MemoryFrame_jMenuItem7_actionAdapter implements java.awt.event.ActionListener {
  MemoryFrame adaptee;

  MemoryFrame_jMenuItem7_actionAdapter(MemoryFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem7_actionPerformed(e);
  }
}

class MemoryFrame_jMenuItem8_actionAdapter implements java.awt.event.ActionListener {
  MemoryFrame adaptee;

  MemoryFrame_jMenuItem8_actionAdapter(MemoryFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem8_actionPerformed(e);
  }
}