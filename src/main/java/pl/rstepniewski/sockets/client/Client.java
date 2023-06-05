package pl.rstepniewski.sockets.client;

/**
 * Created by rafal on 19.04.2023
 *
 * @author : rafal
 * @date : 19.04.2023
 * @project : SocketProgrammingClientServer
 */

import pl.rstepniewski.sockets.io.ConsoleUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String LOCAL_HOST = "localhost";
    private static final int PORT_NUMBER = 6900;
    private Socket clientSocket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public static void main(String[] args) throws IOException {
        final Client client = new Client();
        client.startConnection(LOCAL_HOST, PORT_NUMBER);

        client.processCommunication();
    }

    private void startConnection(final String ipAddress, final int port) throws IOException {
        clientSocket = new Socket(ipAddress, port);
        ConsoleUtils.printLine("Successfully established connection with the server.");
    }

    private void stopConnection() throws IOException {
        bufferedReader.close();
        printWriter.close();
        clientSocket.close();
        ConsoleUtils.printLine("Successfully disconnected.");
    }

    private void processCommunication() throws IOException {
        printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        final Scanner scanner = new Scanner(System.in);

        while (true) {
            final String messageFromServer = bufferedReader.readLine();
            if (messageFromServer != null) {
                printMessageFromServer(messageFromServer);
                final String command = scanner.nextLine();
                printWriter.println(command);
            } else {
                scanner.close();
                stopConnection();
                return;
            }
        }
    }

    private void printMessageFromServer(final String messageFromServer) {
        ConsoleUtils.printLine(messageFromServer);
    }

}