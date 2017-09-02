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

public abstract class Task extends Thread{
  protected Memory memory;
  protected Asker asker;
  private transient Vector resultListeners;

  public Task(Memory memory, Asker asker) {
    this.memory = memory;
    this.asker = asker;
    addResultListener(new ResultListener(){
      public void result(Object answer){
        Task.this.result = answer;
        synchronized (Task.this) {
          Task.this.notify();
        }
      }
    });
  }

  public Task(Task parent){
    this(parent.memory, parent.asker);
  }

  public void run(){
    fireResult(solve());
  }

  public abstract Object solve();

  protected void ask(String question){
    asker.ask(this, question);
  }

  protected void askwhat(Object object, Objects objects){
    Objects dif = objects.getDifferences();
    dif.remove(object);
    String question = "Какой " + object + "? ";
    Iterator iter = dif.iterator();
    Object item = (Object) iter.next();
    String str = item.toString();
    question += str.substring(0, 1).toUpperCase();
    question += str.substring(1);
    while (iter.hasNext()) {
      item = (Object) iter.next();
      if (iter.hasNext()) {
        question += ", " + item;
      }
      else {
        question += " или " + item + "? ";
      }
    }
    ask(question);
  }

  private Objects answer = new Objects();
  public synchronized void send(Object send){
    answer.addLast(send);
    synchronized (this) {
      notify();
    }
  }
  protected Object answer(){
    try {
      while (answer.isEmpty()){
        synchronized (this) {
          wait();
        }
      }
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    return answer.removeFirst();
  }

  private Object result = null;
  public Object result(){
    try {
      synchronized (this) {
        while (result==null) wait();
      }
    }
    catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    Object ret = result;
    result = null;
    return ret;
  }

  public synchronized void addResultListener(ResultListener l) {
    Vector v = resultListeners == null ? new Vector(2) : (Vector) resultListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      resultListeners = v;
    }
  }
  public synchronized void removeResultListener(ResultListener l) {
    if (resultListeners != null && resultListeners.contains(l)) {
      Vector v = (Vector) resultListeners.clone();
      v.removeElement(l);
      resultListeners = v;
    }
  }
  protected void fireResult(Object e) {
    if (resultListeners != null) {
      Vector listeners = resultListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ResultListener) listeners.elementAt(i)).result(e);
      }
    }
  }

}