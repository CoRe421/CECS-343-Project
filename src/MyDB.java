import com.mpatric.mp3agic.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

public class MyDB {
    String user = "root";
    String password = "";
    String url = "jdbc:mysql://localhost:3306/mytunes";

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

    public ArrayList<Song> songList(){
        ArrayList<Song> songList = new ArrayList<>();
        try{
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT * FROM Songs";
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            Song song;
            while (rs.next()){
                song = new Song (rs.getString("Title"),rs.getString("Artist"),rs.getString("Album"),rs.getString("Genre"),rs.getString("Release Year"),rs.getString("Comment"));
                songList.add(song);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return songList;
    }

    public ArrayList<Song> songList(String plName){
        ArrayList<Song> songList = new ArrayList<>();
        try{
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT * FROM Songs";
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            Song song;
            while (rs.next()){
                song = new Song (rs.getString("Title"),rs.getString("Artist"),rs.getString("Album"),rs.getString("Genre"),rs.getString("Release Year"),rs.getString("Comment"));
                songList.add(song);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return songList;
    }

    public void addSong(File song) throws InvalidDataException, IOException, UnsupportedTagException, SQLException {
        Mp3File selectedSong = new Mp3File(song);
        String sql = "INSERT INTO Songs VALUES (?, ?, ?, ?, ?, ?,?)";
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
            String songAYear = idTag.getYear();
            if(songAYear == null ) songAYear = "Unknown";
            String songAlbum = idTag.getAlbum();
            if(songAlbum == null ) songAlbum = "Unknown";
            String songComment = idTag.getComment();
            if(songComment == null ) songComment = "Unknown";
            String songDirect = song.getAbsolutePath();
            preparedStatement.setString(1, songDirect);
            preparedStatement.setString(2, songTitle);
            preparedStatement.setString(3, songArtist);
            preparedStatement.setString(4, songGenre);
            preparedStatement.setString(5, songAYear);
            preparedStatement.setString(6, songAlbum);
            preparedStatement.setString(7, songComment);
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
            String songAlbum = idTag.getAlbum();
            if(songAlbum == null ) songAlbum = "Unknown";
            String songComment = idTag.getComment();
            if(songComment == null ) songComment = "Unknown";
            String songDirect = song.getAbsolutePath();
            preparedStatement.setString(1, songDirect);
            preparedStatement.setString(2, songTitle);
            preparedStatement.setString(3, songArtist);
            preparedStatement.setString(4, songGenre);
            preparedStatement.setString(5, songAYear);
            preparedStatement.setString(6, songAlbum);
            preparedStatement.setString(7, songComment);
            preparedStatement.execute();
        }
        else if (selectedSong.hasCustomTag()) {
            System.out.println("Your song has a custom tag and will not work.");
        }
        else {
            System.out.println("Song type not compatible");
        }
    }
//    public void RemoveSong(File song) throws InvalidDataException, IOException, UnsupportedTagException, SQLException {
//        Mp3File selectedSong = new Mp3File(song);
//        String sql = "DELETE FROM Songs WHERE SongID = ?";
//        connection = DriverManager.getConnection(url, user, password);
//        preparedStatement = connection.prepareStatement(sql);
//        preparedStatement.setString(1, song.getAbsolutePath());
//        preparedStatement.execute();
//    }

    public void removeSong(String songTitle, String songArtist) throws SQLException {
        String sql = "DELETE FROM Songs WHERE Title = ? AND Artist = ?";
        connection = DriverManager.getConnection(url, user, password);
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, songTitle);
        preparedStatement.setString(2, songArtist);
        preparedStatement.execute();
    }


}