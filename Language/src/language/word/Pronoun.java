package language.word;

import intelligence.*;
import language.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author uniCorn
 * @version 1.0
 */

public class Pronoun extends Word{
  public static final intelligence.Object RELATIVE = new intelligence.Object("относительный");

  public Pronoun() {
    super("местоимение");
    addSuperObject(new Word());
  }

  public Pronoun(String word){
    super(word);
    addSuperObject(new ProperName());
  }

  protected Pronoun setRelative(){
    addWordAttribute(new Question. акой(), new intelligence.Object("относительный"));
    return this;
  }

  protected void setAttributes(Word word){
    intelligence.Object[] keys = word.getWordAttributeKeys();
    for (int i=0; i<keys.length; i++){
      addWordAttributes(keys[i], word.getWordAttributes(keys[i]));
    }
  }

  public static Objects getPossibleWords(String word){
    return PronounAdjectiveNoun.getPossibleWords(word);
  }
}