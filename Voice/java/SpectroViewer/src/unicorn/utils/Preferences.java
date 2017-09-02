package unicorn.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author unicorn
 */
public class Preferences<T extends Enum<?>> {
    private final Properties properties = new Properties();
    private final String filename;

    public Preferences(String filename) {
        this.filename = filename;
        try {
            FileInputStream stream = new FileInputStream(filename);
            properties.load(stream);
            stream.close();
        } catch (IOException e) {
            System.err.println("Failed to load properties "+filename);
            e.printStackTrace();
        }
    }

    public String getString(T field) {
        return properties.getProperty(getName(field));
    }

    public void setString(T field, String value) {
        properties.setProperty(getName(field), value);
        save();
    }

    public LinkedList<String> getStringList(T field) {
        LinkedList<String> list = new LinkedList<String>();
        int i = 0;
        while (true) {
            String value = properties.getProperty(getName(field) + "." + i);
            if (value==null) break;
            list.add(value);
            i++;
        }
        return list;
    }

    public void setStringList(T field, List<String> list) {
        int i = 0;
        for (String value : list) {
            properties.setProperty(getName(field)+"."+i, value);
            i++;
        }
        while (true) {
            String name = getName(field) + "." + i;
            String value = properties.getProperty(name);
            if (value!=null) {
                properties.remove(name);
            } else break;
            i++;
        }
        save();
    }

    private String getName(T field) {
        return field.name().toLowerCase();
    }

    private void save() {
        try {
            FileOutputStream stream = new FileOutputStream(filename);
            properties.store(stream, "");
            stream.close();
        } catch (IOException e) {
            System.err.println("Failed to store properties "+filename);
            e.printStackTrace();
        }
    }
}
