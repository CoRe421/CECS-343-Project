import com.mpatric.mp3agic.*;
import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyDB {
    String user = "root";
    String password = "";
    String url = "jdbc:mysql://localhost:3306/myTunes";

    Connection connection;
    Statement statement;
    PreparedStatement preparedStatement;

    public void connect() {
        System.out.println("Connecting to mytunes...");
        try {
            connection = DriverManager.getConnection(url, user, password);
        }
        catch (SQLException ex) {
            Logger.getLogger(MyDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getSongs(String[][] data) throws SQLException {
        statement = connection.createStatement();
        int index = 0;
        if (statement.execute("SELECT * FROM Songs")) {
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                System.out.println(rs.getString("SongID") + " " + rs.getString("Title") + " " + rs.getString("Artist"));
                data[index][0] = rs.getString("Title");
                data[index][1] = rs.getString("Artist");
                data[index][2] = rs.getString("Genre");
                data[index][3] = rs.getString("Release Year");
                data[index][4] = rs.getString("SongID");
                index++;
            }
        }
    }

    public void addSong(File song) throws InvalidDataException, IOException, UnsupportedTagException, SQLException {
        Mp3File selectedSong = new Mp3File(song);
        String sql = "INSERT INTO Songs VALUES (?, ?, ?, ?, ?, ?)";
        connection = DriverManager.getConnection(url, user, password);
        preparedStatement = connection.prepareStatement(sql);
        if (selectedSong.hasId3v1Tag()) {
            ID3v1 idTag = selectedSong.getId3v1Tag();
            String songTitle = idTag.getTitle();
            if(songTitle == null ) songTitle = "Unknown";
            String songArtist = idTag.getArtist();
            if(songArtist == null ) songArtist = "Unknown";
            String songGenre = idTag.getGenreDescription();
            if(songGenre == null ) songGenre = "Unknown";
            String songYear = idTag.getYear();
            if(songYear == null ) songYear = "Unknown";
            String songAlbum = idTag.getAlbum();
            if(songAlbum == null ) songAlbum = "Unknown";
            String songDirect = song.getAbsolutePath();
            preparedStatement.setString(1, songDirect);
            preparedStatement.setString(2, songTitle);
            preparedStatement.setString(3, songArtist);
            preparedStatement.setString(4, songGenre);
            preparedStatement.setString(5, songYear);
            preparedStatement.setString(6, songAlbum);
            preparedStatement.execute();
        }
        else if (selectedSong.hasId3v2Tag()) {
            ID3v2 idTag = selectedSong.getId3v2Tag();
            String songTitle = idTag.getTitle();
            if(songTitle == null ) songTitle = "Unknown";
            String songArtist = idTag.getArtist();
            if(songArtist == null ) songArtist = "Unknown";
            String songGenre = idTag.getGenreDescription();
            if(songGenre == null ) songGenre = "Unknown";
            String songAYear = idTag.getYear();
            if(songAYear == null ) songAYear = "Unknown";
            String songDirect = song.getAbsolutePath();
            String songAlbum = idTag.getAlbum();
            if(songAlbum == null ) songAlbum = "Unknown";
            preparedStatement.setString(1, songDirect);
            preparedStatement.setString(2, songTitle);
            preparedStatement.setString(3, songArtist);
            preparedStatement.setString(4, songGenre);
            preparedStatement.setString(5, songAYear);
            preparedStatement.setString(6, songAlbum);
            preparedStatement.execute();

        }
    }

    public void RemoveSong(File song) throws InvalidDataException, IOException, UnsupportedTagException, SQLException {
        Mp3File selectedSong = new Mp3File(song);
        String sql = "DELETE FROM Songs WHERE SongID = ?";
        connection = DriverManager.getConnection(url, user, password);
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, song.getAbsolutePath());
        preparedStatement.execute();
    }
}