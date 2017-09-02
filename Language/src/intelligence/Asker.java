package intelligence;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Asker {
  public Asker() {
  }

  private class Question{
    private Task task;
    private String question;

    public Question(Task task, String question){
      this.task = task;
      this.question = question;
    }

    public void ask(){
      System.out.println(question);
    }

    public void send(Object obj){
      task.send(obj);
    }
  }
  private Question quest = null;
  private LinkedList quests = new LinkedList();

  public void ask(Task task, String question){
    quests.addLast(new Question(task, question));
    if (quest==null){
      quest = (Question)quests.removeFirst();
      quest.ask();
    }
  }

  public void send(Object obj){
    if (quest!=null) quest.send(obj);
    if (!quests.isEmpty()){
      quest = (Question)quests.removeFirst();
      quest.ask();
    } else {
      quest = null;
    }
  }

  public boolean isEmpty(){
    if (quest!=null) return false;
    if (quests.isEmpty()) return true;
    quest = (Question)quests.removeFirst();
    quest.ask();
    return false;
  }
}