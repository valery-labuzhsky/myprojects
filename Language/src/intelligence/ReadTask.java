package intelligence;

import java.util.*;
import language.phrase.*;
import language.sentence.*;
import language.word.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ReadTask extends Task{
  public ReadTask(Task parent) {
    super(parent);
  }

  private int state = 0;
  private LinkedList textwords = new LinkedList();
  private String word = "";

  private Object getPossibleSentences(LinkedList words){
    LinkedList phrases = new LinkedList();
    Iterator iter = words.iterator();
    while (iter.hasNext()) {
      Objects item = (Objects)iter.next();
      LinkedList phs = new LinkedList();
      Iterator iter0 = item.iterator();
      while (iter0.hasNext()) {
        intelligence.Object item0 = (intelligence.Object)iter0.next();
        phs.addAll(Phrase.getPossiblePhrases(item0));
      }
      phrases.addLast(phs);
    }
    LinkedList ret = new LinkedList();
    ret.add(new Phrases());
    iter = phrases.iterator();
    while (iter.hasNext()) {
      LinkedList item = (LinkedList)iter.next();
      Iterator iter0 = ret.iterator();
      LinkedList newret = new LinkedList();
      while (iter0.hasNext()) {
        Phrases item0 = (Phrases)iter0.next();
        Iterator iter1 = item.iterator();
        while (iter1.hasNext()) {
          Phrase item1 = (Phrase)iter1.next();
          newret.addAll(item0.getPossibleSentences(item1));
        }
      }
      ret = newret;
    }

    iter = ret.iterator();
    while (iter.hasNext()) {
      Phrases item = (Phrases)iter.next();
      if (!item.isValid()) iter.remove();
    }

    Objects objs = new Objects();
    iter = ret.iterator();
    while (iter.hasNext()) {
      Phrases item = (Phrases)iter.next();
      objs.add(item.getFirst());
    }

    return objs.getCommonObject();
//    return objs.getOrObject();
  }

  private void init(){
    word = "";
    state = 0;
    textwords = new LinkedList();
  }

  public Object solve(){
    while (true){
      char c = answer().getName().charAt(0);
      switch (state){
        case 0:
          if (Character.isLetter(c)){
            state = 1;
            word += c;
          } else if (c == '\"'){
            state = 2;
          } else if (c == '-'){
            textwords.addLast(new Objects(new language.word.Dash()));
          } else if (c == '.'){
            if (!word.equals("")){
              textwords.addLast(Word.getPossibleWords(memory, word));
            }
            fireResult(getPossibleSentences(textwords));
          }
          break;
        case 1:
          if (Character.isLetter(c)) {
            word += c;
          }
          else if (Character.isWhitespace(c)){
            textwords.addLast(Word.getPossibleWords(memory, word));
            word = "";
            state = 0;
          } else if (c == '.'){
            if (!word.equals("")){
              textwords.addLast(Word.getPossibleWords(memory, word));
            }
            fireResult(getPossibleSentences(textwords));
            init();
          }
          break;
        case 2:
          if (c == '\"'){
            textwords.addLast(new Objects(new Quotation(word)));
            word = "";
            state = 0;
            break;
          }
          word += c;
          break;
      }
    }
  }

}