package intelligence.util;

import intelligence.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
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

public class PhraseFrame extends JFrame {
  JTree jTree1 = new JTree();
  public PhraseFrame(String title) {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.validate();
    this.setSize(new Dimension(600, 401));
    this.setTitle(title);
    this.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        System.exit(0);
      }
    });
    this.setLocation(0, 401);
    this.setVisible(true);
  }

  private boolean update = true;
  public void setRoot(intelligence.Object root){
    jTree1.setModel(new DefaultTreeModel(new SentenceTreeNode(null, "", root)));
    update = true;
  }

  private void jbInit() throws Exception {
    jTree1.setModel(new DefaultTreeModel(new SentenceTreeNode(null, "", intelligence.Object.unknown)));
    this.getContentPane().add(jTree1, BorderLayout.CENTER);
  }

  private class SentenceTreeNode implements TreeNode{
    private LinkedList children = null;
    private Objects values = new Objects();
    private TreeNode parent;
    private intelligence.Object phrase;
    private String note;

    public SentenceTreeNode(TreeNode parent, String note, intelligence.Object phrase, Objects values){
      this(parent, note, phrase);
      this.values = values;
    }

    public SentenceTreeNode(TreeNode parent, String note, intelligence.Object phrase){
      this.parent = parent;
      this.note = note;
      this.phrase = phrase;
    }

    private void setChildren(){
      if (children==null){
        children = new LinkedList();
        intelligence.Object[] sup;
        sup = phrase.getAttributeKeys();
        for (int i = 0; i < sup.length; i++) {
          children.add(new SentenceTreeNode(this, "(Key)", sup[i],
                                            phrase.getAttributes(sup[i])));
        }
        sup = phrase.getClassAttributeKeys();
        for (int i = 0; i < sup.length; i++) {
          children.add(new SentenceTreeNode(this, "(CKey)", sup[i],
                                            phrase.getClassAttributes(sup[i])));
        }
        sup = phrase.getSuperObjects();
        for (int i = 0; i < sup.length; i++) {
          children.add(new SentenceTreeNode(this, "(Super)", sup[i]));
        }

        Iterator iter = values.iterator();
        while (iter.hasNext()) {
          intelligence.Object item = (intelligence.Object) iter.next();
          this.children.addFirst(new SentenceTreeNode(this, "", item));
        }
      }
    }

    private class PhraseEnumeration implements Enumeration{
      private Iterator iter;

      public PhraseEnumeration(Iterator iter){
        this.iter = iter;
      }

      public java.lang.Object nextElement(){
        return iter.next();
      }

      public boolean hasMoreElements(){
        return iter.hasNext();
      }
    }

    public Enumeration children(){
      setChildren();
      return new PhraseEnumeration(children.iterator());
    }

    public boolean getAllowsChildren(){
      setChildren();
      return !children.isEmpty();
    }

    public TreeNode getChildAt(int index){
      setChildren();
      return (TreeNode)children.get(index);
    }

    public int getChildCount(){
      setChildren();
      return children.size();
    }

    public int getIndex(TreeNode node){
      setChildren();
      return children.indexOf(node);
    }

    public TreeNode getParent(){
      return parent;
    }

    public boolean isLeaf(){
      setChildren();
      return children.isEmpty();
    }

    public String toString(){
      String str = "";
      str+= note+phrase.toString();
      return str;
    }
  }

}
