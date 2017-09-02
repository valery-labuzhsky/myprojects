package intelligence.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class AttributeTreeNode extends AbstractTreeNode{
  public AttributeTreeNode(intelligence.Object object){
    super(null, object);
  }

  protected void setChildren(){
    if (children == null){
      intelligence.Object[] csubobjs = object.getClassAttributeKeys();
      intelligence.Object[] asubobjs = object.getAttributeKeys();
      children = new AbstractTreeNode[csubobjs.length+asubobjs.length];
      for (int i=0;i<csubobjs.length;i++){
        children[i] = new KeyTreeNode(this, csubobjs[i], true);
      }
      for (int i=0;i<asubobjs.length;i++){
        children[i+csubobjs.length] = new KeyTreeNode(this, asubobjs[i], false);
      }
    }
  }
}