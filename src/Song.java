public class Song {
    private String title, artist, album, genre, releaseYear, comment;
    public Song(String title, String artist, String album, String genre, String releaseYear, String comment){
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getComment() {
        return comment;
    }
}
