package intelligence.util;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class KeyTreeNode extends AbstractTreeNode{
  private boolean classAttribute;

  public KeyTreeNode(AbstractTreeNode parent, intelligence.Object object, boolean classAttribute){
    super(parent, object);
    this.classAttribute = classAttribute;
  }

  protected void setChildren(){
    if (children == null){
      intelligence.Objects subobjs;
      if (classAttribute) subobjs = parent.object.getClassAttributes(object);
      else subobjs = parent.object.getAttributes(object);
      children = new AbstractTreeNode[subobjs.size()];
      Iterator iter = subobjs.iterator();
      for (int i=0;i<children.length;i++){
        children[i] = new ValueTreeNode(this, (intelligence.Object)iter.next());
      }
    }
  }

  public String toString(){
    if (classAttribute) return "(class)"+super.toString();
    return super.toString();
  }
}