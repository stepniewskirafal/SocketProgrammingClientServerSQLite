package pl.rstepniewski.sockets.server;

/**
 * Created by rafal on 19.04.2023
 *
 * @author : rafal
 * @date : 19.04.2023
 * @project : SocketProgrammingClientServer
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pl.rstepniewski.sockets.domain.message.*;
import pl.rstepniewski.sockets.domain.user.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class ServerService {
    private final Server server;
    private PrintWriter out;
    private BufferedReader in;
    private final Instant startTime = Instant.now();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectNode jsonNode = objectMapper.createObjectNode();
    private MessageRepository messageRepository = new MessageRepository();
    UserService userService = new UserService();
    MessageService messageService = new MessageService(messageRepository);

    ServerService(final Server server) throws IOException, SQLException {
        this.server = server;
        start();
    }

    void run() throws IOException {
        mainLoop();
    }

    private void start() throws IOException {
        server.start();
        out = new PrintWriter(server.getClientSocket().getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(server.getClientSocket().getInputStream()));
    }

    private void stopServerService() throws IOException {
        in.close();
        out.close();
        server.stop();
    }

    private void mainLoop() throws IOException {
       // handleAdminInterface(new User("admin1", "admin1", UserRole.ADMIN)); /*testing purposes*/
        User user = loginProcess();
        UserRole role = user.getRole();
        if(role == UserRole.USER){
            handleUserInterface(user);
        } else if (role == UserRole.ADMIN) {
            handleAdminInterface(user);
        }
    }

    private void handleAdminInterface(final User user) throws IOException {
        showAdminInterface();
        while (true) {
            switch (getClientAnswer()) {
                case "uptime" -> showUptime();
                case "info"   -> showInfo();
                case "help"   -> showHelp();
                case "stop"   -> {
                    stopServerService();
                    return;
                }
                case "listAllUsers"   -> listAllUsers();
                case "addNewUser"     -> addNewUser();
                case "deleteUser"     -> deleteUser();
                case "sendMessage"    -> sendMessage(user);
                case "readMessage"    -> readMessage(user);
                default       -> unknownCommand();
            }
        }
    }

    private void handleUserInterface(final User user) throws IOException {
        showUserInterface();
        while (true) {
            switch (getClientAnswer()) {
                case "sendMessage"    -> sendMessage(user);
                case "readMessage"    -> readMessage(user);
                case "stop"   -> {
                    stopServerService();
                    return;
                }
                default       -> unknownCommand();
            }
        }
    }

   private void sendMessage(final User user) throws IOException {
        Message message = createMessage(user);
        Optional<User> userByNameOptional = userService.getUserByName(message.getRecipient());
        if (userByNameOptional.isEmpty()){
            jsonNode.put("sendMessageWarning", "There is no such user in the system yet.");
            sendJsonMessage(jsonNode);
            return;
        }

        boolean isMessageSent = messageService.sendMessage(message);
        if(isMessageSent){
            jsonNode.put("sendMessage", "The process of sending message successfully finished.");
        }else{
            jsonNode.put("sendMessage", userByNameOptional.get().getUsername()+"'s email box is full, You can not send a message to this user.");
        }
        sendJsonMessage(jsonNode);

    }
    private Message createMessage(final User user) throws IOException {
        jsonNode.put("Sending a message", "Provide a recipient name and message content");
        jsonNode.put("Recipient", "Who is your text recipient?");
        sendJsonMessage(jsonNode);
        String recipient = getClientAnswer().toLowerCase(Locale.ENGLISH);

        jsonNode.put("Topic", "Provide a topic of your message:");
        sendJsonMessage(jsonNode);
        String topic = getClientAnswer();

        jsonNode.put("Message", "Provide a content of your message (will be trimmed to 255 characters):");
        sendJsonMessage(jsonNode);
        String content = getClientAnswer();
        content = content.substring(0, Math.min(content.length(), MessageConst.MAX_LENGTH_OF_MESSAGE.getMessageLenght()));

        return new Message(topic, content, recipient, user.getUsername());
    }
    private void readMessage(final User user) throws IOException {
        ObjectNode result = objectMapper.createObjectNode();
        Optional<ArrayNode> userMessages = messageService.getUserMessages(user.getUsername());

        ObjectNode warningMessage = objectMapper.createObjectNode();

        if(!userMessages.isPresent()){
            warningMessage.put("emailBoxWarning", "There is no message to read.");
             userMessages.get().add(warningMessage);
            return;
        }

        warningMessage.put("emailBoxWarning", "Please, read your messages carefully as the below list will self-destruct after you pick the next option or close a connection.");
        userMessages.get().add(warningMessage);

        result.put("readMessage", warningMessage);
        sendJsonMessage(result);
    }
    private void listAllUsers() throws JsonProcessingException {
        List<User> allUserList = userService.getUserAndAdminList();
        allUserList.stream()
                .forEach( (element) -> {
                    int index = allUserList.indexOf(element);
                    jsonNode.put(String.valueOf(index), element.getUsername() + " " + element.getRole());
                } );

        sendJsonMessage(jsonNode);
    }

    private void addNewUser() throws IOException {
        jsonNode.put("User name", "Provide new user name");
        sendJsonMessage(jsonNode);
        String userName = getClientAnswer();

        jsonNode.put("Password", "Provide new user password");
        sendJsonMessage(jsonNode);
        String password = getClientAnswer();

        String role;
        while(true) {
            jsonNode.put("Password", "Provide new user role");
            sendJsonMessage(jsonNode);
            role = getClientAnswer().toUpperCase(Locale.ENGLISH);
            List<String> list = Arrays.stream(UserRole.values()).map(UserRole::getRoleName).toList();
            if (list.contains(role)){
                break;
            }
        }

        User userToAdd = new User(userName, password, UserRole.valueOf(role));

        boolean responce = userService.addNewUser(userToAdd);
        if (responce) {
            jsonNode.put("addNewUser", "New user has been successfully added");
        }else {
            jsonNode.put("addNewUser", "The process of adding a new user has failed.");
        }
        sendJsonMessage(jsonNode);
    }

    private void deleteUser() throws IOException {
        jsonNode.put("User name", "Provide new user name");
        sendJsonMessage(jsonNode);
        String userName = getClientAnswer();

        boolean responce = userService.removeUser(userName);
        if (responce) {
            jsonNode.put("addNewUser", "New user has been successfully removed");
        }else {
            jsonNode.put("addNewUser", "The process of removing a new user has failed.");
        }
        sendJsonMessage(jsonNode);
    }

    private User loginProcess() throws IOException {
        Optional<User> loginAttempt;
        List<User> allUserList = userService.getUserAndAdminList();
        showWelcomePage();
        while (true){
            UserDto userDto = getUserNameAndPassword();
            loginAttempt = allUserList
                    .stream()
                    .filter(user -> (user.getUsername().equals(userDto.getUsername())
                            && user.getPassword().equals(userDto.getPassword())))
                    .findFirst();
            if(!loginAttempt.isEmpty()){
                break;
            }
        }
        return loginAttempt.get();
    }

    private void showWelcomePage() throws IOException {
        jsonNode.put("WelcomePage", "Welcome to the Message App:");
        jsonNode.put("Log in", "Provide your credentials:");
    }

    private UserDto getUserNameAndPassword() throws IOException {
        jsonNode.put("User name", "Provide your user name");
        sendJsonMessage(jsonNode);
        String userName = getClientAnswer();

        jsonNode.put("Password", "Provide your password");
        sendJsonMessage(jsonNode);
        String password = getClientAnswer();

        return new UserDto(userName, password);
    }

    private void showUserInterface() throws JsonProcessingException {
        jsonNode.put("SERVER MENU", "Options:");
        jsonNode.put("sendMessage", "Send a message to another User.");
        jsonNode.put("readMessage", "Read a chosen message.");
        jsonNode.put("stop", "Stop server and client");

        sendJsonMessage(jsonNode);
    }

    private void showAdminInterface() throws JsonProcessingException {
        jsonNode.put("SERVER MENU", "Options:");
        jsonNode.put("uptime", "Return server running time");
        jsonNode.put("info", "Return server version and creation date");
        jsonNode.put("help", "Return list of available commands");
        jsonNode.put("stop", "Stop server and client");

        jsonNode.put("listAllUsers", "Show a list of all users and their roles");
        jsonNode.put("addNewUser", "Add a new user to the app");
        jsonNode.put("deleteUser","Delete user from the app");
        jsonNode.put("sendMessage", "Send a message to another User.");
        jsonNode.put("readMessage", "Read the chosen message.");

        sendJsonMessage(jsonNode);
    }

    private void sendJsonMessage(final ObjectNode jsonNode) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(jsonNode);
        jsonNode.removeAll();
        out.println(json);
    }

    private void showUptime() throws JsonProcessingException {
        Duration lifeTimeDuration = Duration.between(startTime, Instant.now());
        long seconds = lifeTimeDuration.getSeconds();

        long hourCounter = seconds / 3600;
        long minutesCounter = (seconds % 3600) / 60;
        long secondsCounter = seconds % 60;
        jsonNode.put("hours", Long.toString(hourCounter));
        jsonNode.put("minutes", Long.toString(minutesCounter));
        jsonNode.put("seconds", Long.toString(secondsCounter));

        sendJsonMessage(jsonNode);
    }

    private void showInfo() throws JsonProcessingException {
        jsonNode.put("server version", server.getServerVersion());
        jsonNode.put("creation date", server.getCreationDate());

        sendJsonMessage(jsonNode);
    }

    private void showHelp() throws JsonProcessingException {
        jsonNode.put("uptime", "Return server running time");
        jsonNode.put("info", "Return server version and creation date");
        jsonNode.put("help", "Return list of available commands");

        sendJsonMessage(jsonNode);
    }

    private void unknownCommand() throws JsonProcessingException {
        jsonNode.put("Command", "Command unknown.");
        sendJsonMessage(jsonNode);
    }

    private String getClientAnswer() throws IOException {
        return in.readLine();
    }


}