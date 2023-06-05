package pl.rstepniewski.sockets.domain.message;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import pl.rstepniewski.sockets.db.DbConnection;

import static org.jooq.impl.DSL.*;

import java.sql.SQLException;

/**
 * Created by rafal on 14.05.2023
 *
 * @author : rafal
 * @date : 14.05.2023
 * @project : SocketProgrammingClientServerDataBase
 */
public class MessageRepository extends DbConnection {
    private DSLContext dslContextSqLite;

    public MessageRepository() throws SQLException {
        super();
        dslContextSqLite = DSL.using(connectionSqLite, SQLDialect.SQLITE );
    }

    public Result<Record> findByUserName(String userName ) {
        SelectConditionStep<Record> selectStatement = dslContextSqLite.select()
                                                            .from(table("message"))
                                                            .where(field("sender").eq(userName));
        return selectStatement.fetch();
    }

    public Record1<Integer> countAllByUserName(String userName ) {
        SelectConditionStep<Record1<Integer>> countStatement = dslContextSqLite.selectCount()
                                                                .from(table("message"))
                                                                .where(field("sender").eq(userName));
        return countStatement.fetchSingle();
    }

    public void addNewMessage(Message message ) {
        dslContextSqLite.insertInto(table("message"))
                .set(field("topic"), message.getTopic())
                .set(field("content"), message.getContent())
                .set(field("recipient"), message.getRecipient())
                .set(field("sender"), message.getSender())
                .execute();
    }

}
