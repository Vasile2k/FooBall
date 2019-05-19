package net.vasile2k.fooball.database;

import net.vasile2k.fooball.game.scene.SceneGame;

import javax.xml.crypto.Data;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Vasile2k on 19.05.2019.
 *
 */

public class DatabaseManager {

    public static final String DATABASE_URL = "jdbc:sqlite:fooball.db";
    public static final String LEVEL_FILE = "fooball.dat";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to register database driver.");
        }
    }

    private DatabaseManager(){

    }

    public static boolean getSettingsFullscreen(){
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            Statement statement = connection.createStatement();
            String query = "SELECT fullscreen FROM Settings";
            ResultSet resultSet = statement.executeQuery(query);

            boolean data = resultSet.getBoolean("fullscreen");

            resultSet.close();
            statement.close();
            connection.close();

            return data;

        } catch (SQLException e) {
            e.printStackTrace();
            setSettingsFullscreen(false);
        }
        return false;
    }

    public static void setSettingsFullscreen(boolean fullscreen){
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            Statement statement = connection.createStatement();
            String query = "DROP TABLE IF EXISTS Settings; CREATE TABLE Settings(fullscreen INTEGER);INSERT INTO Settings VALUES (" + (fullscreen ? "1" : "0") + ");";
            statement.executeUpdate(query);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            // Ignore error, it means there's nothing saved yet
            // Maybe first run?
        }
    }

    public static int[] getHighscores(){
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Scores ORDER BY score DESC"; // DESCENDING
            ResultSet resultSet = statement.executeQuery(query);

            ArrayList<Integer> scores = new ArrayList<>();

            while(resultSet.next()){
                int score = resultSet.getInt("score");
                scores.add(score);
                // Receive only 4 highscores, there's enough
                if(scores.size() == 4){
                    break;
                }
            }

            resultSet.close();
            statement.close();
            connection.close();

            return scores.stream().mapToInt(i -> i).toArray();

        } catch (SQLException e) {
            // Ignore error, it means there's no score yet
            // Maybe first run?
        }
        return new int[0];
    }

    public static void saveScore(int score){
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS Scores(score INTEGER);INSERT INTO Scores VALUES (" + score + ");";
            statement.executeUpdate(query);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveState(SceneGame sceneGame){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(LEVEL_FILE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(sceneGame);

            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static SceneGame loadState(){
        try {
            FileInputStream fileInputStream = new FileInputStream(LEVEL_FILE);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            SceneGame sceneGame = (SceneGame) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();

            return sceneGame;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
