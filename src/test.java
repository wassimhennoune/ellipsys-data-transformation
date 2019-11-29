import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class test
{
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try {
            DataBaseExplorer de = new DataBaseExplorer();
            de.transformDataBase();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}