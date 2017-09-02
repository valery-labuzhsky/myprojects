package intelligence.instinct;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class File implements Object{
  java.io.File file;
  FileManager parent;

  public File(FileManager parent, java.io.File file) {
    this.parent = parent;
    this.file = file;
  }

  public boolean exists(){
    return file!=null&&file.exists();
  }

  public boolean isDirectory(){
    return file.isDirectory();
  }

  public Action getAction(String name){
    return null;
  }
}