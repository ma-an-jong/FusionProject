
import Server.ServerThread;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import persistence.DAO.*;
import persistence.DTO.*;
import persistence.MyBatisConnectionFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/*

org.apache.ibatis.exceptions.PersistenceException:
### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '2' for key 'lecture_registration_date.PRIMARY'
### The error may exist in persistence/Mapper/LectureRegistrationDateMapper.java (best guess)
### The error may involve persistence.Mapper.LectureRegistrationDateMapper.insertSeason-Inline
### The error occurred while setting parameters
### SQL: INSERT INTO lecture_registration_date(grade,start_date,end_date) VALUE (?,?,?)
### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '2' for key 'lecture_registration_date.PRIMARY'
	at org.apache.ibatis.exceptions.ExceptionFactory.wrapException(ExceptionFactory.java:30)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.update(DefaultSqlSession.java:199)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.insert(DefaultSqlSession.java:184)
	at org.apache.ibatis.binding.MapperMethod.execute(MapperMethod.java:62)
	at org.apache.ibatis.binding.MapperProxy$PlainMethodInvoker.invoke(MapperProxy.java:152)
	at org.apache.ibatis.binding.MapperProxy.invoke(MapperProxy.java:85)
	at com.sun.proxy.$Proxy20.insertSeason(Unknown Source)
	at persistence.DAO.LectureRegistrationDateDAO.updateSeason(LectureRegistrationDateDAO.java:69)
	at persistence.DAO.LectureRegistrationDateDAO.setSeason(LectureRegistrationDateDAO.java:91)
	at Server.ServerThread.run(ServerThread.java:944)
Caused by: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '2' for key 'lecture_registration_date.PRIMARY'
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:117)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)
	at com.mysql.cj.jdbc.ClientPreparedStatement.executeInternal(ClientPreparedStatement.java:953)
	at com.mysql.cj.jdbc.ClientPreparedStatement.execute(ClientPreparedStatement.java:370)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:64)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:564)
	at org.apache.ibatis.logging.jdbc.PreparedStatementLogger.invoke(PreparedStatementLogger.java:59)
	at com.sun.proxy.$Proxy17.execute(Unknown Source)
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.update(PreparedStatementHandler.java:47)
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.update(RoutingStatementHandler.java:74)
	at org.apache.ibatis.executor.SimpleExecutor.doUpdate(SimpleExecutor.java:50)
	at org.apache.ibatis.executor.BaseExecutor.update(BaseExecutor.java:117)
	at org.apache.ibatis.executor.CachingExecutor.update(CachingExecutor.java:76)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.update(DefaultSqlSession.java:197)
	... 8 more

 */

public class Main {
    public static void main(String args[]){

        ServerSocket serverSocket = null;

        try{
            serverSocket = new ServerSocket();
        }
        catch (Exception e){
            System.out.println("serverSocketException");
            e.printStackTrace();
            return;
        }

        try
        {
            serverSocket.bind(new InetSocketAddress("192.168.232.7",5000));
            System.out.println("server on");

        }
        catch (Exception e)
        {
            System.out.println("bindException");
            e.printStackTrace();
        }

        Socket socket = null;

        while (true)
        {
            try
            {
                socket = serverSocket.accept();
                System.out.println("클라이언트 접속:" + socket.getInetAddress() + ":" + socket.getPort());
            }

            catch (Exception e)
            {
                System.out.println("acceptException");
                e.printStackTrace();
                return;
            }

            ServerThread serverThread = new ServerThread(socket);
            serverThread.start();
        }

    }
}