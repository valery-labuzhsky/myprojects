package intelligence;

import java.util.*;
import language.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CreateTask extends Task{
  private Object object;

  public CreateTask(Task parent, Object object) {
    super(parent);
    this.object = object;
  }

  public CreateTask(Memory memory, Asker asker, Object object) {
    super(memory, asker);
    this.object = object;
  }

  private class KeyTask{
//    Task key;
    private Objects values;
    private Object key;
    private Object object;
//    LinkedList values = new LinkedList();

    public KeyTask(Object key, Object object, boolean clattr){
      this.key = key;
      this.object = object;
//      this.key = new SpecifyTask(CreateTask.this, key);
//      Objects values;
      if (clattr) values = object.getClassAttributes(key);
      else values = object.getAttributes(key);
//      Iterator iter = values.iterator();
//      while (iter.hasNext()) {
//        Object item = (Object)iter.next();
//        Task task = new SpecifyTask(CreateTask.this, item);
//        this.values.addFirst(task);
//      }
    }

    public Object getKey(){
      return new SpecifyTask(CreateTask.this, key).solve();
//      return key.result();
    }

    public Objects getValues(){
      Objects ret = new Objects();
      Iterator iter = values.iterator();
      while (iter.hasNext()) {
        Object item = (Object)iter.next();
        ret.add(new SpecifyTask(CreateTask.this, item).solve());
//        Task item = (Task)iter.next();
//        ret.add(item.result());
      }
      return ret;
    }
  }

/*  private class Roots{
    LinkedList roots = new LinkedList();

    public Roots(Object obj){
      Object find = (Object)obj.clone();
      find.clearClassAttributes();
      Objects rts = memory.find(find);
      Iterator iter = rts.iterator();
      while (iter.hasNext()) {
        Object item = (Object)iter.next();
        roots.addLast(new Root(item));
      }

      if (!roots.isEmpty()){
        Object[] keys = obj.getClassAttributeKeys();
        for (int i = 0; i < keys.length; i++) {
          Objects values = obj.getClassAttributes(keys[i]);
          iter = values.iterator();
          while (iter.hasNext()) {
            Object item = (Object)iter.next();
            find.addClassAttribute(keys[i], item);
            rts = memory.find(find);
            LinkedList newroots = new LinkedList();
            Iterator iter0 = roots.iterator();
            while (iter0.hasNext()) {
              Root item0 = (Root)iter0.next();
              if (item0.addDescendant(rts)){
                newroots.addLast(item0);
              }
            }
            if (!newroots.isEmpty()) roots = newroots;
          }
        }
      }
    }

    public Objects getRoots(){
      Objects ret = new Objects();
      Iterator iter = roots.iterator();
      while (iter.hasNext()) {
        Root item = (Root)iter.next();
        ret.add(item.root);
      }
      return ret;
    }

    public Root getRoot(Object obj){
      Iterator iter = roots.iterator();
      while (iter.hasNext()) {
        Root item = (Root)iter.next();
        if (item.root==obj) return item;
      }
      return null;
    }

    private class Root {
      private Object root;
      private Objects desc = new Objects();

      public Root(Object root) {
        this.root = root;
      }

      public boolean addDescendant(Objects objs){
        Iterator iter = objs.iterator();
        boolean ret = false;
        while (iter.hasNext()) {
          Object item = (Object)iter.next();
          if (root.isAcestorOf(item)){
            desc.addIfNotHas(item);
            ret = true;
          }
        }
        return ret;
      }

      public Objects getDescendents(){
        Objects ret = new Objects();
        desc.add(root);
        while(!desc.isEmpty()){
          Object obj = desc.removeFirst();
          Iterator iter = desc.iterator();
          boolean add = true;
          while (iter.hasNext()) {
            Object item = (Object)iter.next();
            if (obj.isAcestorOf(item)){
              add = false;
              break;
            }
          }
          if (add){
            ret.add(obj);
          }
        }
        return ret;
      }
    }
  }*/

/*  private void addAttributes(Object ret){
    Object[] keys = object.getClassAttributeKeys();
    KeyTask[] kt = new KeyTask[keys.length];
    for (int i = 0; i < keys.length; i++) {
      kt[i] = new KeyTask(keys[i], true);
      ret.addClassAttributes(kt[i].getKey(), kt[i].getValues());
    }
//    for (int i = 0; i < kt.length; i++) {
//      ret.addClassAttributes(kt[i].getKey(), kt[i].getValues());
//    }

    keys = object.getAttributeKeys();
    kt = new KeyTask[keys.length];
    for (int i = 0; i < keys.length; i++) {
      kt[i] = new KeyTask(keys[i], false);
      ret.addAttributes(kt[i].getKey(), kt[i].getValues());
    }
//    for (int i = 0; i < kt.length; i++) {
//      ret.addAttributes(kt[i].getKey(), kt[i].getValues());
//    }

  }*/

  private class ClassAttribute{
    private Object root;
    private Object key;
    private Object value;
    private Objects attr;
    private Object obj = null;
    private Object create = null;
    private Object find;

    public ClassAttribute(Object root, Object key, Object value){
      this.root = root;
      this.key = key;
      this.value = value;
      find = new Object(root.getName());
      find.addClassAttribute(key, value);
      attr = root.find(find);
      if (attr.isSingleObject()) obj = attr.getSingleObject();
    }

    public void add(ClassAttribute ca){
      find.addClassAttribute(ca.key, ca.value);
    }

    public void create(Object superObject){
      if (obj!=null){
        if (find.classAttributeEquals(obj)){
          create = obj;
        } else{
          find.exchangeAttributes(obj);
          obj.addSuperObject(find);
          create = find;
        }
      } else {
        if (attr.isEmpty()){
          create = new Object(find.getName());
          createAttributes(find, create);
        } else {
          Iterator iter = attr.iterator();
          while (iter.hasNext()){
            Object item = (Object)iter.next();
            find.exchangeAttributes(item);
            item.addSuperObject(find);
          }
          create = find;
        }
        create.addSuperObject(superObject);
      }
    }
  }

  private void createAttributes(Object object, Object ret){
    Object[] keys = object.getClassAttributeKeys();
    KeyTask[] kt = new KeyTask[keys.length];
    for (int i = 0; i < keys.length; i++) {
      kt[i] = new KeyTask(keys[i], object, true);
      ret.addClassAttributes(kt[i].getKey(), kt[i].getValues());
    }

/*    keys = object.getAttributeKeys();
    kt = new KeyTask[keys.length];
    for (int i = 0; i < keys.length; i++) {
      kt[i] = new KeyTask(keys[i], object, false);
      ret.addAttributes(kt[i].getKey(), kt[i].getValues());
    }*/
  }

  private class Root{
    private Object root;
    private LinkedList attr = new LinkedList();
    private Object create;
    private Object find;

    public Root(Object root){
      this.root = root;
      find = new Object(root.getName());
      Object.EntryIterator itere = object.getEntryIterator(true);
      while (itere.hasNext()) {
        Object.Entry iteme = itere.nextEntry();
        attr.add(new ClassAttribute(root, iteme.getKey(), iteme.getValue()));
      }
    }

    public boolean isEmpty(){
      Iterator iter = attr.iterator();
      while (iter.hasNext()) {
        ClassAttribute item = (ClassAttribute)iter.next();
        if (!item.attr.isEmpty()) return false;
      }
      return true;
    }

    public void create(){
      LinkedList sa = new LinkedList();
      Iterator iter = attr.iterator();
      while (iter.hasNext()) {
        ClassAttribute item = (ClassAttribute)iter.next();
        if (item.obj!=null){
          sa.add(item);
          iter.remove();
        }
      }

      LinkedList newsa = new LinkedList();
      iter = sa.iterator();
      while (iter.hasNext()) {
        ClassAttribute item = (ClassAttribute)iter.next();
        Iterator iter0 = newsa.iterator();
        boolean add = false;
        while (iter0.hasNext()) {
          ClassAttribute item0 = (ClassAttribute)iter0.next();
          if (item.obj == item0.obj){
            item0.add(item);
            iter.remove();
            add = true;
            break;
          }
        }
        if (!add){
          newsa.add(item);
        }
      }
      attr.addAll(newsa);

      iter = attr.iterator();
      while (iter.hasNext()) {
        ClassAttribute item = (ClassAttribute)iter.next();
        if (item.obj == root){
          this.add(item.find);
          iter.remove();
        }
      }

      if (find.classAttributeEquals(root)){
        create = root;
      } else {
        Object[] so = root.getSuperObjects();
        for (int i = 0; i < so.length; i++) {
          root.removeSuperObject(so[i]);
          find.addSuperObject(so[i]);
        }
        find.exchangeAttributes(root);
        root.addSuperObject(find);
        create = find;
      }

      iter = attr.iterator();
      while (iter.hasNext()) {
        ClassAttribute item = (ClassAttribute)iter.next();
        item.create(create);
      }

      if (attr.size()==0){
        object.clearClassAttributes();
        createAttributes(object, create);
        object = create;
      } else if (attr.size()==1){
        Object obj = ((ClassAttribute)attr.getFirst()).create;
        object.clearClassAttributes();
        createAttributes(object, obj);
        object = obj;
      } else {
        object.clearClassAttributes();
        Object obj = new Object(object.getName());
        iter = attr.iterator();
        while (iter.hasNext()) {
          Object item = ((ClassAttribute)iter.next()).create;
          obj.addSuperObject(item);
        }
        createAttributes(object, obj);
        object = obj;
      }
    }

    private void add(Object obj){
      Object.EntryIterator itere = obj.getEntryIterator(true);
      while (itere.hasNext()) {
        Object.Entry iteme = itere.nextEntry();
        find.addClassAttribute(iteme.getKey(), iteme.getValue());
      }
    }
  }

  public Object solve(){
    while (object==null);
    Object find = (Object)object.clone();
    object = (Object)object.clone();
    find.clearClassAttributes();
    Objects roots = memory.find(find);
    Object ret = new Object(object.getName());
    if (roots.isEmpty()){
      Object[] so = object.getSuperObjects();
      if (so.length > 0) {
        for (int i = 0; i < so.length; i++) {
          memory.remember(ret, new SpecifyTask(this, so[i]).solve());
//        ret.addSuperObject(st[i].result());
        }
      }
      else {
        memory.remember(ret, memory.getRootObject());
//      ret.addSuperObject(memory.getRootObject());
      }
      createAttributes(object, ret);
    } else if (roots.isSingleObject()) {
      Root root = new Root(roots.getSingleObject());
      root.create();
      ret = object;
    } else {
      LinkedList rts = new LinkedList();
      Objects objs = new Objects();
      Iterator iter = roots.iterator();
      while (iter.hasNext()) {
        Object item = (Object)iter.next();
        Root root = new Root(item);
        if (!root.isEmpty()){
          rts.add(root);
          objs.add(item);
        }
      }
      if (rts.size()==0){
        askwhat(object, roots);
        Object send = answer();
        Root root = new Root(send);
        root.create();
        ret = object;
      } else if (rts.size()==1){
        Root root = (Root)rts.getFirst();
        root.create();
        ret = object;
      } else {
        askwhat(object, objs);
        Object send = answer();
        Root root = new Root(send);
        root.create();
        ret = object;
      }
    }
    return ret;
/*    while (object==null);
    Roots roots = new Roots(object);
    Objects rts = roots.getRoots();
    Object ret = new Object(object.getName());
    while (true){
//      System.out.println(object);
//      System.out.println(rts);
      if (rts.isEmpty()) {
        Object[] so = object.getSuperObjects();
        Task[] st = null;
        if (so.length > 0) {
          st = new Task[so.length];
          for (int i = 0; i < st.length; i++) {
            st[i] = new SpecifyTask(this, so[i]);
            memory.remember(ret, st[i].result());
//        ret.addSuperObject(st[i].result());
          }
        }
        else {
          memory.remember(ret, memory.getRootObject());
//      ret.addSuperObject(memory.getRootObject());
        }

//            if (st==null){
//              ret.addSuperObject(memory.getRootObject());
//            } else {
//              for (int i = 0; i < st.length; i++) {
//                ret.addSuperObject(st[i].result());
//              }
//            }
        addAttributes(ret);
        break;
      }
      else if (rts.isSingleObject()) {
        rts = roots.getRoot(rts.getSingleObject()).getDescendents();
        Iterator iter = rts.iterator();
        while (iter.hasNext()) {
          Object item = (Object) iter.next();
          memory.remember(ret, item);
        }
        addAttributes(ret);
        break;
      }
      else {
        askwhat(object, rts);
        Object send = answer();
        if (send.hasAttribute(new Question.Какой(), new Object("другой"))) {
          rts = new Objects();
        }
        else {
          rts = rts.specify(send);
        }
      }
    }
    fireResult(ret);*/
  }
}