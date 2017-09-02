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

public class Object implements Cloneable, Serializable{
  public static final Object unknown = new Object("@неизвестно");
  private String name;
  private HashMap attributes = new HashMap();

  private LinkedList superObjects = new LinkedList();
  private HashMap classAttributes = new HashMap();
  private LinkedList subObjects = new LinkedList();
//  private Object or = new Or();

  public Object(String name) {
    this.name = name;
  }

  public Object(String name, Object superObject) throws CyclicInheritanceInvolvingClassException{
    this(name);
    addSuperObject(superObject);
  }

  public Object(Object superObject){
    this(superObject.name, superObject);
  }

  private void checkInheriting(Object superObject) throws CyclicInheritanceInvolvingClassException{
    if (superObject==this) throw new CyclicInheritanceInvolvingClassException();
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      item.checkInheriting(superObject);
    }
  }

  public void addSuperObject(Object superObject) throws CyclicInheritanceInvolvingClassException{
    checkInheriting(superObject);
    this.superObjects.add(superObject);
    superObject.subObjects.add(this);
  }

  public boolean removeSubObject(Object subObject){
    Iterator iter = subObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item==subObject) {
        iter.remove();
        return true;
      }
    }
    return false;
  }

  public boolean removeSuperObject(Object superObject){
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item==superObject) {
        iter.remove();
        item.removeSubObject(this);
        return true;
      }
    }
    return false;
  }

  public Object[] getSubObjects(){
    return (Object[])subObjects.toArray(new Object[subObjects.size()]);
  }

  public Object[] getSuperObjects(){
    return (Object[])superObjects.toArray(new Object[superObjects.size()]);
  }

  private boolean classEquals(Objects objs1, Objects objs2, LinkedList probableEquals){
    Iterator iter = objs1.iterator();
    objs2 = (Objects)objs2.clone();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter1 = objs2.iterator();
      boolean eq = false;
      while (iter1.hasNext()) {
        Object item1 = (Object)iter1.next();
        if (item.classEquals(item1,probableEquals)){
          iter1.remove();
          eq = true;
          break;
        }
      }
      if (!eq) return false;
    }
    if (!objs2.isEmpty()) return false;
    return true;
  }

  private boolean classEquals(Object obj, LinkedList probableEquals){
    if (this==obj) return true;
    if (!this.name.equals(obj.name)) return false;
    if (probableEquals(obj, probableEquals)) return true;

    probableEquals.addLast(new Object[]{this,obj});

    LinkedList sobjs = (LinkedList)obj.superObjects.clone();
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter1 = sobjs.iterator();
      boolean eq = false;
      while (iter1.hasNext()) {
        Object item1 = (Object)iter1.next();
        if (item.classEquals(item1,probableEquals)){
          iter1.remove();
          eq = true;
          break;
        }
      }
      if (!eq) return false;
    }
    if (!sobjs.isEmpty()) return false;

    iter = this.classAttributes.entrySet().iterator();
    HashMap attr = (HashMap)obj.classAttributes.clone();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      Iterator iter0 = attr.entrySet().iterator();
      boolean eq = false;
      while (iter0.hasNext()) {
        Map.Entry item0 = (Map.Entry)iter0.next();
        if (((Object)item.getKey()).classEquals(((Object)item0.getKey()),probableEquals) &&
            classEquals((Objects)item.getValue(), (Objects)item0.getValue(),probableEquals)){
          iter0.remove();
          eq = true;
          break;
        }
      }
      if (!eq) return false;
    }
    if (!attr.isEmpty()) return false;

    return true;
  }

  public boolean classEquals(Object obj){
    return classEquals(obj, new LinkedList());
  }

  private boolean classAttributeEquals(Objects objs1, Objects objs2){
    Iterator iter = objs1.iterator();
    objs2 = (Objects)objs2.clone();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter1 = objs2.iterator();
      boolean eq = false;
      while (iter1.hasNext()) {
        Object item1 = (Object)iter1.next();
        if (item.less(item1)){
          iter1.remove();
          eq = true;
          break;
        }
      }
      if (!eq) return false;
    }
    if (!objs2.isEmpty()) return false;
    return true;
  }

  public boolean classAttributeEquals(Object obj){
    if (this==obj) return true;
    if (!this.name.equals(obj.name)) return false;

    Iterator iter = this.classAttributes.entrySet().iterator();
    HashMap attr = (HashMap)obj.classAttributes.clone();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      Iterator iter0 = attr.entrySet().iterator();
      boolean eq = false;
      while (iter0.hasNext()) {
        Map.Entry item0 = (Map.Entry)iter0.next();
        if (((Object)item.getKey()).less(((Object)item0.getKey())) &&
            classAttributeEquals((Objects)item.getValue(), (Objects)item0.getValue())){
          iter0.remove();
          eq = true;
          break;
        }
      }
      if (!eq) return false;
    }
    if (!attr.isEmpty()) return false;

    return true;
  }

  public boolean less(Object obj){
    return less(obj, new LinkedList());
  }

  public Object intersection(Object obj){
    if (this.classEquals(obj)){
      Object ret = (Object)this.clone();
      HashMap[] maps = intersection(this.attributes, obj.attributes);
      ret.attributes = maps[0];
      if (!maps[1].isEmpty() || !maps[2].isEmpty()){
        ret.addAttributes(new Or(), new Objects(getOrObject(maps[1], maps[2])));
      }
      return ret;
    } else {
      Object or = new Or();
      addAndObject(or, this);
      addAndObject(or, obj);
      return or;
    }
/*    if (obj.isInstanceOf(new And())) return intersection(obj, obj.getClassAttributeKeys()[0]);
    if (this.isInstanceOf(new And())) return intersection(obj, this.getClassAttributeKeys()[0]);
    return intersection(obj, new Question.ЧтоИ());*/
  }

/*  public Object intersection(Object obj, Object key){
    Object ret;
    if (this==obj) return this;
    if (this.isInstanceOf(new Link())){
      ret = new Or();
      Iterator iter = this.classAttributes.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry item = (Map.Entry)iter.next();
        ret.addClassAttributes((Object)item.getKey(), (Objects)item.getValue());
      }
      ret = ret.intersection(obj);
    } else if (obj.isInstanceOf(new Link())){
      ret = new Or();
      Iterator iter = obj.classAttributes.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry item = (Map.Entry)iter.next();
        ret.addClassAttributes((Object)item.getKey(), (Objects)item.getValue());
      }
      ret = this.intersection(ret);
    }
    else if (this.isInstanceOf(new Or())){
      if (obj.isInstanceOf(new Or())){
        ret = new Or();
        obj = (Object)obj.clone();
        Iterator iter = classAttributes.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry item = (Map.Entry)iter.next();
          Object key0 = (Object)item.getKey();
          ret.addClassAttributes(key0, ((Objects)item.getValue()).intersection(obj.removeClassAttributes(key0)));
        }
        iter = obj.classAttributes.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry item = (Map.Entry)iter.next();
          ret.addClassAttributes((Object)item.getKey(), (Objects)item.getValue());
        }

        iter = attributes.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry item = (Map.Entry)iter.next();
          Object key0 = (Object)item.getKey();
          ret.addAttributes(key0, ((Objects)item.getValue()).intersection(obj.removeAttributes(key0)));
        }
        iter = obj.attributes.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry item = (Map.Entry)iter.next();
          ret.addAttributes((Object)item.getKey(), (Objects)item.getValue());
        }
      } else {
        ret = (Object)this.clone();
        Objects val = ret.removeClassAttributes(key);
        ret.addClassAttributes(key, val.intersection(obj));
      }
    } else if (obj.isInstanceOf(new Or())){
      ret = (Object)obj.clone();
      Objects val = ret.removeClassAttributes(key);
      ret.addClassAttributes(key, val.intersection(this));
    } else if (this.classEquals(obj)){
      ret = (Object)this.clone();
      ret.attributes.clear();
      HashMap[] maps = intersection(this.attributes, obj.attributes);
      ret.attributes.putAll(maps[0]);
      ret.attributes.put(new Or(), getOrObject(maps[1], maps[2]));
 //
      ret = (Object)this.clone();
      obj = (Object)obj.clone();
      ret.attributes.clear();
      Iterator iter = attributes.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry item = (Map.Entry)iter.next();
        Object key0 = (Object)item.getKey();
        ret.addAttributes(key0, ((Objects)item.getValue()).intersection(key0, obj.removeAttributes(key0)));
      }
      iter = obj.attributes.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry item = (Map.Entry)iter.next();
        ret.addAttributes((Object)item.getKey(), (Objects)item.getValue());
      }
//
    } else {
      ret = new Or(key, this, obj);
    }
    if (ret.isInstanceOf(new Or())){
      Object[] keys = ret.getClassAttributeKeys();
      if (keys.length==1){
        Objects values = ret.getClassAttributes(keys[0]);
        if (values.isSingleObject()){
          ret = new Link(keys[0], values.getSingleObject());
        }
      }
    }
    return ret;
  }*/

  private void addAndObject(Object or, Object and){
    if (and.isInstanceOf(new Or())||and.isInstanceOf(new Link())){
      HashMap[] maps = intersection(or.classAttributes, and.classAttributes);
      or.classAttributes = maps[0];
      putAll(or.classAttributes, maps[1]);
      putAll(or.classAttributes, maps[2]);
    } else if (and.isInstanceOf(new And())){
      or.addClassAttribute(new And(), and);
    } else {
      or.addClassAttribute(new Question.ЧтоИ(), and);
    }
  }

  private Object getOrObject(HashMap map1, HashMap map2){
    Object and1 = getAndObject(map1);
    Object and2 = getAndObject(map2);
    Object or = new Or();
    addAndObject(or, and1);
    addAndObject(or, and2);
    return or;
  }

  private Object getAndObject(HashMap map){
    if (map.size()==0){
      return new Object("@ничего");
    } else if (map.size()==1){
      Object key = (Object)map.keySet().toArray()[0];
      Objects values = (Objects)map.values().toArray()[0];
      if (key.isInstanceOf(new Or())){
        return values.getSingleObject();
      } else if (values.isSingleObject()){
        Object value = values.getSingleObject();
//        if (value.isI)
        return new Link(key, value);
      }
    }
    Object and = new And();
    and.classAttributes = map;
    return and;
  }

  private Objects[] intersection(Objects objs1, Objects objs2){
    Objects[] ret = new Objects[]{new Objects(), null, null};
    objs1 = (Objects)objs1.clone();
    objs2 = (Objects)objs2.clone();
    Iterator iter = objs1.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter0 = objs2.iterator();
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        if (item.classEquals(item0)){
          ret[0].add(item.intersection(item0));
          iter.remove();
          iter0.remove();
          break;
        }
      }
    }
    ret[1] = objs1;
    ret[2] = objs2;
    return ret;
  }

  private void putAll(HashMap dest, HashMap src){
    Iterator iter = src.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      put(dest, (Object)item.getKey(), (Objects)item.getValue());
    }
  }

  private void put(HashMap map, Object key, Object value){
    Objects val = (Objects)map.get(key);
    if (val!=null){
      val.add(value);
    } else{
      map.put(key, new Objects(value));
    }
  }

  private void put(HashMap map, Object key, Objects values){
    if (!values.isEmpty()){
      Objects val = (Objects)map.get(key);
      if (val!=null){
        val.add(values);
      } else{
        map.put(key, values);
      }
    }
  }

  private HashMap[] intersection(HashMap map1, HashMap map2){
    HashMap[] map = new HashMap[]{new HashMap(), new HashMap(), new HashMap()};
    map1 = (HashMap)map1.clone();
    map2 = (HashMap)map2.clone();
    Iterator iter = map1.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      Objects val = (Objects)map2.remove(item.getKey());
      if (val!=null){
        Objects[] objs = intersection(val, (Objects)item.getValue());
        put(map[0], (Object)item.getKey(), objs[0]);
        put(map[1], (Object)item.getKey(), objs[1]);
        put(map[2], (Object)item.getKey(), objs[2]);
      } else {
        put(map[1], (Object)item.getKey(), (Objects)item.getValue());
      }
    }
    putAll(map[2], map2);
    return map;
  }

  public class Entry{
    private Object key;
    private Object value;

    public Entry(Object key, Object value){
      this.key = key;
      this.value = value;
    }

    public Object getKey(){
      return key;
    }

    public Object getValue(){
      return value;
    }
  }

  public class EntryIterator implements Iterator{
    private Iterator entry;
    private Iterator iter = null;
    private Objects values;
    private Object key;
    private Object value;

    private EntryIterator(boolean cl){
      HashMap map = cl?classAttributes:attributes;
      entry = map.entrySet().iterator();
    }

    public void remove(){
      if (iter!=null) {
        iter.remove();
        if (values.isEmpty()) {
          entry.remove();
        }
      }
    }

    public Entry nextEntry(){
      if (iter==null || !iter.hasNext()){
        Map.Entry item = (Map.Entry)entry.next();
        key = (Object)item.getKey();
        values = (Objects)item.getValue();
        iter = values.iterator();
        value = (Object)iter.next();
      } else{
        value = (Object)iter.next();
      }
      return new Entry(key, value);
    }

    public java.lang.Object next(){
      return nextEntry();
    }

    public boolean hasNext(){
      return entry.hasNext()||(iter!=null&&iter.hasNext());
    }
  }

  public EntryIterator getEntryIterator(boolean cl){
    return new EntryIterator(cl);
  }

  public void clearSubObjects(){
    subObjects.clear();
  }

  public void clearClassAttributes(){
    classAttributes.clear();
  }

  public Objects find(Object obj){
    if (obj.superObjects.isEmpty()){
      if (this.name.equals(obj.name)){
        obj = (Object)obj.clone();
        Iterator iter = obj.classAttributes.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry item = (Map.Entry)iter.next();
          Objects values = (Objects)item.getValue();
          if (values.remove(this.getClassAttributes((Object)item.getKey()))){
            if (values.isEmpty()){
              iter.remove();
            }
          }
        }
        if (obj.classAttributes.isEmpty()) return new Objects(this);
        Objects ret = new Objects();
        iter = subObjects.iterator();
        while (iter.hasNext()) {
          Object item = (Object)iter.next();
          if (item.name.equals(obj.name)) {
            Objects find = item.find(obj);
            ret = ret.getAncestors(find);
          }
        }
        return ret;
      } else {
        Objects ret = new Objects();
        Iterator iter = subObjects.iterator();
        while (iter.hasNext()) {
          Object item = (Object)iter.next();
          Objects find = item.find(obj);
          ret = ret.getAncestors(find);
        }
        return ret;
      }
    } else {
      LinkedList so = new LinkedList();
      Iterator iter = obj.superObjects.iterator();
      while (iter.hasNext()) {
        Object item = (Object)iter.next();
        so.add(find(item));
      }
      obj = (Object)obj.clone();
      obj.superObjects.clear();
      Objects ret = new Objects();
      iter = so.iterator();
      Iterator iter0 = ((Objects)iter.next()).iterator();
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        ret.addIfNotHas(item0.find(obj));
      }
      while (iter.hasNext()) {
        Objects item = (Objects)iter.next();
        Objects find = new Objects();
        iter0 = item.iterator();
        while (iter0.hasNext()) {
          Object item0 = (Object)iter0.next();
          find.addIfNotHas(item0.find(obj));
        }
        ret = ret.getDescendents(find);
      }
      return ret;
    }
  }

  private void getAllAncestors(Objects ancestors){
    ancestors.add(this);
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (!ancestors.has(item)) item.getAllAncestors(ancestors);
    }
  }

  public Object getSuperObject(Object obj){
    return getSuperObjects(obj).getSingleObject();
  }

  public Objects getSuperObjects(Object obj){
    Objects ret = new Objects();
    if (obj.less(this)){
      ret.add(this);
    } else {
      Iterator iter = superObjects.iterator();
      while (iter.hasNext()) {
        Object item = (Object)iter.next();
        ret.addIfNotHas(item.getSuperObjects(obj));
      }
    }
    return ret;
  }

  public Objects getAllAncestors(){
    Objects ancestors = new Objects();
    getAllAncestors(ancestors);
    return ancestors;
  }

  public Objects getDifferences(Objects commonness){
    Objects dif = new Objects();
    boolean b = true;
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (b&&commonness.has(item)) {
        dif.add(this);
        b = false;
      } else {
        dif.add(item.getDifferences(commonness));
      }
    }
    return dif;
  }

  public void addAttribute(Object key, Object value){
    if (value!=unknown) {
/*      if (value.isInstanceOf(new Or())){
        if (key.isInstanceOf(new Or())){
          putAll(or.classAttributes, value.classAttributes);
        } else {
          put(or.classAttributes, key, value.getClassAttributes(key));
        }
      } else {*/
        put(attributes, key, value);
//      }
    }
  }

  public void addAttributes(Object key, Objects values){
    Objects val = (Objects)attributes.get(key);
    if (val==null) attributes.put(key, values);
    else val.add(values);
  }

  public void addClassAttributes(Object key, Objects values){
    Objects val = (Objects)classAttributes.get(key);
    if (val==null) classAttributes.put(key, values);
    else val.add(values);
  }

  public String getName(){
    return name;
  }

  private Objects getAnyAttributeFromSuperObject(Object key){
    Iterator iter = superObjects.iterator();
    Objects objs = new Objects();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Objects val = item.getAnyAttributesRecursive(key);
      objs.add(val);
    }
    return objs;
  }

  public Objects getAnyAttributesRecursive(Object key){
    Objects objs = new Objects();
    objs.add((Objects)attributes.get(key));
    objs.add((Objects)classAttributes.get(key));
    objs.add(getAnyAttributeFromSuperObject(key));
    return objs;
  }

  public Object getAnyAttributeRecursive(Object key){
    return getAnyAttributesRecursive(key).getSingleObject();
  }

  public Object[] getAttributeKeys(){
    return (Object[])attributes.keySet().toArray(new Object[attributes.size()]);
//    Object[] ret = (Object[])attributes.keySet().toArray(new Object[attributes.size()+1]);
//    ret[attributes.size()] = new Or();
//    return ret;
  }

  public Object[] getClassAttributeKeys(){
    return (Object[])classAttributes.keySet().toArray(new Object[classAttributes.size()]);
  }

  public boolean hasAttribute(Object key){
    return attributes.get(key)!=null;
  }

  public boolean hasAttribute(Object key, Object val){
    Objects objs = (Objects)attributes.get(key);
    if (objs!=null && objs.contains(val)) return true;
    else return false;
  }

  public boolean hasClassAttribute(Object key, Object val){
    Objects objs = (Objects)classAttributes.get(key);
    if (objs!=null && objs.contains(val)) return true;
    else return false;
  }

  public boolean hasClassAttribute(Object key){
    return classAttributes.get(key)!=null;
  }

  private boolean hasSuperClassAttribute(Object key, Object val){
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item.hasClassAttributeRecursive(key,val)) return true;
    }
    return false;
  }

  public boolean hasClassAttributeRecursive(Object key, Object val){
    Objects objs = (Objects)classAttributes.get(key);
    if (objs!=null && objs.contains(val)) return true;
    else return hasSuperClassAttribute(key,val);
  }

  public boolean hasAttributeRecursive(Object key, Object value){
    if (hasAttribute(key, value)) return true;
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item.hasAttributeRecursive(key, value)) return true;
    }
    return false;
  }

  public boolean hasAnyAttributeRecursive(Object key, Object value){
    return hasAttributeRecursive(key, value) | hasClassAttributeRecursive(key, value);
  }

  public void addClassAttribute(Object key, Object value){
    if (value!=unknown) {
      Objects objs = (Objects)classAttributes.get(key);
      if (objs==null){
        objs = new Objects();
        classAttributes.put(key, objs);
      }
      objs.add(value);
    }
  }

  public Objects getAttributes(Object key){
//    if (key.isInstanceOf(new Or())){
//      return new Objects(or);
//    } else {
      Objects ret = new Objects();
      ret.add((Objects)attributes.get(key));
//      Objects attr = or.getAttributes(key);
//      Object orattr = new Or();
//      put(orattr.attributes, key, attr);
//      ret.add(orattr);
      return ret;
//    }
  }

  public Object getAttribute(Object key){
    return getAttributes(key).getSingleObject();
  }

  public Objects getAttributesRecursive(Object key){
    Objects ret = new Objects();
    ret.add(getAttributes(key));
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      ret.add(item.getAttributesRecursive(key));
    }
    return ret;
  }

  public Object getAttributeRecursive(Object key){
    return getAttributesRecursive(key).getSingleObject();
  }

  private Objects getClassAttributeFromSuperObject(Object key){
    Iterator iter = superObjects.iterator();
    Objects objs = new Objects();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Objects val = item.getClassAttributesRecursive(key);
      objs.add(val);
    }
    return objs;
  }

  public Objects getClassAttributes(Object key){
    Objects ret = new Objects();
    ret.add((Objects)classAttributes.get(key));
    return ret;
  }

  public Object getClassAttribute(Object key){
    return getClassAttributes(key).getSingleObject();
  }

  public Objects getClassAttributesRecursive(Object key){
    Objects objs = new Objects();
    objs.add((Objects)classAttributes.get(key));
    objs.add(getClassAttributeFromSuperObject(key));
    return objs;
  }

  public Object getClassAttributeRecursive(Object key){
    return getClassAttributesRecursive(key).getSingleObject();
  }

  private boolean probableEquals(Object obj, LinkedList probableEquals){
    Iterator iter = probableEquals.iterator();
    while (iter.hasNext()) {
      Object[] item = (Object[])iter.next();
      if (item[0]==this && item[1]==obj) return true;
      if (item[0]==obj && item[1]==this) return true;
    }
    return false;
  }

  private boolean less(Objects objs1, Objects objs2, LinkedList probableEquals){
    Iterator iter = objs1.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter1 = ((Objects)objs2.clone()).iterator();
      boolean eq = false;
      while (iter1.hasNext()) {
        Object item1 = (Object)iter1.next();
        if (item.less(item1,probableEquals)){
          iter1.remove();
          eq = true;
          break;
        }
      }
      if (!eq) return false;
    }
    return true;
  }

  private boolean less(Object obj, LinkedList probableEquals){
    if (obj==this) return true;
    if (obj.hashCode()!=this.hashCode()) return false;
    if (!obj.name.equals(this.name)) return false;
    if (probableEquals(obj, probableEquals)) return true;

    probableEquals.addLast(new Object[]{this,obj});

    LinkedList sobjs = (LinkedList)obj.superObjects.clone();
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter1 = sobjs.iterator();
      boolean eq = false;
      while (iter1.hasNext()) {
        Object item1 = (Object)iter1.next();
        if (item.less(item1,probableEquals)){
          iter1.remove();
          eq = true;
          break;
        }
      }
      if (!eq) return false;
    }

    iter = this.classAttributes.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      HashMap attr = (HashMap)obj.classAttributes.clone();
      Iterator iter0 = attr.entrySet().iterator();
      boolean eq = false;
      while (iter0.hasNext()) {
        Map.Entry item0 = (Map.Entry)iter0.next();
        if (((Object)item.getKey()).less(((Object)item0.getKey()),probableEquals) &&
            less((Objects)item.getValue(), (Objects)item0.getValue(),probableEquals)){
          iter0.remove();
          eq = true;
          break;
        }
      }
      if (!eq) return false;
    }

    return true;
  }

//  private Object getRoot(){
//    if (superObjects.isEmpty()) return this;
//    else return ((Object)superObjects.getFirst()).getRoot();
//  }

  public boolean equals(java.lang.Object obj){
    if (obj == this) return true;
    if (obj.hashCode()!=this.hashCode()) return false;
    if (obj instanceof Object) {
//      if (this.getRoot()==((Object)obj).getRoot()) return this==obj;
      boolean ret = this.less((Object)obj)||((Object)obj).less(this);
      return ret;
    }
    return false;
  }

  public int hashCode(){
    return name.hashCode();
  }

  public java.lang.Object clone(){
    Object obj = null;
    try {
      obj = (Object)super.clone();
      obj.superObjects = (LinkedList)this.superObjects.clone();
      obj.attributes = new HashMap();
      Iterator iter = this.attributes.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry item = (Map.Entry)iter.next();
        obj.attributes.put(item.getKey(), ((Objects)item.getValue()).clone());
      }
      obj.classAttributes = new HashMap();
      iter = this.classAttributes.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry item = (Map.Entry)iter.next();
        obj.classAttributes.put(item.getKey(), ((Objects)item.getValue()).clone());
      }
      obj.subObjects = (LinkedList)this.subObjects.clone();
    }
    catch (CloneNotSupportedException ex) {
      ex.printStackTrace();
    }
    return obj;
  }

  private boolean specifyLink(Object link){
    Object[] keys = link.getClassAttributeKeys();
    for (int i = 0; i < keys.length; i++) {
      Objects values = link.getClassAttributes(keys[i]);
      boolean obj = keys[i].isInstanceOf(new Question.ЧтоИ());
      Iterator iter = values.iterator();
      while (iter.hasNext()) {
        Object item = (Object)iter.next();
        if (obj && this.specifyObject(item)){
          return true;
        } else if (this.hasAnyAttributeRecursive(keys[i], item)){
          return true;
        }
      }
    }
    return false;
  }

  private boolean specifyObject(Object obj){
    if (obj.less(this)) return true;
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object) iter.next();
      if (item.specifyObject(obj)) return true;
    }
    return false;
  }

  public boolean specify(Object obj){
    if (obj.isInstanceOf(new Link())||obj.isInstanceOf(new Or())){
      return specifyLink(obj);
    } else {
      return specifyObject(obj);
    }
  }

  public boolean isInstanceOf(Object obj){
    if (obj.less(this)) return true;
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item.isInstanceOf(obj)) return true;
    }
    return false;
  }

  public boolean isAcestorOf(Object obj){
    if (this==obj) return true;
    Iterator iter = obj.superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (this.isAcestorOf(item)) return true;
    }
    return false;
  }

  public void forget(Object obj){
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (obj.isAcestorOf(item)) {
        item.subObjects.remove(this);
        iter.remove();
      }
    }
  }

  public void forget(Objects objs){
    Iterator iter = objs.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      this.forget(item);
    }
  }

  private Object cloneStructure(LinkedList clones){
    Iterator iter = clones.iterator();
    while (iter.hasNext()) {
      Object[] item = (Object[])iter.next();
      if (item[0]==this) return item[1];
    }

    Object obj = (Object)this.clone();
    obj.subObjects.clear();
    obj.superObjects.clear();
    iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      obj.addSuperObject(item.cloneStructure(clones));
    }
    clones.add(new Object[]{this,obj});
    return obj;
  }

  public Object cloneStructure(){
    return cloneStructure(new LinkedList());
  }

  public boolean removeAncestors(Object obj){
    boolean ti = false;
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item.removeAncestors(obj)) ti = true;
    }
    if (ti) return true;

    if (obj.less(this)) {
      superObjects.clear();
      return true;
    }
    return false;
  }

  public Objects removeClassAttributes(Object key){
    Objects ret = (Objects)classAttributes.remove(key);
    if (ret==null) ret = new Objects();
    return ret;
  }

  public Objects removeAttributes(Object key){
    Objects ret = (Objects)attributes.remove(key);
    if (ret==null) ret = new Objects();
    return ret;
  }

  private void exchangeAttributes(Map.Entry entry, Object obj){
    Iterator iter = obj.classAttributes.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      if (((Object)entry.getKey()).less((Object)item.getKey())){
        Objects values = (Objects)item.getValue();
        Objects attr = values.removeMore((Objects)entry.getValue());
        if (values.isEmpty()) iter.remove();
        if (!attr.isEmpty()) addClassAttributes((Object)entry.getKey(), attr);
      }
    }
  }

  public void exchangeAttributes(Object obj){
    Iterator iter = classAttributes.entrySet().iterator();
    classAttributes = new HashMap();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      exchangeAttributes(item, obj);
    }
  }

  public String toStringWithClassAttribute(){
    String str = name+"[";
    Iterator iter = classAttributes.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      str+=item.getKey()+"-";
      str+=item.getValue();
      if (iter.hasNext()) str+=",";
    }
    str+="]";
    return str;
  }

  public String toStringWithAnyAttribute(){
    String str = name+"[";
    Iterator iter = classAttributes.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      str+=item.getKey()+"-";
      str+=item.getValue();
      if (iter.hasNext()) str+=",";
    }

    iter = attributes.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      str+=item.getKey()+"-";
      str+=item.getValue();
      if (iter.hasNext()) str+=",";
    }
    str+="]";
    return str;
  }

  public String toStringWithAttribute(){
    String str = name+"[";
    Iterator iter = attributes.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      str+=item.getKey()+"-";
      str+=item.getValue();
      if (iter.hasNext()) str+=",";
    }
    str+="]";
    return str;
  }

  public String toStringWithSubobjects(){
    String str = name+"[";
    Iterator iter = subObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      str+=item;
      if (iter.hasNext()) str+=",";
    }
    str+="]";
    return str;
  }

  public String toStringWithID(){
    return name+"@"+super.hashCode();
  }

  public String toString(){
    return name;
  }

  private int id = 0;
  private boolean writed = false;
  private void nullID(){
    id = 0;
    writed = false;
    Iterator iter = subObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      item.nullID();
    }
  }
  private int setID(int m){
    if (id==0){
      id = m;
      m++;
      Iterator iter = subObjects.iterator();
      while (iter.hasNext()) {
        Object item = (Object) iter.next();
        m = item.setID(m);
      }
    }
    return m;
  }
  private void deleteFile(File file){
    if (file.isDirectory()){
      File[] files = file.listFiles();
      for (int i = 0; i < files.length; i++) {
        deleteFile(files[i]);
      }
    }
    file.delete();
  }
  private void writeString(OutputStream out, String str) throws IOException{
    out.write(str.getBytes());
  }
  private void writeObjects(OutputStream link, File file) throws IOException{
    if (writed) return;
    writed = true;

    writeString(link, id+" - "+name+"\n");

    File obj = new File(file, name);
    if (!obj.exists()) obj.mkdir();
    obj = new File(obj, ""+id);
    if (!obj.exists()) obj.createNewFile();
    OutputStream out = new FileOutputStream(obj);
    writeString(out, "SuperObjects[");
    Iterator iter = superObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      writeString(out, ""+item.id);
      if (iter.hasNext()) writeString(out, ",");
    }

    writeString(out, "]\nClassAttributes\n");
    iter = classAttributes.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      writeString(out, ""+((Object)item.getKey()).id+"[");
      Objects values = (Objects)item.getValue();
      Iterator iter0 = values.iterator();
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        writeString(out, ""+item0.id);
        if (iter0.hasNext()) writeString(out, ",");
      }
      writeString(out, "]\n");
    }

    writeString(out, "Attributes\n");
    iter = attributes.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry item = (Map.Entry)iter.next();
      writeString(out, ""+((Object)item.getKey()).id+"[");
      Objects values = (Objects)item.getValue();
      Iterator iter0 = values.iterator();
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        writeString(out, ""+item0.id);
        if (iter0.hasNext()) writeString(out, ",");
      }
      writeString(out, "]\n");
    }
    out.close();

    iter = subObjects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      item.writeObjects(link, file);
    }
  }
  public void write(File file) throws IOException{
    nullID();
    setID(1);
    if (!file.exists()) file.createNewFile();
    String n = file.getCanonicalPath();
    File obj = new File(n.substring(0, n.length()-4));
    if (!obj.exists()) obj.mkdir();
    else {
      deleteFile(obj);
      obj.mkdir();
    }
    OutputStream out = new FileOutputStream(file);
    writeObjects(out, obj);
    out.close();
  }

  public static void main(String[] args){
    Object obj = new Object("name");
    System.out.println(obj.equals((java.lang.Object)obj));
  }
}