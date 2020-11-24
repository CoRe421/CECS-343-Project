import com.mpatric.mp3agic.*;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTunesGUI extends JFrame {
    BasicPlayer player;

    JFrame plNameFrame, plWindowFrame;
    JPanel mainPanel, mainSongPanel, treePanel, plNameMainPanel, plNameTopPanel, plNameBottomPanel, plMainPanel;
    ButtonPanel buttonsPanel;
    SongTable songTable;
    JButton plNameOKButton, plNameCancelButton;
    JTextField plNameUserText;
    JLabel plNameText;
    JMenuItem addSongMenu, addSongPopup, deleteSongPopup, deleteSongFromPlaylistPopup, openSong, exitGUI, AddPlaylistMenu, openPlaylistPopup, deletePlaylistPopup, plOpenSong, plExit;
    JPopupMenu libPopUp, playlistPopUp, plLibPopup;
    JMenuBar menuBar, plMenuBar;
    JMenu fileMenu, addPlaylistPopUp, plFileMenu, plAddPlaylistPopUp;
    JTree playlistTree;
    DefaultMutableTreeNode root, playlistNode;
    int currentSelectedRow;
    int currentPlayingRow;
    int[] currentSelectedRows;
    AddSong addAction;
    RemoveSong removeAction;
    PlaySong playAction;
    ExitGUI exitAction;
    OpenAddPlaylistWindow openAddPlaylistWindow;
    OpenPlaylistWindow openPlaylistWindow;
    DeletePlaylist deletePlaylist;
    DeleteSongFromPlaylist deleteSongFromPlaylist;

    ArrayList<SongTable> currentTables = new ArrayList<>();
    String[] currentlyPlaying = new String[2];
    MyDB songDB = new MyDB();

    public MyTunesGUI() {
        player = new BasicPlayer();
        mainPanel = new JPanel();
        menuBar = new JMenuBar();

        //Creates the panel with a vertical layout.
        mainPanel.setLayout(new BorderLayout());

        fileMenu = new JMenu("File");
        addPlaylistPopUp = new JMenu("Add to playlist");
        plAddPlaylistPopUp = new JMenu("Add to playlist");
        openSong = new JMenuItem("Open");
        addSongMenu = new JMenuItem("Add File To Library");
        AddPlaylistMenu = new JMenuItem("New Playlist");
        exitGUI = new JMenuItem("Exit");

        addSongPopup = new JMenuItem("Add song to Library");
        deleteSongPopup = new JMenuItem("Delete currently selected song");
        deleteSongFromPlaylistPopup = new JMenuItem("Delete currently selected song from playlist");

        openPlaylistPopup = new JMenuItem("Open in a new window");
        deletePlaylistPopup = new JMenuItem("Delete Playlist");

        addAction = new AddSong();
        removeAction = new RemoveSong();
        playAction = new PlaySong();
        exitAction = new ExitGUI();
        openAddPlaylistWindow = new OpenAddPlaylistWindow();
        openPlaylistWindow = new OpenPlaylistWindow();
        deletePlaylist = new DeletePlaylist();
        deleteSongFromPlaylist = new DeleteSongFromPlaylist();

        addSongMenu.addActionListener(addAction);
        AddPlaylistMenu.addActionListener(openAddPlaylistWindow);
        openSong.addActionListener(playAction);
        exitGUI.addActionListener(exitAction);
        addSongPopup.addActionListener(addAction);
        deleteSongPopup.addActionListener(removeAction);
        deleteSongFromPlaylistPopup.addActionListener(deleteSongFromPlaylist);
        openPlaylistPopup.addActionListener(openPlaylistWindow);
        deletePlaylistPopup.addActionListener(deletePlaylist);

        libPopUp = new JPopupMenu();
        libPopUp.add(addSongPopup);
        libPopUp.add(addPlaylistPopUp);
        libPopUp.add(deleteSongPopup);

        plLibPopup = new JPopupMenu();
        plLibPopup.add(plAddPlaylistPopUp);
        plLibPopup.add(deleteSongFromPlaylistPopup);

        playlistPopUp = new JPopupMenu();
        playlistPopUp.add(openPlaylistPopup);
        playlistPopUp.add(deletePlaylistPopup);


        songDB.connect();
        //The set of data that will be in the table.

        songTable = new SongTable();

        buttonsPanel = new ButtonPanel(songTable);

        this.setMinimumSize(new Dimension(300, 250));
        buttonsPanel.setPreferredSize(new Dimension(getWidth(), 40));
        buttonsPanel.setMaximumSize(new Dimension(getWidth(), 40));


        //Adds all the components to the panel.

        treePanel = new JPanel();
        mainSongPanel = new JPanel();
        mainSongPanel.setLayout(new BoxLayout(mainSongPanel, BoxLayout.X_AXIS));
        treePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        treePanel.setBackground(Color.white);

        MouseListener mouseListenerTree = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    playlistPopUp.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };


        root = new DefaultMutableTreeNode("Root");
        playlistNode = new DefaultMutableTreeNode("Playlist");
        root.add(new DefaultMutableTreeNode("Library"));
        root.add(playlistNode);

        ArrayList<String> plNodes = songDB.getListOfPlaylist();
        for (String node : plNodes) {
            playlistNode.add(new DefaultMutableTreeNode(node));
            JMenuItem menuItem = new JMenuItem(node);
            JMenuItem plMenuItem = new JMenuItem(node);
            menuItem.addActionListener(new AddSongToPlaylistPopup());
            plMenuItem.addActionListener(new AddSongToPlaylistPopup());
            addPlaylistPopUp.add(menuItem);
            plAddPlaylistPopUp.add(plMenuItem);
        }

        playlistTree = new JTree(root);
        playlistTree.setRootVisible(false);
        playlistTree.addMouseListener(mouseListenerTree);
        playlistTree.addTreeSelectionListener(new TablePlaylistChanger());

        treePanel.add(playlistTree);
        treePanel.setMinimumSize(new Dimension(100, 200));
        treePanel.setMaximumSize(new Dimension(100, 5000));
        treePanel.setPreferredSize(new Dimension(100, 200));
        playlistTree.setMaximumSize(new Dimension(treePanel.getWidth(), treePanel.getHeight()));

        mainSongPanel.add(treePanel);
        mainSongPanel.add(songTable.getScrollPane());
        mainPanel.add(mainSongPanel);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        fileMenu.add(openSong);
        fileMenu.add(AddPlaylistMenu);
        fileMenu.add(addSongMenu);
        fileMenu.add(exitGUI);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);
        this.setTitle("StreamPlayer");//change the name to yours
        this.setSize(600, 350);
        this.add(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class ButtonPanel extends JPanel {
        JButton playButton, pauseButton, skipBackButton, skipForwardButton;
        SongTable givenTable;
        JSlider slider;

        public ButtonPanel(SongTable givenSongTable) {
            givenTable = givenSongTable;

            this.setLayout(new BorderLayout());

            //Creates a Button Listener to attach to the playButton.
            playButton = new JButton("▶");
            playButton.addActionListener(new PlayButtonListener());
            playButton.setMinimumSize(new Dimension(450, 25));
            playButton.setMaximumSize(new Dimension(450, 25));

            pauseButton = new JButton("⏸");
            pauseButton.addActionListener(new PauseButtonListener());
            pauseButton.setMinimumSize(new Dimension(450, 25));
            pauseButton.setMaximumSize(new Dimension(450, 25));

            skipBackButton = new JButton("⏮");
            skipBackButton.addActionListener(new SkipBackButtonListener());
            skipBackButton.setMinimumSize(new Dimension(450, 25));
            skipBackButton.setMaximumSize(new Dimension(450, 25));

            skipForwardButton = new JButton("⏭");
            skipForwardButton.addActionListener(new SkipForwardButtonListener());
            skipForwardButton.setMinimumSize(new Dimension(450, 25));
            skipForwardButton.setMaximumSize(new Dimension(450, 25));

            slider = new JSlider(JSlider.HORIZONTAL, 0 , 100 ,100);
            slider.setMajorTickSpacing(25);
            slider.setPaintTicks(true);
            event e = new event();
            slider.addChangeListener(e);

            slider.setPreferredSize(new Dimension(100, 10));
            slider.setMaximumSize(new Dimension(100, 10));
            slider.setMinimumSize(new Dimension(100, 10));

            JPanel buttonHolderPanel = new JPanel();

            buttonHolderPanel.add(skipBackButton);
            buttonHolderPanel.add(playButton);
            buttonHolderPanel.add(pauseButton);
            buttonHolderPanel.add(skipForwardButton);
            this.add(buttonHolderPanel);
            this.add(slider, BorderLayout.EAST);
            this.setBackground(Color.blue);

        }

        class event implements ChangeListener {
            @Override
            public void stateChanged(ChangeEvent e) {
                double value = slider.getValue()/100.0;
                try {
                    player.setGain(value);
                } catch (BasicPlayerException basicPlayerException) {
                    basicPlayerException.printStackTrace();
                }
            }
        }

        class PlayButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = givenTable.getTable();
                if (player.getStatus() == 1 && ((currentlyPlaying[0] == table.getValueAt(currentSelectedRow,0)) && (currentlyPlaying[1] == table.getValueAt(currentSelectedRow,1)))) {
                    try {
                        player.resume();
                    } catch (BasicPlayerException basicPlayerException) {
                        basicPlayerException.printStackTrace();
                    }
                }
                else {
                    File selectedSong;
                    if (table.getSelectedRow() != -1) {
                        String title = (String)table.getValueAt(currentSelectedRow,0);
                        String artist = (String)table.getValueAt(currentSelectedRow,1);
                        currentlyPlaying[0] = title;
                        currentlyPlaying[1] = artist;
                        String sql = "SELECT SongID FROM SONGS WHERE Title = ? AND Artist = ?";
                        try {
                            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytunes","root", "");
                            PreparedStatement preparedStatement = connection.prepareStatement(sql);
                            preparedStatement.setString(1,title);
                            preparedStatement.setString(2,artist);
                            ResultSet rs = preparedStatement.executeQuery();
                            rs.next();
                            String url = rs.getString("SongID");
                            selectedSong = new File(url);
                            try {
                                player.open(selectedSong);
                                player.play();
                                currentPlayingRow = currentSelectedRow;
                            } catch (BasicPlayerException basicPlayerException) {
                                basicPlayerException.printStackTrace();
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
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
                JTable table = givenTable.getTable();
                File selectedSong;
                String sql = "SELECT SongID FROM SONGS WHERE Title = ? AND Artist = ?";
                if(currentPlayingRow == 0){
                    String title = (String)table.getValueAt(table.getRowCount()-1,0);
                    String artist = (String)table.getValueAt(table.getRowCount()-1,1);
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytunes","root", "");
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1,title);
                        preparedStatement.setString(2,artist);
                        ResultSet rs = preparedStatement.executeQuery();
                        rs.next();
                        String url = rs.getString("SongID");
                        selectedSong = new File(url);
                        try {
                            player.open(selectedSong);
                            player.play();
                            currentPlayingRow = table.getRowCount() - 1;
                            table.setRowSelectionInterval(currentPlayingRow,currentPlayingRow);
                        } catch (BasicPlayerException basicPlayerException) {
                            basicPlayerException.printStackTrace();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                } else {
                    String title = (String)table.getValueAt(currentPlayingRow - 1,0);
                    String artist = (String)table.getValueAt(currentPlayingRow - 1,1);
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytunes","root", "");
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1,title);
                        preparedStatement.setString(2,artist);
                        ResultSet rs = preparedStatement.executeQuery();
                        rs.next();
                        String url = rs.getString("SongID");
                        selectedSong = new File(url);
                        try {
                            player.open(selectedSong);
                            player.play();
                            currentPlayingRow = currentPlayingRow - 1;
                            table.setRowSelectionInterval(currentPlayingRow,currentPlayingRow);
                        } catch (BasicPlayerException basicPlayerException) {
                            basicPlayerException.printStackTrace();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }

        class SkipForwardButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = givenTable.getTable();
                File selectedSong;
                String sql = "SELECT SongID FROM SONGS WHERE Title = ? AND Artist = ?";
                if(currentPlayingRow == table.getRowCount() - 1){
                    String title = (String)table.getValueAt(0,0);
                    String artist = (String)table.getValueAt(0,1);
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytunes","root", "");
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1,title);
                        preparedStatement.setString(2,artist);
                        ResultSet rs = preparedStatement.executeQuery();
                        rs.next();
                        String url = rs.getString("SongID");
                        selectedSong = new File(url);
                        try {
                            player.open(selectedSong);
                            player.play();
                            currentPlayingRow = 0;
                            table.setRowSelectionInterval(currentPlayingRow,currentPlayingRow);
                        } catch (BasicPlayerException basicPlayerException) {
                            basicPlayerException.printStackTrace();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                } else {
                    String title = (String)table.getValueAt(currentPlayingRow + 1,0);
                    String artist = (String)table.getValueAt(currentPlayingRow + 1,1);
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytunes","root", "");
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1,title);
                        preparedStatement.setString(2,artist);
                        ResultSet rs = preparedStatement.executeQuery();
                        rs.next();
                        String url = rs.getString("SongID");
                        selectedSong = new File(url);
                        try {
                            player.open(selectedSong);
                            player.play();
                            currentPlayingRow = currentPlayingRow + 1;
                            table.setRowSelectionInterval(currentPlayingRow,currentPlayingRow);
                        } catch (BasicPlayerException basicPlayerException) {
                            basicPlayerException.printStackTrace();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }

            }
        }
    }

    class SongTable extends ScrollPane {
        JTable table;
        DefaultTableModel model;
        JScrollPane scrollPane;
        String plName;
        String[] columns = {"Title", "Artist", "Album", "Genre", "Release Year", "Comment"};

        public SongTable() {
            //songTable.getSongs(data);
            model = new DefaultTableModel();
            model.setColumnIdentifiers(columns);
            table = new JTable();
            table.setModel(model);
            showSongs(plName);
            //Attaches the mouseListener to the table.

            table.setFillsViewportHeight(true);
            table.setDragEnabled(true);
            table.setTransferHandler(new Transfer());

            //Sets the width of the URL column to be larger.
            TableColumn column = table.getColumnModel().getColumn(0);
            column.setPreferredWidth(250);

            scrollPane = new JScrollPane(table);
        }

        public SongTable(String playlistName) {
            //songTable.getSongs(data);
            plName = playlistName;
            model = new DefaultTableModel();
            model.setColumnIdentifiers(columns);
            table = new JTable();
            table.setModel(model);
            showSongs(playlistName);
            //Attaches the mouseListener to the table.

            table.setFillsViewportHeight(true);
            table.setDragEnabled(true);
            table.setTransferHandler(new Transfer());

            //Sets the width of the URL column to be larger.
            TableColumn column = table.getColumnModel().getColumn(0);
            column.setPreferredWidth(250);

            scrollPane = new JScrollPane(table);
        }

        public JScrollPane getScrollPane() {
            return scrollPane;
        }

        private void showSongs(String playlistName) {
            plName = playlistName;
            ArrayList<Song> list;
            table.removeMouseListener(mouseListenerTable);
            table.removeMouseListener(plMouseListenerTable);
            if (playlistName == null) {
                list = songDB.songList();
                table.addMouseListener(mouseListenerTable);
            }
            else {
                list = songDB.songList(playlistName);
                table.addMouseListener(plMouseListenerTable);
            }
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            Object[] row = new Object[6];
            for (int i = 0 ; i < list.size() ; i ++) {
                row[0] = list.get(i).getTitle();
                row[1] = list.get(i).getArtist();
                row[2] = list.get(i).getAlbum();
                row[3] = list.get(i).getGenre();
                row[4] = list.get(i).getReleaseYear();
                row[5] = list.get(i).getComment();
                model.addRow(row);
            }
        }

        class Transfer extends TransferHandler {
            public boolean canImport(TransferHandler.TransferSupport support) {
                return true;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection("Eyo");
            }

            public int getSourceActions(JComponent component) {
                //  AccessibleContext accessibleContext1 = component.getAccessibleContext();int[] selectedRows = table.getSelectedRows();
                currentSelectedRows = table.getSelectedRows();

                return TransferHandler.COPY_OR_MOVE;
            }

            public boolean importData(TransferSupport supp) {
                if (supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        List<File> result = (List) supp.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        for(File o : result) {
                            try {
                                Mp3File selectedSong = new Mp3File(o);
                                String songTitle = "", songArtist= "", songGenre= "", songAYear= "", songAlbum= "", songComment= "";
                                if (selectedSong.hasId3v1Tag()) {
                                    ID3v1 idTag = selectedSong.getId3v1Tag();
                                    songTitle = idTag.getTitle();
                                    if (songTitle == null) songTitle = "Unknown";
                                    songArtist = idTag.getArtist();
                                    if (songArtist == null) songArtist = "Unknown";
                                    songGenre = idTag.getGenreDescription();
                                    if (songGenre == null) songGenre = "Unknown";
                                    songAYear = idTag.getYear();
                                    if (songAYear == null) songAYear = "Unknown";
                                    songAlbum = idTag.getAlbum();
                                    if (songAlbum == null) songAlbum = "Unknown";
                                    songComment = idTag.getComment();
                                    if (songComment == null) songComment = "Unknown";

                                } else if (selectedSong.hasId3v2Tag()) {
                                    ID3v2 idTag = selectedSong.getId3v2Tag();
                                    songTitle = idTag.getTitle();
                                    if (songTitle == null) songTitle = "Unknown";
                                    songArtist = idTag.getArtist();
                                    if (songArtist == null) songArtist = "Unknown";
                                    songGenre = idTag.getGenreDescription();
                                    if (songGenre == null) songGenre = "Unknown";
                                    songAYear = idTag.getYear();
                                    if (songAYear == null) songAYear = "Unknown";
                                    songAlbum = idTag.getAlbum();
                                    if (songAlbum == null) songAlbum = "Unknown";
                                    songComment = idTag.getComment();
                                    if (songComment == null) songComment = "Unknown";
                                }
                                Object[] row = new Object[6];
                                row[0] = songTitle;
                                row[1] = songArtist;
                                row[2] = songAlbum;
                                row[3] = songGenre;
                                row[4] = songAYear;
                                row[5] = songComment;
                                try {
                                    songDB.addSong(o);
                                    model.addRow(row);
                                }
                                catch (SQLIntegrityConstraintViolationException ex) {
                                    System.out.println("A song already exists in the library");
                                }
                                if (plName != null) {
                                    songDB.addSongToPlayList(plName, o.getAbsolutePath());
                                    if (songTable.getPlName() == null) {
                                        songTable.updateTable(null);
                                    }
                                    updateTable(plName);
                                }
                            } catch (IOException | UnsupportedTagException | InvalidDataException | SQLException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                else {
                    JTable target = (JTable) supp.getComponent();
                    JTable mainTable = songTable.getTable();
                    SongTable targetSongTable = songTable;

                    for (SongTable openTable : currentTables) {
                        if (openTable.getTable().equals(target));
                        targetSongTable = openTable;
                    }
                    for (int currentRow : currentSelectedRows) {
                        String title = (String)mainTable.getValueAt(currentRow,0);
                        String artist = (String)mainTable.getValueAt(currentRow,1);
                        String sql = "SELECT SongID FROM SONGS WHERE Title = ? AND Artist = ?";
                        try {
                            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytunes","root", "");
                            PreparedStatement preparedStatement = connection.prepareStatement(sql);
                            preparedStatement.setString(1,title);
                            preparedStatement.setString(2,artist);
                            ResultSet rs = preparedStatement.executeQuery();
                            rs.next();
                            String url = rs.getString("SongID");
                            songDB.addSongToPlayList(targetSongTable.getPlName(), url);
                            for (SongTable openTable : currentTables) {
                                if (targetSongTable.getPlName().equals(openTable.getPlName())) {
                                    openTable.updateTable(openTable.getPlName());
                                }
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
                return true;
            }

            protected void exportDone(JComponent component, Transferable transferable, int action)
            {
                if (action == TransferHandler.MOVE) {
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }

        public void updateTable(String plName) {
            showSongs(plName);
        }

        public String getPlName() {
            return plName;
        }

        public JTable getTable() {
            return table;
        }

        public DefaultTableModel getModel() {
            return model;
        }

        //Creates a new listener for the mouse attached to the table.
        MouseListener mouseListenerTable = new MouseAdapter() {
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

        MouseListener plMouseListenerTable = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showPopup(e);
                currentSelectedRow = table.getSelectedRow();
                System.out.println("plSelected index " + currentSelectedRow);
            }

            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    plLibPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
    }

    class TablePlaylistChanger implements TreeSelectionListener {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            try {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) playlistTree.getLastSelectedPathComponent();
                if (!(selectedNode.toString().equals("Playlist"))) {
                    if (!(selectedNode.toString().equals("Library"))) {
                        songTable.updateTable(selectedNode.toString());
                    }
                    else {
                        songTable.updateTable(null);
                    }
                }
            }
            catch (NullPointerException err) {
                //playlistTree.setSelectionRow(0);
            }
        }
    }

    class AddSong implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultTableModel model = songTable.getModel();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            int result = fileChooser.showOpenDialog(fileMenu);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                try {
                    Mp3File selectedSong = new Mp3File(selectedFile);
                    String songTitle = "", songArtist= "", songGenre= "", songAYear= "", songAlbum= "", songComment= "";
                    if (selectedSong.hasId3v1Tag()) {
                        ID3v1 idTag = selectedSong.getId3v1Tag();
                        songTitle = idTag.getTitle();
                        if (songTitle == null) songTitle = "Unknown";
                        songArtist = idTag.getArtist();
                        if (songArtist == null) songArtist = "Unknown";
                        songGenre = idTag.getGenreDescription();
                        if (songGenre == null) songGenre = "Unknown";
                        songAYear = idTag.getYear();
                        if (songAYear == null) songAYear = "Unknown";
                        songAlbum = idTag.getAlbum();
                        if (songAlbum == null) songAlbum = "Unknown";
                        songComment = idTag.getComment();
                        if (songComment == null) songComment = "Unknown";

                    } else if (selectedSong.hasId3v2Tag()) {
                        ID3v2 idTag = selectedSong.getId3v2Tag();
                        songTitle = idTag.getTitle();
                        if (songTitle == null) songTitle = "Unknown";
                        songArtist = idTag.getArtist();
                        if (songArtist == null) songArtist = "Unknown";
                        songGenre = idTag.getGenreDescription();
                        if (songGenre == null) songGenre = "Unknown";
                        songAYear = idTag.getYear();
                        if (songAYear == null) songAYear = "Unknown";
                        songAlbum = idTag.getAlbum();
                        if (songAlbum == null) songAlbum = "Unknown";
                        songComment = idTag.getComment();
                        if (songComment == null) songComment = "Unknown";
                    }
                    songDB.addSong(selectedFile);
                    Object[] row = new Object[6];
                    row[0] = songTitle;
                    row[1] = songArtist;
                    row[2] = songAlbum;
                    row[3] = songGenre;
                    row[4] = songAYear;
                    row[5] = songComment;
                    model.addRow(row);

                } catch (IOException | InvalidDataException | UnsupportedTagException | SQLException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    class RemoveSong implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTable table = songTable.getTable();
            DefaultTableModel model = songTable.getModel();
            try {
                String title = (String)table.getValueAt(currentSelectedRow,0);
                String artist = (String)table.getValueAt(currentSelectedRow,1);
                songDB.removeSong(title, artist);
                model.removeRow(currentSelectedRow);
                for (SongTable openTable : currentTables) {
                    openTable.updateTable(openTable.getPlName());
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
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

    class OpenAddPlaylistWindow implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Adding a new playlist");
            plNameFrame = new JFrame();
            plNameFrame.setSize(350, 130);

            plNameMainPanel = new JPanel();
            plNameMainPanel.setLayout(new BoxLayout(plNameMainPanel, BoxLayout.Y_AXIS));
            plNameTopPanel = new JPanel();
            plNameTopPanel.setLayout(new BoxLayout(plNameTopPanel, BoxLayout.Y_AXIS));
            plNameTopPanel.setPreferredSize(new Dimension(plNameFrame.getWidth(), plNameFrame.getHeight()));
            plNameTopPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            plNameBottomPanel = new JPanel(new FlowLayout());
            plNameBottomPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            plNameText = new JLabel("Playlist name:");
            plNameText.setAlignmentX(plNameTopPanel.LEFT_ALIGNMENT);

            plNameUserText = new JTextField();
            plNameUserText.setMaximumSize(new Dimension(250, 20));
            plNameUserText.setMinimumSize(new Dimension(250, 20));
            plNameUserText.setAlignmentX(plNameUserText.LEFT_ALIGNMENT);

            plNameTopPanel.add(plNameText);
            plNameTopPanel.add(plNameUserText);

            plNameOKButton = new JButton("OK");
            plNameOKButton.setPreferredSize(new Dimension(74, 25));
            plNameOKButton.addActionListener(new AddPlaylist());

            plNameCancelButton = new JButton("Cancel");
            plNameCancelButton.setPreferredSize(new Dimension(74, 25));
            plNameCancelButton.addActionListener(new ClosePLName());

            plNameBottomPanel.add(plNameOKButton);
            plNameBottomPanel.add(plNameCancelButton);

            plNameMainPanel.add(plNameTopPanel);
            plNameMainPanel.add(plNameBottomPanel);
            plNameFrame.add(plNameMainPanel);
            plNameFrame.setTitle("Input");
            plNameFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            plNameFrame.setResizable(false);
            plNameFrame.setVisible(true);
        }
    }

    class OpenPlaylistWindow implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            TreePath playlistName = playlistTree.getSelectionPath();
            if (!(playlistName.getLastPathComponent().toString().equals("Playlist"))) {
                if (!(playlistName.getLastPathComponent().toString().equals("Library"))) {
                    plWindowFrame = new JFrame();
                    plMainPanel = new JPanel();
                    plMenuBar = new JMenuBar();
                    plMainPanel.setLayout(new BoxLayout(plMainPanel, BoxLayout.Y_AXIS));
                    SongTable playlistTable = new SongTable(playlistName.getLastPathComponent().toString());
                    ButtonPanel playlistButtons = new ButtonPanel(playlistTable);

                    plOpenSong = new JMenuItem("Open");
                    plExit = new JMenuItem("Exit");
                    plOpenSong.addActionListener(new PlaySong());
                    plExit.addActionListener(new ExitGUI());

                    plFileMenu = new JMenu("File");
                    plMenuBar = new JMenuBar();

                    plFileMenu.add(plOpenSong);
                    plFileMenu.add(plExit);

                    playlistButtons.setPreferredSize(new Dimension(getWidth(), 40));
                    playlistButtons.setMaximumSize(new Dimension(getWidth(), 40));
                    plMainPanel.add(playlistTable.getScrollPane());
                    plMainPanel.add(playlistButtons);
                    plMenuBar.add(plFileMenu);
                    plWindowFrame.setJMenuBar(plMenuBar);
                    plWindowFrame.setTitle(playlistName.getLastPathComponent().toString());
                    plWindowFrame.setSize(600, 350);
                    plWindowFrame.add(plMainPanel);
                    plWindowFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    currentTables.add(playlistTable);
                    plWindowFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                            currentTables.remove(playlistTable);
                        }
                    });
                    plWindowFrame.setVisible(true);

                    for (SongTable table : currentTables) {
                        System.out.println(table.getPlName());
                    }

                    playlistTree.setSelectionRow(0);
                }
                else {
                    System.out.println("That action cannot be performed on base nodes");
                }
            }
        }
    }

    class AddPlaylist implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultTreeModel playlistTreeModel = (DefaultTreeModel)playlistTree.getModel();
            String newPLName = plNameUserText.getText();

            DefaultMutableTreeNode newPlaylist = new DefaultMutableTreeNode(newPLName);
            TreePath treePath = new TreePath(playlistNode);
            playlistTree.setSelectionPath(treePath.pathByAddingChild(newPlaylist));

            playlistTreeModel.insertNodeInto(newPlaylist, playlistNode, playlistNode.getChildCount());

            try {
                songDB.addPlayList(newPLName);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


            JMenuItem menuItem = new JMenuItem(newPLName);
            JMenuItem plMenuItem = new JMenuItem(newPLName);
            menuItem.addActionListener(new AddSongToPlaylistPopup());
            plMenuItem.addActionListener(new AddSongToPlaylistPopup());
            addPlaylistPopUp.add(menuItem);
            plAddPlaylistPopUp.add(plMenuItem);

            plNameFrame.dispatchEvent(new WindowEvent(plNameFrame, WindowEvent.WINDOW_CLOSING));
        }
    }

    class DeletePlaylist implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            TreePath playlistName = playlistTree.getSelectionPath();
            if (!(playlistName.getParentPath().equals(new TreePath(root)))) {
                try {
                    songDB.deletePlayList(playlistName.getLastPathComponent().toString());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                Component[] playlistItems = addPlaylistPopUp.getMenuComponents();
                Component[] plPlaylistItems = plAddPlaylistPopUp.getMenuComponents();
                for (Component playlist : playlistItems) {
                    JMenuItem tempItem = (JMenuItem)playlist;
                    if (tempItem.getText().equals(playlistName.getLastPathComponent().toString())) {
                        addPlaylistPopUp.remove(playlist);
                    }
                }
                for (Component playlist : plPlaylistItems) {
                    JMenuItem tempItem = (JMenuItem)playlist;
                    if (tempItem.getText().equals(playlistName.getLastPathComponent().toString())) {
                        plAddPlaylistPopUp.remove(playlist);
                    }
                }

                DefaultTreeModel playlistTreeModel = (DefaultTreeModel)playlistTree.getModel();
                playlistTreeModel.removeNodeFromParent((MutableTreeNode)playlistName.getLastPathComponent());
            }
            else {
                System.out.println("Cannot remove base nodes");
            }
        }
    }

    class AddSongToPlaylistPopup implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            JTable table = songTable.getTable();

            String title = (String)table.getValueAt(currentSelectedRow,0);
            String artist = (String)table.getValueAt(currentSelectedRow,1);
            String sql = "SELECT SongID FROM SONGS WHERE Title = ? AND Artist = ?";
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytunes","root", "");
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1,title);
                preparedStatement.setString(2,artist);
                ResultSet rs = preparedStatement.executeQuery();
                rs.next();
                String url = rs.getString("SongID");
                songDB.addSongToPlayList(item.getText(), url);
                for (SongTable openTable : currentTables) {
                    if (item.getText().equals(openTable.getPlName())) {
                        openTable.updateTable(item.getText());
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    class DeleteSongFromPlaylist implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem deleteItem = (JMenuItem)e.getSource();
            JPopupMenu deleteMenu = (JPopupMenu)deleteItem.getParent();
            JTable deleteTable = (JTable) deleteMenu.getInvoker();
            if (songTable.getTable().equals(deleteTable)) {
                JTable mainTable = songTable.getTable();
                String title = (String)mainTable.getValueAt(currentSelectedRow,0);
                String artist = (String)mainTable.getValueAt(currentSelectedRow,1);
                String sql = "SELECT SongID FROM SONGS WHERE Title = ? AND Artist = ?";
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytunes","root", "");
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1,title);
                    preparedStatement.setString(2,artist);
                    ResultSet rs = preparedStatement.executeQuery();
                    rs.next();
                    String url = rs.getString("SongID");
                    songDB.deleteSongFromPlaylist(songTable.getPlName(), url);
                    songTable.updateTable(songTable.getPlName());
                    for (SongTable openTable : currentTables) {
                        if (songTable.getPlName().equals(openTable.getPlName())) {
                            openTable.updateTable(openTable.getPlName());
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            else {
                for (SongTable openTable : currentTables) {
                    if (openTable.getTable().equals(deleteTable)) {
                        JTable mainTable = openTable.getTable();
                        String title = (String)mainTable.getValueAt(currentSelectedRow,0);
                        String artist = (String)mainTable.getValueAt(currentSelectedRow,1);
                        String sql = "SELECT SongID FROM SONGS WHERE Title = ? AND Artist = ?";
                        try {
                            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytunes","root", "");
                            PreparedStatement preparedStatement = connection.prepareStatement(sql);
                            preparedStatement.setString(1,title);
                            preparedStatement.setString(2,artist);
                            ResultSet rs = preparedStatement.executeQuery();
                            rs.next();
                            String url = rs.getString("SongID");
                            songDB.deleteSongFromPlaylist(openTable.getPlName(), url);
                            openTable.updateTable(openTable.getPlName());
                            songTable.updateTable(songTable.getPlName());
                            for (SongTable currentTable : currentTables) {
                                if (openTable.getPlName().equals(currentTable.getPlName())) {
                                    currentTable.updateTable(currentTable.getPlName());
                                }
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    class ClosePLName implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            plNameFrame.dispatchEvent(new WindowEvent(plNameFrame, WindowEvent.WINDOW_CLOSING));
        }
    }

    class ExitGUI implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}