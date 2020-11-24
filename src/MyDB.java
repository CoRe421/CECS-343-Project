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
            String playListSQL = "SELECT SongID FROM `Playlist Songs` WHERE title = ?"; //Run a Query to get a list of songID's for a specific playlist
            PreparedStatement playListPS = connection.prepareStatement(playListSQL);
            playListPS.setString(1,plName);
            ResultSet playListResults = playListPS.executeQuery(); //List of songsID for specific playlist
            while(playListResults.next()){
                String curSongID = playListResults.getString("SongID");
                String SongSQL = "SELECT * FROM Songs WHERE SongID = ?"; // Run a Query to get all the song info based on the list of song IDs
                PreparedStatement songPS = connection.prepareStatement(SongSQL);
                songPS.setString(1,curSongID);
                ResultSet songResults = songPS.executeQuery();
                Song song;
                while (songResults.next()){
                    song = new Song (songResults.getString("Title"),songResults.getString("Artist"),songResults.getString("Album"),songResults.getString("Genre"),songResults.getString("Release Year"),songResults.getString("Comment"));
                    songList.add(song);
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return songList;
    }

    public void addSong(File song) throws InvalidDataException, IOException, UnsupportedTagException, SQLException {
        Mp3File selectedSong = new Mp3File(song);
        String sql = "INSERT INTO Songs VALUES (?, ?, ?, ?, ?, ?, ?)";
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

    public void removeSong(String songTitle, String songArtist) throws SQLException {
        String sql = "DELETE FROM Songs WHERE Title = ? AND Artist = ?";
        connection = DriverManager.getConnection(url, user, password);
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, songTitle);
        preparedStatement.setString(2, songArtist);
        preparedStatement.execute();
    }

    public void addPlayList(String playList) throws SQLException{
        String sql = "INSERT INTO Playlist VALUE (?)";
        connection = DriverManager.getConnection(url, user, password);
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, playList);
        preparedStatement.execute();
    }

    public void deletePlayList(String playList) throws SQLException{
        String sql = "DELETE FROM Playlist WHERE Title = ?";
        connection = DriverManager.getConnection(url, user, password);
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, playList);
        preparedStatement.execute();
    }

    public void addSongToPlayList(String playList, String songID) throws SQLException{
        String sql = "INSERT INTO `Playlist songs` VALUE (?,?)";
        connection = DriverManager.getConnection(url, user, password);
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, playList);
        preparedStatement.setString(2, songID);
        preparedStatement.execute();
    }

    public void deleteSongFromPlaylist(String playList, String songID) throws SQLException{
        String sql = "DELETE FROM `Playlist Songs` WHERE Title = ? AND SongID = ?";
        connection = DriverManager.getConnection(url, user, password);
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, playList);
        preparedStatement.setString(2, songID);
        preparedStatement.execute();
    }

    public ArrayList<String> getListOfPlaylist(){
        ArrayList<String> ListOfPlayList = new ArrayList<>();
        try{
            connection = DriverManager.getConnection(url, user, password);
            String sql = "SELECT * FROM Playlist";
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                String title = rs.getString("Title");
                ListOfPlayList.add(title);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ListOfPlayList;
    }

}