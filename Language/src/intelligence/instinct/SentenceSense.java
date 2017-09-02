package intelligence.instinct;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
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

public class SentenceSense extends Sense{

  public SentenceSense(intelligence.Memory memory) {
    SentenceFrame frame = null;//new SentenceFrame(this, memory);
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    frame.validate();
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setSize(600,500);
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        System.exit(0);
      }
    });
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }

  public void takeSentences(LinkedList as){
    think(as);
  }

  private void think(LinkedList sentences){
    Vector objs = new Vector();
    intelligence.Object obj;
    Iterator iter = sentences.iterator();
    while (iter.hasNext()) {
      obj = think((Phrases)iter.next());
      if (obj!=null){
        objs.add(obj);
      }
    }
    if (objs.size()<1){
      return;
    }

    iter = objs.iterator();
    intelligence.Objects ret = new intelligence.Objects();
    while (iter.hasNext()) {
      intelligence.Object item = (intelligence.Object)iter.next();
      ret.add(item);
    }
    this.fireSenseListener(ret);
/*    obj = (intelligence.Object)objs.firstElement();
    if (obj.getName().equals("команда")){
      this.fireSenseListener(obj);
    }*/
  }

  private intelligence.Object think(Phrase phrase){
    return phrase;
  }

  private intelligence.Object think(Word word){
    return word;
  }

  private intelligence.Object think(Phrases sentence){
    return this.think(sentence.getFirst());
  }
}