package unicorn.voice.spectroviewer.menu;

import unicorn.utils.Listeners;
import unicorn.utils.collection.CollectionMap;
import unicorn.voice.spectroviewer.file.FileOpenListener;
import unicorn.voice.spectroviewer.utils.PrefEnum;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

/**
 * @author unicorn
 */
public class ReopenMenu extends JMenu implements FileOpenListener {
    private final static int REOPEN_LIMIT = 10;

    @SuppressWarnings("unchecked")
    private final CollectionMap<String, Set<String>, String> map = new CollectionMap<String, Set<String>, String>(HashSet.class);
    private final LinkedList<String> files;

    public final Listeners<FileOpenListener> listeners = new FileOpenListener.s();

    public ReopenMenu() {
        super("Reopen");
        files = PrefEnum.PREFERENCES.getStringList(PrefEnum.REOPEN);
        for (String filename : files) {
            File file = new File(filename);
            map.add(file.getName(), filename);
        }
        listeners.add(this);
        create();
    }

    private void create() {
        for (String file : files) {
            add(new Item(file));
        }
    }

    public void opened(String file) {
        String name = new File(file).getName();
        Set<String> paths = map.get(name);
        if (!paths.contains(file)) {
            files.addFirst(file);
            map.add(name, file);
            if (files.size()>REOPEN_LIMIT) {
                String looser = files.removeLast();
                map.get(new File(looser).getName()).remove(looser);
            }
        } else {
            files.remove(file);
            files.addFirst(file);
        }
        save();
        recreate();
    }

    private void recreate() {
        this.removeAll();
        create();
    }

    private class Item extends JMenuItem {
        public Item(final String filename) {
            String name = new File(filename).getName();
            if (map.get(name).size()>1) {
                setText(filename);
            } else {
                setText(name);
            }
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    listeners.fire.opened(filename);
                }
            });
        }
    }

    private void save() {
        PrefEnum.PREFERENCES.setStringList(PrefEnum.REOPEN, files);
    }
}
