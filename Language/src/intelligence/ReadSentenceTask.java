package intelligence;

import java.util.*;
import language.*;
import language.phrase.*;
import language.word.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ReadSentenceTask extends Task{
  private Object object;

  public ReadSentenceTask(Task parent, Object object) {
    super(parent);
    this.object = object;
  }

  private Objects read(Objects objects){
    Objects ret = new Objects();
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      ret.add(read(item));
    }
    return ret;
  }

  private Object read(Object object){
    return new ReadSentenceTask(this, object).solve();
  }

/*  private void addAttribute(Object object, Object key, Object value){
    if (value.isInstanceOf(new Link())){
      object.addAttribute(key, value.getAttribute(key).getSingleObject());
    } else if (value.isInstanceOf(new And())) {
      if (object.isInstanceOf(new Or())){
        object.addAttribute(key, value);
      } else {
        addAttributes(object, key, value.getAttribute(key));
      }
    } else if (!object.hasAttribute(key, value)){
      object.addAttribute(key, value);
    }
  }

  private void addAttributes(Object object, Object key, Objects values){
    Iterator iter = values.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      addAttribute(object, key, item);
    }
  }*/

  private void addClassAttribute(Object object, Object key, Object value){
    if (value.isInstanceOf(new Link())){
      object.addClassAttribute(key, value.getClassAttribute(key));
    } else if (value.isInstanceOf(new And())) {
      key = value.getClassAttributeKeys()[0];
      if (object.isInstanceOf(new Or())){
        object.addClassAttribute(key, value);
      } else {
        addClassAttributes(object, key, value.getClassAttributes(key));
      }
    } else if (!object.hasClassAttribute(key, value)){
      object.addClassAttribute(key, value);
    }
  }

  private void addClassAttributes(Object object, Object key, Objects values){
    Iterator iter = values.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      addClassAttribute(object, key, item);
    }
  }

  private Object getObject(Object word){
    Object norm;
    if (word.isInstanceOf(Form.NORMAL)){
      norm = word;
    } else {
      norm = word.getClassAttribute(Form.NORMAL);
    }
    Object value = norm.getAttribute(new Value());
    if (value == Object.unknown) {
      value = new Object(norm.getName());
    }
    value = value.cloneStructure();
    if (!value.hasAttribute(new Word(), norm)){
      value.addAttribute(new Word(), norm);
    }
    return value;
  }

  private void readWord(Object word){
    Object norm;
    if (word.isInstanceOf(Form.NORMAL)){
      norm = word;
    } else {
      norm = word.getClassAttribute(Form.NORMAL);
    }
    Object value = norm.getAttribute(new Value());
    if (value == Object.unknown) {
      value = new Object(norm.getName());
    }
    value = value.cloneStructure();
    if (!value.hasAttribute(new Word(), norm)){
      value.addAttribute(new Word(), norm);
    }
  }

  private void readWords(Object obj){
    Object word = obj.getClassAttribute(new Word());
    readWord(word);
    Object.EntryIterator iter = obj.getEntryIterator(false);
    while (iter.hasNext()) {
      Object.Entry item = iter.nextEntry();
      readWords(item.getValue());
    }
  }

  private void readWords(){
    readWords(object);
  }

  public Object solve(){
    Object ret = Object.unknown;
    while (object==null);
    readWords();
    object = object.cloneStructure();
    if (object.removeAncestors(new Phrase())){
      if (object.removeAncestors(new Subject())){
        Object word = object.getAnyAttributeRecursive(new Word());
        if (object.hasClassAttributeRecursive(new language.phrase.Particle(), new language.word.Particle.Negative())){
          ret = new Object("@не");
          ret.addAttribute(new Question.ЧтоИ(), getObject(word));
        } else {
          ret = getObject(word);
        }
//        ret.addAttribute(new Word(), word);
        if (word.removeAncestors(new Quotation())){
          ret.addSuperObject(new ProperName());
        }
        addClassAttributes(ret, new Question.Какой(), read(object.getAnyAttributesRecursive(new Question.Какой())));
        addClassAttributes(ret, new Question.Чего(), read(object.getAnyAttributesRecursive(new Question.Чего())));
      }
      else if (object.removeAncestors(new Predicate())){
        Object word = object.getAnyAttributeRecursive(new Word());
        Object phrase = object.getAnyAttributeRecursive(new Phrase());
        if (word!=Object.unknown){
          if (word.removeAncestors(new Verb())) {
            ret = new Action(getObject(word).getName());
//            ret.addAttribute(new Word(), word);
            if (word.hasClassAttribute(new Verb.Mood(),
                                       new Verb.Mood.Imperative())) {
              ret.addClassAttribute(new Question.Какой(), Action.imperative);
            }
            addClassAttributes(ret, new Question.ЧтоВ(),
                               read(object.getAnyAttributesRecursive(new Question.ЧтоВ())));
          }
        } else {
          ret = read(phrase);
        }
      }
      else if (object.removeAncestors(new Attribute())){
        Object word = object.getAnyAttributeRecursive(new Word());
        ret = getObject(word);
//        ret.addAttribute(new Word(), word);
        ret = new Link(new Question.Какой(), ret);
      }
      else if (object.removeAncestors(new DirectObject())){
        Object word = object.getAnyAttributeRecursive(new Word());
        Object name = read(object.getAttributeRecursive(new Question.Какой()));//.get(new Question.ЧтоИ()).getSingleObject();
        ret = getObject(word);
//        ret.addAttribute(new Word(), word);
        if (name!=Object.unknown) {
          ret = new Object(name.getName(), ret);
          ret.addSuperObject(new ProperName());
        }
        addClassAttributes(ret, new Question.Какой(), read(object.getAnyAttributesRecursive(new Question.Какой())));
        ret = new Link(new Question.ЧтоВ(), ret);
      } else if (object.removeAncestors(new IndirectObject())){
        if (object.hasClassAttribute(new Noun.Case(), new Noun.Case.Genetive())){
          Object word = object.getAnyAttributeRecursive(new Word());
          ret = getObject(word);
//          ret.addAttribute(new Word(), word);
          addClassAttributes(ret, new Question.Какой(), read(object.getAnyAttributesRecursive(new Question.Какой())));
          ret = new Link(new Question.Чего(), ret);
        }
      }
      else if (object.removeAncestors(new Apposition())){
        Object word = object.getAnyAttributeRecursive(new Word());
        if (word.removeAncestors(new Quotation())) {
          ret = getObject(word);
          ret.addSuperObject(new ProperName());
//          ret.addAttribute(new Word(), word);
        }
      }
      else if (object.removeAncestors(new language.phrase.Dash())){
        Object subObject = read(object.getAnyAttributeRecursive(new Subject()));
        Object superObject = read(object.getAnyAttributeRecursive(new Predicate()));
        if (subObject!=Object.unknown&&superObject!=Object.unknown){
          subObject.addAttribute(new ThisIs(), superObject);
          ret = subObject;
        }
      }
    } else if (object.isInstanceOf(new Or())){
//      ret = new Or();
      Object[] keys = object.getClassAttributeKeys();
      Objects rets = new Objects();
      for (int i = 0; i < keys.length; i++) {
        Objects values = object.getClassAttributes(keys[i]);
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
          Object item = (Object)iter.next();
          rets.add(read(item));
        }
//        Object com = rets.getCommonObject();
/*        if (com.isInstanceOf(new Or())){
          addClassAttributes(ret, keys[i], com.getClassAttribute(new Question.ЧтоИ()));
          addClassAttributes(ret, keys[i], com.getClassAttribute(keys[i]));
        } else {
          if (keys.length==1){
            if (keys[i].isInstanceOf(new Question.ЧтоИ())){
              ret = com;
            } else {
              ret = new Link(keys[i], com);
            }
          } else {
            addClassAttribute(ret, keys[i], com);
          }
        }*/
      }
      ret = rets.getCommonObject();
    } else if (object.isInstanceOf(new And())){
      ret = new And();
      Object[] keys = object.getClassAttributeKeys();
      for (int i = 0; i < keys.length; i++) {
        Objects rets = new Objects();
        Objects values = object.getClassAttributes(keys[i]);
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
          Object item = (Object) iter.next();
          rets.add(read(item));
        }
        addClassAttributes(ret, keys[i], rets);
      }
    }
    return ret;
  }

}