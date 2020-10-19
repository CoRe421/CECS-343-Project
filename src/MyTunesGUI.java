import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyTunesGUI extends JFrame {
    BasicPlayer player;

    JPanel mainPanel, buttonsPanel;
    JButton playButton, pauseButton, skipBackButton, skipForwardButton;
    JMenuItem addSong, deleteSong, openSong, exitGUI;
    JPopupMenu libPopUp;
    JTable table;
    JMenuBar menuBar;
    JMenu fileMenu;
    JScrollPane scrollPane;
    int currentSelectedRow;
    PlayButtonListener playBL;
    PauseButtonListener pauseBL;
    SkipBackButtonListener skipBackBL;
    SkipForwardButtonListener skipForwardBL;
    public MyTunesGUI() throws SQLException {
        player = new BasicPlayer();
        mainPanel = new JPanel();
        buttonsPanel = new JPanel();
        menuBar = new JMenuBar();

        //Creates the panel with a vertical layout.
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        buttonsPanel.setLayout(new FlowLayout());

        //Creates a Button Listener to attach to the playButton.
        playBL = new PlayButtonListener();
        pauseBL = new PauseButtonListener();
        skipBackBL = new SkipBackButtonListener();
        skipForwardBL = new SkipForwardButtonListener();

        //Sets the button's text, attaches the listener, and also sets it's size and alignment.
        playButton = new JButton("▶");
        playButton.addActionListener(playBL);
        playButton.setMinimumSize(new Dimension(450, 25));
        playButton.setMaximumSize(new Dimension(450, 25));

        pauseButton = new JButton("⏸");
        pauseButton.addActionListener(pauseBL);
        pauseButton.setMinimumSize(new Dimension(450, 25));
        pauseButton.setMaximumSize(new Dimension(450, 25));

        skipBackButton = new JButton("⏮");
        skipBackButton.addActionListener(skipBackBL);
        skipBackButton.setMinimumSize(new Dimension(450, 25));
        skipBackButton.setMaximumSize(new Dimension(450, 25));

        skipForwardButton = new JButton("⏭");
        skipForwardButton.addActionListener(skipForwardBL);
        skipForwardButton.setMinimumSize(new Dimension(450, 25));
        skipForwardButton.setMaximumSize(new Dimension(450, 25));

        fileMenu = new JMenu("File");
        addSong = new JMenuItem("Add a Song");
        deleteSong = new JMenuItem("Delete a Song");
        openSong = new JMenuItem("Open a Song");
        exitGUI = new JMenuItem("Exit");


        libPopUp = new JPopupMenu();
        libPopUp.add(addSong);
        libPopUp.add(deleteSong);

        MyDB songTable = new MyDB();
        songTable.connect();
        //The set of data that will be in the table.
        String[] columns = {"Title", "Artist", "Genre", "Release Year", "ID"};
        String[][] data = new String[20][5];
        songTable.getSongs(data);

        table = new JTable(data, columns);

        //Creates a new listener for the mouse attached to the table.
        MouseListener mouseListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                currentSelectedRow = table.getSelectedRow();
                System.out.println("Selected index " + currentSelectedRow);
            }

            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    libPopUp.show(e.getComponent(), e.getX(), e.getY());
                    System.out.println("Success");
                }
            }
        };


        //Attaches the mouseListener to the table.
        table.addMouseListener(mouseListener);

        table.setDropTarget(new MyDropTarget());

        //Sets the width of the URL column to be larger.
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(250);

        scrollPane = new JScrollPane(table);



        this.setMinimumSize(new Dimension(300, 250));
        table.setPreferredSize(new Dimension(getWidth(), getHeight()));
        buttonsPanel.setPreferredSize(new Dimension(getWidth(), 40));
        buttonsPanel.setMaximumSize(new Dimension(getWidth(), 40));
        table.setPreferredSize(new Dimension(getWidth(), getHeight()));

        /**
        buttonsPanel.setBackground(Color.gray);
        mainPanel.setBackground(Color.green);
        tablePanel.setBackground(Color.blue);
         **/

        //Adds all the components to the panel.
        buttonsPanel.add(skipBackButton);
        buttonsPanel.add(playButton);
        buttonsPanel.add(pauseButton);
        buttonsPanel.add(skipForwardButton);
        mainPanel.add(scrollPane);
        mainPanel.add(buttonsPanel);
        fileMenu.add(addSong);
        fileMenu.add(deleteSong);
        fileMenu.add(openSong);
        fileMenu.add(exitGUI);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);
        this.setTitle("StreamPlayer by Cory Reardon");//change the name to yours
        this.setSize(400, 150);
        this.add(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class PlayButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String url = null;

            //If there is a row selected on the table, sets url to that element.
            if (table.getSelectedRow() != -1) {
                url = (String)table.getValueAt(currentSelectedRow, 0);
            }

            //Attempts to play the url.
            try {
                player.open(new URL(url));
                player.play();
            } catch (MalformedURLException ex) {
                Logger.getLogger(MyTunesGUI.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Malformed url");
            } catch (BasicPlayerException ex) {
                System.out.println("BasicPlayer exception");
                Logger.getLogger(MyTunesGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class PauseButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String url = null;

            //If there is a row selected on the table, sets url to that element.
            if (table.getSelectedRow() != -1) {
                url = (String)table.getValueAt(currentSelectedRow, 0);
            }

            //Attempts to play the url.
            try {
                player.open(new URL(url));
                player.play();
            } catch (MalformedURLException ex) {
                Logger.getLogger(MyTunesGUI.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Malformed url");
            } catch (BasicPlayerException ex) {
                System.out.println("BasicPlayer exception");
                Logger.getLogger(MyTunesGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class SkipBackButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String url = null;

            //If there is a row selected on the table, sets url to that element.
            if (table.getSelectedRow() != -1) {
                url = (String)table.getValueAt(currentSelectedRow, 0);
            }

            //Attempts to play the url.
            try {
                player.open(new URL(url));
                player.play();
            } catch (MalformedURLException ex) {
                Logger.getLogger(MyTunesGUI.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Malformed url");
            } catch (BasicPlayerException ex) {
                System.out.println("BasicPlayer exception");
                Logger.getLogger(MyTunesGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class SkipForwardButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String url = null;

            //If there is a row selected on the table, sets url to that element.
            if (table.getSelectedRow() != -1) {
                url = (String)table.getValueAt(currentSelectedRow, 0);
            }

            //Attempts to play the url.
            try {
                player.open(new URL(url));
                player.play();
            } catch (MalformedURLException ex) {
                Logger.getLogger(MyTunesGUI.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Malformed url");
            } catch (BasicPlayerException ex) {
                System.out.println("BasicPlayer exception");
                Logger.getLogger(MyTunesGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class MyDropTarget extends DropTarget {
        public  void drop(DropTargetDropEvent evt) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);

                java.util.List result = new ArrayList();
                result = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);


                for(Object o : result)
                    System.out.println(o.toString());

            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

    }
}