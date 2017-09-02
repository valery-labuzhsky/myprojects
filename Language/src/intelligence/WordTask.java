package intelligence;

import language.word.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author uniCorn
 * @version 1.0
 */

public class WordTask extends Task{
  private Object object;
  private Object word;

  public WordTask(Task parent, Object object, Object word) {
    super(parent);
    this.object = object;
    this.word = word;
  }

  public Object solve(){
    Object key = new Word();
    Object ow = object.getAttribute(key);
    Object ww = word.getAttribute(key);
    key = memory.find(key).getSingleObject();
    return object;
  }
}