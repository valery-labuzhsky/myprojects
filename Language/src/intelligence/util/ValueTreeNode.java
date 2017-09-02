package intelligence.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ValueTreeNode extends AbstractTreeNode{
  public ValueTreeNode(AbstractTreeNode parent, intelligence.Object object){
    super(parent, object);
  }

  protected void setChildren(){
    if (children == null){
      children = new AbstractTreeNode[0];
    }
  }
}