package persistence.DAO;

import persistence.PooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCConnection {
    private static Connection conn;

    public static String url = "jdbc:mysql://localhost/lecture_registration?characterEncoding=utf8&serverTimezone=UTC&useSSL = false";

    public static Connection getConnection(String url)
    {
        if(conn == null){
            try
            {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = PooledDataSource.getDataSource().getConnection();
                // conn = DriverManager.getConnection(url, "root", "20180017");
                conn.setAutoCommit(false);

            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                System.out.println("Connect 실패");
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                System.out.println("Connect 실패");
            }
        }

        return conn;

    }



}
