package pl.rstepniewski.sockets.server;

/**
 * Created by rafal on 19.04.2023
 *
 * @author : rafal
 * @date : 19.04.2023
 * @project : SocketProgrammingClientServer
 */

import pl.rstepniewski.sockets.io.ConsoleUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
    private static final String SERVER_VERSION = "0.4.0";
    private static final String CREATION_DATE = "16.05.2023";
    private static final int PORT_NUMBER = 6900;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    void start() throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
        clientSocket = serverSocket.accept();
        ConsoleUtils.printLine("The client has been successfully connected.");
    }

    void stop() throws IOException {
        clientSocket.close();
        serverSocket.close();
        ConsoleUtils.printLine("The client has been successfully disconnected.");
    }

    String getServerVersion() {
        return SERVER_VERSION;
    }

    String getCreationDate() {
        return CREATION_DATE;
    }

    Socket getClientSocket() {
        return clientSocket;
    }

    public static void main(String[] args) throws IOException, SQLException {
        ServerService serverService = new ServerService(new Server());
        serverService.run();
    }
}