package pl.rstepniewski.sockets.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by rafal on 05.06.2023
 *
 * @author : rafal
 * @date : 05.06.2023
 * @project : SocketProgrammingClientServerDataBase — SQLite
 */
public class DbConnection {
    protected Connection connectionSqLite;

    public DbConnection() throws SQLException {
        connectionSqLite = DriverManager.getConnection("jdbc:sqlite:/C:\\sqlite\\sqlite-tools\\ClientServerDataBase.db");
    }
}
