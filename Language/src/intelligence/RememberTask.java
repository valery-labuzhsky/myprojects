package intelligence;

import language.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class RememberTask extends Task{
  private Object subObject;
  private Object superObject;

  public RememberTask(Task parent, Object subObject, Object superObject){
    super(parent);
    this.subObject = subObject;
    this.superObject = superObject;
  }

  public Object solve(){
    while(subObject==null||superObject==null);
    boolean not = false;
    Task subtask = new SpecifyTask(this, subObject);
    subObject = subtask.solve();
//    subtask.start();
    if (superObject.getName().equals("@не")){
      superObject = superObject.getAnyAttributeRecursive(new Question.ЧтоИ());
      not = true;
    } else {
      Task supertask = new SpecifyTask(this, superObject);
//      supertask.start();
      superObject = supertask.solve();
    }
    if (not) {
      memory.forget(subObject, memory.find(superObject));
    } else memory.remember(subObject, superObject);
    return subObject;
  }
}