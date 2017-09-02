package intelligence;

import intelligence.util.*;
import java.io.*;
import java.util.*;
import language.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Memory{
  private Object rootObject = new Object("@корень");
  private MemoryFrame mf = new MemoryFrame(this);

  public Memory() {
    mf.updateUI();
  }

  public void setRootObject(Object rootObject){
    this.rootObject = rootObject;
    mf.updateUI();
  }

  public Object getRootObject(){
    return rootObject;
  }

  private boolean isPartOfWord(Object obj){
    if (obj.isInstanceOf(language.word.Word.WORD)) return true;
    Object word = rootObject.find(language.word.Word.WORD).getSingleObject();
    Object[] words = word.getSubObjects();
    for (int i=0; i<words.length; i++){
      if (words[i].getName().equals(obj.getName())) return true;
    }
    return false;
  }

  public Objects find(Object obj){
    return find(null, obj);
  }

  public Objects find(Object root, Object obj){
    obj = (Object)obj.clone();
    obj.clearSubObjects();
    if (root==null) root = rootObject;
    Objects ret = root.find(obj);
    if (!isPartOfWord(obj)){
      Iterator iter = ret.iterator();
      while (iter.hasNext()) {
        Object item = (Object)iter.next();
        if (item.isInstanceOf(language.word.Word.WORD)){
          iter.remove();
        }
      }
    }
    return ret;
  }

  private class FileObject{
    private int id;
    private String name;
    private FileObject[] objs;
    private int[] superObjects;
    private int[][] classAttributes;
    private int[][] attributes;
    private Object object;

    public FileObject(FileObject[] objs, String str, File file) throws IOException{
      this.objs = objs;
      int d = str.indexOf(" - ");
      id = Integer.parseInt(str.substring(0, d));
      name = str.substring(d+3);
      file = new File(new File(file, name), ""+id);
      FileInputStream in = new FileInputStream(file);
      byte[] buf = new byte[(int)file.length()];
      in.read(buf);
      String obj = new String(buf);

      String so = obj.substring(0, obj.indexOf('\n'));
      so = so.substring(so.indexOf('[')+1, so.lastIndexOf(']'));
      StringTokenizer attrst = new StringTokenizer(so, ",");
      superObjects = new int[attrst.countTokens()];
      for (int i = 0; i < superObjects.length; i++) {
        superObjects[i] = Integer.parseInt(attrst.nextToken());
      }

      String attr = obj.substring(obj.indexOf("ClassAttributes"), obj.lastIndexOf("Attributes"));
      attr = attr.substring(attr.indexOf('\n'));
      StringTokenizer st = new StringTokenizer(attr, "\n");
      classAttributes = new int[st.countTokens()][];
      for (int i = 0; i < classAttributes.length; i++) {
        attr = st.nextToken();
        d = attr.indexOf('[');
        int key = Integer.parseInt(attr.substring(0, d));
        attr = attr.substring(d+1, attr.lastIndexOf(']'));
        attrst = new StringTokenizer(attr, ",");
        classAttributes[i] = new int[attrst.countTokens()+1];
        classAttributes[i][0] = key;
        for (int j = 1; j < classAttributes[i].length; j++) {
          classAttributes[i][j] = Integer.parseInt(attrst.nextToken());
        }
      }

      attr = obj.substring(obj.lastIndexOf("Attributes"));
      attr = attr.substring(attr.indexOf('\n'));
      st = new StringTokenizer(attr, "\n");
      attributes = new int[st.countTokens()][];
      for (int i = 0; i < attributes.length; i++) {
        attr = st.nextToken();
        d = attr.indexOf('[');
        int key = Integer.parseInt(attr.substring(0, d));
        attr = attr.substring(d+1, attr.lastIndexOf(']'));
        attrst = new StringTokenizer(attr, ",");
        attributes[i] = new int[attrst.countTokens()+1];
        attributes[i][0] = key;
        for (int j = 1; j < attributes[i].length; j++) {
          attributes[i][j] = Integer.parseInt(attrst.nextToken());
        }
      }
      in.close();

      object = new Object(name);
    }

    public void createObject(){
      for (int i = 0; i < superObjects.length; i++) {
        object.addSuperObject(objs[superObjects[i]-1].object);
      }
      for (int i = 0; i < classAttributes.length; i++) {
        Object key = objs[classAttributes[i][0]-1].object;
        Objects values = new Objects();
        for (int j = 1; j < classAttributes[i].length; j++) {
          values.add(objs[classAttributes[i][j]-1].object);
        }
        object.addClassAttributes(key, values);
      }
      for (int i = 0; i < attributes.length; i++) {
        Object key = objs[attributes[i][0]-1].object;
        Objects values = new Objects();
        for (int j = 1; j < attributes[i].length; j++) {
          values.add(objs[attributes[i][j]-1].object);
        }
        object.addAttributes(key, values);
      }
    }
  }

  public void load(File file) throws IOException{
    FileInputStream in = new FileInputStream(file);
    byte[] buf = new byte[(int)file.length()];
    String name = file.getCanonicalPath();
    mf.setFile(file);
    file = new File(name.substring(0, name.length()-4));
    in.read(buf);
    String str = new String(buf);
    StringTokenizer st = new StringTokenizer(str, "\n");
    FileObject[] objs = new FileObject[st.countTokens()];
    int i = 0;
    while (st.hasMoreElements()) {
      String item = st.nextToken();
      objs[i] = new FileObject(objs, item, file);
      i++;
    }
    for (i = 0; i < objs.length; i++) {
      objs[i].createObject();
    }
    rootObject = objs[0].object;
    in.close();
    mf.updateUI();
  }

  public void save(File file) throws IOException{
    rootObject.write(file);
  }

  public void clear(){
    rootObject = new Object("@корень");
    mf.updateUI();
  }

  public void remember(Object subObject, Object superObject){
    superObject.forget(subObject);
    if (!superObject.isAcestorOf(subObject)) {
      subObject.addSuperObject(superObject);
    }
    mf.updateUI();
  }

  public void forget(Object subObject, Objects superObjects){
    subObject.forget(superObjects);
    mf.updateUI();
  }

  public static void main(String[] args){
    System.out.println("КоЗа".toUpperCase());
  }
}