import java.sql.*;

public class DataBaseExplorer {

    private Connection connection = null;
    private Statement statement;
    private Connection _connection = null;
    private Statement _statement;
    private PreparedStatement pStatement;


    DataBaseExplorer() throws ClassNotFoundException {
        //constructor for the database Explorer
        Class.forName("org.sqlite.JDBC");
        try {
            // create a database connection to the previous database
            //please if you want to run this, do not forget to change the path to your local database
            this.connection = DriverManager.getConnection("jdbc:sqlite:C:/ellipsys_test_db.db");
            this.statement = this.connection.createStatement();
            this.statement.setQueryTimeout(30);
            // create a database connection to the new database
            this._connection = DriverManager.getConnection("jdbc:sqlite:./myDataBase.db");
            this._statement = this._connection.createStatement();
            this._statement.setQueryTimeout(30);
            System.out.println("A new database has been created.");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void transformDataBase() throws SQLException {
        //core function, this function will transform the table oa-trf-src to oa-trf-src-red
        //read all rows
        ResultSet rs = this.statement.executeQuery("select * from \"oa-trf-src\"");
        while (rs.next()) {
            //for each row, create his equivalent in the oa-trf-src-red table
            this.createUpdatedTable(
                    createJoinTable("id", rs.getString("id")),
                    createJoinTable("trf", rs.getString("trf")),
                    createJoinTable("tgtTb", rs.getString("tgtTb")),
                    createJoinTable("tgtLab", rs.getString("tgtLab")),
                    createJoinTable("srcTb", rs.getString("srcTb")),
                    createJoinTable("srcLab", rs.getString("srcLab")),
                    rs.getInt("impact"));
        }

    }

    private int createJoinTable(String columnLabel, String value) throws SQLException {


        //this function is for creating the join tables
        _statement.executeUpdate("create table IF NOT EXISTS " + getTableName(columnLabel) + " (id INTEGER  PRIMARY KEY AUTOINCREMENT, champ TEXT )");
        //check if the value, has alreadey a row in the table or not
        pStatement = this._connection.prepareStatement("select * from " + getTableName(columnLabel) + " where champ= ?");
        pStatement.setString(1, value);
        ResultSet rs = pStatement.executeQuery();

        while (rs.next()) {
            //the value already has a row in this join table.
            // return the id of the value
            return rs.getInt("id");
        }
        //the value does not exist in this  join table, we need to insert it
        pStatement = this._connection.prepareStatement("insert into " + getTableName(columnLabel) + " (champ) values (?)");
        pStatement.setString(1, value);
        pStatement.executeUpdate();
        //get the id produced by the AUTO INCREMENT Property
        pStatement = this._connection.prepareStatement("select * from " + getTableName(columnLabel) + " where champ= ?");
        pStatement.setString(1, value);
        rs = pStatement.executeQuery();
        while (rs.next()) {
            // return the id of the value
            return rs.getInt("id");
        }
        //a return that will never be executed
        return 0;
    }

    private void createUpdatedTable(int id, int trf, int tgtTb, int tgtLab, int srcTb, int srcLab, int impact) throws SQLException {

        //this function is responsible for creating the new row in the oa-trf-src-red table
        _statement.executeUpdate("create table IF NOT EXISTS \"oa-trf-src-red\" " +
                "(id integer ," +
                "trf integer," +
                "tgtTb integer," +
                "tgtLab integer ," +
                "srcTb integer ," +
                "srcLab integer ," +
                "impact integer )");
        pStatement = this._connection.prepareStatement("insert into \"oa-trf-src-red\" (id,trf,tgtTb,tgtLab,srcTb,srcLab,impact) values (?,?,?,?,?,?,?)");
        pStatement.setInt(1, id);
        pStatement.setInt(2, trf);
        pStatement.setInt(3, tgtTb);
        pStatement.setInt(4, tgtLab);
        pStatement.setInt(5, srcTb);
        pStatement.setInt(6, srcTb);
        pStatement.setInt(7, impact);
        pStatement.executeUpdate();
    }

    private String getTableName(String label) {
        return "\"oa-trf-src-" + label + "-lkp\"";
    }
}


