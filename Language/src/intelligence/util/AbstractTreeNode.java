package intelligence.util;

import java.util.*;
import javax.swing.tree.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public abstract class AbstractTreeNode implements TreeNode{
  protected intelligence.Object object;
  protected AbstractTreeNode[] children = null;
  protected AbstractTreeNode parent;

  public AbstractTreeNode(AbstractTreeNode parent, intelligence.Object object){
    this.parent = parent;
    this.object = object;
  }

  public intelligence.Object getObject(){
    return object;
  }

  abstract protected void setChildren();

  public Enumeration children(){
    setChildren();
    return new ArrayEnumeration(children);
  }

  public boolean getAllowsChildren(){
    setChildren();
    return children.length>0;
  }

  public TreeNode getChildAt(int i){
    setChildren();
    return children[i];
  }

  public int getChildCount(){
    setChildren();
    return children.length;
  }

  public int getIndex(TreeNode node){
    setChildren();
    for (int i=0;i<children.length;i++){
      if (children.equals(node)) return i;
    }
    return -1;
  }

  public TreeNode getParent(){
    return parent;
  }

  public boolean isLeaf(){
    return false;
  }

  public String toString(){
    return object.toStringWithID();
  }
}

