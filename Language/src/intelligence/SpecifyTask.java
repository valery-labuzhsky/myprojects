package intelligence;

import java.util.*;
import language.*;
import language.word.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SpecifyTask extends Task{
  private Object object;
  private Object word;

  public SpecifyTask(Task parent, Object object) {
    super(parent);
    this.object = object;
    word = object;
  }

  public Object solve(){
    while (object==null);
    if (object.hasAttribute(new Question.Какой(), new Object("другой"))){
      object = new CreateTask(this, object).solve();
    } else {
      Objects objects = memory.find(object);
      if (objects.isEmpty()) {
        object = new CreateTask(this, object).solve();
      }
      else if (objects.isSingleObject()) {
        object = objects.getSingleObject();
        object = new WordTask(this, object, word).solve();
      }
      else {
        while (true){
          askwhat(object, objects);
          Object send = answer();
          if (send.hasAttribute(new Question.Какой(), new Object("другой"))) {
            object = new CreateTask(this, object).solve();
            break;
          } else {
            objects = objects.specify(send);
            if (objects.isSingleObject()){
              object = objects.getSingleObject();
              object = new WordTask(this, object, word).solve();
            }
          }
          if (objects.isEmpty()) {
            object = new CreateTask(this, object).solve();
            break;
          }
        }
      }
    }
    return object;
  }
}