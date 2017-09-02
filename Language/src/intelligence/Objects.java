package intelligence;

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

public class Objects implements Cloneable, Serializable{
  private LinkedList objects = new LinkedList();

  public Objects() {
  }

  public Objects(Object item){
    this.add(item);
  }

  public void add(Object obj){
    objects.add(obj);
  }

  public void addLast(Object obj){
    objects.addLast(obj);
  }

  public void add(Objects objs){
    if (objs!=null) objects.addAll(objs.objects);
  }

  public Object removeFirst(){
    return (Object)objects.removeFirst();
  }

  public boolean has(Object obj){
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (obj==item) return true;
    }
    return false;
  }

  public boolean isSingleObject(){
    return objects.size()==1;
  }

  public Object getSingleObject(){
    if (isSingleObject()) return (Object)objects.getFirst();
    else return Object.unknown;
  }

  public Objects getEquals(Objects objs){
    Objects ret = new Objects();
    Iterator iter = this.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter0 = objs.iterator();
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        if (item==item0) ret.add(item);
      }
    }
    return ret;
  }

  public Object getFirst(){
    return (Object)objects.getFirst();
  }

  public Object getCommonObject(){
    Iterator iter = objects.iterator();
    Object ret = Object.unknown;
    if (iter.hasNext()) {
      ret = (Object)iter.next();
      while (iter.hasNext()) {
        Object item = (Object) iter.next();
        ret = ret.intersection(item);
      }
    }
    return ret;
  }

  public Objects getInstances(Object obj){
    Objects ret = new Objects();
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item.isInstanceOf(obj)){
        ret.add(item);
      }
    }
    return ret;
  }

  public Object getInstance(Object obj){
    return getInstances(obj).getSingleObject();
  }

  public Objects getAncestors(Objects objs){
    Objects t = (Objects)this.clone();
    objs = (Objects)objs.clone();
    Iterator iter = t.objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter0 = objs.iterator();
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        if (item.isAcestorOf(item0)) {
          iter0.remove();
        } else if (item0.isAcestorOf(item)){
          iter.remove();
          break;
        }
      }
    }
    t.add(objs);
    return t;
  }

  public Objects getDescendents(Objects objs){
    Objects ret = new Objects();
    Objects t = (Objects)this.clone();
    objs = (Objects)objs.clone();
    Iterator iter = t.objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter0 = objs.iterator();
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        if (item.isAcestorOf(item0)) {
          ret.add(item0);
          iter0.remove();
        } else if (item0.isAcestorOf(item)){
          ret.add(item);
          iter.remove();
          break;
        }
      }
    }
    t.add(objs);
    return t;
  }

  public Objects removeAncestors(Objects objs){
    Objects ret = new Objects();
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter0 = objs.iterator();
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        if (item.isAcestorOf(item0)) {
          ret.add(item);
          iter.remove();
          break;
        }
      }
    }
    return ret;
  }

  public Objects removeMore(Objects objs){
    Objects ret = new Objects();
    Iterator iter = objs.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter0 = this.iterator();
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        if (item.less(item0)){
          ret.add(item0);
          iter0.remove();
        }
      }
    }
    return ret;
  }

  public boolean remove(Object obj){
    return objects.remove(obj);
  }

  public boolean remove(Objects objs){
    boolean ret = false;
    if (objs!=null){
      Iterator iter = objs.objects.iterator();
      while (iter.hasNext()) {
        Object item = (Object) iter.next();
        Iterator iter0 = this.objects.iterator();
        while (iter0.hasNext()) {
          Object item0 = (Object) iter0.next();
          if (item0.less(item)) {
            iter0.remove();
            ret = true;
          }
        }
      }
    }
    return ret;
  }

/*  public Objects intersection(Object obj){
    Objects ret = new Objects();
    Iterator iter = this.iterator();
    boolean b = true;
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item.classEquals(obj)){
        ret.add(item.intersection(obj));
        b = false;
        break;
      }
      ret.add(item);
    }
    if (b) ret.add(obj);
    while (iter.hasNext()) {
      Object item = (Object) iter.next();
      ret.add(item);
    }
    return ret;
  }*/

/*  public Objects intersection(Object key, Objects objs){
    Object t;
    Object o;
    if (this.isEmpty()) t = new Object("@ничего");
    else if (this.isSingleObject()) t = this.getSingleObject();
    else t = this.getAndObject(key);
    if (objs.isEmpty()) o = new Object("@ничего");
    else if (objs.isSingleObject()) o = objs.getSingleObject();
    else o = objs.getAndObject(key);
    return new Objects(t.intersection(o, key));
  }*/

  public Object getAndObject(Object key){
    if (isSingleObject()) return getSingleObject();
    And and = new And();
    and.addClassAttributes(key, this);
    return and;
  }

  public Object getOrObject(Object key){
    if (isSingleObject()) return getSingleObject();
    Or or = new Or();
    or.addClassAttributes(key, this);
    return or;
  }

  public Object getOrObject(){
    return getOrObject(new Question.ЧтоИ());
  }

/*  public Objects intersection(Objects objs){
    Objects ret = (Objects)this.clone();
    Iterator iter = objs.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      Iterator iter0 = ret.iterator();
      boolean b = true;
      while (iter0.hasNext()) {
        Object item0 = (Object)iter0.next();
        if (item.classEquals(item0)){
          iter0.remove();
          ret.add(item.intersection(item0));
          b = false;
          break;
        }
      }
      if (b) ret.add(item);
    }
    return ret;
  }*/

  public boolean isEmpty(){
    return objects.isEmpty();
  }

  public boolean contains(Object val){
    return objects.contains(val);
  }

  public Iterator iterator(){
    return objects.iterator();
  }

  public int size(){
    return objects.size();
  }

  public void addIfNotHas(Object obj){
    if (!has(obj)) objects.add(obj);
  }

  public void addIfNotHas(Objects objs){
    Iterator iter = objs.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      addIfNotHas(item);
    }
  }

  public Objects getDifferences(){
    if (isEmpty()) return this;
    Iterator iter = objects.iterator();
    Object item = (Object)iter.next();
    Objects com = item.getAllAncestors();
    while (iter.hasNext()) {
      item = (Object)iter.next();
      com.addIfNotHas(item.getAllAncestors());
    }

    iter = objects.iterator();
    item = (Object)iter.next();
    Objects dif = item.getDifferences(com);
    while (iter.hasNext()) {
      item = (Object)iter.next();
      dif.addIfNotHas(item.getDifferences(com));
    }

    return dif;
  }

  public Objects specify(Object obj){
    Objects ret = new Objects();
    Object[] keys = obj.getClassAttributeKeys();
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item.specify(obj)) ret.add(item);
    }
    return ret;
  }

  public Objects specify(Link link){
    Objects ret = new Objects();
    return ret;
  }

  public boolean hasClassAttribute(Object key, Object value){
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item.hasClassAttribute(key, value)) return true;
    }
    return false;
  }

  public boolean hasAnyAttributeRecursive(Object key, Object value){
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      if (item.hasAnyAttributeRecursive(key, value)) return true;
    }
    return false;
  }

  public java.lang.Object clone(){
    Objects obj = null;
    try {
      obj = (Objects)super.clone();
      obj.objects = (LinkedList)this.objects.clone();
    } catch (CloneNotSupportedException ex){
      ex.printStackTrace();
    }
    return obj;
  }

  public Object[] toArray(){
    return (Object[])objects.toArray(new Object[objects.size()]);
  }

  public String toString(){
    String str = "[";
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object item = (Object)iter.next();
      str+=item;
      if (iter.hasNext()) str+=",";
    }
    str+="]";
    return str;
  }
}