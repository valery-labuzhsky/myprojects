package intelligence.instinct;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public abstract class Sense {
  private LinkedList listeners = new LinkedList();

  public void addSenseListener(SenseListener listener){
    listeners.addLast(listener);
  }

  protected void fireSenseListener(intelligence.Objects objs){
    Iterator iter = listeners.iterator();
    while (iter.hasNext()) {
      ((SenseListener)iter.next()).sense(objs);
    }
  }
}