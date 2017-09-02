package language.phrase;

import intelligence.*;
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

public class Property extends Phrase {
  public Property(){
    super("свойство");
  }

  protected intelligence.Object property;

  public Property(intelligence.Object property) {
    this();
    this.property = property;
    addAttribute(new Question.Какой(), property);
  }

}
