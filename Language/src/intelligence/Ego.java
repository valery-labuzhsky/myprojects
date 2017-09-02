package intelligence;

import intelligence.instinct.*;
import intelligence.util.*;
import java.io.*;
import java.util.*;
import language.*;
import language.letter.*;
import language.phrase.*;
import language.sentence.*;
import language.word.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Ego extends Task{

  public Ego(){
    super(new Memory(), new Asker());
    read = new ReadTask(this);
    read.addResultListener(new ResultListener(){
      public void result(Object res){
        send(res);
      }
    });
    pf.setLocation(430, 400);
    read.start();
  }

  public void load(java.io.File file) throws IOException{
    memory.load(file);
  }

  private Task read;
  private PhraseFrame of = new PhraseFrame("Object");
  private PhraseFrame pf = new PhraseFrame("Phrase");

  public Object solve(){
    while (true){
      Object send = answer();
      if (send.isInstanceOf(new Letter())) read.send(send);
      else {
        pf.setRoot(send);
//        Object obj = new ReadSentenceTask(this, send).result();
        Object obj = send.getAttribute(new Value());
        of.setRoot(obj);

        Objects thisis = obj.removeAttributes(new ThisIs());
        Iterator iter = thisis.iterator();
        while (iter.hasNext()) {
          Object item = (Object)iter.next();
          new RememberTask(this, obj, item).solve();
        }
      }
    }
  }

  public static void main(String[] args) throws IOException{
    Ego ego = new Ego();
    try {
      ego.load(new java.io.File("memory/memory.mem"));
    }
    catch (IOException ex) {
      System.out.println(ex);
    }
    SentenceFrame sf = new SentenceFrame(ego);
    ego.start();
  }
}