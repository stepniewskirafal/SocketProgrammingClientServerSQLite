package pl.rstepniewski.sockets.domain.message;

/**
 * Created by rafal on 19.04.2023
 *
 * @author : rafal
 * @date : 19.04.2023
 * @project : SocketProgrammingClientServer
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;

import java.io.IOException;
import java.util.Optional;

public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ObjectNode objectNode = objectMapper.createObjectNode();


    public Optional<ArrayNode> getUserMessages(final String userName) {
        ArrayNode jsonNodes = objectMapper.createArrayNode();
        Result<Record> resultByUsername = messageRepository.findByUserName(userName);

        if (resultByUsername.isNotEmpty()) {
            jsonNodes = resultToJsonArray(resultByUsername);
        }

        return Optional.ofNullable(jsonNodes);
    }

    private ArrayNode resultToJsonArray(Result<Record> result) {
        ArrayNode jsonMessages = objectMapper.createArrayNode();

        for (Record record : result) {
            ObjectNode jsonMessage = objectMapper.createObjectNode();
            jsonMessage.put("id",  record.getValue("id").toString());
            jsonMessage.put("topic", record.getValue("topic").toString());
            jsonMessage.put("content", record.getValue("content").toString());
            jsonMessage.put("recipient", record.getValue("recipient").toString());
            jsonMessage.put("sender", record.getValue("sender").toString());

            jsonMessages.add(jsonMessage);
        }

        ArrayNode resultMessages = objectMapper.createArrayNode();
        ObjectNode resultMessage = objectMapper.createObjectNode();

        resultMessage.put("Message", jsonMessages);
        resultMessages.add(resultMessage);

        return resultMessages;
    }

    public boolean sendMessage(final Message message ) throws IOException {
        boolean isMessageSent = true;

        Record1<Integer> messagesInTheBoxRecord = messageRepository.countAllByUserName(message.getRecipient());
        Integer messagesInTheBoxCnt = messagesInTheBoxRecord.value1();

        if (messagesInTheBoxCnt > 5){
            isMessageSent = false;
        }else {
            try {
                messageRepository.addNewMessage(message);
            } catch (Exception e) {
                isMessageSent = false;
            }
        }
        return isMessageSent;
    }
}
