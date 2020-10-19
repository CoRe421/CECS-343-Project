import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyDB {
    String user = "root";
    String password = "";
    String url = "jdbc:mysql://localhost:3306/myTunes";

    Connection connection;
    Statement statement;

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
}