package intelligence.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ClassTreeNode extends AbstractTreeNode{
  public ClassTreeNode(AbstractTreeNode parent, intelligence.Object object){
    super(parent, object);
  }

  protected void setChildren(){
    if (children == null){
      intelligence.Object[] subobjs = object.getSubObjects();
      children = new AbstractTreeNode[subobjs.length];
      for (int i=0;i<subobjs.length;i++){
        children[i] = new ClassTreeNode(this, subobjs[i]);
      }
    }
  }
}