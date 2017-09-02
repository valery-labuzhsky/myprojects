package intelligence.instinct;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Directory extends File{
  public class OpenAction implements Action{
    private String name;
    protected OpenAction(String name){
      this.name = name;
    }

    public void doAction(){
      parent.changeCD(Directory.this.file);
    }
  }

  public class CloseAction implements Action{
    private String name;
    protected CloseAction(String name){
      this.name = name;
    }

    public void doAction(){
      parent.changeCD(Directory.this.file.getParentFile());
    }
  }

  public Directory(FileManager parent, java.io.File file) {
    super(parent,file);
  }

  public Action getAction(String name){
    if (name.equals("открыть")){
      return new OpenAction(name);
    } else if (name.equals("закрыть")){
      return new CloseAction(name);
    }
    return null;
  }

}