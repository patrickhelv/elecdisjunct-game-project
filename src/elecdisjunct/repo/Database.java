package elecdisjunct.repo;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class creates database connection.
 *
 * Inspired by the implementation in last year's Green Trondheim project
 * 
 * @author Victoria Blichfeldt
 */

public class Database {
    private static Database database;
    private static ComboPooledDataSource comboPooledDataSource;

    private static String DATABASE_URL =  "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/";
    private static String DATABASE_USERNAME = null;
    private static String DATABASE_PASSWORD = null;
    private static String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";


    /**
     * Sets up ComboPooledDataSource to handle multiple simultaneous queries to Database
     */
    private Database() {

        // if not given pw & username, read from .properties file
        if (DATABASE_USERNAME == null || DATABASE_PASSWORD == null) {

            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
            encryptor.setKeyObtentionIterations(1000);
            encryptor.setPassword("HelloThere82_whatever--heh");

            Properties props = new EncryptableProperties(encryptor);
            try (FileInputStream fis = new FileInputStream("database.properties")){
                props.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("FAILED TO READ DATABASE PROPERTIES, EXITING...");
                System.exit(-1);
            }

            DATABASE_URL = props.getProperty("db.url");
            DATABASE_USERNAME = props.getProperty("db.username");
            DATABASE_PASSWORD = props.getProperty("db.password");
            DATABASE_DRIVER = props.getProperty("db.driver");
        }

        try {
            comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource.setDriverClass(DATABASE_DRIVER); //loads the jdbc driver
            comboPooledDataSource.setJdbcUrl(DATABASE_URL + DATABASE_USERNAME);
            comboPooledDataSource.setUser(DATABASE_USERNAME);
            comboPooledDataSource.setPassword(DATABASE_PASSWORD);

            comboPooledDataSource.setMaxPoolSize(10);
            comboPooledDataSource.setMaxStatements(100);

            // fixing the timeout
            //comboPooledDataSource.setTestConnectionOnCheckout(true); // if this slows down too much, go for the two below *instead* --> it did.
            // alternative 4 better performance
            comboPooledDataSource.setTestConnectionOnCheckout(false);
            comboPooledDataSource.setTestConnectionOnCheckin(true);
            comboPooledDataSource.setIdleConnectionTestPeriod(30);

            // DO NOT do some aggressive connection age limit or max idle time, rely on the testing to work
        }catch (Exception e){
            System.out.println("Database connection failed");
        }
    }

    /**
     * Checks if there is an existing instance of Database. If not, it will create one.
     *
     * @return an instance of Database, either the existing one, or a new one.
     */

    public static Database getInstance(){
        if(database == null){
            database = new Database();
            return database;
        }else{
            return database;
        }
    }

    /**
     * Returns the connection from Database object.
     *
     * @return a connection to the Database object
     * @throws SQLException
     */

    public Connection getConnection() throws SQLException{ // why was this static, if you don't mind me asking?
        return comboPooledDataSource.getConnection();
    }


    public static void setNewDatabase(String url, String username, String password){ // fixed now
        DATABASE_URL = url;
        setNewDatabase(username, password);
    }

    public static void setNewDatabase(String username, String password){ // fixed now
        DATABASE_USERNAME = username;
        DATABASE_PASSWORD = password;
    }

}
