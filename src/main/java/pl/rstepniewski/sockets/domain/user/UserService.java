package pl.rstepniewski.sockets.domain.user;

/**
 * Created by rafal on 19.04.2023
 *
 * @author : rafal
 * @date : 19.04.2023
 * @project : SocketProgrammingClientServer
 */

import org.jooq.Record;
import org.jooq.Result;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService{
    private List<User> userList = new ArrayList<>();

    private UserRepository userRepository = new UserRepository();

    public UserService() throws SQLException {
    }

    public Optional<User> getUserByName(final String userName) {
        Result<Record> userByName = userRepository.getUserByName(userName);
        User userFound;

        if(userByName.isNotEmpty()) {
            userFound = new User(userByName.get(0).getValue("username", String.class)
                    , userByName.get(0).getValue("password", String.class)
                    , userByName.get(0).getValue("role", UserRole.class));
        }else {
            userFound = null;
        }

        return Optional.ofNullable(userFound);
    }

    public List<User> getUserAndAdminList() {
        Result<Record> userAndAdminRecordList = userRepository.getUserAndAdminList();
        return getListFromRecord(userAndAdminRecordList);
    }

    private List<User> getListFromRecord(Result<Record> recordList) {
        User userFound;
        if(recordList.isNotEmpty()) {
            for(Record record : recordList){
                userFound = new User(record.getValue("username", String.class)
                        , record.getValue("password", String.class)
                        , record.getValue("role", UserRole.class));
                userList.add(userFound);
            }
        }
        return userList;
    }

    public boolean addNewUser(final User user) {
        return userRepository.addNewUser(user);
    }

    public boolean removeUser(final String userName) {
        return userRepository.removeUser(userName);
    }
}
