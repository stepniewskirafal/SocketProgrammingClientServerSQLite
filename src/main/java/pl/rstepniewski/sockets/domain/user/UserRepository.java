package pl.rstepniewski.sockets.domain.user;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import pl.rstepniewski.sockets.db.DbConnection;

import static org.jooq.impl.DSL.*;

import java.sql.SQLException;

/**
 * Created by rafal on 15.05.2023
 *
 * @author : rafal
 * @date : 15.05.2023
 * @project : SocketProgrammingClientServerDataBase
 */
public class UserRepository extends DbConnection {
    private DSLContext dslContextSqLite;

    public UserRepository() throws SQLException {
        super();
        dslContextSqLite = DSL.using(connectionSqLite, SQLDialect.SQLITE );
    }

    public Result<Record> getUserByName(String userName) {
        SelectConditionStep<Record> userRecord = dslContextSqLite.select()
                                                           .from(table("user"))
                                                           .where(field("username").eq(userName));

        return userRecord.fetch();
    }

    public Result<Record> getUserAndAdminList() {
        SelectJoinStep<Record> usersAdminsRecord = dslContextSqLite.select()
                                                             .from(table("user"));

        SelectJoinStep<Record> usersAdminsRecord2 = dslContextSqLite.select()
                .from(table("user"));
        return usersAdminsRecord2.fetch();
    }

    public boolean addNewUser(User user) {
        boolean responce = true;
        try {
            dslContextSqLite.insertInto(table("user"))
                    .set(field("userName"), user.getUsername())
                    .set(field("password"), user.getPassword())
                    .set(field("role"), user.getRole().getRoleName())
                    .execute();
        }catch (Exception e){
            responce = false;
        }
        return responce;
    }

    public boolean removeUser(String userName) {
        boolean responce = true;
        try {
            dslContextSqLite.delete(table("user"))
                    .where(field("username").eq(userName))
                    .execute();
        }catch (Exception e){
            responce = false;
        }
        return responce;
    }
}
