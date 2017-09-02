/*
 * SpectroFrame.java
 *
 * Created on 28 ���� 2008 �., 11:49
 */

package unicorn.voice.spectroviewer;

import unicorn.utils.Listeners;
import unicorn.utils.Preferences;
import unicorn.voice.spectrogram.Spectrogram;
import unicorn.voice.spectroviewer.file.FileOpenListener;
import unicorn.voice.spectroviewer.menu.ReopenMenu;
import unicorn.voice.spectroviewer.utils.PrefEnum;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author  uniCorn
 */
public class SpectroFrame extends JFrame implements FileOpenListener {
    private JMenu fileMenu;
    private JMenuBar menuBar;

    private SpectroPane spectro = new SpectroPane();
    private JFileChooser fileChooser;

    public final Listeners<FileOpenListener> listeners = new FileOpenListener.s();

    public SpectroFrame() {
        listeners.add(this);
        initComponents();
        LinkedList<String> reopen = PrefEnum.PREFERENCES.getStringList(PrefEnum.REOPEN);
        if (!reopen.isEmpty()) {
            opened(reopen.getFirst());
        }
    }
    
    private void initComponents() {
        setTitle("Spectro");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initMenuBar();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(spectro);

//        setExtendedState(JFrame.MAXIMIZED_BOTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                KeyListener[] keyListeners = spectro.getKeyListeners();
                for (KeyListener keyListener : keyListeners) {
                    keyListener.keyPressed(e);
                }
            }
        });
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu();

        fileChooser = new JFileChooser(".");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Sound", "wav"));

        fileMenu.setText("File");
        JMenuItem openMenuItem = new JMenuItem("Open ...");
        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String folder = PrefEnum.PREFERENCES.getString(PrefEnum.FOLDER);
                if (folder==null) folder = ".";
                fileChooser.setCurrentDirectory(new File(folder));
                if (fileChooser.showOpenDialog(SpectroFrame.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    PrefEnum.PREFERENCES.setString(PrefEnum.FOLDER, file.getParent());
                    listeners.fire.opened(file.getPath());
                }
            }
        });
        fileMenu.add(openMenuItem);
        menuBar.add(fileMenu);

        ReopenMenu reopenMenu = new ReopenMenu();
        reopenMenu.listeners.add(this);
        this.listeners.add(reopenMenu);
        fileMenu.add(reopenMenu);

        setJMenuBar(menuBar);
    }

    public void opened(String file) {
        try {
            spectro.setSpectro(null);
            spectro.setSpectro(new Spectrogram(file));
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(SpectroFrame.this, ex.getMessage(), "Cannot open file", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                SpectroFrame frame = new SpectroFrame();
                frame.pack();
//                GraphicsEnvironment env =
//                GraphicsEnvironment.getLocalGraphicsEnvironment();
//                frame.setMaximizedBounds(env.getMaximumWindowBounds());
                frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);
            }
        });
    }
}
