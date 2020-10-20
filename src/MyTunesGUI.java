import com.mpatric.mp3agic.*;
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
import java.io.File;
import java.io.IOException;
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
    JMenuItem addSongMenu, deleteSongMenu, addSongPopup, deleteSongPopup, openSong, exitGUI;
    JPopupMenu libPopUp;
    JTable table;
    JMenuBar menuBar;
    JMenu fileMenu;
    JScrollPane scrollPane;
    int currentSelectedRow;
    AddSong addAction;
    RemoveSong removeAction;
    PlaySong playAction;
    ExitGUI exitAction;
    PlayButtonListener playBL;
    PauseButtonListener pauseBL;
    SkipBackButtonListener skipBackBL;
    SkipForwardButtonListener skipForwardBL;
    public MyTunesGUI() throws SQLException, InvalidDataException, IOException, UnsupportedTagException {
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
        addSongMenu = new JMenuItem("Add a Song");
        deleteSongMenu = new JMenuItem("Delete a Song");
        openSong = new JMenuItem("Open a Song");
        exitGUI = new JMenuItem("Exit");

        addSongPopup = new JMenuItem("Add a Song");
        deleteSongPopup = new JMenuItem("Delete a Song");

        addAction = new AddSong();
        removeAction = new RemoveSong();
        playAction = new PlaySong();
        exitAction = new ExitGUI();

        addSongMenu.addActionListener(addAction);
        deleteSongMenu.addActionListener(removeAction);
        openSong.addActionListener(playAction);
        exitGUI.addActionListener(exitAction);
        addSongPopup.addActionListener(addAction);
        deleteSongPopup.addActionListener(removeAction);

        libPopUp = new JPopupMenu();
        libPopUp.add(addSongPopup);
        libPopUp.add(deleteSongPopup);

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
                showPopup(e);
                currentSelectedRow = table.getSelectedRow();
                System.out.println("Selected index " + currentSelectedRow);
            }

            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    libPopUp.show(e.getComponent(), e.getX(), e.getY());
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
         **/


        //Adds all the components to the panel.
        buttonsPanel.add(skipBackButton);
        buttonsPanel.add(playButton);
        buttonsPanel.add(pauseButton);
        buttonsPanel.add(skipForwardButton);
        mainPanel.add(scrollPane);
        mainPanel.add(buttonsPanel);
        fileMenu.add(addSongMenu);
        fileMenu.add(deleteSongMenu);
        fileMenu.add(openSong);
        fileMenu.add(exitGUI);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);
        this.setTitle("StreamPlayer by Cory Reardon");//change the name to yours
        this.setSize(600, 350);
        this.add(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class PlayButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (player.getStatus() == 1) {
                try {
                    player.resume();
                } catch (BasicPlayerException basicPlayerException) {
                    basicPlayerException.printStackTrace();
                }
            }
            else {
                File selectedSong = null;
                if (table.getSelectedRow() != -1) {
                    selectedSong = (File)table.getValueAt(currentSelectedRow, 4);
                    try {
                        player.open(selectedSong);
                        player.play();
                    } catch (BasicPlayerException basicPlayerException) {
                        basicPlayerException.printStackTrace();
                    }
                }
                else {
                    System.out.println("No row selected.");
                }
            }

        }
    }

    class PauseButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(player.getStatus());
            if (player.getStatus() == 0) {
                try {
                    player.pause();
                } catch (BasicPlayerException basicPlayerException) {
                    basicPlayerException.printStackTrace();
                }
            }
            else if (player.getStatus() == 1) {
                try {
                    player.resume();
                } catch (BasicPlayerException basicPlayerException) {
                    basicPlayerException.printStackTrace();
                }
            }

        }
    }

    class SkipBackButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {



        }
    }

    class SkipForwardButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {


        }
    }

    class AddSong implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            int result = fileChooser.showOpenDialog(fileMenu);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                try {
                    Mp3File selectedSong = new Mp3File(selectedFile);

                    if (selectedSong.hasId3v1Tag()) {
                        ID3v1 idTag = selectedSong.getId3v1Tag();
                        String songTitle = idTag.getTitle();
                        String songArtist = idTag.getArtist();
                        int songGenre = idTag.getGenre();
                        String songAYear = idTag.getYear();
                        File songDirect = selectedFile;
                    }
                    else if (selectedSong.hasId3v2Tag()) {
                        ID3v2 idTag = selectedSong.getId3v2Tag();
                        String songTitle = idTag.getTitle();
                        String songArtist = idTag.getArtist();
                        int songGenre = idTag.getGenre();
                        String songAYear = idTag.getYear();
                        File songDirect = selectedFile;
                    }
                    else if (selectedSong.hasCustomTag()) {
                        System.out.println("Your song has a custom tag and will not work.");
                    }
                    else {
                        System.out.println("Song type not compatible");
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (UnsupportedTagException unsupportedTagException) {
                    unsupportedTagException.printStackTrace();
                } catch (InvalidDataException invalidDataException) {
                    invalidDataException.printStackTrace();
                }
            }

        }
    }

    class RemoveSong implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {


        }
    }

    class PlaySong implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            int result = fileChooser.showOpenDialog(fileMenu);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                try {
                    player.open(selectedFile);
                    player.play();
                } catch (BasicPlayerException basicPlayerException) {
                    basicPlayerException.printStackTrace();
                }
            }

        }
    }

    class ExitGUI implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
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