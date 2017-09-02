package intelligence.instinct;

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

public class FileManager extends JFrame implements Device{
  private java.io.File cd = null;
  private java.io.File[] children;
  private JScrollPane jScrollPane1 = new JScrollPane();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private FileTreeNode rootFile = new FileTreeNode();
  private FileTreeNode curFile = rootFile;
  private JTree jTree1 = new JTree(rootFile, false);

  private class FileTreeNode implements TreeNode{
    private Vector children = null;
    private FileTreeNode parent = null;
    private java.io.File file = null;

    public FileTreeNode(){
    }

    public FileTreeNode(FileTreeNode parent, java.io.File file){
      this.parent = parent;
      this.file = file;
    }

    private void setChildren(){
      if (children == null){
        children = new Vector();
        java.io.File[] chn = null;
        if (file == null){
          chn = java.io.File.listRoots();
        } else {
          chn = file.listFiles();
        }
        if (chn!=null){
          for (int i=0;i<chn.length;i++){
            children.add(new FileTreeNode(this, chn[i]));
          }
        }
      }
    }

    public Enumeration children(){
      setChildren();
      return children.elements();
    }

    public boolean getAllowsChildren(){
      return file==null||file.getParent()==null||file.isDirectory();
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
      return file!=null&&file.getParent()!=null&&file.isFile();
    }

    public String toString(){
      if (file==null) return "root";
      if (file.getName().equals("")) return file.getPath();
      return file.getName();
    }

    public FileTreePath getTreePath(){
      if (parent==null) return new FileTreePath(this);
      return new FileTreePath(parent.getTreePath(), this);
    }

    private class FileTreePath extends TreePath{
      public FileTreePath(FileTreeNode single){
        super(single);
      }

      public FileTreePath(FileTreePath path, FileTreeNode last){
        super(path, last);
      }
    }

    public void recursiveCollapse(){
      if (parent!=null) {
        jTree1.collapsePath(getTreePath());
        parent.recursiveCollapse();
      }
    }

    public FileTreePath getTreePath(FileTreePath path, String name){
      Enumeration enum = children();
      while (enum.hasMoreElements()) {
        FileTreeNode item = (FileTreeNode)enum.nextElement();
        if (item.file.getName().toUpperCase().equals(name.toUpperCase())){
          return new FileTreePath(getTreePath(file.getParentFile()), item);
        }
      }
      return null;
    }

    public FileTreePath getTreePath(java.io.File file){
      if (file == null) return new FileTreePath(this);
      else if (file.getName().equals("")) {
        Enumeration enum = children();
        while (enum.hasMoreElements()) {
          FileTreeNode item = (FileTreeNode)enum.nextElement();
          if (item.file.getPath().toUpperCase().equals(file.getPath().toUpperCase())){
            return item.getTreePath();
          }
        }
      } else {
        FileTreePath tp = getTreePath(file.getParentFile());
        return ((FileTreeNode)tp.getLastPathComponent()).getTreePath(tp, file.getName());
      }
      return null;
    }

    public void recursiveExpand(){
      this.recursiveExpand(getTreePath());
    }

    private void recursiveExpand(TreePath path){
      if (parent!=null) parent.recursiveExpand(path.getParentPath());
      jTree1.expandPath(path);
    }

    public void setCD(java.io.File cd){
      TreePath tp = getTreePath(cd);
      curFile = (FileTreeNode)tp.getLastPathComponent();
    }
  }

  public FileManager() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    changeCD(cd);
  }

  private void jbInit(){
    this.getContentPane().setLayout(gridBagLayout1);
    jTree1.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
      public void treeExpanded(TreeExpansionEvent e) {
        jTree1_treeExpanded(e);
      }
      public void treeCollapsed(TreeExpansionEvent e) {
      }
    });
    jTree1.setMaximumSize(new Dimension(32767, 32767));
    jTree1.setMinimumSize(new Dimension(24, 24));
    jTree1.setExpandsSelectedPaths(false);
    this.getContentPane().add(jScrollPane1,     new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(jTree1, null);
    this.validate();
    this.setSize(new Dimension(600, 401));
    this.setTitle("FileManager");
    this.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        System.exit(0);
      }
    });
    this.setVisible(true);
  }

  public void changeCD(java.io.File cd){
    if (cd == null){
      children = java.io.File.listRoots();
    } else {
      children = cd.listFiles();
    }
    this.cd = cd;
    curFile.recursiveCollapse();
    rootFile.setCD(cd);
    curFile.recursiveExpand();
  }

  public Object getObject(intelligence.Object obj){
/*    if (obj.isInstanceOf("файл")){
      String name = obj.getClassAttributeRecursive("имя").getSingleObject().getName();
      for (int i=0;i<children.length;i++){
        if ((children[i].getName().equals("")&&children[i].getPath().toUpperCase().equals(name.toUpperCase()))||
            children[i].getName().toUpperCase().equals(name.toUpperCase())){
          try {
            File f = new File(this, children[i].getCanonicalFile());
            if (f.exists()) return f;
          } catch (IOException ex){
          }
        }
      }
    }
    else if (obj.isInstanceOf("папка")){
      intelligence.Object nm = obj.getClassAttributeRecursive("имя").getSingleObject();
      if (nm!=intelligence.Object.unknown){
        String name = obj.getClassAttributeRecursive("имя").getSingleObject().getName();
        for (int i=0;i<children.length;i++){
          if ((children[i].getName().equals("")&&children[i].getPath().toUpperCase().equals(name.toUpperCase()))||
              children[i].getName().toUpperCase().equals(name.toUpperCase())){
            try {
              Directory f = new Directory(this, children[i].getCanonicalFile());
              if (f.exists()&&f.isDirectory()) return f;
            } catch (IOException ex){
              ex.printStackTrace();
            }
          }
        }
      } else {
        Directory f = new Directory(this, cd);
        if (f.exists()&&f.isDirectory()) return f;
      }
    }
    else if (obj.isInstanceOf("подпапка")){
      if (cd==null || cd.getParent()==null) {
//        Directory f = new Directory(this, null);
//        return f;
        return null;
      }
      try {
        Directory f = new Directory(this, new java.io.File(cd, "..").getCanonicalFile());
        if (f.exists()&&f.isDirectory()) return f;
      } catch (IOException ex){
      }
    }*/
    return null;
  }

  void jTree1_treeExpanded(TreeExpansionEvent e) {
  }

}